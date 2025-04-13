package org.nasabot.nasabot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import org.json.JSONArray;
import org.nasabot.nasabot.NASABot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.TimerTask;

public class APODSchedulePostTask extends TimerTask {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final int timeOption;

    public APODSchedulePostTask(int timeOption) {
        this.timeOption = timeOption;
    }

    @Override
    public void run() {
        EmbedBuilder embedBuilder = NASABot.NASAClient.getPictureOfTheDay(simpleDateFormat.format(System.currentTimeMillis() - 86400000));
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
                e.printStackTrace();
                return;
            }
        }

        JSONArray postChannels = NASABot.dbClient.getPostChannelsForPostTimeOption(timeOption);

        for (int i = 0; i < postChannels.length(); i++) {
            sendAPODToChannel(postChannels.getJSONObject(i).getString("server_id"), postChannels.getJSONObject(i).getString("channel_id"), embedBuilder, fileUpload);
        }

        try {
            if (fileUpload != null) {
                fileUpload.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (NASABot.isLoggingEnabled()) {
            PrivateChannel privateChannel = NASABot.jda.openPrivateChannelById("181588597558738954").complete();
            privateChannel.sendMessage("Starting APOD for time option " + timeOption + " for " + postChannels.length() + " servers.").queue();
        }
    }

    private void sendAPODToChannel(String serverId, String channelId, EmbedBuilder embedBuilder, FileUpload fileUpload) {
        Guild guild;
        TextChannel textChannel;
        try {
            guild = NASABot.jda.getGuildById(serverId);
        } catch (Exception e) {
            System.out.println(String.format("Guild %s no longer visible to bot, deleting Post Channel", serverId));
            ErrorLogging.handleError("APODSchedulePostTask", "sendAPODToChannel", String.format("Guild %s no longer visible to bot, deleting Post Channel", serverId), e);
            NASABot.dbClient.deletePostChannel(serverId);
            return;
        }

        try {
            textChannel = Objects.requireNonNull(guild).getTextChannelById(channelId);
        } catch (Exception e) {
            System.out.println(String.format("Text channel %s in guild %s no longer visible to bot, skipping APOD post.", channelId, serverId));
            ErrorLogging.handleError("APODSchedulePostTask", "sendAPODToChannel", String.format("Text channel %s in guild %s no longer visible to bot, skipping APOD post.", channelId, serverId), e);
            NASABot.dbClient.deletePostChannel(serverId);
            return;
        }

        try {
            if (fileUpload != null) {
                Objects.requireNonNull(textChannel).sendFiles(fileUpload).setEmbeds(embedBuilder.build()).queue();

            } else {
                Objects.requireNonNull(textChannel).sendMessageEmbeds(embedBuilder.build()).queue();
            }
        } catch (Exception e) {
            System.out.println(String.format("Unable to send APOD to text channel %s in guild %s.", channelId, serverId));
            ErrorLogging.handleError("APODSchedulePostTask", "sendAPODToChannel", String.format("Unable to send APOD to text channel %s in guild %s.", channelId, serverId), e);
        }
    }
}
