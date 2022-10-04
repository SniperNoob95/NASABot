package org.nasabot.nasabot.commands;

import org.nasabot.nasabot.NASABot;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import java.util.Objects;

public class ToggleLoggingSlashCommand extends NASASlashCommand {

    public ToggleLoggingSlashCommand() {
        super();
        this.name = "togglelogging";
        this.help = "Allows the bot owner to enable or disable logging.";
        this.ownerCommand = true;
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        try {
            if (!Objects.requireNonNull(slashCommandEvent.getMember()).isOwner()) {
                slashCommandEvent.reply("Only the bot owner may use this command.").queue();
                return;
            }
        } catch (NullPointerException e) {
            slashCommandEvent.reply("Unable to find the calling member, please try again. If this issue persists, please contact the owner of the bot.").queue();
            return;
        }

        if (NASABot.isLoggingEnabled()) {
            NASABot.setLoggingEnabled(false);
            slashCommandEvent.reply("Logging has been disabled.").queue();
        } else {
            NASABot.setLoggingEnabled(true);
            slashCommandEvent.reply("Logging has been enabled.").queue();
        }
    }
}
