package org.nasabot.nasabot.utils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.nasabot.nasabot.NASABot;

import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class TopGGClient {

    private final static String url = "https://top.gg/api/bots/748775876077813881";
    private static String token;
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();

    public TopGGClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            token = resourceBundle.getString("TopGGKey");
        } catch (Exception e) {
            ErrorLogging.handleError("TopGGClient", "TopGGClient", "Cannot contact TopGG API.", e);
            System.exit(1);
        }

        TimerTask timerTask = new TopGGTimerTask();
        Timer timer = new Timer(true);
        // 1 minute
        timer.scheduleAtFixedRate(timerTask, 0, 60 * 1000);
    }

    private class TopGGTimerTask extends TimerTask {

        @Override
        public void run() {
            int serverCount = NASABot.shardManager.getGuilds().size();
            try {
                RequestBody requestBody = RequestBody.create(new JSONObject().put("server_count", serverCount).toString(),
                        MediaType.parse("application/json"));
                Request request = new Request.Builder().url(url + "/stats").method("POST", requestBody)
                        .addHeader("Authorization", "Bearer " + token).build();
                try (Response response = httpClient.newCall(request).execute()) {
                }
            } catch (Exception e) {
                ErrorLogging.handleError("TopGGClient", "setStats", "Cannot set stats.", e);
            }
        }
    }
}
