package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.clients.ISSClient;

import java.util.Collections;

public class ISSSlashCommand extends NASABotSlashCommand {
    private final ISSClient issClient = ISSClient.getInstance();

    public ISSSlashCommand() {
        super("iss", "⭐ Displays the current location of the International Space Station.", Collections.emptyList(), true, false);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        slashCommandEvent.deferReply().queue();

        MessageEmbed messageEmbed = issClient.getISSLocation();
        if (messageEmbed != null) {
            slashCommandEvent.getHook().sendMessageEmbeds(messageEmbed).queue();
        } else {
            slashCommandEvent.getHook().sendMessage("Unable to retrieve ISS location. This is usually due to a failure in the ISS API. Please try again.").queue();
        }
    }
}
