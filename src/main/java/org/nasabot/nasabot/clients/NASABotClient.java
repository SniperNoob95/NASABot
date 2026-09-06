package org.nasabot.nasabot.clients;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public abstract class NASABotClient {
    protected static final OkHttpClient httpClient = new OkHttpClient();
    protected static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    private ErrorLoggingClient errorLoggingClient;

    protected ErrorLoggingClient getErrorLoggingClient() {
        if (errorLoggingClient == null) {
            errorLoggingClient = ErrorLoggingClient.getInstance();
        }
        return errorLoggingClient;
    }
}
