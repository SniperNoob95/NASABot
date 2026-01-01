package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.clients.MoonphaseClient;

import java.util.Collections;

public class MoonphaseSlashCommand extends NASABotSlashCommand {
    private final MoonphaseClient moonphaseClient = MoonphaseClient.getInstance();

    public MoonphaseSlashCommand() {
        super("moonphase", "Displays the current phase of the moon.", Collections.emptyList());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        insertCommand(event);

        MessageEmbed moonphase = moonphaseClient.getMoonPhase();

        event.replyEmbeds(moonphase).queue();
    }

}
