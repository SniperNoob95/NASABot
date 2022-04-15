package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;

public class ImageSearchSlashCommand extends NASASlashCommand{

    public ImageSearchSlashCommand() {
        this.name = "image";
        this.help = "Displays a random NASA image from the given search.";
        this.arguments = "<search term>";
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "search", "Image keywords to search for.").setRequired(true));
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        if (slashCommandEvent.getOptions().isEmpty()) {
            slashCommandEvent.reply(String.format("No search term provided, please check your formatting: %s", this.getArgumentsString())).queue();
        } else {
            slashCommandEvent.replyEmbeds(NASABot.NASAClient.getNASAImage(slashCommandEvent.getOptions().get(0).getAsString())).queue();
        }
    }
}
