package org.nasabot.nasabot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.nasabot.nasabot.commands.APODSlashCommand;
import org.nasabot.nasabot.commands.GetPostChannelSlashCommand;
import org.nasabot.nasabot.commands.GetPostTimeSlashCommand;
import org.nasabot.nasabot.commands.HelpSlashCommand;
import org.nasabot.nasabot.commands.ISSSlashCommand;
import org.nasabot.nasabot.commands.ImageSearchSlashCommand;
import org.nasabot.nasabot.commands.InfoSlashCommand;
import org.nasabot.nasabot.commands.MoonphaseSlashCommand;
import org.nasabot.nasabot.commands.RemovePostChannelSlashCommand;
import org.nasabot.nasabot.commands.SetPostChannelSlashCommand;
import org.nasabot.nasabot.commands.SetPostTimeSlashCommand;
import org.nasabot.nasabot.commands.ToggleLoggingSlashCommand;
import org.nasabot.nasabot.utils.*;

import javax.security.auth.login.LoginException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    public static String NASABotServerID;
    public static boolean loggingEnabled = false;
    public static List<SlashCommand> slashCommands;

    public static final String VERSION = "8.0.0";

    public static void main(String[] args) throws InterruptedException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");
        String token = null;
        String ownerId = null;

        try {
            token = resourceBundle.getString("token");
            ownerId = resourceBundle.getString("owner");
            NASABotServerID = resourceBundle.getString("NASABotServer");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot get Discord token.");
            System.exit(1);
        }

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix(prefix);

        // Slash commands
        builder.addSlashCommands(new APODSlashCommand(),
                new GetPostChannelSlashCommand(),
                new GetPostTimeSlashCommand(),
                new HelpSlashCommand(),
                new ImageSearchSlashCommand(),
                new InfoSlashCommand(),
                new ISSSlashCommand(),
                new RemovePostChannelSlashCommand(),
                new SetPostChannelSlashCommand(),
                new SetPostTimeSlashCommand(),
                new ToggleLoggingSlashCommand(),
                new MoonphaseSlashCommand());
        builder.setOwnerId(ownerId);
        CommandClient commandClient = builder.build();
        slashCommands = commandClient.getSlashCommands();

        jda = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES) .addEventListeners(commandClient).build().awaitReady();
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.LISTENING, "commands..."));
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
