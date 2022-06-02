package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.ErrorLogging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Objects;

public class APODSlashCommand extends NASASlashCommand{
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public APODSlashCommand() {
        super();
        this.name = "apod";
        this.help = "Displays the Picture of the Day from yesterday, or the specified date.";
        this.arguments = "[date (yyyy-mm-dd)]";
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "date", "[yyyy-mm-dd] Date of the APOD to retrieve.").setRequired(false));
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        if (!slashCommandEvent.hasOption("date")) {
            slashCommandEvent.replyEmbeds(NASABot.NASAClient.getPictureOfTheDay(simpleDateFormat.format(System.currentTimeMillis() - 86400000))).queue();
        } else {
            try {
                if (simpleDateFormat.parse(Objects.requireNonNull(slashCommandEvent.getOption("date")).getAsString()) != null) {
                    slashCommandEvent.replyEmbeds(NASABot.NASAClient.getPictureOfTheDay(Objects.requireNonNull(slashCommandEvent.getOption("date")).getAsString())).queue();
                } else {
                    slashCommandEvent.reply(String.format("Unable to get APOD, please check your formatting: %s", this.getArgumentsString())).queue();
                }
            } catch (ParseException e) {
                ErrorLogging.handleError("APODSlashCommand", "execute", String.format("Unable to parse date: %s", slashCommandEvent.getOption("date")), e);
                slashCommandEvent.reply(String.format("Unable to get APOD, please check your formatting: %s", this.getArgumentsString())).queue();
            }
        }
    }
}
