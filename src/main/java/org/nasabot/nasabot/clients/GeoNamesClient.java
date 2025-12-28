package org.nasabot.nasabot.clients;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.Objects;
import java.util.ResourceBundle;

public class GeoNamesClient extends NASABotClient {
    private final String url = "http://api.geonames.org/countryCodeJSON";
    private String username;

    private GeoNamesClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            username = resourceBundle.getString("geoNamesUsername");
        } catch (Exception e) {
            errorLoggingClient.handleError("GeoNamesClient", "GeoNamesClient", "Cannot create GeoNamesClient.", e);
            System.exit(1);
        }
    }

    private static class GeoNamesClientSingleton {
        private static final GeoNamesClient INSTANCE = new GeoNamesClient();
    }

    public static GeoNamesClient getInstance() {
        return GeoNamesClient.GeoNamesClientSingleton.INSTANCE;
    }

    public String getCountryFromLatitudeLongitude(String latitude, String longitude) {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
            builder
                    .addQueryParameter("lat", latitude)
                    .addQueryParameter("lng", longitude)
                    .addQueryParameter("username", username);
            Request request = new Request.Builder().url(builder.build().toString()).build();
            try (Response response = httpClient.newCall(request).execute()) {
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                return jsonObject.optString("countryName", "Not currently over a country. " +
                        "Generally this means the ISS is currently in international territory or over an ocean.");
            }
        } catch (Exception e) {
            errorLoggingClient.handleError("GeoNamesClient", "getCountryFromLatitudeLongitude", "Unable to get ISS location.", e);
        }

        return null;
    }
}
