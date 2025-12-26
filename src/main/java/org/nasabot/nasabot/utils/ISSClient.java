package org.nasabot.nasabot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.nasabot.nasabot.NASABot;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class ISSClient {

    private static final String url = "http://api.open-notify.org/iss-now.json";
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
    private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm:ss z");

    public MessageEmbed getISSLocation() {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
            Request request = new Request.Builder().url(builder.build().toString()).build();
            try (Response response = httpClient.newCall(request).execute()) {
                return formatISSLocation(Objects.requireNonNull(response.body()).string());
            }
        } catch (Exception e) {
            ErrorLogging.handleError("ISSClient", "getISSLocation", "Cannot get ISS location.", e);
        }

        return null;
    }

    public MessageEmbed formatISSLocation(String issLocationResponse) {
        outputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            JSONObject jsonObject = new JSONObject(issLocationResponse);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("ISS Current Location")
                    .setDescription(String.format("%s",
                            outputDateFormat.format(new Date(jsonObject.getLong("timestamp") * 1000))))
                    .setColor(new Color(192, 32, 232))
                    .addField("Latitude", jsonObject.getJSONObject("iss_position").getString("latitude"), true)
                    .addField("Longitude", jsonObject.getJSONObject("iss_position").getString("longitude"), true)
                    .addField("Google Maps Location",
                            getGoogleMapsLink(
                                    Double.parseDouble(jsonObject.getJSONObject("iss_position").getString("latitude")),
                                    Double.parseDouble(
                                            jsonObject.getJSONObject("iss_position").getString("longitude"))),
                            false)
                    .addField("Currently Over Country",
                            NASABot.geoNamesClient.getCountryFromLatitudeLongitude(
                                    jsonObject.getJSONObject("iss_position").getString("latitude"),
                                    jsonObject.getJSONObject("iss_position").getString("longitude")),
                            false)
                    .setThumbnail("https://i.imgur.com/xm3XSgc.jpg");
            return embedBuilder.build();
        } catch (Exception e) {
            ErrorLogging.handleError("ISSClient", "updateHealthCheck", "Cannot format ISS location.", e);
            return new EmbedBuilder().setTitle("ISS Current Location")
                    .addField("ERROR", "Unable to obtain ISS location.", false).setColor(Color.RED).build();
        }
    }

    public String getGoogleMapsLink(Double latitude, Double longitude) {
        return String.format("https://maps.google.com/?q=%s,%s&ll=%s,%s&z=3", latitude, longitude, latitude, longitude);
    }
}
