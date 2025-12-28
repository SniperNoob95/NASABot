package org.nasabot.nasabot.timers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.nasabot.nasabot.NASABot;
import org.nasabot.nasabot.clients.ErrorLoggingClient;
import org.nasabot.nasabot.managers.ButtonManager;

import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

public class NASAImageTimerTask extends TimerTask {
    private final String guildId;
    private final String channelId;
    private final String messageId;
    private final ErrorLoggingClient errorLoggingClient = ErrorLoggingClient.getInstance();
    private final ButtonManager buttonManager = ButtonManager.getInstance();

    public NASAImageTimerTask(String guildId, String channelId, String messageId) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;
    }

    @Override
    public void run() {
        List<String> buttonIds = buttonManager.getButtonsForMessage(messageId);
        // If the message isn't in the map then the interaction is completed.
        if (buttonIds == null) {
            return;
        }

        Guild guild = NASABot.shardManager.getGuildById(guildId);
        try {
            TextChannel textChannel = Objects.requireNonNull(guild).getTextChannelById(channelId);
            try {
                Objects.requireNonNull(textChannel).deleteMessageById(messageId).queue(s -> {
                    buttonManager.removeButtonsFromMap(messageId);
                });
            } catch (NullPointerException e) {
                errorLoggingClient.handleError("NASAImageTimerTask", "run", "Unable to find Channel for expired image.", e.getClass().getName());
            }
        } catch (NullPointerException e) {
            errorLoggingClient.handleError("NASAImageTimerTask", "run", "Unable to find Guild for expired image.", e.getClass().getName());
        }
    }
}
