package org.nasabot.nasabot.clients;

import okhttp3.OkHttpClient;

public abstract class NASABotClient {
    protected static final OkHttpClient httpClient = new OkHttpClient();
    protected final ErrorLoggingClient errorLoggingClient = ErrorLoggingClient.getInstance();
}
