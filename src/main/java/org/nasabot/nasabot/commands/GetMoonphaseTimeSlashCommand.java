package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.NASABot;

import java.util.Collections;
import java.util.Objects;

public class GetMoonphaseTimeSlashCommand extends NASABotSlashCommand {
    public GetMoonphaseTimeSlashCommand() {
        super("getmoonphasetime", "Gets the Moonphase Time for the server.", Collections.emptyList());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        int moonphaseChannelId = -1;
        try {
            moonphaseChannelId = dbClient.getMoonphaseChannelId(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
        } catch (NullPointerException e) {
            slashCommandEvent.reply("Unable to retrieve Moonphase time, please try again. If this issue persists, please contact the owner of the bot.").queue();
        }

        if (moonphaseChannelId == -1) {
            slashCommandEvent.reply("This server does not have a Moonphase Channel configured. To set a Moonphase Channel, use the setMoonphaseChannel command.").queue();
            return;
        }

        int postTime = dbClient.getMoonphaseTimeForServer(moonphaseChannelId);

        if (postTime == -1) {
            slashCommandEvent.reply("This server does not have a Moonphase Channel configured. To set a Moonphase Channel, use the setMoonphaseChannel command.").queue();
        } else {
            slashCommandEvent.reply(String.format("The Moonphase Time for this server is %s:00 UTC.", NASABot.postTimes.get(postTime))).queue();
        }
    }
}
