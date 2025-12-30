package org.nasabot.nasabot;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.commands.APODSlashCommand;
import org.nasabot.nasabot.commands.GetPostChannelSlashCommand;
import org.nasabot.nasabot.commands.GetPostTimeSlashCommand;
import org.nasabot.nasabot.commands.HelpSlashCommand;
import org.nasabot.nasabot.commands.ISSSlashCommand;
import org.nasabot.nasabot.commands.ImageSearchSlashCommand;
import org.nasabot.nasabot.commands.InfoSlashCommand;
import org.nasabot.nasabot.commands.MoonphaseSlashCommand;
import org.nasabot.nasabot.commands.NASABotSlashCommand;
import org.nasabot.nasabot.commands.RemovePostChannelSlashCommand;
import org.nasabot.nasabot.commands.SetPostChannelSlashCommand;
import org.nasabot.nasabot.commands.SetPostTimeSlashCommand;
import org.nasabot.nasabot.commands.ToggleLoggingSlashCommand;
import org.nasabot.nasabot.listeners.ButtonHandler;
import org.nasabot.nasabot.listeners.SlashCommandHandler;
import org.nasabot.nasabot.timers.APODSchedulePostTimerTask;
import org.nasabot.nasabot.timers.TopGGTimerTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class NASABot extends ListenerAdapter {

    public static ShardManager shardManager;
    public static Map<Integer, Integer> postTimes;
    public static String NASABotServerID;
    public static boolean loggingEnabled;
    public static List<NASABotSlashCommand> slashCommands;
    public static String ownerId;
    private static boolean commandsUpdated;
    public static final String VERSION = "10.2.0";

    public static void main(String[] args) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");
        String token = null;

        try {
            token = resourceBundle.getString("token");
            ownerId = resourceBundle.getString("owner");
            NASABotServerID = resourceBundle.getString("NASABotServer");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot get Discord token.");
            System.exit(1);
        }

        // Slash commands
        slashCommands = List.of(new APODSlashCommand(),
                new GetPostChannelSlashCommand(),
                new GetPostTimeSlashCommand(),
                new HelpSlashCommand(),
                new ImageSearchSlashCommand(),
                new InfoSlashCommand(),
                new ISSSlashCommand(),
                new MoonphaseSlashCommand(),
                new RemovePostChannelSlashCommand(),
                new SetPostChannelSlashCommand(),
                new SetPostTimeSlashCommand(),
                new ToggleLoggingSlashCommand());

        shardManager = DefaultShardManagerBuilder.createLight(token)
                .disableIntents(EnumSet.allOf(GatewayIntent.class))
                .addEventListeners(new NASABot(), new SlashCommandHandler(), new ButtonHandler())
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.customStatus("Watching the sky..."))
                .setMemberCachePolicy(MemberCachePolicy.NONE)
                .setChunkingFilter(ChunkingFilter.NONE)
                .build();

        postTimes = new HashMap<>(Map.ofEntries(
                entry(0, 16), // OPTION 0
                entry(1, 6), // OPTION 1
                entry(2, 11), // OPTION 2
                entry(3, 21) // OPTION 3
        ));

        // When testing locally don't schedule APODs
        if (!Boolean.parseBoolean(System.getenv("testMode"))) {
            System.out.println("Scheduling APODs.");
            for (HashMap.Entry<Integer, Integer> entry : postTimes.entrySet()) {
                scheduleAPODPostTask(entry.getKey());
            }
        }

        scheduleTopGGTask();
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
        TimerTask APODSchedulePostTask = new APODSchedulePostTimerTask(timeOption);
        Timer timer = new Timer();
        // 1 day
        timer.scheduleAtFixedRate(APODSchedulePostTask, getAPODScheduleStartDate(postTimes.get(timeOption)), 86400000);
    }

    private static void scheduleTopGGTask() {
        TimerTask timerTask = new TopGGTimerTask();
        Timer timer = new Timer(true);
        // 10 minutes
        timer.scheduleAtFixedRate(timerTask, 0, 60 * 1000 * 10);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (!commandsUpdated) {
            commandsUpdated = true;
            System.out.println("Loading commands...");
            List<CommandData> commandData = slashCommands.stream()
                    .map(NASABotSlashCommand::getCommandData)
                    .collect(Collectors.toList());
            event.getJDA().updateCommands().addCommands(commandData).queue();
        }
    }
}
