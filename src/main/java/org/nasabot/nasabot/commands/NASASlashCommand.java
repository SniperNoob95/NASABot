package org.nasabot.nasabot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import org.nasabot.nasabot.NASABot;

public abstract class NASASlashCommand extends SlashCommand {

    public NASASlashCommand() {
        super();
    }

    public String getArgumentsString() {
        String arguments;
        try {
            arguments = String.format("\n```/%s %s```", this.getName(), this.getArguments());
        } catch (NullPointerException e) {
            arguments = String.format("\n```/%s```", this.getName());
        }

        return arguments;
    }

    public void insertCommand(SlashCommandEvent slashCommandEvent) {
        NASABot.dbClient.insertCommand(slashCommandEvent, this.getName());
    }
}
