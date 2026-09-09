package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jspecify.annotations.NonNull;
import org.nasabot.nasabot.objects.epic.EPICData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class EPICSlashCommand extends NASABotSlashCommand {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public EPICSlashCommand() {
        super("epic", "⭐ Browse NASA's EPIC Earth imagery.",
                List.of(
                        new OptionData(OptionType.STRING, "date", "[yyyy-mm-dd] Date of the imagery to retrieve.")
                                .setRequired(true),
                        new OptionData(OptionType.STRING, "lighting", "Image type: natural or enhanced.")
                                .addChoice("Natural", "natural")
                                .addChoice("Enhanced", "enhanced")
                                .setRequired(false)
                ), true, false);
    }

    @Override
    public void execute(@NonNull SlashCommandInteractionEvent slashCommandEvent) {
        insertCommand(slashCommandEvent);

        slashCommandEvent.deferReply().queue();

        String collection = "natural";
        if (slashCommandEvent.getOption("lighting") != null) {
            collection = Objects.requireNonNull(slashCommandEvent.getOption("lighting")).getAsString();
        }

        String date = Objects.requireNonNull(slashCommandEvent.getOption("date")).getAsString();
        try {
            simpleDateFormat.parse(date);
        } catch (ParseException e) {
            slashCommandEvent.getHook().sendMessage(
                    String.format("Invalid date format. Please use yyyy-mm-dd. %s", getArgumentsString())).queue();
            return;
        }

        EPICData epicData = nasaClient.getEPICData(collection, date);
        if (epicData == null || epicData.getImages().isEmpty()) {
            slashCommandEvent.getHook().sendMessage(
                    "No EPIC imagery found for the specified parameters. Please try a different date or lighting type.").queue();
            return;
        }

        try {
            slashCommandEvent.getHook().sendMessageComponents(epicData.renderContainer())
                    .useComponentsV2()
                    .queue();
        } catch (Exception e) {
            errorLoggingClient.handleError("EPICSlashCommand", "execute", "Unable to format components.", e);
            slashCommandEvent.getHook().sendMessage("Unable to format EPIC imagery.").queue();
        }
    }
}
