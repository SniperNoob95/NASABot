package org.nasabot.nasabot.clients;

import okhttp3.OkHttpClient;

public abstract class NASABotClient {
    protected final ErrorLoggingClient errorLoggingClient = ErrorLoggingClient.getInstance();
    protected final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
}
