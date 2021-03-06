package utils;

import com.jagrosh.jdautilities.command.CommandEvent;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
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

    /**
     * Inserts a command issued by a user.
     *
     * @param commandEvent Event in which the command occurred.
     * @param command The command issued.
     */
    public void insertCommand(CommandEvent commandEvent, String command) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("date", System.currentTimeMillis() / 1000);
            payload.put("username", commandEvent.getMember().getUser().getName());
            payload.put("userid", commandEvent.getMember().getId());
            payload.put("serverid", commandEvent.getGuild().getId());
            payload.put("servername", commandEvent.getGuild().getName());
            payload.put("command", command);
            payload.put("args", commandEvent.getArgs() == null ? "" : commandEvent.getArgs());
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    /**
     * Creates a new postChannel for the given serverId and channelId.
     *
     * @param serverId ID of the server.
     * @param channelId ID of the channel.
     * @return True if successful, else false.
     */
    public boolean addPostChannel(String serverId, String channelId) {
        JSONObject payload = new JSONObject();
        payload.put("dateAdded", System.currentTimeMillis() / 1000);
        payload.put("serverId", serverId);
        payload.put("channelId", channelId);

        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), payload.toString());
            Request request = new Request.Builder().url(url + "/postChannels").post(requestBody).build();
            Response response = httpClient.newCall(request).execute();

            if (response.code() != 201) {
                System.out.println(String.format("Failed to insert Post Channel: %s", payload));
                response.close();
                return false;
            }

            response.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format("Failed to add Post Channel: %s", payload));
            return false;
        }
    }

    /**
     * Deletes the postChannels for the given serverId
     *
     * @param serverId ID of the server.
     * @return True if successful, else false.
     */
    public boolean deletePostChannel(String serverId) {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url + "/postChannels")).newBuilder();
            builder.addQueryParameter("serverId", serverId);
            Request request = new Request.Builder().url(builder.build()).delete().build();
            Response response = httpClient.newCall(request).execute();

            if (response.code() != 200) {
                System.out.println(String.format("Failed to delete Post Channel: %s", serverId));
                response.close();
                return false;
            }

            response.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format("Failed to delete Post Channel for server: %s", serverId));
            return false;
        }
    }

    /**
     * Gets postChannels for the given serverId.
     *
     * @param serverId ID of the server.
     * @return ID of postChannel, or 0 if none.
     */
    public String getPostChannel(String serverId) {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url + "/postChannels")).newBuilder();
            builder.addQueryParameter("serverId", serverId);
            Request request = new Request.Builder().url(builder.build()).get().build();
            Response response = httpClient.newCall(request).execute();

            if (response.code() != 200) {
                System.out.println(String.format("Failed to get Post Channel: %s", serverId));
                response.close();
                return null;
            }

            JSONArray jsonArray = new JSONArray(Objects.requireNonNull(response.body()).string());
            response.close();

            if (jsonArray.length() == 0) {
                return null;
            } else {
                return jsonArray.getJSONObject(0).getString("channel_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format("Failed to get Post Channel for server: %s", serverId));
            return null;
        }
    }

    public JSONArray getAllPostChannels() {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url + "/postChannels")).newBuilder();
            Request request = new Request.Builder().url(builder.build()).get().build();
            Response response = httpClient.newCall(request).execute();

            if (response.code() != 200) {
                System.out.println("Failed to get all Post Channels");
                response.close();
                return null;
            }

            JSONArray jsonArray = new JSONArray(Objects.requireNonNull(response.body()).string());
            response.close();

            if (jsonArray.length() == 0) {
                return null;
            } else {
                return jsonArray;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to get all Post Channels.");
            return null;
        }
    }
}