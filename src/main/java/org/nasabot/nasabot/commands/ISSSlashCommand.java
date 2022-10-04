package org.nasabot.nasabot.commands;

import org.nasabot.nasabot.NASABot;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

public class ISSSlashCommand extends NASASlashCommand{

    public ISSSlashCommand() {
        super();
        this.name = "iss";
        this.help = "Displays the current location of the International Space Station.";
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        slashCommandEvent.replyEmbeds(NASABot.issClient.getISSLocation()).queue();
    }
}
