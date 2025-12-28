package org.nasabot.nasabot.timers;

import org.nasabot.nasabot.clients.TopGGClient;

import java.util.TimerTask;

public class TopGGTimerTask extends TimerTask {
    private final TopGGClient topGGClient = TopGGClient.getInstance();

    @Override
    public void run() {
        topGGClient.updateTopGGStats();
    }
}
