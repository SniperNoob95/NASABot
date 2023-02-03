package org.nasabot.nasabot.commands;

import org.nasabot.nasabot.NASABot;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Objects;

public class GetPostChannelSlashCommand extends NASASlashCommand{
    public GetPostChannelSlashCommand() {
        this.name = "getpostchannel";
        this.help = "Gets the Post Channel for the server.";
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        String postChannelId = null;

        try {
            if (!Objects.requireNonNull(slashCommandEvent.getMember()).hasPermission(Permission.ADMINISTRATOR) && !slashCommandEvent.getMember().isOwner()) {
                slashCommandEvent.reply("Only server administrators or the server owner may use this command.").queue();
                return;
            }

            postChannelId = NASABot.dbClient.getPostChannelForServer(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
        } catch (NullPointerException e) {
            slashCommandEvent.reply("Unable to find the calling member, please try again. If this issue persists, please contact the owner of the bot.").queue();
            return;
        }

        if (postChannelId != null) {
            try {
                TextChannel textChannel = Objects.requireNonNull(slashCommandEvent.getGuild()).getTextChannelById(NASABot.dbClient.getPostChannelForServer(slashCommandEvent.getGuild().getId()));
                slashCommandEvent.reply(String.format("This server is using %s as the Post Channel. You can remove it before setting a new one with the following command:" +
                        "\n```NASA_removePostChannel```", Objects.requireNonNull(textChannel).getAsMention())).queue();
            } catch (Exception e) {
                slashCommandEvent.reply("There is already a Post Channel set for this server, but the bot does not have permission to view it. " +
                        "Please fix your permission settings or clear the current Post Channel with the following command:\n```NASA_removePostChannel```").queue();
            }

        } else {
            slashCommandEvent.reply("No Post Channel has been set for this server. You can set the Post Channel with the following command:" +
                    "\n```NASA_setPostChannel <#channel>```").queue();
        }
    }
}
