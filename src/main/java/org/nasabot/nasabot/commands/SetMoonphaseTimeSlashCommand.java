package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.NASABot;

import java.util.Collections;
import java.util.Objects;

public class SetMoonphaseTimeSlashCommand extends NASABotSlashCommand {

    public SetMoonphaseTimeSlashCommand() {
        super("setmoonphasetime", "Sets the Moonphase Time for the server.",
                Collections.singletonList(new OptionData(OptionType.INTEGER, "moonphase_time", "The Moonphase Time option you want to use.").setRequired(true)
                        .addChoice("16:00 UTC (default)", 0)
                        .addChoice("6:00 UTC", 1)
                        .addChoice("11:00 UTC", 2)
                        .addChoice("21:00 UTC", 3)));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        try {
            if (!Objects.requireNonNull(slashCommandEvent.getMember()).hasPermission(Permission.ADMINISTRATOR) && !Objects.equals(Objects.requireNonNull(slashCommandEvent.getGuild()).getOwner(), slashCommandEvent.getMember())) {
                slashCommandEvent.reply("Only server administrators or the server owner may use this command.").queue();
                return;
            }
        } catch (NullPointerException e) {
            slashCommandEvent.reply("Unable to find the calling member, please try again. If this issue persists, please contact the owner of the bot.").queue();
            return;
        }

        int timeOption = -1;
        try {
            timeOption = Objects.requireNonNull(slashCommandEvent.getOption("moonphase_time")).getAsInt();
        } catch (NumberFormatException e) {
            slashCommandEvent.reply("Missing moonphase_time, please retry.").queue();
            return;
        }

        int moonphaseChannelId;

        try {
            moonphaseChannelId = dbClient.getMoonphaseChannelId(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
        } catch (NullPointerException e) {
            slashCommandEvent.reply("There was a problem setting the server's Moonphase Time.").queue();
            return;
        }

        if (moonphaseChannelId == -1) {
            slashCommandEvent.reply("This server does not have a Moonphase Channel configured. To set a Moonphase Channel, use the setMoonphaseChannel command.").queue();
        } else {
            boolean result = dbClient.updateMoonphaseChannelConfiguration(timeOption, moonphaseChannelId);
            if (result) {
                slashCommandEvent.reply(String.format("Moonphase Time set to %s:00 UTC.", NASABot.postTimes.get(timeOption))).queue();
            } else {
                slashCommandEvent.reply("Unable to set Moonphase Time. Please contact the bot owner or join the NASABot Discord channel to report this error.").queue();
            }
        }
    }
}
