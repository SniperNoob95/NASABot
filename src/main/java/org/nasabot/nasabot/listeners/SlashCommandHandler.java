package org.nasabot.nasabot.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.NASABot;
import org.nasabot.nasabot.commands.NASABotSlashCommand;

import java.util.HashMap;
import java.util.Map;

public class SlashCommandHandler extends ListenerAdapter {
    private final Map<String, NASABotSlashCommand> commands = new HashMap<>();

    public SlashCommandHandler() {
        loadCommands();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        NASABotSlashCommand command = commands.get(event.getName());
        if (command == null) {
            event.reply("Invalid command detected.").queue();
        } else {
            command.execute(event);
        }
    }

    private void loadCommands() {
        NASABot.slashCommands.forEach(command -> commands.put(command.getName(), command));
    }
}
