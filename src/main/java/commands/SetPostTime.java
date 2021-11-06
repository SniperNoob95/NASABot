package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;

import java.util.Objects;

public class SetPostTime extends NASACommand{

    private final int minimumOption = 0;
    private final int maximumOption = 3;

    public SetPostTime() {
        this.name = "setPostTime";
        this.help = "Sets the Post Time for the server.";
        this.arguments = "<post time option number (0 - 3)>";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (!commandEvent.getMember().hasPermission(Permission.ADMINISTRATOR) && !Objects.equals(commandEvent.getGuild().getOwner(), commandEvent.getMember())) {
            commandEvent.replyError("Only server administrators or the server owner may use this command.");
            return;
        }

        int timeOption = -1;
        try {
            timeOption = Integer.parseInt(commandEvent.getArgs());
        } catch (NumberFormatException e) {
            commandEvent.reply(String.format("Invalid Post Time option, please check your formatting: %s", this.getArgumentsString()));
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
            commandEvent.reply(String.format("The Post Time options are \n```%s```\nFor more information, please visit the top.gg or GitHub pages, which can be found via the `NASA_info` command.", timeOptions));
        }

        String postChannelId = NASABot.dbClient.getPostChannelForServer(commandEvent.getGuild().getId());

        if (postChannelId == null) {
            commandEvent.reply("This server does not have a Post Channel configured. To set a Post Channel, use the setPostChannel command.");
        } else {
            boolean result = NASABot.dbClient.updatePostChannelConfiguration(timeOption, Integer.parseInt(postChannelId));
            if (result) {
                commandEvent.reply(String.format("Post Time set to %s:00 UTC.", NASABot.postTimes.get(Integer.parseInt(postChannelId))));
            } else {
                commandEvent.reply("Unable to set Post Time. Please contact the bot owner or join the NASABot Discord channel to report this error.");
            }
        }
    }
}
