package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

public class ISSSlashCommand extends NASASlashCommand{

    public ISSSlashCommand() {
        super();
        this.name = "ISS";
        this.help = "Displays the current location of the International Space Station.";
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        slashCommandEvent.replyEmbeds(NASABot.issClient.getISSLocation()).queue();
    }
}
