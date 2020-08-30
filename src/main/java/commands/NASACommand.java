package commands;

import com.jagrosh.jdautilities.command.Command;

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
}
