package utils;

import bot.NASABot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimerTask;

public class APODSchedulePostTask extends TimerTask {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void run() {
        MessageEmbed embed = NASABot.NASAClient.getPictureOfTheDay(simpleDateFormat.format(System.currentTimeMillis() - 86400000));
        JSONArray postChannels = NASABot.dbClient.getAllPostChannels();

        for (int i = 0; i < postChannels.length(); i++) {
            sendAPODToChannel(postChannels.getJSONObject(i).getString("server_id"), postChannels.getJSONObject(i).getString("channel_id"), embed);
        }
    }

    private void sendAPODToChannel(String serverId, String channelId, MessageEmbed embed) {
        Guild guild;
        TextChannel textChannel;
        try {
            guild = NASABot.jda.getGuildById(serverId);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format("Guild %s no longer visible to bot, deleting Post Channels", serverId));
            NASABot.dbClient.deletePostChannel(serverId);
            return;
        }

        try {
            textChannel = Objects.requireNonNull(guild).getTextChannelById(channelId);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format("Text channel %s in guild %s no longer visible to bot, skipping APOD post.", channelId, serverId));
            return;
        }

        try {
            Objects.requireNonNull(textChannel).sendMessage(embed).queue();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format("Unable to send APOD to text channel %s in guild %s.", channelId, serverId));
        }
    }
}
