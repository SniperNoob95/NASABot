package org.firepowered.nasabot.utils;

import java.util.Objects;
import java.util.ResourceBundle;

import org.json.JSONObject;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GeoNamesClient {

    private final String url = "http://api.geonames.org/countryCodeJSON";
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
    private String username;

    public GeoNamesClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            username = resourceBundle.getString("geoNamesUsername");
        } catch (Exception e) {
            ErrorLogging.handleError("GeoNamesClient", "GeoNamesClient", "Cannot create GeoNamesClient.", e);
            System.exit(0);
        }
    }

    public String getCountryFromLatitudeLongitude(String latitude, String longitude) {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
            builder
                    .addQueryParameter("lat", latitude)
                    .addQueryParameter("lng", longitude)
                    .addQueryParameter("username", username);
            Request request = new Request.Builder().url(builder.build().toString()).build();
            Response response = httpClient.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
            String country = jsonObject.optString("countryName", "Not currently over a country. " +
                    "Generally this means the ISS is currently in international territory or over an ocean.");
            response.close();
            return country;
        } catch (Exception e) {
            ErrorLogging.handleError("GeoNamesClient", "getCountryFromLatitudeLongitude", "Unable to get ISS location.", e);
        }

        return null;
    }
}
