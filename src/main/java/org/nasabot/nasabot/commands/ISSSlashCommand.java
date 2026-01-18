package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.clients.ISSClient;
import org.nasabot.nasabot.clients.MapBoxClient;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

public class ISSSlashCommand extends NASABotSlashCommand {
    private final ISSClient issClient = ISSClient.getInstance();
    private final MapBoxClient mapBoxClient = MapBoxClient.getInstance();

    public ISSSlashCommand() {
        super("iss", "⭐ Displays the current location of the International Space Station.", Collections.emptyList(), true, false);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        slashCommandEvent.deferReply().queue();

        MessageEmbed messageEmbed = issClient.getISSLocation();
        if (messageEmbed != null) {
            String longitude = messageEmbed.getFields().stream()
                    .filter(field -> Objects.requireNonNull(field.getName()).equals("Longitude"))
                    .findFirst()
                    .map(MessageEmbed.Field::getValue)
                    .orElse("0");
            String latitude = messageEmbed.getFields().stream()
                    .filter(field -> Objects.requireNonNull(field.getName()).equals("Latitude"))
                    .findFirst()
                    .map(MessageEmbed.Field::getValue)
                    .orElse("0");

            try (FileUpload fileUpload = mapBoxClient.getMapImageForLocation(longitude, latitude)) {
                if (fileUpload != null) {
                    slashCommandEvent.getHook().sendMessageEmbeds(messageEmbed).addFiles(fileUpload).queue(s -> mapBoxClient.deleteFile(fileUpload.getName()));
                } else {
                    slashCommandEvent.getHook().sendMessage("Unable to retrieve ISS location. This is usually due to a failure in the ISS API. Please try again.").queue();
                }
            } catch (IOException e) {
                slashCommandEvent.getHook().sendMessage("Unable to retrieve ISS location. This is usually due to a failure in the ISS API. Please try again.").queue();
            }
        } else {
            slashCommandEvent.getHook().sendMessage("Unable to retrieve ISS location. This is usually due to a failure in the ISS API. Please try again.").queue();
        }
    }
}
