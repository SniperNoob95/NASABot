package org.nasabot.nasabot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.clients.ErrorLoggingClient;
import org.nasabot.nasabot.managers.ButtonManager;
import org.nasabot.nasabot.objects.NASAImage;

import java.awt.Color;
import java.text.SimpleDateFormat;

public class ButtonHandler extends ListenerAdapter {
    private final ButtonManager buttonManager = ButtonManager.getInstance();
    private final ErrorLoggingClient errorLoggingClient = ErrorLoggingClient.getInstance();
    private static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy");

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        NASAImage buttonImage = buttonManager.getImageForButton(buttonId);
        if (buttonImage == null) {
            event.reply("Encountered an error while processing your request. This can happen if the message " +
                    "sent by NASABot has been deleted, or if the button you are clicking is very old. Please try again.").queue();
            return;
        }
        MessageEmbed embed = formatNASAImage(buttonImage);
        event.editMessageEmbeds(embed).setReplace(true).queue(s -> {
            buttonManager.removeButtonsFromMap(event.getMessageId());
        });
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
            e.printStackTrace();
            errorLoggingClient.handleError("NASAClient", "formatNASAImage", "Cannot format NASA image.", e);
            return new EmbedBuilder().setTitle("NASA Image").addField("ERROR", "Unable to format the NASA image.", false).setColor(Color.RED).build();
        }
    }
}
