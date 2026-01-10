package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jspecify.annotations.NonNull;

import java.util.Collections;

public class PremiumSlashCommand extends NASABotSlashCommand {
    private final String storeUrl = "https://discord.com/application-directory/748775876077813881/store";

    public PremiumSlashCommand() {
        super("premium", "Get information about Premium options for NASABot.", Collections.emptyList());
    }

    @Override
    public void execute(@NonNull SlashCommandInteractionEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        slashCommandEvent.reply(String.format("Check out our Premium NASABot options in our Discord store: %s", storeUrl))
                .setEphemeral(true)
                .queue();
    }
}
