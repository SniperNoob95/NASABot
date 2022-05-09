package utils;

import bot.NASABot;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

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
            e.printStackTrace();
            System.out.println("Cannot load top.gg client.");
            System.exit(0);
        }

        TimerTask timerTask = new TopGGTimerTask();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 5*1000);
    }

    private static void setStats(OkHttpClient httpClient, int serverCount) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), new JSONObject().put("server_count", serverCount).toString());
            Request request = new Request.Builder().url(url + "/stats").method("POST", requestBody).addHeader("Authorization", "Bearer " + token).build();
            httpClient.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to update status.");
        }
    }

    private class TopGGTimerTask extends TimerTask {

        @Override
        public void run() {
            setStats(httpClient, NASABot.jda.getGuilds().size());
        }
    }
}
