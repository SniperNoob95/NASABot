package org.nasabot.nasabot.clients;

import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nasabot.nasabot.objects.NASAImage;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class NASAClient extends NASABotClient {
    private final String baseUrl = "https://api.nasa.gov";
    private final String imageUrl = "https://images-api.nasa.gov";
    private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private String apiKey;

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
            System.out.println(e.getMessage());
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
            } catch (Exception ignored) {
                ignored.printStackTrace();
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
}
