package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONArray;

import java.util.Objects;

public class Announcement extends NASACommand {

    public Announcement() {
        super();
        this.name = "announcement";
        this.help = "Allows the bot owner to make announcements.";
        this.arguments = "<announcement content>";
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        this.insertCommand(commandEvent);

        if (!commandEvent.isOwner()) {
            commandEvent.replyError("Only the bot owner may use this command.");
            return;
        }

        String announcement = String.format(":loudspeaker: NASABot Announcement\n```%s```", commandEvent.getArgs());

        JSONArray postChannels = NASABot.dbClient.getAllPostChannels();

        for (int i = 0; i < postChannels.length(); i++) {
            sendAnnouncementToChannel(postChannels.getJSONObject(i).getString("server_id"), postChannels.getJSONObject(i).getString("channel_id"), announcement);
        }
        
    }

    private void sendAnnouncementToChannel(String serverId, String channelId, String announcement) {
        Guild guild;
        TextChannel textChannel;
        try {
            guild = NASABot.jda.getGuildById(serverId);
        } catch (Exception e) {
            System.out.println(String.format("Guild %s no longer visible to bot, deleting Post Channels", serverId));
            NASABot.dbClient.deletePostChannel(serverId);
            return;
        }

        try {
            textChannel = Objects.requireNonNull(guild).getTextChannelById(channelId);
        } catch (Exception e) {
            System.out.println(String.format("Text channel %s in guild %s no longer visible to bot, skipping announcement post.", channelId, serverId));
            return;
        }

        try {
            Objects.requireNonNull(textChannel).sendMessage(announcement).queue();
        } catch (Exception e) {
            System.out.println(String.format("Unable to send announcement to text channel %s in guild %s.", channelId, serverId));
        }
    }
}
