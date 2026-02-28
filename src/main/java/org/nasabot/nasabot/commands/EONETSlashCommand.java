package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.objects.eonet.EONETEventsData;

import java.util.Collections;

public class EONETSlashCommand extends NASABotSlashCommand {
    public EONETSlashCommand() {
        super("eonet", "Displays up to 10 of the most recent active EONET weather events.", Collections.emptyList());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        insertCommand(slashCommandEvent);

        slashCommandEvent.deferReply().queue();

        EONETEventsData data = nasaClient.getEONETEventsData();
        if (data == null || data.getEvents().isEmpty()) {
            slashCommandEvent.getHook().sendMessage("Unable to obtain recent EONET events. Please try again soon.").queue();
            return;
        }

        try {
            slashCommandEvent.getHook().sendMessageComponents(data.renderContainer())
                    .useComponentsV2()
                    .queue();
        } catch (Exception e) {
            errorLoggingClient.handleError("EONETSlashCommand", "execute", "Unable to format components.", e);
            slashCommandEvent.getHook().sendMessage("Unable to format EONET events.").queue();
        }
    }
}
