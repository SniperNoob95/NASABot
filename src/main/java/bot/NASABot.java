package bot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import commands.GetPostChannel;
import commands.ImageSearch;
import commands.Info;
import commands.APOD;
import commands.RemovePostChannel;
import commands.SetPostChannel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import utils.APIClient;
import utils.APODSchedulePost;
import utils.DBClient;
import utils.TopGGClient;

import javax.security.auth.login.LoginException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class NASABot {

    public static JDA jda;
    public static String prefix = "NASA_";
    public static APIClient apiClient;
    public static DBClient dbClient;
    public static TopGGClient topGGClient;
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
        builder.addCommands(new APOD(), new ImageSearch(), new Info(), new SetPostChannel(), new GetPostChannel(), new RemovePostChannel());
        builder.setOwnerId(ownerId);
        CommandClient commandClient = builder.build();

        jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.GUILD_MEMBERS).addEventListeners(commandClient).build().awaitReady();
        dbClient = new DBClient();
        apiClient = new APIClient();
        topGGClient = new TopGGClient();

        scheduleAPODPostTask();
    }

    public static Date getAPODScheduleStartDate() {
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now(ZoneId.of("UTC"));
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        LocalDateTime tomorrowNoon = todayMidnight.plusHours(12);

        return Date.from(tomorrowNoon.atZone(ZoneId.of("UTC")).toInstant());
    }

    private static void scheduleAPODPostTask() {
        APODSchedulePostTask = new APODSchedulePost();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(APODSchedulePostTask, getAPODScheduleStartDate(), 86400000);
    }
}
