package org.nasabot.nasabot.timers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.nasabot.nasabot.NASABot;
import org.nasabot.nasabot.clients.DBClient;
import org.nasabot.nasabot.clients.ErrorLoggingClient;
import org.nasabot.nasabot.clients.MoonphaseClient;
import org.nasabot.nasabot.objects.MoonphaseChannel;

import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

public class MoonphaseScheduleTimerTask extends TimerTask {
    private final DBClient dbClient = DBClient.getInstance();
    private final ErrorLoggingClient errorLoggingClient = ErrorLoggingClient.getInstance();
    private final MoonphaseClient moonphaseClient = MoonphaseClient.getInstance();
    private final int timeOption;

    public MoonphaseScheduleTimerTask(int timeOption) {
        this.timeOption = timeOption;
    }

    @Override
    public void run() {
        MessageEmbed moonphaseEmbed = moonphaseClient.getMoonPhase();

        List<MoonphaseChannel> moonphaseChannels = dbClient.getMoonphaseChannelsForMoonphaseTimeOption(timeOption);

        if (NASABot.loggingEnabled) {
            try {
                NASABot.shardManager.getShards().get(0).openPrivateChannelById("181588597558738954").queue(channel ->
                        channel.sendMessage("Starting Moonphase for time option " + timeOption + " for " + moonphaseChannels.size() + " servers.").queue());
            } catch (NullPointerException e) {
                errorLoggingClient.handleError("MoonphaseScheduleTimerTask", "run", "Unable to find bot owner for logging.", e.getClass().getName());
            }
        }

        moonphaseChannels.forEach(channel -> sendMoonPhaseToChannel(channel, moonphaseEmbed));
    }

    private void sendMoonPhaseToChannel(MoonphaseChannel moonphaseChannel, MessageEmbed moonphaseEmbed) {
        Guild guild;
        TextChannel textChannel;
        try {
            guild = NASABot.shardManager.getGuildById(moonphaseChannel.getServerId());
        } catch (NullPointerException e) {
            errorLoggingClient.handleError("MoonphaseScheduleTimerTask", "sendMoonPhaseToChannel", String.format("Guild %s no longer visible to bot, deleting Moonphase Channel", moonphaseChannel.getServerId()), e.getClass().getName());
            dbClient.deleteMoonphaseChannel(moonphaseChannel.getServerId());
            return;
        }

        try {
            textChannel = Objects.requireNonNull(guild).getTextChannelById(moonphaseChannel.getChannelId());
        } catch (NullPointerException e) {
            errorLoggingClient.handleError("MoonphaseScheduleTimerTask", "sendMoonPhaseToChannel", String.format("Text channel %s in guild %s no longer visible to bot, deleting Moonphase Channel.", moonphaseChannel.getChannelId(), moonphaseChannel.getServerId()), e.getClass().getName());
            dbClient.deleteMoonphaseChannel(moonphaseChannel.getServerId());
            return;
        }

        try {
            Objects.requireNonNull(textChannel).sendMessageEmbeds(moonphaseEmbed).queue();
        } catch (InsufficientPermissionException e) {
            try {
                Objects.requireNonNull(textChannel).sendMessage(
                        "NASABot does not have permission to send Embeds in this channel! Please verify that all permissions are correctly set up.").queue();
            } catch (InsufficientPermissionException e2) {
                errorLoggingClient.handleError("MoonphaseScheduleTimerTask", "sendMoonPhaseToChannel", String.format("Guild %s and channel %s inaccessible, deleting Moonphase Channel", moonphaseChannel.getServerId(), moonphaseChannel.getChannelId()), e2);
                dbClient.deleteMoonphaseChannel(moonphaseChannel.getServerId());
            }
        } catch (NullPointerException e) {
            errorLoggingClient.handleError("MoonphaseScheduleTimerTask", "sendMoonPhaseToChannel", String.format("Unable to find channel %s in guild %s. Deleting Moonphase Channel.", moonphaseChannel.getChannelId(), moonphaseChannel.getServerId()), e);
            dbClient.deleteMoonphaseChannel(moonphaseChannel.getServerId());
        } catch (Exception e) {
            errorLoggingClient.handleError("MoonphaseScheduleTimerTask", "sendMoonPhaseToChannel", String.format("Unexpected error serving channel %s in guild %s.", moonphaseChannel.getChannelId(), moonphaseChannel.getServerId()), e);
        }
    }
}
