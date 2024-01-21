package org.nasabot.nasabot.utils;

import java.util.ResourceBundle;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HealthCheckClient {

    private String url;
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();

    public HealthCheckClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            url = resourceBundle.getString("healthCheckUrl");
        } catch (Exception e) {
            ErrorLogging.handleError("HealthCheckClient", "HealthCheckClient", "Cannot contact HealthCheck API.", e);
            System.exit(1);
        }
    }

    public boolean updateHealthCheck(long timestamp) {
        JSONObject payload = new JSONObject();
        payload.put("last_updated", timestamp);
        payload.put("resource", "NASABot");

        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), payload.toString());
            Request request = new Request.Builder().url(url).put(requestBody).build();
            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            ErrorLogging.handleError("HealthCheckClient", "updateHealthCheck", "Cannot contact HealthCheck API.", e);
            return false;
        }
    }
}
