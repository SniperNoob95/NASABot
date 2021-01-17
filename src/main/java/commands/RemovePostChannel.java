package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;

public class RemovePostChannel extends NASACommand {
    public RemovePostChannel() {
        this.name = "removePostChannel";
        this.help = "Removes the Post Channel for the server.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        this.insertCommand(commandEvent);

        if (!commandEvent.getMember().hasPermission(Permission.ADMINISTRATOR) && !commandEvent.getMember().isOwner()) {
            commandEvent.replyError("Only server administrators or the server owner may use this command.");
            return;
        }

        if (NASABot.dbClient.deletePostChannel(commandEvent.getGuild().getIdLong())) {
            commandEvent.reply("Post Channels for this server have been removed.");
        } else {
            commandEvent.replyError("There was a problem removing the server's Post Channel.");
        }

    }
}
