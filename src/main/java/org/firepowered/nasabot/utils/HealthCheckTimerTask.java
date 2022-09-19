package org.firepowered.nasabot.utils;

import java.util.TimerTask;

import org.firepowered.nasabot.NASABot;

public class HealthCheckTimerTask extends TimerTask {

    public HealthCheckTimerTask() {

    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis() / 1000;
        NASABot.healthCheckClient.updateHealthCheck(currentTime);
    }
}
