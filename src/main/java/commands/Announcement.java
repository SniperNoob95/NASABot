package commands;

import com.jagrosh.jdautilities.command.CommandEvent;

public class Announcement extends NASACommand {

    public Announcement() {
        super();
        this.name = "Announcement";
        this.help = "Allows the bot owner to make announcements.";
    }
    @Override
    protected void execute(CommandEvent commandEvent) {
        this.insertCommand(commandEvent);

        if (!commandEvent.isOwner()) {
            commandEvent.replyError("Only server administrators or the server owner may use this command.");
            return;
        }

        else {
            commandEvent.reply("success");
        }
    }
}
