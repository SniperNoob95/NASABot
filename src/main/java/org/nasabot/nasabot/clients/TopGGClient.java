package org.nasabot.nasabot.clients;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.nasabot.nasabot.NASABot;

import java.util.ResourceBundle;

public class TopGGClient extends NASABotClient {
    private final static String url = "https://top.gg/api/bots/748775876077813881";
    private String token;

    private TopGGClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            token = resourceBundle.getString("TopGGKey");
        } catch (Exception e) {
            errorLoggingClient.handleError("TopGGClient", "TopGGClient", "Cannot contact TopGG API.", e);
            System.exit(1);
        }
    }

    private static class TopGGClientSingleton {
        private static final TopGGClient INSTANCE = new TopGGClient();
    }

    public static TopGGClient getInstance() {
        return TopGGClient.TopGGClientSingleton.INSTANCE;
    }

    public void updateTopGGStats() {
        int serverCount = NASABot.shardManager.getGuilds().size();
        try {
            RequestBody requestBody = RequestBody.create(new JSONObject().put("server_count", serverCount).toString(),
                    MediaType.parse("application/json"));
            Request request = new Request.Builder().url(url + "/stats").method("POST", requestBody)
                    .addHeader("Authorization", "Bearer " + token).build();
            try (Response response = httpClient.newCall(request).execute()) {
            }
        } catch (Exception e) {
            errorLoggingClient.handleError("TopGGClient", "setStats", "Cannot set stats.", e);
        }
    }
}
