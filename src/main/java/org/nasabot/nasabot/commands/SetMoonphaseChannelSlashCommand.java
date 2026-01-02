package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;

public class SetMoonphaseChannelSlashCommand extends NASABotSlashCommand {

    public SetMoonphaseChannelSlashCommand() {
        super("setmoonphasechannel", "⭐ Sets the Daily Moonphase Channel for the server.",
                Collections.singletonList(new OptionData(OptionType.CHANNEL, "channel_mention", "The channel to set as the Moonphase Channel.").setRequired(true)), true, true);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        try {
            if (!Objects.requireNonNull(slashCommandEvent.getMember()).hasPermission(Permission.ADMINISTRATOR) && !Objects.equals(Objects.requireNonNull(slashCommandEvent.getGuild()).getOwner(), slashCommandEvent.getMember())) {
                slashCommandEvent.reply("Only server administrators or the server owner may use this command.").queue();
                return;
            }
        } catch (NullPointerException e) {
            slashCommandEvent.reply("Unable to find the calling member, please try again. If this issue persists, please contact the owner of the bot.").queue();
            return;
        }

        try {
            String existingMoonphaseChannelId = dbClient.getMoonphaseChannelForServer(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
            if (existingMoonphaseChannelId != null) {
                try {
                    TextChannel textChannel = slashCommandEvent.getGuild().getTextChannelById(existingMoonphaseChannelId);
                    slashCommandEvent.reply(String.format("This server is already using %s as the Moonphase Channel. Please clear " +
                            "it before setting a new one with the removeMoonphaseChannel command.", Objects.requireNonNull(textChannel).getAsMention())).queue();
                    return;
                } catch (Exception e) {
                    slashCommandEvent.reply("There is already a Moonphase Channel set for this server, but the bot does not have permission to view it. " +
                            "Please clear the current Moonphase Channel before setting a new one with the removeMoonphaseChannel command.").queue();
                    return;
                }
            }
        } catch (NullPointerException e) {
            slashCommandEvent.reply("There was a problem setting the server's Moonphase Channel.").queue();
            return;
        }

        try {
            if (slashCommandEvent.getOption("channel_mention") != null) {
                GuildChannel mentionedChannel = Objects.requireNonNull(slashCommandEvent.getOption("channel_mention")).getAsChannel();
                if (dbClient.createMoonphaseChannel(Objects.requireNonNull(slashCommandEvent.getGuild()).getId(), mentionedChannel.getId())) {
                    int moonphaseChannelId = dbClient.getMoonphaseChannelId(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
                    if (moonphaseChannelId != -1) {
                        if (dbClient.createMoonphaseChannelConfiguration(moonphaseChannelId)) {
                            slashCommandEvent.reply(String.format("%s has been set as the Moonphase Channel for this server. You can configure the Moonphase Time by using the following command: \n```/setmoonphasetime```", mentionedChannel.getAsMention())).queue();
                        } else {
                            slashCommandEvent.reply("Unable to set Moonphase Channel Configuration. Please contact the bot owner or join the NASABot Discord channel to report this error.").queue();
                        }
                    } else {
                        slashCommandEvent.reply("Unable to get Moonphase Channel ID. Please contact the bot owner or join the NASABot Discord channel to report this error.").queue();
                    }
                }
            } else {
                slashCommandEvent.reply("No channels were mentioned, or the bot does not have permission to view the " +
                        "mentioned channel. Please check your permission settings.").queue();
            }
        } catch (NullPointerException e) {
            slashCommandEvent.reply("There was a problem setting the server's Moonphase Channel.").queue();
        }
    }
}
