package org.nasabot.nasabot.commands;

import org.nasabot.nasabot.NASABot;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.Objects;

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
            slashCommandEvent.reply("Missing search term, please retry.").queue();
        } else {
            try {
                slashCommandEvent.replyEmbeds(NASABot.NASAClient.getNASAImage(Objects.requireNonNull(slashCommandEvent.getOption("search")).getAsString())).queue();
            } catch (NullPointerException e) {
                slashCommandEvent.reply("Missing search term, please retry.").queue();
            }
        }
    }
}
