package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Objects;

public class SetPostChannel extends NASACommand {

    public SetPostChannel() {
        this.name = "setPostChannel";
        this.help = "Sets the Post Channel for the server.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        this.insertCommand(commandEvent);

        if (!commandEvent.getMember().hasPermission(Permission.ADMINISTRATOR) && !commandEvent.getMember().isOwner()) {
            commandEvent.replyError("Only server administrators or the server owner may use this command.");
            return;
        }

        if (NASABot.dbClient.getPostChannel(commandEvent.getGuild().getId()) != null) {
            try {
                TextChannel textChannel = commandEvent.getGuild().getTextChannelById(NASABot.dbClient.getPostChannel(commandEvent.getGuild().getId()));
                commandEvent.replyError(String.format("This server is already using %s as the Post Channel. Please clear " +
                        "it before setting a new one with the following command:\n```NASA_removePostChannel```", Objects.requireNonNull(textChannel).getAsMention()));
            } catch (Exception e) {
                commandEvent.replyError("There is already a Post Channel set for this server, but the bot does not have permission to view it. " +
                        "Please clear the current Post Channel before setting a new one with the following command:\n```NASA_removePostChannel```");
            }

        }

        if (commandEvent.getMessage().getMentionedChannels().size() > 0) {
            TextChannel textChannel = commandEvent.getMessage().getMentionedChannels().get(0);
            if(NASABot.dbClient.addPostChannel(commandEvent.getGuild().getId(), textChannel.getId())) {
                commandEvent.reply(String.format("%s has been set as the Post Channel for this server.", textChannel.getAsMention()));
            }
        } else {
            commandEvent.replyError("No channels were mentioned, or the bot does not have permission to view the " +
                    "mentioned channel. Please check your permission settings or command formatting:" +
                    "\n```NASA_setPostChannel <#channel>```");
        }
    }
}
