package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.NASABot;

import java.util.Collections;
import java.util.Objects;

public class ToggleLoggingSlashCommand extends NASABotSlashCommand {

    public ToggleLoggingSlashCommand() {
        super("togglelogging", "Allows the bot owner to enable or disable logging.", Collections.emptyList(), true);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
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

        if (NASABot.loggingEnabled) {
            NASABot.loggingEnabled = false;
            slashCommandEvent.reply("Logging has been disabled.").queue();
        } else {
            NASABot.loggingEnabled = true;
            slashCommandEvent.reply("Logging has been enabled.").queue();
        }
    }
}
