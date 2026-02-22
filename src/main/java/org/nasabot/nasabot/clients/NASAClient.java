package org.nasabot.nasabot.clients;

import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nasabot.nasabot.objects.NASAImage;
import org.nasabot.nasabot.objects.marsweather.AT;
import org.nasabot.nasabot.objects.marsweather.HWS;
import org.nasabot.nasabot.objects.marsweather.MarsWeatherData;
import org.nasabot.nasabot.objects.marsweather.PRE;
import org.nasabot.nasabot.objects.marsweather.Sol;
import org.nasabot.nasabot.objects.marsweather.WD;
import org.nasabot.nasabot.objects.marsweather.WindDirection;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NASAClient extends NASABotClient {
    private final String baseUrl = "https://api.nasa.gov";
    private final String imageUrl = "https://images-api.nasa.gov";
    private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String apiKey;
    private Pair<Long, MarsWeatherData> cachedMarsWeatherData;
    private Pair<Long, EmbedBuilder> cachedAPOD;

    private NASAClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            apiKey = resourceBundle.getString("NASAKey");
        } catch (Exception e) {
            errorLoggingClient.handleError("NASAClient", "NASAClient", "Cannot contact NASA API.", e);
            System.exit(0);
        }
    }

    private static class NASAClientSingleton {
        private static final NASAClient INSTANCE = new NASAClient();
    }

    public static NASAClient getInstance() {
        return NASAClient.NASAClientSingleton.INSTANCE;
    }

    public EmbedBuilder getPictureOfTheDay(String date) {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseUrl + "/planetary/apod")).newBuilder();
        builder.addQueryParameter("api_key", apiKey).addQueryParameter("date", date);
        Request request = new Request.Builder().url(builder.build().toString()).build();
        try (Response response = httpClient.newCall(request).execute()) {
            return formatPictureOfTheDay(Objects.requireNonNull(response.body()).string());
        } catch (Exception e) {
            errorLoggingClient.handleError("NASAClient", "getPictureOfTheDay", "Cannot get picture of the day.", e);
        }

        return null;
    }

    public EmbedBuilder getLatestPictureOfTheDay() {
        // If no cache or if cache is older than 1 hour
        if (cachedAPOD == null || cachedAPOD.getFirst() < System.currentTimeMillis() / 1000 - 3600) {
            LocalDate now = LocalDate.now().plusDays(2);
            System.out.println(now.toString());
            while (true) {
                HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseUrl + "/planetary/apod")).newBuilder();
                builder.addQueryParameter("api_key", apiKey).addQueryParameter("date", inputDateFormat.format(Date.from(now.atStartOfDay(ZoneOffset.UTC).toInstant())));
                Request request = new Request.Builder().url(builder.build().toString()).build();
                System.out.println(request.url());
                try (Response response = httpClient.newCall(request).execute()) {
                    ResponseBody responseBody = response.body();
                    String responseString = responseBody.string();
                    if (responseString.contains("No data available") || responseString.contains("Date must be between")) {
                        now = now.minusDays(1);
                        continue;
                    }
                    System.out.println(responseString);
                    EmbedBuilder embedBuilder = formatPictureOfTheDay(responseString);
                    cachedAPOD = new Pair<>(System.currentTimeMillis() / 1000, embedBuilder);
                    return embedBuilder;
                } catch (Exception e) {
                    errorLoggingClient.handleError("NASAClient", "getPictureOfTheDay", "Cannot get picture of the day.", e);
                    // If we get an error just return the cached APOD if present.
                    return cachedAPOD != null ? cachedAPOD.getSecond() : null;
                }
            }
        } else {
            System.out.println("Cached APOD.");
            return cachedAPOD.getSecond();
        }
    }

    private EmbedBuilder formatPictureOfTheDay(String POTDResponse) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            JSONObject jsonObject = new JSONObject(POTDResponse);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder
                    .setTitle(jsonObject.getString("title"), String.format("https://apod.nasa.gov/apod/ap%s.html", jsonObject.getString("date").replaceAll("-", "").substring(2)))
                    .setDescription(String.format("%s", outputDateFormat.format(inputDateFormat.parse(jsonObject.getString("date")))))
                    .setColor(new Color(192, 32, 232))
                    .addField("Description", jsonObject.getString("explanation").length() > 1024 ? String.format("%s", jsonObject.getString("explanation")).substring(0, 1020) + "..." : String.format("%s", jsonObject.getString("explanation")), false);
            if (jsonObject.has("hdurl")) {
                embedBuilder.addField("HD Image Link", jsonObject.getString("hdurl"), false);
            }
            if (jsonObject.has("url")) {
                if (jsonObject.getString("url").contains("youtube.com") || jsonObject.getString("url").contains("video")) {
                    embedBuilder.addField("Video Link", jsonObject.getString("url"), false);
                }
            }
            return embedBuilder;
        } catch (Exception e) {
            errorLoggingClient.handleError("NASAClient", "formatPictureOfTheDay", "Cannot format picture of the day.", e);
            return new EmbedBuilder().setTitle("Picture of the Day").addField("ERROR", "Unable to obtain Picture of the Day.", false).setColor(Color.RED);
        }
    }

    public List<NASAImage> getNASAImages(String searchTerm, int pageNumber) {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(imageUrl + "/search")).newBuilder();
        builder.addQueryParameter("media_type", "image").addQueryParameter("q", searchTerm).addQueryParameter("page", String.valueOf(pageNumber));
        Request request = new Request.Builder().url(builder.build().toString()).build();
        try (Response response = httpClient.newCall(request).execute()) {
            JSONObject responseBody = new JSONObject(Objects.requireNonNull(response.body()).string());
            JSONArray responseArray = responseBody.has("reason") ? new JSONArray() : responseBody.getJSONObject("collection").getJSONArray("items");
            return filterSuitableImages(responseArray);
        } catch (Exception e) {
            errorLoggingClient.handleError("NASAClient", "getNASAImage", "Cannot get NASA images.", e);
        }

        return null;
    }

    private List<NASAImage> filterSuitableImages(JSONArray imageResponseArray) {
        List<NASAImage> images = new ArrayList<>();

        for (int i = 0; i < imageResponseArray.length(); i++) {
            JSONObject selection = imageResponseArray.getJSONObject(i);
            List<String> requiredDataFields = List.of("title", "nasa_id", "description", "date_created", "location");
            try {
                boolean valid = true;
                JSONObject data = selection.getJSONArray("data").getJSONObject(0);
                for (String field : requiredDataFields) {
                    if (!data.has(field)) {
                        valid = false;
                        break;
                    }
                }

                // Make sure there is an image link we can use.
                if (!selection.getJSONArray("links").getJSONObject(0).has("href") || !selection.getJSONArray("links").getJSONObject(0).getString("render").equals("image")) {
                    valid = false;
                }

                if (valid) {
                    images.add(formatImageFromJSON(selection));
                }
            } catch (Exception e) {
                errorLoggingClient.handleError("NASAClient", "filterSuitableImages", "Uncaught exception when filtering images.", e);
            }
        }

        return images;
    }

    private NASAImage formatImageFromJSON(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONArray("data").getJSONObject(0);
        return new NASAImage(
                data.getString("title"),
                data.getString("nasa_id"),
                data.getString("description"),
                data.getString("date_created"),
                data.getString("location"),
                jsonObject.getJSONArray("links").getJSONObject(0).getString("href").replace(" ", "%20")
        );
    }

    public MarsWeatherData getMarsWeatherData() {
        // If no cache or if cache is older than 1 hour
        if (cachedMarsWeatherData == null || cachedMarsWeatherData.getFirst() < System.currentTimeMillis() / 1000 - 3600) {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseUrl + "/insight_weather/")).newBuilder();
            builder.addQueryParameter("api_key", apiKey).addQueryParameter("feedtype", "json").addQueryParameter("ver", "1.0");
            Request request = new Request.Builder().url(builder.build().toString()).build();
            try (Response response = httpClient.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                String responseString = responseBody.string();
                MarsWeatherData weatherData = formatMarsWeatherData(responseString);
                cachedMarsWeatherData = new Pair<>(System.currentTimeMillis() / 1000, weatherData);
                return weatherData;
            } catch (Exception e) {
                errorLoggingClient.handleError("NASAClient", "getMarsWeatherData", "Cannot get Mars weather data.", e);
                return null;
            }
        } else {
            return cachedMarsWeatherData.getSecond();
        }
    }

    private MarsWeatherData formatMarsWeatherData(String responseString) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            List<Sol> sols = new ArrayList<>();
            JSONArray solKeys = jsonObject.getJSONArray("sol_keys");
            for (int i = 0; i < solKeys.length(); i++) {
                JSONObject sol = jsonObject.getJSONObject(solKeys.getString(i));
                String name = solKeys.getString(i);
                String season = sol.optString("Season", "UNKNOWN");
                String firstUTC = sol.optString("First_UTC", "UNKNOWN");
                String lastUTC = sol.optString("Last_UTC", "UNKNOWN");

                // WD
                WindDirection mostCommon = null;
                List<WindDirection> windDirectionList = new ArrayList<>();
                JSONObject windDirections = sol.getJSONObject("WD");
                for (String key : windDirections.keySet()) {
                    JSONObject wd = windDirections.getJSONObject(key);
                    if (key.equals("most_common")) {
                        mostCommon = new WindDirection(
                                wd.getDouble("compass_degrees"),
                                wd.getDouble("compass_right"),
                                wd.getDouble("compass_up"),
                                wd.getString("compass_point"),
                                wd.getInt("ct"));
                    } else {
                        windDirectionList.add(new WindDirection(
                                wd.getDouble("compass_degrees"),
                                wd.getDouble("compass_right"),
                                wd.getDouble("compass_up"),
                                wd.getString("compass_point"),
                                wd.getInt("ct")));
                    }
                }

                WD wd = new WD(windDirectionList, mostCommon);

                // PRE
                JSONObject preObject = sol.getJSONObject("PRE");
                PRE pre = new PRE(
                        "Pa",
                        preObject.getDouble("av"),
                        preObject.getInt("ct"),
                        preObject.getDouble("mx"),
                        preObject.getDouble("mn")
                );

                // AT
                JSONObject atObject = sol.getJSONObject("AT");
                AT at = new AT(
                        "° C",
                        atObject.getDouble("av"),
                        atObject.getInt("ct"),
                        atObject.getDouble("mx"),
                        atObject.getDouble("mn")
                );

                // HWS
                JSONObject hwsObject = sol.getJSONObject("PRE");
                HWS hws = new HWS(
                        "m/s",
                        hwsObject.getDouble("av"),
                        hwsObject.getInt("ct"),
                        hwsObject.getDouble("mx"),
                        hwsObject.getDouble("mn")
                );

                sols.add(new Sol(name, at, hws, pre, wd, season, firstUTC, lastUTC));
            }

            return new MarsWeatherData(sols.stream().collect(Collectors.toMap(Sol::getName, Function.identity())));
        } catch (Exception e) {
            errorLoggingClient.handleError("NASAClient", "formatMarsWeatherData", "Cannot format Mars weather data.", e);
            return null;
        }
    }
}
