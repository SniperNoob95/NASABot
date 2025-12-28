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
            slashCommandEvent.reply("Unable to retrieve post time, please try again. If this issue persists, please contact the owner of the bot.").queue();
        }

        if (postChannelId == -1) {
            slashCommandEvent.reply("This server does not have a Post Channel configured. To set a Post Channel, use the setPostChannel command.").queue();
            return;
        }

        int postTime = dbClient.getPostTimeForServer(postChannelId);

        if (postTime == -1) {
            slashCommandEvent.reply("This server does not have a Post Channel configured. To set a Post Channel, use the setPostChannel command.").queue();
        } else {
            slashCommandEvent.reply(String.format("The Post Time for this server is %s:00 UTC.", NASABot.postTimes.get(postTime))).queue();
        }
    }
}
