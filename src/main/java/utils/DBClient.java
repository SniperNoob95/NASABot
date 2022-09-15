package utils;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import static java.util.Map.entry;

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

    public boolean insertErrorLog(String className, String method, String log, String exception) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("date", System.currentTimeMillis() / 1000);
            payload.put("class", className);
            payload.put("method", method);
            payload.put("log", log);
            payload.put("exception", exception);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return issuePostRequest("/errors", payload);
    }

    private JSONArray issueGetRequest(String path, Map<String, String> queryParameters) {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url + path)).newBuilder();
            if (queryParameters != null) {
                for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
                    builder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            Request request = new Request.Builder().url(builder.build()).get().build();
            Response response = httpClient.newCall(request).execute();

            if (response.code() != 200) {
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
            return null;
        }
    }

    public boolean issuePostRequest(String path, JSONObject payload) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), payload.toString());
            Request request = new Request.Builder().url(url + path).post(requestBody).build();
            Response response = httpClient.newCall(request).execute();

            if (response.code() != 201) {
                response.close();
                return false;
            }

            response.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean issuePutRequest(String path, JSONObject payload) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), payload.toString());
            Request request = new Request.Builder().url(url + path).put(requestBody).build();
            Response response = httpClient.newCall(request).execute();

            if (response.code() != 201) {
                response.close();
                return false;
            }

            response.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean issueDeleteRequest(String path, Map<String, String> queryParameters) {
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url + path)).newBuilder();
            if (queryParameters != null) {
                for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
                    builder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            Request request = new Request.Builder().url(builder.build()).delete().build();
            Response response = httpClient.newCall(request).execute();

            response.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Inserts a command issued by a user.
     *
     * @param commandEvent Event in which the command occurred.
     * @param command      The command issued.
     */
    public boolean insertCommand(CommandEvent commandEvent, String command) {
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

        return issuePostRequest("/commands", payload);
    }

    /**
     * Inserts a command issued by a user.
     *
     * @param slashCommandEvent Event in which the command occurred.
     * @param command           The command issued.
     */
    public boolean insertCommand(SlashCommandEvent slashCommandEvent, String command) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("date", System.currentTimeMillis() / 1000);
            payload.put("username", Objects.requireNonNull(slashCommandEvent.getMember()).getUser().getName());
            payload.put("userid", slashCommandEvent.getMember().getId());
            payload.put("serverid", Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
            payload.put("servername", slashCommandEvent.getGuild().getName());
            payload.put("command", command);

            List<OptionMapping> options = slashCommandEvent.getOptions();
            StringBuilder optionString = new StringBuilder();
            for (OptionMapping optionMapping : options) {
                optionString.append(String.format(" %s", optionMapping.getAsString()));
            }
            payload.put("args", optionString.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return issuePostRequest("/commands", payload);
    }

    /**
     * Creates a new postChannel for the given serverId and channelId.
     *
     * @param serverId  ID of the server.
     * @param channelId ID of the channel.
     * @return True if successful, else false.
     */
    public boolean createPostChannel(String serverId, String channelId) {
        JSONObject payload = new JSONObject();
        payload.put("dateAdded", System.currentTimeMillis() / 1000);
        payload.put("serverId", serverId);
        payload.put("channelId", channelId);

        return issuePostRequest("/postChannels", payload);
    }

    /**
     * Deletes the postChannels for the given serverId
     *
     * @param serverId ID of the server.
     * @return True if successful, else false.
     */
    public boolean deletePostChannel(String serverId) {
        try {
            return issueDeleteRequest("/postChannels", new HashMap<>(Map.ofEntries(entry("serverId", serverId))));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format("Failed to delete Post Channel for server: %s", serverId));
            return false;
        }
    }

    public JSONArray getAllPostChannels() {
        return issueGetRequest("/postChannels", null);
    }

    /**
     * Gets postChannels for the given serverId.
     *
     * @param serverId ID of the server.
     * @return ID of postChannel, or 0 if none.
     */
    public String getPostChannelForServer(String serverId) {
        try {
            return Objects.requireNonNull(issueGetRequest("/postChannels", new HashMap<>(Map.ofEntries(entry("serverId", serverId))))).getJSONObject(0).getString("channel_id");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format("Failed to get Post Channel for server ID: %s", serverId));
            return null;
        }
    }

    /**
     * Gets postChannels for the given serverId.
     *
     * @param serverId ID of the server.
     * @return ID of postChannel, or 0 if none.
     */
    public int getPostChannelId(String serverId) {
        try {
            return Objects.requireNonNull(issueGetRequest("/postChannels", new HashMap<>(Map.ofEntries(entry("serverId", serverId))))).getJSONObject(0).getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format("Failed to get Post Channel for server ID: %s", serverId));
            return -1;
        }
    }

    /**
     * Gets postChannels for the given timeOption.
     *
     * @param timeOption time option to query against.
     * @return postChannels that match the query.
     */
    public JSONArray getPostChannelsForPostTimeOption(int timeOption) {
        return issueGetRequest("/postChannels", new HashMap<>(Map.ofEntries(entry("timeOption", String.valueOf(timeOption)))));
    }

    /**
     * Creates a new postChannelConfiguration for the given postChannelId.
     *
     * @param postChannelId postChannelId to create the configuration for.
     * @return True if successful, else false.
     */
    public boolean createPostChannelConfiguration(int postChannelId) {
        JSONObject payload = new JSONObject();
        payload.put("postChannelId", postChannelId);

        return issuePostRequest("/postChannelConfigurations", payload);
    }

    public int getPostTimeForServer(int postChannelId) {
        try {
            return Objects.requireNonNull(issueGetRequest("/postChannelConfigurations", new HashMap<>(Map.ofEntries(entry("postChannelId", String.valueOf(postChannelId)))))).getJSONObject(0).getInt("time_option");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format("Failed to get Post Configuration for Post Channel id: %s.", postChannelId));
            return -1;
        }
    }

    public boolean updatePostChannelConfiguration(int timeOption, int postChannelId) {
        JSONObject payload = new JSONObject();
        payload.put("timeOption", timeOption);
        payload.put("postChannelId", postChannelId);

        return issuePutRequest("/postChannelConfigurations", payload);
    }
}