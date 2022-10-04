package org.nasabot.nasabot.utils;

import okhttp3.*;
import org.json.JSONObject;

import java.util.ResourceBundle;

public class HealthCheckClient {

    private String url;
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
    private String user;
    private String password;

    public HealthCheckClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            url = resourceBundle.getString("healthCheckUrl");
            user = resourceBundle.getString("healthCheckUserName");
            password = resourceBundle.getString("healthCheckPassword");
        } catch (Exception e) {
            ErrorLogging.handleError("HealthCheckClient", "HealthCheckClient", "Cannot contact HealthCheck API.", e);
            System.exit(0);
        }
    }

    public boolean updateHealthCheck(long timestamp) {
        JSONObject payload = new JSONObject();
        payload.put("last_updated", timestamp);
        payload.put("resource", "NASABot");

        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), payload.toString());
            Request request = new Request.Builder().url(url).put(requestBody).build();
            Response response = httpClient.newCall(request).execute();

            if (response.code() != 200) {
                response.close();
                return false;
            }

            response.close();
            return true;
        } catch (Exception e) {
            ErrorLogging.handleError("HealthCheckClient", "updateHealthCheck", "Cannot contact HealthCheck API.", e);
            return false;
        }
    }
}
