package bot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import utils.*;

import javax.security.auth.login.LoginException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class NASABot {

    public static JDA jda;
    public static String prefix = "NASA_";
    public static NASAClient NASAClient;
    public static DBClient dbClient;
    public static TopGGClient topGGClient;
    public static ISSClient issClient;
    private static TimerTask APODSchedulePostTask;

    public static void main(String[] args) throws LoginException, InterruptedException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");
        String token = null;
        String ownerId = null;

        try {
            token = resourceBundle.getString("token");
            ownerId = resourceBundle.getString("owner");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot get Discord token.");
            System.exit(0);
        }

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix(prefix);
        builder.addCommands(new APOD(), new ImageSearch(), new Info(), new SetPostChannel(), new GetPostChannel(), new RemovePostChannel(), new ISS(), new Announcement());
        builder.setOwnerId(ownerId);
        CommandClient commandClient = builder.build();

        jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.GUILD_MEMBERS).addEventListeners(commandClient).build().awaitReady();
        dbClient = new DBClient();
        NASAClient = new NASAClient();
        topGGClient = new TopGGClient();
        issClient = new ISSClient();

        scheduleAPODPostTask();
    }

    public static Date getAPODScheduleStartDate() {
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now(ZoneId.of("UTC"));
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        LocalDateTime tomorrowNoon = todayMidnight.plusHours(16);

        return Date.from(tomorrowNoon.atZone(ZoneId.of("UTC")).toInstant());
    }

    private static void scheduleAPODPostTask() {
        APODSchedulePostTask = new APODSchedulePostTask();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(APODSchedulePostTask, getAPODScheduleStartDate(), 86400000);
    }
}
