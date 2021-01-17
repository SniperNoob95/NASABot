package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Objects;

public class GetPostChannel extends NASACommand {

    public GetPostChannel() {
        this.name = "getPostChannel";
        this.help = "Gets the Post Channel for the server.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        this.insertCommand(commandEvent);

        if (!commandEvent.getMember().hasPermission(Permission.ADMINISTRATOR) && !commandEvent.getMember().isOwner()) {
            commandEvent.replyError("Only server administrators or the server owner may use this command.");
            return;
        }

        long channelId = NASABot.dbClient.getPostChannel(commandEvent.getGuild().getIdLong());

        if (channelId != 0) {
            try {
                TextChannel textChannel = commandEvent.getGuild().getTextChannelById(NASABot.dbClient.getPostChannel(commandEvent.getGuild().getIdLong()));
                commandEvent.reply(String.format("This server is using %s as the Post Channel. You can remove it before setting a new one with the following command:" +
                        "\n```NASA_removePostChannel```", Objects.requireNonNull(textChannel).getAsMention()));
            } catch (Exception e) {
                commandEvent.replyError("There is already a Post Channel set for this server, but the bot does not have permission to view it. " +
                        "Please fix your permission settings or clear the current Post Channel with the following command:\n```NASA_removePostChannel```");
            }

        } else {
            commandEvent.reply("No Post Channel has been set for this server. You can set the Post Channel with the following command:" +
                    "\n```NASA_setPostChannel <#channel>```");
        }
    }
}
