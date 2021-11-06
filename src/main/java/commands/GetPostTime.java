package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;

public class GetPostTime extends NASACommand{

    public GetPostTime() {
        this.name = "getPostTime";
        this.help = "Gets the Post Time for the server.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        this.insertCommand(commandEvent);

        String postChannelId = NASABot.dbClient.getPostChannelForServer(commandEvent.getGuild().getId());

        int postTime = NASABot.dbClient.getPostTimeForServer(postChannelId);

        if (postTime == -1) {
            commandEvent.reply("This server does not have a Post Channel configured. To set a Post Channel, use the setPostChannel command.");
        } else {
            commandEvent.reply(String.format("The Post Time for this server is %s:00 UTC.", NASABot.postTimes.get(postTime)));
        }
    }
}
