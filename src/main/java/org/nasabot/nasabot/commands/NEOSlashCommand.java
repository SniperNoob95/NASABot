package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.objects.neo.NEOData;

import java.util.List;

public class NEOSlashCommand extends NASABotSlashCommand {
    public NEOSlashCommand() {
        super("neos", "⭐ Browse NASA's Near Earth Object Web Service (NeoWs) database.",
                List.of(
                        new OptionData(OptionType.INTEGER, "page", "Page number of NEOs to browse (starts at 1).")
                                .setRequired(false)
                                .setMinValue(1)
                ), true, false);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        insertCommand(slashCommandEvent);

        slashCommandEvent.deferReply().queue();

        int page = 0;
        if (slashCommandEvent.getOption("page") != null) {
            page = (int) slashCommandEvent.getOption("page").getAsLong() - 1;
            if (page < 0) {
                page = 0;
            }
        }

        NEOData data = nasaClient.getNEOData(page);
        if (data == null || data.getNeos().isEmpty()) {
            slashCommandEvent.getHook().sendMessage("Unable to obtain Near Earth Objects data. Please try again soon.").queue();
            return;
        }

        try {
            slashCommandEvent.getHook().sendMessageComponents(data.renderContainer())
                    .useComponentsV2()
                    .queue();
        } catch (Exception e) {
            errorLoggingClient.handleError("NEOSlashCommand", "execute", "Unable to format components.", e);
            slashCommandEvent.getHook().sendMessage("Unable to format Near Earth Objects.").queue();
        }
    }
}
