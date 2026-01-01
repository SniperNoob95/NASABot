package org.nasabot.nasabot.clients;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nasabot.nasabot.NASABot;
import org.nasabot.nasabot.objects.APODChannel;
import org.nasabot.nasabot.objects.MoonphaseChannel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static java.util.Map.entry;

public class DBClient {
    private String url;
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();

    private DBClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            url = resourceBundle.getString("DBUrl");
            healthCheck();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading DBClient config.");
            System.exit(0);
        }
    }

    private static class DBClientSingleton {
        private static final DBClient INSTANCE = new DBClient();
    }

    public static DBClient getInstance() {
        return DBClient.DBClientSingleton.INSTANCE;
    }

    public void healthCheck() throws IOException {
        Request request = new Request.Builder().url(url + "/health").method("GET", null).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                System.out.println("Healthcheck failed.");
                System.exit(0);
            }
        }
    }

    public void insertErrorLog(String className, String method, String log, String exception) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("date", System.currentTimeMillis() / 1000);
            payload.put("class", className);
            payload.put("method", method);
            payload.put("log", log);
            payload.put("exception", exception);
        } catch (Exception e) {
            if (NASABot.loggingEnabled) e.printStackTrace();
        }

        issuePostRequest("/errors", payload);
    }

    public void insertErrorLog(String className, String method, String log) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("date", System.currentTimeMillis() / 1000);
            payload.put("class", className);
            payload.put("method", method);
            payload.put("log", log);
        } catch (Exception e) {
            if (NASABot.loggingEnabled) e.printStackTrace();
        }

        issuePostRequest("/errors", payload);
    }

    private JSONArray issueGetRequest(String path, Map<String, String> queryParameters) {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url + path)).newBuilder();
        if (queryParameters != null) {
            for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder().url(builder.build()).get().build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                return null;
            }

            JSONArray jsonArray = new JSONArray(Objects.requireNonNull(response.body()).string());

            if (jsonArray.isEmpty()) {
                return null;
            } else {
                return jsonArray;
            }
        } catch (Exception e) {
            if (NASABot.loggingEnabled) e.printStackTrace();
            return null;
        }
    }

    public boolean issuePostRequest(String path, JSONObject payload) {
        RequestBody requestBody = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder().url(url + path).post(requestBody).build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.code() == 201;
        } catch (Exception e) {
            if (NASABot.loggingEnabled) e.printStackTrace();
            return false;
        }
    }

    public boolean issuePutRequest(String path, JSONObject payload) {
        RequestBody requestBody = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder().url(url + path).put(requestBody).build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.code() == 201;
        } catch (Exception e) {
            if (NASABot.loggingEnabled) e.printStackTrace();
            return false;
        }
    }

    public boolean issueDeleteRequest(String path, Map<String, String> queryParameters) {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url + path)).newBuilder();
        if (queryParameters != null) {
            for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder().url(builder.build()).delete().build();
        try (Response response = httpClient.newCall(request).execute()) {
            return true;
        } catch (Exception e) {
            if (NASABot.loggingEnabled) e.printStackTrace();
            return false;
        }
    }

    /**
     * Inserts a command issued by a user.
     *
     * @param slashCommandEvent Event in which the command occurred.
     * @param command           The command issued.
     */
    public boolean insertCommand(SlashCommandInteractionEvent slashCommandEvent, String command) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("date", System.currentTimeMillis() / 1000);
            payload.put("username", slashCommandEvent.getUser().getName());
            payload.put("userid", slashCommandEvent.getUser().getId());
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
            if (NASABot.loggingEnabled) e.printStackTrace();
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
            if (NASABot.loggingEnabled) e.printStackTrace();
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
            if (NASABot.loggingEnabled) e.printStackTrace();
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
            if (NASABot.loggingEnabled) e.printStackTrace();
            return -1;
        }
    }

    /**
     * Gets APODChannels for the given timeOption.
     *
     * @param timeOption time option to query against.
     * @return APODChannels that match the query.
     */
    public List<APODChannel> getPostChannelsForPostTimeOption(int timeOption) {
        List<APODChannel> apodChannels = new ArrayList<>();
        JSONArray postChannels = issueGetRequest("/postChannels", new HashMap<>(Map.ofEntries(entry("timeOption", String.valueOf(timeOption)))));
        if (postChannels != null) {
            for (int i = 0; i < postChannels.length(); i++) {
                apodChannels.add(new APODChannel(postChannels.getJSONObject(i).getString("server_id"), postChannels.getJSONObject(i).getString("channel_id")));
            }
        }
        return apodChannels;
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
            if (NASABot.loggingEnabled) e.printStackTrace();
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

    /**
     * Creates a new moonphaseChannel for the given serverId and channelId.
     *
     * @param serverId  ID of the server.
     * @param channelId ID of the channel.
     * @return True if successful, else false.
     */
    public boolean createMoonphaseChannel(String serverId, String channelId) {
        JSONObject payload = new JSONObject();
        payload.put("dateAdded", System.currentTimeMillis() / 1000);
        payload.put("serverId", serverId);
        payload.put("channelId", channelId);

        return issuePostRequest("/moonphaseChannels", payload);
    }

    /**
     * Deletes the moonphaseChannels for the given serverId
     *
     * @param serverId ID of the server.
     * @return True if successful, else false.
     */
    public boolean deleteMoonphaseChannel(String serverId) {
        try {
            return issueDeleteRequest("/moonphaseChannels", new HashMap<>(Map.ofEntries(entry("serverId", serverId))));
        } catch (Exception e) {
            if (NASABot.loggingEnabled) e.printStackTrace();
            System.out.println(String.format("Failed to delete Moonphase Channel for server: %s", serverId));
            return false;
        }
    }

    public JSONArray getAllMoonphaseChannels() {
        return issueGetRequest("/moonphaseChannels", null);
    }

    /**
     * Gets moonphaseChannels for the given serverId.
     *
     * @param serverId ID of the server.
     * @return ID of moonphaseChannel, or 0 if none.
     */
    public String getMoonphaseChannelForServer(String serverId) {
        try {
            return Objects.requireNonNull(issueGetRequest("/moonphaseChannels", new HashMap<>(Map.ofEntries(entry("serverId", serverId))))).getJSONObject(0).getString("channel_id");
        } catch (Exception e) {
            if (NASABot.loggingEnabled) e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets moonphaseChannels for the given serverId.
     *
     * @param serverId ID of the server.
     * @return ID of moonphaseChannel, or 0 if none.
     */
    public int getMoonphaseChannelId(String serverId) {
        try {
            return Objects.requireNonNull(issueGetRequest("/moonphaseChannels", new HashMap<>(Map.ofEntries(entry("serverId", serverId))))).getJSONObject(0).getInt("id");
        } catch (Exception e) {
            if (NASABot.loggingEnabled) e.printStackTrace();
            return -1;
        }
    }

    /**
     * Gets moonphaseChannels for the given timeOption.
     *
     * @param timeOption time option to query against.
     * @return moonphaseChannels that match the query.
     */
    public List<MoonphaseChannel> getMoonphaseChannelsForMoonphaseTimeOption(int timeOption) {
        List<MoonphaseChannel> apodChannels = new ArrayList<>();
        JSONArray postChannels = issueGetRequest("/moonphaseChannels", new HashMap<>(Map.ofEntries(entry("timeOption", String.valueOf(timeOption)))));
        if (postChannels != null) {
            for (int i = 0; i < postChannels.length(); i++) {
                apodChannels.add(new MoonphaseChannel(postChannels.getJSONObject(i).getString("server_id"), postChannels.getJSONObject(i).getString("channel_id")));
            }
        }
        return apodChannels;
    }

    /**
     * Creates a new moonphaseChannelConfiguration for the given moonphaseChannelId.
     *
     * @param moonphaseChannelId moonphaseChannelId to create the configuration for.
     * @return True if successful, else false.
     */
    public boolean createMoonphaseChannelConfiguration(int moonphaseChannelId) {
        JSONObject payload = new JSONObject();
        payload.put("moonphaseChannelId", moonphaseChannelId);

        return issuePostRequest("/moonphaseChannelConfigurations", payload);
    }

    public int getMoonphaseTimeForServer(int moonphaseChannelId) {
        try {
            return Objects.requireNonNull(issueGetRequest("/moonphaseChannelConfigurations", new HashMap<>(Map.ofEntries(entry("moonphaseChannelId", String.valueOf(moonphaseChannelId)))))).getJSONObject(0).getInt("time_option");
        } catch (Exception e) {
            if (NASABot.loggingEnabled) e.printStackTrace();
            System.out.println(String.format("Failed to get Moonphase Configuration for MoonPhase Channel id: %s.", moonphaseChannelId));
            return -1;
        }
    }

    public boolean updateMoonphaseChannelConfiguration(int timeOption, int moonphaseChannelId) {
        JSONObject payload = new JSONObject();
        payload.put("timeOption", timeOption);
        payload.put("moonphaseChannelId", moonphaseChannelId);

        return issuePutRequest("/moonphaseChannelConfigurations", payload);
    }
}