package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.NASABot;

import java.util.Collections;
import java.util.Objects;

public class SetPostTimeSlashCommand extends NASABotSlashCommand {

    public SetPostTimeSlashCommand() {
        super("setposttime", "Sets the Post Time for the server.",
                Collections.singletonList(new OptionData(OptionType.INTEGER, "post_time", "The Post Time option you want to use.").setRequired(true)
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

        int timeOption = Objects.requireNonNull(slashCommandEvent.getOption("post_time")).getAsInt();

        int postChannelId;

        try {
            postChannelId = dbClient.getPostChannelId(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
        } catch (NullPointerException e) {
            errorLoggingClient.handleError("SetPostTimeSlashCommand", "execute", "Unable to find Guild.", e.getClass().getName());
            slashCommandEvent.reply("There was a problem setting the server's Post Time.").queue();
            return;
        }

        if (postChannelId == -1) {
            slashCommandEvent.reply("This server does not have a Post Channel configured. To set a Post Channel, use the setPostChannel command.").queue();
        } else {
            boolean result = dbClient.updatePostChannelConfiguration(timeOption, postChannelId);
            if (result) {
                slashCommandEvent.reply(String.format("Post Time set to %s:00 UTC.", NASABot.postTimes.get(timeOption))).queue();
            } else {
                slashCommandEvent.reply("Unable to set Post Time. Please contact the bot owner or join the NASABot Discord channel to report this error.").queue();
            }
        }
    }
}
