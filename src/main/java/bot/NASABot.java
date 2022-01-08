package bot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import utils.*;

import javax.security.auth.login.LoginException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.Map.entry;

public class NASABot {

    public static JDA jda;
    public static String prefix = "NASA_";
    public static NASAClient NASAClient;
    public static DBClient dbClient;
    public static HealthCheckClient healthCheckClient;
    public static TopGGClient topGGClient;
    public static ISSClient issClient;
    public static GeoNamesClient geoNamesClient;
    public static Map<Integer, Integer> postTimes;
    public static boolean loggingEnabled = false;

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
        builder.addCommands(new APOD(), new ImageSearch(), new Info(), new SetPostChannel(), new GetPostChannel(), new RemovePostChannel(), new SetPostTime(), new GetPostTime(), new ISS(), new Announcement(), new ToggleLogging());
        builder.setOwnerId(ownerId);
        CommandClient commandClient = builder.build();

        jda = JDABuilder.createDefault(token).addEventListeners(commandClient).build().awaitReady();
        dbClient = new DBClient();
        healthCheckClient = new HealthCheckClient();
        NASAClient = new NASAClient();
        topGGClient = new TopGGClient();
        issClient = new ISSClient();
        geoNamesClient = new GeoNamesClient();

        postTimes = new HashMap<>(Map.ofEntries(
                entry(0, 16), // OPTION 0
                entry(1, 6), // OPTION 1
                entry(2, 11), // OPTION 2
                entry(3, 21) // OPTION 3
        ));

        for (HashMap.Entry<Integer, Integer> entry : postTimes.entrySet()) {
            scheduleAPODPostTask(entry.getKey());
        }

        scheduleHealthCheckTask();
    }

    public static Date getAPODScheduleStartDate(int hours) {
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now(ZoneId.of("UTC"));
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        LocalDateTime tomorrowTime = todayMidnight.plusHours(hours);
        if (tomorrowTime.isBefore(ChronoLocalDateTime.from(today.atTime(LocalTime.now())))) {
            return Date.from(tomorrowTime.atZone(ZoneId.of("UTC")).plusDays(1).toInstant());
        } else {
            return Date.from(tomorrowTime.atZone(ZoneId.of("UTC")).toInstant());
        }
    }

    private static void scheduleAPODPostTask(int timeOption) {
        TimerTask APODSchedulePostTask = new APODSchedulePostTask(timeOption);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(APODSchedulePostTask, getAPODScheduleStartDate(postTimes.get(timeOption)), 86400000);
    }

    private static void scheduleHealthCheckTask() {
        TimerTask healthCheckTimerTask = new HealthCheckTimerTask();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(healthCheckTimerTask, 0, 60000);
    }

    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public static void setLoggingEnabled(boolean value) {
        loggingEnabled = value;
    }
}
