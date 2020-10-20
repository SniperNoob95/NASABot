package utils;

import com.jagrosh.jdautilities.command.CommandEvent;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ResourceBundle;

public class DBClient {

    private String url;
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();

    public DBClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            url = resourceBundle.getString("DBUrl");
            Response healthCheck = healthCheck();

            if (healthCheck.code() != 200) {
                System.out.println("Healthcheck failed.");
                System.exit(0);
            }

            healthCheck.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot contact API.");
            System.exit(0);
        }
    }

    public Response healthCheck() throws IOException {
        Request request = new Request.Builder().url(url + "/health").method("GET", null).build();
        return httpClient.newCall(request).execute();
    }

    public void insertCommand(CommandEvent commandEvent, String command) {
        JSONObject payload = new JSONObject();
        payload.put("date", System.currentTimeMillis() / 1000);
        payload.put("username", commandEvent.getMember().getUser().getName());
        payload.put("userid", commandEvent.getMember().getIdLong());
        payload.put("serverid", commandEvent.getGuild().getIdLong());
        payload.put("servername", commandEvent.getGuild().getName());
        payload.put("command", command);
        payload.put("args", commandEvent.getArgs());

        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), payload.toString());
            Request request = new Request.Builder().url(url + "/commands").post(requestBody).build();
            Response response = httpClient.newCall(request).execute();

            if (response.code() != 201) {
                System.out.println(String.format("Failed to insert command: %s", payload));
            }

            response.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format("Failed to insert command: %s", payload));
        }
    }
}