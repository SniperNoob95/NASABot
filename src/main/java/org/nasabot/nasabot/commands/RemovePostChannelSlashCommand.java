package org.nasabot.nasabot.commands;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import org.nasabot.nasabot.NASABot;

import java.util.Objects;

public class RemovePostChannelSlashCommand extends NASASlashCommand {

    public RemovePostChannelSlashCommand() {
        this.name = "removepostchannel";
        this.help = "Removes the Post Channel for the server.";
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
            if (NASABot.dbClient.deletePostChannel(Objects.requireNonNull(slashCommandEvent.getGuild()).getId())) {
                slashCommandEvent.reply("Post Channels for this server have been removed.").queue();
            } else {
                slashCommandEvent.reply("There was a problem removing the server's Post Channel.").queue();
            }
        } catch (NullPointerException e) {
            slashCommandEvent.reply("There was a problem removing the server's Post Channel.").queue();
        }
    }
}
