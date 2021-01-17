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
        String arguments;
        try {
            arguments =  String.format("\n```NASA_%s %s```", this.name, this.arguments);
        } catch (NullPointerException e) {
            arguments = String.format("\n```NASA_%s %s```", this.name, "");
        }

        return arguments;
    }

    public void insertCommand(CommandEvent commandEvent) {
        NASABot.dbClient.insertCommand(commandEvent, this.getName());
    }
}
