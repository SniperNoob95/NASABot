package org.nasabot.nasabot.timers;

import com.google.common.util.concurrent.RateLimiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.utils.FileUpload;
import org.nasabot.nasabot.NASABot;
import org.nasabot.nasabot.clients.DBClient;
import org.nasabot.nasabot.clients.ErrorLoggingClient;
import org.nasabot.nasabot.clients.NASAClient;
import org.nasabot.nasabot.objects.APODChannel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimerTask;

public class APODScheduleTimerTask extends TimerTask {
    private final DBClient dbClient = DBClient.getInstance();
    private final ErrorLoggingClient errorLoggingClient = ErrorLoggingClient.getInstance();
    private final NASAClient nasaClient = NASAClient.getInstance();
    private final int timeOption;
    private final RateLimiter rateLimiter = RateLimiter.create(30.0);

    public APODScheduleTimerTask(int timeOption) {
        this.timeOption = timeOption;
    }

    @Override
    public void run() {
        EmbedBuilder embedBuilder = nasaClient.getLatestPictureOfTheDay();
        FileUpload fileUpload = null;
        Optional<MessageEmbed.Field> imageField = embedBuilder.getFields().stream()
                .filter(field -> field.getName() != null)
                .filter(field -> field.getName().equals("HD Image Link"))
                .findFirst();

        if (imageField.isPresent()) {
            try {
                InputStream file = new URL(Objects.requireNonNull(imageField.get().getValue())).openStream();
                fileUpload = FileUpload.fromData(file, "image.png");
                embedBuilder.setImage("attachment://image.png");
            } catch (IOException e) {
                errorLoggingClient.handleError("APODScheduleTimerTask", "run", "Error creating fileUpload.", e);
                return;
            }
        }

        List<APODChannel> apodChannels = dbClient.getPostChannelsForPostTimeOption(timeOption);

        if (NASABot.loggingEnabled) {
            try {
                NASABot.shardManager.getShards().get(0).openPrivateChannelById("181588597558738954").queue(channel ->
                        channel.sendMessage("Starting APOD for time option " + timeOption + " for " + apodChannels.size() + " servers.").queue());
            } catch (NullPointerException e) {
                errorLoggingClient.handleError("APODScheduleTimerTask", "run", "Unable to find bot owner for logging.", e.getClass().getName());
            }
        }

        FileUpload finalFileUpload = fileUpload;
        apodChannels.forEach(apodChannel -> {
            rateLimiter.acquire();
            System.out.println("Processing channel " + apodChannel);
            sendAPODToChannel(apodChannel, embedBuilder, finalFileUpload);
        });

        try {
            if (fileUpload != null) {
                fileUpload.close();
            }
        } catch (IOException e) {
            errorLoggingClient.handleError("APODScheduleTimerTask", "run", "Error closing fileUpload.", e);
        }
    }

    private void sendAPODToChannel(APODChannel apodChannel, EmbedBuilder embedBuilder, FileUpload fileUpload) {
        Guild guild;
        TextChannel textChannel;
        try {
            guild = NASABot.shardManager.getGuildById(apodChannel.getServerId());
        } catch (NullPointerException e) {
            errorLoggingClient.handleError("APODScheduleTimerTask", "sendAPODToChannel", String.format("Guild %s no longer visible to bot, deleting Post Channel", apodChannel.getServerId()), e.getClass().getName());
            dbClient.deletePostChannel(apodChannel.getServerId());
            return;
        }

        try {
            textChannel = Objects.requireNonNull(guild).getTextChannelById(apodChannel.getChannelId());
        } catch (NullPointerException e) {
            errorLoggingClient.handleError("APODScheduleTimerTask", "sendAPODToChannel", String.format("Text Channel %s in Guild %s no longer visible to bot, deleting Post Channel.", apodChannel.getChannelId(), apodChannel.getServerId()), e.getClass().getName());
            dbClient.deletePostChannel(apodChannel.getServerId());
            return;
        }

        try {
            if (fileUpload != null) {
                Objects.requireNonNull(textChannel).sendFiles(fileUpload).setEmbeds(embedBuilder.build()).queue();
            } else {
                Objects.requireNonNull(textChannel).sendMessageEmbeds(embedBuilder.build()).queue();
            }
        } catch (InsufficientPermissionException e) {
            try {
                Objects.requireNonNull(textChannel).sendMessage(
                        "NASABot does not have permission to send Files and/or Embeds in this channel! Please verify that all permissions are correctly set up.").queue();
            } catch (InsufficientPermissionException e2) {
                errorLoggingClient.handleError("APODScheduleTimerTask", "sendAPODToChannel", String.format("Guild %s and Channel %s inaccessible, deleting Post Channel", apodChannel.getServerId(), apodChannel.getChannelId()), e2);
                dbClient.deletePostChannel(apodChannel.getServerId());
            }
        } catch (NullPointerException e) {
            errorLoggingClient.handleError("APODScheduleTimerTask", "sendAPODToChannel", String.format("Unable to find Channel %s in Guild %s. Deleting Post Channel.", apodChannel.getChannelId(), apodChannel.getServerId()), e);
            dbClient.deletePostChannel(apodChannel.getServerId());
        } catch (Exception e) {
            errorLoggingClient.handleError("APODScheduleTimerTask", "sendAPODToChannel", String.format("Unexpected error serving Channel %s in Guild %s.", apodChannel.getChannelId(), apodChannel.getServerId()), e);
        }
    }
}
