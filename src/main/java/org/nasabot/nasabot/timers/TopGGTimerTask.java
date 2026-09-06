package org.nasabot.nasabot.timers;

import org.nasabot.nasabot.clients.ErrorLoggingClient;
import org.nasabot.nasabot.clients.TopGGClient;

import java.util.TimerTask;

public class TopGGTimerTask extends TimerTask {
    private final TopGGClient topGGClient = TopGGClient.getInstance();
    private final ErrorLoggingClient errorLoggingClient = ErrorLoggingClient.getInstance();

    @Override
    public void run() {
        try {
            topGGClient.updateTopGGStats();
        } catch (Exception e) {
            errorLoggingClient.handleError("TopGGTimerTask", "run", "Unexpected error in TopGG scheduled task.", e);
        }
    }
}
