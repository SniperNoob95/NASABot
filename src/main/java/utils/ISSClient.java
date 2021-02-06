package utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.awt.Color;;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class ISSClient {

    private final String url = "http://api.open-notify.org/iss-now.json";
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
    private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm:ss z");

    public MessageEmbed getISSLocation() {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
            Request request = new Request.Builder().url(builder.build().toString()).build();
            Response response = httpClient.newCall(request).execute();
            MessageEmbed embed = formatISSLocation(Objects.requireNonNull(response.body()).string());
            response.close();
            return embed;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public MessageEmbed formatISSLocation(String issLocationResponse) {
        outputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            JSONObject jsonObject = new JSONObject(issLocationResponse);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder
                    .setTitle("ISS Current Location")
                    .setDescription(String.format("%s", outputDateFormat.format(new Date(jsonObject.getLong("timestamp") * 1000))))
                    .setColor(Color.BLUE)
                    .addField("Latitude", jsonObject.getJSONObject("iss_position").getString("latitude"), false)
                    .addField("Longitude", jsonObject.getJSONObject("iss_position").getString("longitude"), true)
                    .addField("\u200B", "\u200B", true)
                    .addField("\u200B", "\u200B", true)
                    .addField("Google Maps Location", getGoogleMapsLink(Double.parseDouble(jsonObject.getJSONObject("iss_position").getString("latitude")), Double.parseDouble(jsonObject.getJSONObject("iss_position").getString("longitude"))), false)
                    .setThumbnail("https://www.nationalgeographic.com/content/dam/science/2020/10/28/ISS/international_space_station_in_2018.adapt.1900.1.jpg");
            return embedBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return new EmbedBuilder().setTitle("ISS Current Location").addField("ERROR", "Unable to obtain Picture of the Day.", false).setColor(Color.RED).build();
        }
    }

    public String getGoogleMapsLink(Double latitude, Double longitude) {
        return String.format("https://maps.google.com/?q=%s,%s&ll=%s,%s&z=3", latitude, longitude, latitude, longitude);
    }
}
