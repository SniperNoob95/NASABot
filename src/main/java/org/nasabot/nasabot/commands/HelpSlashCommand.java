package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.NASABot;

import java.util.Collections;

public class HelpSlashCommand extends NASABotSlashCommand {

    public HelpSlashCommand() {
        super("help", "Displays helpful information about using the bot.", Collections.emptyList());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        StringBuilder stringBuilder = new StringBuilder("**NASABot** commands:\n");
        for (NASABotSlashCommand command : NASABot.slashCommands) {
            if (command.isOwnerCommand()) {
                continue;
            }
            stringBuilder.append(String.format("\n**`/%s`** - %s", command.getName(), command.getDescription()));
            for (OptionData optionData : command.getOptionData()) {
                stringBuilder.append(String.format("\n\t**`[%s]`** - %s ***(%s)***", optionData.getName(), optionData.getDescription(), optionData.isRequired() ? "Required" : "Optional"));
            }
        }

        try {
            slashCommandEvent.reply(stringBuilder.toString()).queue();
        } catch (Exception e) {
            errorLoggingClient.handleError("HelpSlashCommand", "execute", "Unable to send help content.", e);
            slashCommandEvent.reply("Unable to send help content").queue();
        }
    }
}
