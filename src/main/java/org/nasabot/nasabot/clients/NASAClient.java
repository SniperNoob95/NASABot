package org.nasabot.nasabot.clients;

import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.nasabot.nasabot.objects.NASAImage;
import org.nasabot.nasabot.objects.eonet.EONETEvent;
import org.nasabot.nasabot.objects.eonet.EONETEventsData;
import org.nasabot.nasabot.objects.epic.EPICData;
import org.nasabot.nasabot.objects.epic.EPICImage;
import org.nasabot.nasabot.objects.marsweather.AT;
import org.nasabot.nasabot.objects.marsweather.HWS;
import org.nasabot.nasabot.objects.marsweather.MarsWeatherData;
import org.nasabot.nasabot.objects.marsweather.PRE;
import org.nasabot.nasabot.objects.marsweather.Sol;
import org.nasabot.nasabot.objects.marsweather.WD;
import org.nasabot.nasabot.objects.marsweather.WindDirection;
import org.nasabot.nasabot.objects.neo.NEO;
import org.nasabot.nasabot.objects.neo.NEOData;
import org.nasabot.nasabot.objects.neo.OrbitalData;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NASAClient extends NASABotClient {
    private final String baseUrl = "https://api.nasa.gov";
    private final String apodBaseUrl = "https://science.nasa.gov/wp-json/wp/v2/apod-basic";
    private final String imageUrl = "https://images-api.nasa.gov";
    private final String epicBaseUrl = "https://epic.gsfc.nasa.gov";
    private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String apiKey;
    private Pair<Long, MarsWeatherData> cachedMarsWeatherData;
    private Pair<Long, EONETEventsData> cachedEONETEvents;
    private Pair<Long, EmbedBuilder> cachedAPOD;
    private Pair<Long, EPICData> cachedEPICData;
    private final Map<Integer, Pair<Long, NEOData>> cachedNEOData = new ConcurrentHashMap<>();

    private NASAClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            apiKey = resourceBundle.getString("NASAKey");
        } catch (Exception e) {
            getErrorLoggingClient().handleError("NASAClient", "NASAClient", "Cannot contact NASA API.", e);
            System.exit(1);
        }
    }

    private static class NASAClientSingleton {
        private static final NASAClient INSTANCE = new NASAClient();
    }

    public static NASAClient getInstance() {
        return NASAClient.NASAClientSingleton.INSTANCE;
    }

    public EmbedBuilder getPictureOfTheDay(String date) {
        // New date format is YYMMDD instead of YYYY-MM-DD
        String formattedDate = date.replace("-", "").replaceFirst("20", "");
        Request request = new Request.Builder().url(apodBaseUrl + "/" + formattedDate).build();
        try (Response response = httpClient.newCall(request).execute()) {
            String responseString = Objects.requireNonNull(response.body()).string();
            JSONObject jsonObject = new JSONObject(responseString);
            return formatPictureOfTheDay(jsonObject);
        } catch (Exception e) {
            getErrorLoggingClient().handleError("NASAClient", "getPictureOfTheDay", "Cannot get picture of the day.", e);
        }

        return null;
    }

    public EmbedBuilder getLatestPictureOfTheDay(boolean forceRefresh) {
        if (forceRefresh) {
            return fetchAndCacheAPOD();
        }
        // If no cache or if cache is older than 1 hour
        if (cachedAPOD == null || cachedAPOD.getFirst() < System.currentTimeMillis() / 1000 - 3600) {
            return fetchAndCacheAPOD();
        } else {
            System.out.println(true);
            return cachedAPOD.getSecond();
        }
    }

    private EmbedBuilder fetchAndCacheAPOD() {
        Request request = new Request.Builder().url(apodBaseUrl).build();
        try (Response response = httpClient.newCall(request).execute()) {
            String responseString = Objects.requireNonNull(response.body()).string();
            JSONArray jsonArray = new JSONArray(responseString);
            if (!jsonArray.isEmpty()) {
                EmbedBuilder embedBuilder = formatPictureOfTheDay(jsonArray.getJSONObject(0));
                cachedAPOD = new Pair<>(System.currentTimeMillis() / 1000, embedBuilder);
                return embedBuilder;
            }
        } catch (Exception e) {
            getErrorLoggingClient().handleError("NASAClient", "fetchAndCacheAPOD", "Cannot get picture of the day.", e);
        }
        // If we get an error or no results, just return the cached APOD if present.
        return cachedAPOD != null ? cachedAPOD.getSecond() : null;
    }

    private EmbedBuilder formatPictureOfTheDay(JSONObject jsonObject) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            String explanation = Jsoup.parse(jsonObject.getString("explanation")).text();
            String titleUrl = jsonObject.optString("url", String.format("https://apod.nasa.gov/apod/ap%s.html", jsonObject.getString("date").replace("-", "").substring(2)));
            embedBuilder
                    .setTitle(jsonObject.getString("title"), titleUrl)
                    .setDescription(outputDateFormat.format(inputDateFormat.parse(jsonObject.getString("date"))))
                    .setColor(new Color(192, 32, 232))
                    .addField("Description", explanation.length() > 1024 ? explanation.substring(0, 1020) + "..." : explanation, false);
            if (jsonObject.has("hdurl")) {
                embedBuilder.addField("HD Image Link", jsonObject.getString("hdurl"), false);
            }
            if (jsonObject.has("media_type") && jsonObject.getString("media_type").equals("video")) {
                embedBuilder.addField("Video Link", jsonObject.optString("url", ""), false);
            }
            return embedBuilder;
        } catch (Exception e) {
            getErrorLoggingClient().handleError("NASAClient", "formatPictureOfTheDay", "Cannot format picture of the day.", e);
            return cachedAPOD != null
                    ? cachedAPOD.getSecond()
                    : new EmbedBuilder().setTitle("Picture of the Day").addField("ERROR", "Unable to obtain Picture of the Day.", false).setColor(Color.RED);
        }
    }

    public EONETEventsData getEONETEventsData() {
        // Cache for 10 minutes
        if (cachedEONETEvents != null && cachedEONETEvents.getFirst() >= (System.currentTimeMillis() / 1000 - 600)) {
            return cachedEONETEvents.getSecond();
        }

        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse("https://eonet.gsfc.nasa.gov/api/v3/events")).newBuilder();
        builder.addQueryParameter("limit", "10");
        builder.addQueryParameter("status", "open");
        Request request = new Request.Builder().url(builder.build().toString()).build();
        try (Response response = httpClient.newCall(request).execute()) {
            String responseString = Objects.requireNonNull(response.body()).string();
            EONETEventsData data = parseEONETEvents(responseString);
            if (data != null) {
                cachedEONETEvents = new Pair<>(System.currentTimeMillis() / 1000, data);
            }
            return data;
        } catch (Exception e) {
            getErrorLoggingClient().handleError("NASAClient", "getEONETEventsData", "Cannot get recent EONET events.", e);
            return cachedEONETEvents != null ? cachedEONETEvents.getSecond() : null;
        }
    }

    private EONETEventsData parseEONETEvents(String responseString) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            JSONArray events = jsonObject.optJSONArray("events");
            Map<String, EONETEvent> map = new HashMap<>();
            if (events != null) {
                for (int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    String id = event.getString("id");
                    String title = event.getString("title");

                    // Categories
                    List<String> categories = new ArrayList<>();
                    JSONArray categoriesArr = event.optJSONArray("categories");
                    if (categoriesArr != null && !categoriesArr.isEmpty()) {
                        for (int c = 0; c < categoriesArr.length(); c++) {
                            JSONObject cat = categoriesArr.getJSONObject(c);
                            categories.add(cat.optString("title", "Unknown"));
                        }
                    }

                    // Most recent geometry date or closed
                    String dateStr;
                    JSONArray geometry = event.optJSONArray("geometry");
                    if (geometry != null && !geometry.isEmpty()) {
                        JSONObject geomLatest = geometry.getJSONObject(geometry.length() - 1);
                        dateStr = geomLatest.optString("date", null);
                    } else {
                        dateStr = event.optString("closed", null);
                    }

                    EONETEvent ev = new EONETEvent(id, title, dateStr, categories);
                    map.put(id, ev);
                }
            }

            return new EONETEventsData(map);
        } catch (Exception e) {
            getErrorLoggingClient().handleError("NASAClient", "parseEONETEvents", "Cannot parse EONET events.", e);
            return null;
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
            getErrorLoggingClient().handleError("NASAClient", "getNASAImage", "Cannot get NASA images.", e);
        }

        return null;
    }

    private List<NASAImage> filterSuitableImages(JSONArray imageResponseArray) {
        List<NASAImage> images = new ArrayList<>();
        List<String> requiredDataFields = List.of("title", "nasa_id", "description", "date_created", "location");

        for (int i = 0; i < imageResponseArray.length(); i++) {
            JSONObject selection = imageResponseArray.getJSONObject(i);
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
                getErrorLoggingClient().handleError("NASAClient", "filterSuitableImages", "Uncaught exception when filtering images.", e);
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

    public EPICData getEPICData(String collection, String date) {
        String url;
        if (date != null && !date.isEmpty()) {
            url = epicBaseUrl + "/api/" + collection + "/date/" + date;
        } else {
            url = epicBaseUrl + "/api/" + collection;
        }

        Request request = new Request.Builder().url(url).build();
        try (Response response = httpClient.newCall(request).execute()) {
            String responseString = Objects.requireNonNull(response.body()).string();
            return parseEPICData(responseString, collection, date);
        } catch (Exception e) {
            getErrorLoggingClient().handleError("NASAClient", "getEPICData", "Cannot get EPIC data.", e);
            return null;
        }
    }

    private EPICData parseEPICData(String responseString, String collection, String date) {
        try {
            JSONArray jsonArray = new JSONArray(responseString);
            Map<String, EPICImage> map = new java.util.LinkedHashMap<>();

            int limit = Math.min(jsonArray.length(), 8);
            for (int i = 0; i < limit; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String identifier = obj.getString("identifier");
                String caption = obj.optString("caption", "No caption available");
                String imageName = obj.getString("image");
                String imageDate = obj.optString("date", null);

                double centroidLat = 0;
                double centroidLon = 0;
                JSONObject centroid = obj.optJSONObject("centroid_coordinates");
                if (centroid != null) {
                    centroidLat = centroid.optDouble("lat", 0);
                    centroidLon = centroid.optDouble("lon", 0);
                }

                // Build image URL: https://epic.gsfc.nasa.gov/archive/{collection}/{year}/{month}/{day}/jpg/{imageName}.jpg
                String imageUrl = buildEPICImageUrl(collection, imageDate, imageName);

                map.put(identifier, new EPICImage(identifier, caption, imageName, imageDate,
                        centroidLat, centroidLon, imageUrl));
            }

            return new EPICData(map, collection, date);
        } catch (Exception e) {
            getErrorLoggingClient().handleError("NASAClient", "parseEPICData", "Cannot parse EPIC data.", e);
            return null;
        }
    }

    private String buildEPICImageUrl(String collection, String date, String imageName) {
        // Date format from API: "2026-09-06 00:59:48"
        String datePart = date != null ? date.split(" ")[0] : "";
        String[] parts = datePart.split("-");
        if (parts.length == 3) {
            return String.format("https://epic.gsfc.nasa.gov/archive/%s/%s/%s/%s/jpg/%s.jpg",
                    collection, parts[0], parts[1], parts[2], imageName);
        }
        return String.format("https://epic.gsfc.nasa.gov/archive/%s/jpg/%s.jpg", collection, imageName);
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
                getErrorLoggingClient().handleError("NASAClient", "getMarsWeatherData", "Cannot get Mars weather data.", e);
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
                JSONObject hwsObject = sol.getJSONObject("HWS");
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
            getErrorLoggingClient().handleError("NASAClient", "formatMarsWeatherData", "Cannot format Mars weather data.", e);
            return null;
        }
    }

    public NEOData getNEOData(int page) {
        if (page < 0) {
            page = 0;
        }
        Pair<Long, NEOData> cached = cachedNEOData.get(page);
        if (cached != null && cached.getFirst() >= (System.currentTimeMillis() / 1000 - 600)) {
            return cached.getSecond();
        }

        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseUrl + "/neo/rest/v1/neo/browse")).newBuilder();
        builder.addQueryParameter("page", String.valueOf(page));
        builder.addQueryParameter("size", "10");
        builder.addQueryParameter("api_key", apiKey);

        Request request = new Request.Builder().url(builder.build().toString()).build();
        try (Response response = httpClient.newCall(request).execute()) {
            String responseString = Objects.requireNonNull(response.body()).string();
            NEOData data = parseNEOData(responseString);
            if (data != null) {
                cachedNEOData.put(page, new Pair<>(System.currentTimeMillis() / 1000, data));
            }
            return data;
        } catch (Exception e) {
            getErrorLoggingClient().handleError("NASAClient", "getNEOData", "Cannot get NEO data for page " + page, e);
            return cached != null ? cached.getSecond() : null;
        }
    }

    private NEOData parseNEOData(String responseString) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            JSONObject pageObj = jsonObject.optJSONObject("page");
            int pageNumber = pageObj != null ? pageObj.optInt("number", 0) : 0;
            int pageSize = pageObj != null ? pageObj.optInt("size", 10) : 10;
            int totalPages = pageObj != null ? pageObj.optInt("total_pages", 1) : 1;
            long totalElements = pageObj != null ? pageObj.optLong("total_elements", 0) : 0;

            JSONArray neosArray = jsonObject.optJSONArray("near_earth_objects");
            Map<String, NEO> map = new LinkedHashMap<>();
            if (neosArray != null) {
                for (int i = 0; i < neosArray.length(); i++) {
                    JSONObject neoObj = neosArray.getJSONObject(i);
                    String id = neoObj.optString("id", String.valueOf(i));
                    String name = neoObj.optString("name", "Unknown NEO");
                    String nasaJplUrl = neoObj.optString("nasa_jpl_url", "");
                    double absoluteMagnitude = neoObj.optDouble("absolute_magnitude_h", 0.0);
                    boolean isHazardous = neoObj.optBoolean("is_potentially_hazardous_asteroid", false);

                    double minDiameter = 0.0;
                    double maxDiameter = 0.0;
                    JSONObject estDiam = neoObj.optJSONObject("estimated_diameter");
                    if (estDiam != null) {
                        JSONObject meters = estDiam.optJSONObject("meters");
                        if (meters != null) {
                            minDiameter = meters.optDouble("estimated_diameter_min", 0.0);
                            maxDiameter = meters.optDouble("estimated_diameter_max", 0.0);
                        }
                    }

                    List<String> approachDates = new ArrayList<>();
                    JSONArray closeApproachData = neoObj.optJSONArray("close_approach_data");
                    if (closeApproachData != null) {
                        for (int j = 0; j < closeApproachData.length(); j++) {
                            JSONObject approachObj = closeApproachData.getJSONObject(j);
                            String date = approachObj.optString("close_approach_date", null);
                            if (date != null && !date.isEmpty()) {
                                approachDates.add(date);
                            }
                        }
                    }

                    OrbitalData orbitalData = null;
                    JSONObject orbitalDataObj = neoObj.optJSONObject("orbital_data");
                    if (orbitalDataObj != null) {
                        JSONObject orbitClass = orbitalDataObj.optJSONObject("orbit_class");
                        String orbitClassType = orbitClass != null ? orbitClass.optString("orbit_class_type", "N/A") : "N/A";
                        String orbitClassDescription = orbitClass != null ? orbitClass.optString("orbit_class_description", "N/A") : "N/A";
                        String orbitClassRange = orbitClass != null ? orbitClass.optString("orbit_class_range", "N/A") : "N/A";

                        orbitalData = new OrbitalData(
                                orbitalDataObj.optString("orbit_id", "N/A"),
                                orbitalDataObj.optString("orbit_determination_date", "N/A"),
                                orbitalDataObj.optString("first_observation_date", "N/A"),
                                orbitalDataObj.optString("last_observation_date", "N/A"),
                                orbitalDataObj.optString("data_arc_in_days", "N/A"),
                                orbitalDataObj.optString("observations_used", "N/A"),
                                orbitalDataObj.optString("orbit_uncertainty", "N/A"),
                                orbitalDataObj.optString("minimum_orbit_intersection", "N/A"),
                                orbitalDataObj.optString("jupiter_tisserand_invariant", "N/A"),
                                orbitalDataObj.optString("epoch_osculation", "N/A"),
                                orbitalDataObj.optString("eccentricity", "N/A"),
                                orbitalDataObj.optString("semi_major_axis", "N/A"),
                                orbitalDataObj.optString("inclination", "N/A"),
                                orbitalDataObj.optString("ascending_node_longitude", "N/A"),
                                orbitalDataObj.optString("orbital_period", "N/A"),
                                orbitalDataObj.optString("perihelion_distance", "N/A"),
                                orbitalDataObj.optString("perihelion_argument", "N/A"),
                                orbitalDataObj.optString("aphelion_distance", "N/A"),
                                orbitalDataObj.optString("perihelion_time", "N/A"),
                                orbitalDataObj.optString("mean_anomaly", "N/A"),
                                orbitalDataObj.optString("mean_motion", "N/A"),
                                orbitalDataObj.optString("equinox", "N/A"),
                                orbitClassType,
                                orbitClassDescription,
                                orbitClassRange
                        );
                    }

                    NEO neo = new NEO(id, name, nasaJplUrl, absoluteMagnitude, minDiameter, maxDiameter, isHazardous, approachDates, orbitalData);
                    map.put(id, neo);
                }
            }

            return new NEOData(map, pageNumber, pageSize, totalPages, totalElements);
        } catch (Exception e) {
            getErrorLoggingClient().handleError("NASAClient", "parseNEOData", "Cannot parse NEO data.", e);
            return null;
        }
    }
}
