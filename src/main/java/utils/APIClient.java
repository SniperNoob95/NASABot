package utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

public class APIClient {

    private final String baseUrl = "https://api.nasa.gov";
    private final String imageUrl = "https://images-api.nasa.gov";
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
    SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
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
            return formatPictureOfTheDay(Objects.requireNonNull(httpClient.newCall(request).execute().body()).string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private MessageEmbed formatPictureOfTheDay(String POTDResponse) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            JSONObject jsonObject = new JSONObject(POTDResponse);
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
            return formatNASAImage(Objects.requireNonNull(httpClient.newCall(request).execute().body()).string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private MessageEmbed formatNASAImage(String imageResponse) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        try {
            JSONArray responseArray = new JSONObject(imageResponse).getJSONObject("collection").getJSONArray("items");
            if (responseArray.length() == 0) {
                return new EmbedBuilder().setTitle("NASA Image").addField("ERROR", "No images found with that search term.", false).setColor(Color.RED).build();
            } else {
                JSONObject selection = responseArray.getJSONObject(findSuitableImageSelection(responseArray));
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(selection.getJSONArray("data").getJSONObject(0).getString("nasa_id"));
                embedBuilder.setDescription(
                        selection.getJSONArray("data").getJSONObject(0).getString("description").length() > 1850 ?
                                selection.getJSONArray("data").getJSONObject(0).getString("description").substring(0, 1500) + "..." :
                                selection.getJSONArray("data").getJSONObject(0).getString("description"));
                embedBuilder.addField("Date", outputDateFormat.format(inputDateFormat.parse(selection.getJSONArray("data").getJSONObject(0).getString("date_created"))), false);
                embedBuilder.setColor(Color.GREEN);
                embedBuilder.setImage(selection.getJSONArray("links").getJSONObject(0).getString("href"));
                return embedBuilder.build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new EmbedBuilder().setTitle("NASA Image").addField("ERROR", "Unable to obtain an image.", false).setColor(Color.RED).build();
        }
    }

    private int findSuitableImageSelection(JSONArray imageArray) {
        for (int i = 0; i < imageArray.length(); i++) {
            int index = new Random().nextInt(imageArray.length());
            JSONObject selection = imageArray.getJSONObject(index);
            try {
                if (!selection.getJSONArray("data").getJSONObject(0).has("nasa_id")) {
                    continue;
                }
                if (!selection.getJSONArray("data").getJSONObject(0).has("date_created")) {
                    continue;
                }
                if (!selection.getJSONArray("links").getJSONObject(0).has("href") || !selection.getJSONArray("links").getJSONObject(0).getString("render").equals("image")) {
                    continue;
                }

                return index;
            } catch (Exception ignored) {
            }

        }

        return -1;
    }
}
