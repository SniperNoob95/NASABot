package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jspecify.annotations.NonNull;
import org.nasabot.nasabot.objects.marsweather.MarsWeatherData;

import java.util.Collections;

public class MarsWeatherSlashCommand extends NASABotSlashCommand {
    public MarsWeatherSlashCommand() {
        super("marsweather", "⭐ Explore Mars weather data!", Collections.emptyList(), true, false);
    }

    @Override
    public void execute(@NonNull SlashCommandInteractionEvent slashCommandEvent) {
        insertCommand(slashCommandEvent);

        slashCommandEvent.deferReply().queue();

        MarsWeatherData marsWeatherData = nasaClient.getMarsWeatherData();
        if (marsWeatherData == null) {
            slashCommandEvent.getHook().sendMessage("Error while retrieving Mars Weather Data").queue();
            return;
        }

        try {
            slashCommandEvent.getHook().sendMessageComponents(marsWeatherData.renderContainer())
                    .useComponentsV2()
                    .queue();
        } catch (Exception e) {
            errorLoggingClient.handleError("MarsWeatherSlashCommand", "execute", "Unable to format components.", e);
            slashCommandEvent.getHook().sendMessage("Unable to format Mars Weather data.").queue();
        }
    }
}
