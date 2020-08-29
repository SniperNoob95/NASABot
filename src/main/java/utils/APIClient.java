package utils;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

public class APIClient {

    private final String baseUrl = "https://api.nasa.gov";
    private final String imageUrl = "https://images-api.nasa.gov";
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
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

    public Response getPictureOfTheDay() {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseUrl + "/planetary/apod")).newBuilder();
            builder.addQueryParameter("api_key", apiKey);
            Request request = new Request.Builder().url(builder.build().toString()).build();
            return httpClient.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Response getPictureOfTheDay(String date) {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseUrl + "/planetary/apod")).newBuilder();
            builder.addQueryParameter("api_key", apiKey).addQueryParameter("date", date);
            Request request = new Request.Builder().url(builder.build().toString()).build();
            return httpClient.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Response getNASAImage(String searchTerm) {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(imageUrl + "/search")).newBuilder();
            builder.addQueryParameter("media_type", "image").addQueryParameter("q", searchTerm);
            Request request = new Request.Builder().url(builder.build().toString()).build();
            return httpClient.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        APIClient apiClient = new APIClient();
        Response response = apiClient.getNASAImage("apollo 11");
        assert response.body() != null;
        JSONObject jsonObject = new JSONObject(response.body().string());
        System.out.println(jsonObject.toString(4));
        System.out.println(response.header("X-RateLimit-Remaining"));
    }
}
