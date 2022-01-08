package utils;

import bot.NASABot;

import java.sql.SQLException;
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
