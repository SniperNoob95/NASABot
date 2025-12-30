package org.nasabot.nasabot.timers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.utils.FileUpload;
import org.json.JSONArray;
import org.nasabot.nasabot.NASABot;
import org.nasabot.nasabot.clients.DBClient;
import org.nasabot.nasabot.clients.ErrorLoggingClient;
import org.nasabot.nasabot.clients.NASAClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.TimerTask;

public class APODSchedulePostTimerTask extends TimerTask {
    private final DBClient dbClient = DBClient.getInstance();
    private final ErrorLoggingClient errorLoggingClient = ErrorLoggingClient.getInstance();
    private final NASAClient nasaClient = NASAClient.getInstance();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final int timeOption;

    public APODSchedulePostTimerTask(int timeOption) {
        this.timeOption = timeOption;
    }

    @Override
    public void run() {
        EmbedBuilder embedBuilder = nasaClient.getPictureOfTheDay(simpleDateFormat.format(System.currentTimeMillis() - 86400000));
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
                errorLoggingClient.handleError("APODSchedulePostTask", "run", "Error creating fileUpload.", e);
                return;
            }
        }

        JSONArray postChannels = dbClient.getPostChannelsForPostTimeOption(timeOption);

        if (NASABot.loggingEnabled) {
            try {
                NASABot.shardManager.getShards().get(0).openPrivateChannelById("181588597558738954").queue(channel ->
                        channel.sendMessage("Starting APOD for time option " + timeOption + " for " + postChannels.length() + " servers.").queue());
            } catch (NullPointerException e) {
                errorLoggingClient.handleError("APODSchedulePostTask", "run", "Unable to find bot owner for logging.", e.getClass().getName());
            }
        }

        for (int i = 0; i < postChannels.length(); i++) {
            sendAPODToChannel(postChannels.getJSONObject(i).getString("server_id"), postChannels.getJSONObject(i).getString("channel_id"), embedBuilder, fileUpload);
        }

        try {
            if (fileUpload != null) {
                fileUpload.close();
            }
        } catch (IOException e) {
            errorLoggingClient.handleError("APODSchedulePostTask", "run", "Error closing fileUpload.", e);
        }
    }

    private void sendAPODToChannel(String serverId, String channelId, EmbedBuilder embedBuilder, FileUpload fileUpload) {
        Guild guild;
        TextChannel textChannel;
        try {
            guild = NASABot.shardManager.getGuildById(serverId);
        } catch (NullPointerException e) {
            errorLoggingClient.handleError("APODSchedulePostTask", "sendAPODToChannel", String.format("Guild %s no longer visible to bot, deleting Post Channel", serverId), e.getClass().getName());
            dbClient.deletePostChannel(serverId);
            return;
        }

        try {
            textChannel = Objects.requireNonNull(guild).getTextChannelById(channelId);
        } catch (NullPointerException e) {
            errorLoggingClient.handleError("APODSchedulePostTask", "sendAPODToChannel", String.format("Text channel %s in guild %s no longer visible to bot, deleting Post Channel.", channelId, serverId), e.getClass().getName());
            dbClient.deletePostChannel(serverId);
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
                errorLoggingClient.handleError("APODSchedulePostTask", "sendAPODToChannel", String.format("Guild %s and channel %s inaccessible, deleting Post Channel", serverId, channelId), e2);
                dbClient.deletePostChannel(serverId);
            }
        } catch (Exception e) {
            errorLoggingClient.handleError("APODSchedulePostTask", "sendAPODToChannel", String.format("Unable to send APOD to text channel %s in guild %s.", channelId, serverId), e);
        }
    }
}
