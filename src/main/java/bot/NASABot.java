package bot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import commands.GetPostChannel;
import commands.ImageSearch;
import commands.Info;
import commands.APOD;
import commands.RemovePostChannel;
import commands.SetPostChannel;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import utils.APIClient;
import utils.DBClient;
import utils.TopGGClient;

import javax.security.auth.login.LoginException;
import java.util.ResourceBundle;

public class NASABot {

    public static JDA jda;
    public static String prefix = "NASA_";
    public static APIClient apiClient;
    public static DBClient dbClient;
    public static TopGGClient topGGClient;
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
    }
}
