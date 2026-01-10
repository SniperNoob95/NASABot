package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.NASABot;

import java.util.Collections;
import java.util.Objects;

public class GetPostTimeSlashCommand extends NASABotSlashCommand {
    public GetPostTimeSlashCommand() {
        super("getposttime", "Gets the Post Time for the server.", Collections.emptyList());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        int postChannelId = -1;
        try {
            postChannelId = dbClient.getPostChannelId(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
        } catch (NullPointerException e) {
            slashCommandEvent.reply("Unable to retrieve Post time, please try again. If this issue persists, please contact the owner of the bot.").queue();
        }

        if (postChannelId == -1) {
            slashCommandEvent.reply("No Post Channel has been set for this server. You can set the Post Channel with the following command:" +
                    "\n```/setpostchannel <#channel>```").queue();
            return;
        }

        int postTime = dbClient.getPostTimeForServer(postChannelId);

        if (postTime == -1) {
            slashCommandEvent.reply("No Post Channel has been set for this server. You can set the Post Channel with the following command:" +
                    "\n```/setpostchannel <#channel>```").queue();
        } else {
            slashCommandEvent.reply(String.format("The Post Time for this server is %s:00 UTC.", NASABot.postTimes.get(postTime))).queue();
        }
    }
}
