package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ToggleLogging extends NASACommand{

    public ToggleLogging() {
        super();
        this.name = "enableLogging";
        this.help = "Allows the bot owner to enable or disable logging.";
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        this.insertCommand(commandEvent);

        if (!commandEvent.isOwner()) {
            commandEvent.replyError("Only the bot owner may use this command.");
            return;
        }

        if (NASABot.isLoggingEnabled()) {
            NASABot.setLoggingEnabled(false);
            commandEvent.reply("Logging has been disabled.");
        } else {
            NASABot.setLoggingEnabled(true);
            commandEvent.reply("Logging has been enabled.");
        }

    }
}
