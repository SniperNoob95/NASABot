package utils;

import bot.NASABot;
import org.discordbots.api.client.DiscordBotListAPI;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class TopGGClient {

    DiscordBotListAPI discordBotListAPI;

    public TopGGClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            String authKey = resourceBundle.getString("TopGGKey");
            discordBotListAPI = new DiscordBotListAPI.Builder()
                    .token(authKey)
                    .botId(NASABot.jda.getSelfUser().getId())
                    .build();

            discordBotListAPI.getBot(NASABot.jda.getSelfUser().getId());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot load top.gg client.");
            System.exit(0);
        }

        TimerTask timerTask = new TopGGTimerTask();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 5*1000);
    }

    private class TopGGTimerTask extends TimerTask {

        @Override
        public void run() {
            discordBotListAPI.setStats(NASABot.jda.getGuilds().size());
        }
    }
}
