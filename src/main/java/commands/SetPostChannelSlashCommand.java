package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SetPostChannelSlashCommand extends NASASlashCommand {

    public SetPostChannelSlashCommand() {
        this.name = "setPostChannel";
        this.help = "Sets the Post Channel for the server.";
        this.arguments = "<#channelMention>";
        this.options = Collections.singletonList(new OptionData(OptionType.CHANNEL, "channel_mention", "The channel to set as the Post Channel").setRequired(true));
    }
    
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
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
            if (NASABot.dbClient.getPostChannelForServer(Objects.requireNonNull(slashCommandEvent.getGuild()).getId()) != null) {
                try {
                    TextChannel textChannel = slashCommandEvent.getGuild().getTextChannelById(NASABot.dbClient.getPostChannelForServer(slashCommandEvent.getGuild().getId()));
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
            if (slashCommandEvent.hasOption("channel_mention")) {
                List<GuildChannel> guildChannels = Objects.requireNonNull(slashCommandEvent.getOption("channel_mention")).getMentionedChannels();
                if (guildChannels.size() == 0) {
                    slashCommandEvent.reply(String.format("No channels were mentioned, or the bot does not have permission to view the " +
                            "mentioned channel. Please check your permission settings or command formatting: %s", this.getArgumentsString())).queue();
                    return;
                }
                if (NASABot.dbClient.createPostChannel(Objects.requireNonNull(slashCommandEvent.getGuild()).getId(), guildChannels.get(0).getId())) {
                    int postChannelId = NASABot.dbClient.getPostChannelId(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
                    if (postChannelId != -1) {
                        if (NASABot.dbClient.createPostChannelConfiguration(postChannelId)) {
                            slashCommandEvent.reply(String.format("%s has been set as the Post Channel for this server.", guildChannels.get(0).getAsMention())).queue();
                        } else {
                            slashCommandEvent.reply("Unable to set Post Channel Configuration. Please contact the bot owner or join the NASABot Discord channel to report this error.").queue();
                        }
                    } else {
                        slashCommandEvent.reply("Unable to get Post Channel ID. Please contact the bot owner or join the NASABot Discord channel to report this error.").queue();
                    }
                }
            } else {
                slashCommandEvent.reply("Missing channel_mention, please retry.").queue();
            }
        } catch (NullPointerException e) {
            slashCommandEvent.reply("There was a problem setting the server's Post Channel.").queue();
        }
    }
}
