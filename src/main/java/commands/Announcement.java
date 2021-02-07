package commands;

import com.jagrosh.jdautilities.command.CommandEvent;

public class Announcement extends NASACommand {

    public Announcement() {
        super();
        this.name = "announcement";
        this.help = "Allows the bot owner to make announcements.";
        this.ownerCommand = true;
    }
    @Override
    protected void execute(CommandEvent commandEvent) {
        this.insertCommand(commandEvent);

        if (!commandEvent.isOwner()) {
            commandEvent.replyError("Only the bot owner may use this command.");
            return;
        }

        
    }
}
