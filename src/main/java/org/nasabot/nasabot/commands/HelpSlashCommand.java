package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.NASABot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelpSlashCommand extends NASABotSlashCommand {

    private static final int MAX_COMMANDS_PER_PAGE = 10;

    public HelpSlashCommand() {
        super("help", "Displays helpful information about using the bot.", Collections.emptyList());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        List<String> pages = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder("**NASABot** commands:\n");
        int commandCount = 0;

        for (NASABotSlashCommand command : NASABot.slashCommands) {
            if (command.isOwnerCommand()) {
                continue;
            }
            if (commandCount == MAX_COMMANDS_PER_PAGE) {
                pages.add(stringBuilder.toString());
                stringBuilder = new StringBuilder("**NASABot** commands:\n");
                commandCount = 0;
            }
            stringBuilder.append(String.format("\n**`/%s`** - %s", command.getName(), command.getDescription()));
            for (OptionData optionData : command.getOptionData()) {
                stringBuilder.append(String.format("\n\t**`[%s]`** - %s ***(%s)***", optionData.getName(), optionData.getDescription(), optionData.isRequired() ? "Required" : "Optional"));
            }
            commandCount++;
        }
        if (commandCount > 0 || pages.isEmpty()) {
            pages.add(stringBuilder.toString());
        }

        try {
            slashCommandEvent.reply(pages.get(0)).queue(interactionHook -> sendNextPage(interactionHook, pages, 1));
        } catch (Exception e) {
            errorLoggingClient.handleError("HelpSlashCommand", "execute", "Unable to send help content.", e);
            slashCommandEvent.reply("Unable to send help content").queue();
        }
    }

    private void sendNextPage(InteractionHook interactionHook, List<String> pages, int pageIndex) {
        if (pageIndex < pages.size()) {
            interactionHook.sendMessage(pages.get(pageIndex)).queue(ignored -> sendNextPage(interactionHook, pages, pageIndex + 1));
        }
    }
}
