package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public abstract class NASACommand extends Command {

    /**
     * NASABot-specific wrapper for the Command Abstract Class. Adds helper methods for specific needs.
     */
    public NASACommand() {
        super();
    }

    public String getArgumentsString() {
        return String.format("\n```NASA_%s %s```", this.name, this.arguments);
    }

    public void insertCommand(CommandEvent commandEvent) {
        NASABot.dbClient.insertCommand(commandEvent, this.getName());
    }
}
