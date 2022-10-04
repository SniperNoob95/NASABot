package org.nasabot.nasabot.utils;

import org.nasabot.nasabot.NASABot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimerTask;

public class APODSchedulePostTask extends TimerTask {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private int timeOption;

    public APODSchedulePostTask(int timeOption) {
        this.timeOption = timeOption;
    }

    @Override
    public void run() {
        MessageEmbed embed = NASABot.NASAClient.getPictureOfTheDay(simpleDateFormat.format(System.currentTimeMillis() - 86400000));
        JSONArray postChannels = NASABot.dbClient.getPostChannelsForPostTimeOption(timeOption);

        for (int i = 0; i < postChannels.length(); i++) {
            sendAPODToChannel(postChannels.getJSONObject(i).getString("server_id"), postChannels.getJSONObject(i).getString("channel_id"), embed);
        }

        if (NASABot.isLoggingEnabled()) {
            PrivateChannel privateChannel = NASABot.jda.openPrivateChannelById("181588597558738954").complete();
            privateChannel.sendMessage("Starting APOD for time option " + timeOption + " for " + postChannels.length() + " servers.").queue();
        }
    }

    private void sendAPODToChannel(String serverId, String channelId, MessageEmbed embed) {
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
            return;
        }

        try {
            Objects.requireNonNull(textChannel).sendMessageEmbeds(embed).queue();
        } catch (Exception e) {
            System.out.println(String.format("Unable to send APOD to text channel %s in guild %s.", channelId, serverId));
            ErrorLogging.handleError("APODSchedulePostTask", "sendAPODToChannel", String.format("Unable to send APOD to text channel %s in guild %s.", channelId, serverId), e);
        }
    }
}
