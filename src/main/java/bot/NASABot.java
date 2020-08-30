package bot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import commands.POTD;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import utils.APIClient;

import javax.security.auth.login.LoginException;
import java.util.ResourceBundle;

public class NASABot {

    public static JDA jda;
    public static String prefix = "NASA_";
    public static APIClient apiClient = new APIClient();

    public static void main(String[] args) throws LoginException {
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
        builder.setPrefix("NASA_");
        builder.addCommands(new POTD());
        builder.setOwnerId(ownerId);
        CommandClient commandClient = builder.build();

        jda = new JDABuilder(AccountType.BOT).setToken(token).setActivity(Activity.watching("the sky.")).addEventListeners(commandClient).build();
    }
}
