package org.nasabot.nasabot.commands;

import org.nasabot.nasabot.NASABot;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.Objects;

public class SetPostTimeSlashCommand extends NASASlashCommand {

    private final int minimumOption = 0;
    private final int maximumOption = 3;

    public SetPostTimeSlashCommand() {
        this.name = "setposttime";
        this.help = "Sets the Post Time for the server.";
        this.arguments = "<post time option number (0 - 3)>";
        this.options = Collections.singletonList(new OptionData(OptionType.INTEGER, "post_time", "The Post Time option you want to use.").setRequired(true)
                .addChoice("16:00 UTC (default)", 0)
                .addChoice("6:00 UTC", 1)
                .addChoice("11:00 UTC", 2)
                .addChoice("21:00 UTC", 3));
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
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
            timeOption = Objects.requireNonNull(slashCommandEvent.getOption("post_time")).getAsInt();
        } catch (NumberFormatException e) {
            slashCommandEvent.reply("Missing post_time, please retry.").queue();
            return;
        }

        StringBuilder timeOptions = new StringBuilder();
        for (int i = minimumOption; i <= maximumOption; i++) {
            if (i == 0) {
                timeOptions.append(String.format("\n%s: %s:00 UTC (default)", i, NASABot.postTimes.get(i)));
            } else {
                timeOptions.append(String.format("\n%s: %s:00 UTC", i, NASABot.postTimes.get(i)));
            }
        }

        if (timeOption < minimumOption || timeOption > maximumOption) {
            slashCommandEvent.reply(String.format("The Post Time options are \n```%s```\nFor more information, please visit the top.gg or GitHub pages, which can be found via the `NASA_info` command.", timeOptions)).queue();
            return;
        }

        int postChannelId = 0;

        try {
            postChannelId = NASABot.dbClient.getPostChannelId(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
        } catch (NullPointerException e) {
            slashCommandEvent.reply("There was a problem setting the server's Post Time.").queue();
            return;
        }

        if (postChannelId == -1) {
            slashCommandEvent.reply("This server does not have a Post Channel configured. To set a Post Channel, use the setPostChannel command.").queue();
        } else {
            boolean result = NASABot.dbClient.updatePostChannelConfiguration(timeOption, postChannelId);
            if (result) {
                slashCommandEvent.reply(String.format("Post Time set to %s:00 UTC.", NASABot.postTimes.get(timeOption))).queue();
            } else {
                slashCommandEvent.reply("Unable to set Post Time. Please contact the bot owner or join the NASABot Discord channel to report this error.").queue();
            }
        }
    }
}
