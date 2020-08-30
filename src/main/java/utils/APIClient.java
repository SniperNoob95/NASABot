package utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.ResourceBundle;

public class APIClient {

    private final String baseUrl = "https://api.nasa.gov";
    private final String imageUrl = "https://images-api.nasa.gov";
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
    private SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String apiKey;

    public APIClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            apiKey = resourceBundle.getString("NASAKey");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public MessageEmbed getPictureOfTheDay(String date) {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseUrl + "/planetary/apod")).newBuilder();
            builder.addQueryParameter("api_key", apiKey).addQueryParameter("date", date);
            Request request = new Request.Builder().url(builder.build().toString()).build();
            return formatPictureOfTheDay(httpClient.newCall(request).execute());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private MessageEmbed formatPictureOfTheDay(Response POTDResponse) {
        try {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(POTDResponse.body()).string());
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder
                    .setTitle(jsonObject.getString("title"))
                    .setDescription(String.format("%S", jsonObject.getString("explanation")))
                    .setColor(Color.GREEN)
                    .addField("Date", String.format("%S", outputDateFormat.format(inputDateFormat.parse(jsonObject.getString("date")))), false);
            if (jsonObject.getString("url").contains("youtube.com")) {
                embedBuilder.addField("Video Link", jsonObject.getString("url"), false);
            } else {
                embedBuilder.setImage(jsonObject.getString("url"));
            }
            return embedBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return new EmbedBuilder().setTitle("Picture of the Day").addField("ERROR", "Unable to obtain Picture of the Day.", false).setColor(Color.RED).build();
        }
    }

    public MessageEmbed getNASAImage(String searchTerm) {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(imageUrl + "/search")).newBuilder();
            builder.addQueryParameter("media_type", "image").addQueryParameter("q", searchTerm);
            Request request = new Request.Builder().url(builder.build().toString()).build();
            return formatNASAImage(httpClient.newCall(request).execute());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private MessageEmbed formatNASAImage(Response imageResponse) {

    }
}
