package org.nasabot.nasabot.utils;

import org.nasabot.nasabot.NASABot;

import java.util.TimerTask;

public class HealthCheckTimerTask extends TimerTask {

    public HealthCheckTimerTask() {

    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis() / 1000;
        NASABot.healthCheckClient.updateHealthCheck(currentTime);
    }
}
