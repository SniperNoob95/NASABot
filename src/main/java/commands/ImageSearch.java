package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ImageSearch  extends NASACommand {

    public ImageSearch() {
        this.name = "image";
        this.help = "Displays a random NASA image from the given search.";
        this.arguments = "<search term>";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isEmpty()) {
            commandEvent.replyError(String.format("No search term provided, please check your formatting: %s", this.getArgumentsString()));
        } else {
            commandEvent.reply(NASABot.apiClient.getNASAImage(commandEvent.getArgs()));
        }
    }
}
