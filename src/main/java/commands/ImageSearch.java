package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ImageSearch  extends Command {

    public ImageSearch() {
        this.name = "image";
        this.help = "Displays a random NASA image from the given search.";
        this.arguments = "<search term>";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isEmpty()) {
            commandEvent.replyError("No search term provided, please check your formatting: \n```");
        } else {
            commandEvent.reply(NASABot.apiClient.getNASAImage(commandEvent.getArgs()));
        }
    }
}
