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

public class SetPostChannelSlashCommand extends NASABotSlashCommand {

    public SetPostChannelSlashCommand() {
        super("setpostchannel", "Sets the Post Channel for the server.",
                Collections.singletonList(new OptionData(OptionType.CHANNEL, "channel_mention", "The channel to set as the Post Channel.").setRequired(true)));
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
            String existingPostChannelId = dbClient.getPostChannelForServer(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
            if (existingPostChannelId != null) {
                try {
                    TextChannel textChannel = slashCommandEvent.getGuild().getTextChannelById(existingPostChannelId);
                    slashCommandEvent.reply(String.format("This server is already using %s as the Post Channel. Please clear " +
                            "it before setting a new one with the removePostChannel command.", Objects.requireNonNull(textChannel).getAsMention())).queue();
                    return;
                } catch (Exception e) {
                    slashCommandEvent.reply("There is already a Post Channel set for this server, but the bot does not have permission to view it. " +
                            "Please clear the current Post Channel before setting a new one with the removePostChannel command.").queue();
                    return;
                }
            }
        } catch (NullPointerException e) {
            slashCommandEvent.reply("There was a problem setting the server's Post Channel.").queue();
            return;
        }

        try {
            if (slashCommandEvent.getOption("channel_mention") != null) {
                GuildChannel mentionedChannel = Objects.requireNonNull(slashCommandEvent.getOption("channel_mention")).getAsChannel();
                if (dbClient.createPostChannel(Objects.requireNonNull(slashCommandEvent.getGuild()).getId(), mentionedChannel.getId())) {
                    int postChannelId = dbClient.getPostChannelId(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
                    if (postChannelId != -1) {
                        if (dbClient.createPostChannelConfiguration(postChannelId)) {
                            slashCommandEvent.reply(String.format("%s has been set as the Post Channel for this server.", mentionedChannel.getAsMention())).queue();
                        } else {
                            slashCommandEvent.reply("Unable to set Post Channel Configuration. Please contact the bot owner or join the NASABot Discord channel to report this error.").queue();
                        }
                    } else {
                        slashCommandEvent.reply("Unable to get Post Channel ID. Please contact the bot owner or join the NASABot Discord channel to report this error.").queue();
                    }
                }
            } else {
                slashCommandEvent.reply(String.format("No channels were mentioned, or the bot does not have permission to view the " +
                        "mentioned channel. Please check your permission settings or command formatting: %s", this.getArgumentsString())).queue();
            }
        } catch (NullPointerException e) {
            slashCommandEvent.reply("There was a problem setting the server's Post Channel.").queue();
        }
    }
}
