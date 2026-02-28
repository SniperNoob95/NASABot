package org.nasabot.nasabot.listeners;

import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.clients.ErrorLoggingClient;
import org.nasabot.nasabot.clients.NASAClient;
import org.nasabot.nasabot.managers.ButtonManager;
import org.nasabot.nasabot.objects.NASAImage;
import org.nasabot.nasabot.objects.marsweather.Sol;

import java.awt.Color;
import java.text.SimpleDateFormat;

public class ButtonHandler extends ListenerAdapter {
    private final NASAClient nasaClient = NASAClient.getInstance();
    private final ButtonManager buttonManager = ButtonManager.getInstance();
    private final ErrorLoggingClient errorLoggingClient = ErrorLoggingClient.getInstance();
    private static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy");

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String userId = event.getUser().getId();
        String buttonId = event.getComponentId();

        if (buttonId.startsWith("IMAGESEARCH:")) {
            handleISSImageButton(event, userId, buttonId);
        } else if (buttonId.startsWith("MARSWEATHER:")) {
            handleMarsWeatherButton(event, buttonId.split(":")[1]);
        } else if (buttonId.startsWith("EONET:")) {
            handleEONETButton(event, buttonId.split(":")[1]);
        } else {
            event.reply("Unable to handle this button, please contact the owner for support using the `/info` command.").queue();
        }
    }

    private void handleISSImageButton(ButtonInteractionEvent event, String userId, String buttonId) {
        Pair<String, NASAImage> buttonImage = buttonManager.getUserImageForButton(buttonId);
        if (buttonImage == null) {
            event.reply("Encountered an error while processing your request. This can happen if the message " +
                    "sent by NASABot has been deleted, or if the button you are clicking is very old. Please try again.").queue();
            return;
        }
        if (!buttonImage.getFirst().equals(userId)) {
            event.reply("You cannot choose the image for someone else's event!").setEphemeral(true).queue();
            return;
        }
        MessageEmbed embed = formatNASAImage(buttonImage.getSecond());
        event.editMessageEmbeds(embed).setReplace(true).queue(s -> buttonManager.removeButtonsFromMap(event.getMessageId()));
    }

    private MessageEmbed formatNASAImage(NASAImage nasaImage) {
        try {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(new Color(192, 32, 232));
            embedBuilder.setTitle(nasaImage.getTitle());
            embedBuilder.setDescription(nasaImage.getDescription().length() > 1500
                    ? nasaImage.getDescription().substring(0, 1500) + "..."
                    : nasaImage.getDescription());
            embedBuilder.addField("Date", outputDateFormat.format(inputDateFormat.parse(nasaImage.getDate())), false);
            embedBuilder.addField("Location", nasaImage.getLocation(), false);
            embedBuilder.setImage(nasaImage.getImageLink());
            return embedBuilder.build();

        } catch (Exception e) {
            errorLoggingClient.handleError("NASAClient", "formatNASAImage", "Cannot format NASA image.", e);
            return new EmbedBuilder().setTitle("NASA Image").addField("ERROR", "Unable to format the NASA image.", false).setColor(Color.RED).build();
        }
    }

    private void handleMarsWeatherButton(ButtonInteractionEvent event, String solId) {
        if (solId.equals("HOME")) {
            event.editComponents(nasaClient.getMarsWeatherData().renderContainer()).setReplace(true).useComponentsV2().queue();
            return;
        }

        Sol sol = nasaClient.getMarsWeatherData().getSols().get(solId);
        if (sol == null) {
            event.reply("This Sol data cannot be found, it may have just expired. Please try again.").queue();
            return;
        }

        event.editComponents(sol.renderContainer()).setReplace(true).useComponentsV2().queue();
    }

    private void handleEONETButton(ButtonInteractionEvent event, String eventId) {
        if (eventId.equals("HOME")) {
            var data = nasaClient.getEONETEventsData();
            if (data == null) {
                event.reply("Unable to refresh EONET events. Please try again soon.").setEphemeral(true).queue();
                return;
            }
            event.editComponents(data.renderContainer()).setReplace(true).useComponentsV2().queue();
            return;
        }

        var data = nasaClient.getEONETEventsData();
        if (data == null || data.getEvents().isEmpty()) {
            event.reply("EONET events are not available right now. Please try again soon.").setEphemeral(true).queue();
            return;
        }

        var eonetEvent = data.getEvents().get(eventId);
        if (eonetEvent == null) {
            event.reply("This EONET event cannot be found, it may have just expired. Please try again.").setEphemeral(true).queue();
            return;
        }

        event.editComponents(eonetEvent.renderContainer()).setReplace(true).useComponentsV2().queue();
    }
}
