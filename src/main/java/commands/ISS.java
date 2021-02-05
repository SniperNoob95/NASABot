package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ISS extends NASACommand {

    public ISS() {
        super();
        this.name = "ISS";
        this.help = "Displays the current location of the International Space Station.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        this.insertCommand(commandEvent);

        commandEvent.reply(NASABot.issClient.getISSLocation());
    }
}
