package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.ErrorLogging;

public class HelpSlashCommand extends NASASlashCommand {

    public HelpSlashCommand() {
        this.name = "help";
        this.help = "Displays helpful information about using the bot.";
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {

        this.insertCommand(slashCommandEvent);

        StringBuilder stringBuilder = new StringBuilder("**NASABot commands**:\n");
        for (SlashCommand command : NASABot.slashCommands) {
            stringBuilder.append(String.format("\n`%s` - %s", command.getName(), command.getHelp()));
            if (command.getOptions().size() > 0) {
                for (OptionData optionData : command.getOptions()) {
                    stringBuilder.append(String.format("\n\t`%s` - %s (Required: %s)", optionData.getName(), optionData.getDescription(), optionData.isRequired()));
                }
            }
        }

        try {
            slashCommandEvent.reply(stringBuilder.toString()).queue();
        } catch (Exception e) {
            ErrorLogging.handleError("HelpSlashCommand", "execute", "Unable to send help content.", e);
            slashCommandEvent.reply("Unable to send help content").queue();
        }
    }
}
