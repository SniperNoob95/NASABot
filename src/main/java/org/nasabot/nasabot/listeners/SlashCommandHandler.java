package org.nasabot.nasabot.listeners;

import net.dv8tion.jda.api.entities.emoji.Emoji;
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
            return;
        }
        if (command.isPremium()) {
            if (!command.isAuthorized(event, event.isGuildCommand())) {
                event.reply(premiumMessage(event, event.isGuildCommand())).setEphemeral(true).queue();
                return;
            }
        }
        command.execute(event);
    }

    private String premiumMessage(SlashCommandInteractionEvent event, boolean guildOnly) {
        if (guildOnly) {
            return String.format("%s %s This command requires the Server you are in to have Premium! " +
                            "To see our Premium subscriptions and features, use the `/premium` command.",
                    Emoji.fromUnicode("U+2B50"), event.getUser().getAsMention());
        }
        return String.format("%s %s This command requires either you or the Server you are in to have Premium! " +
                        "To see our Premium subscriptions and features, use the `/premium` command.",
                Emoji.fromUnicode("U+2B50"), event.getUser().getAsMention());
    }

    private void loadCommands() {
        NASABot.slashCommands.forEach(command -> commands.put(command.getName(), command));
        // TODO remove
        NASABot.localCommands.forEach(command -> commands.put(command.getName(), command));
    }
}
