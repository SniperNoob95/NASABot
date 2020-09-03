package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class POTD extends NASACommand {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public POTD() {
        this.name = "POTD";
        this.help = "Displays the Picture of the Day from yesterday, or the specified date.";
        this.arguments = "[date (yyyy-mm-dd)]";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isEmpty()) {
            commandEvent.reply(NASABot.apiClient.getPictureOfTheDay(simpleDateFormat.format(System.currentTimeMillis() - 86400000)));
        } else {
            try {
                if (simpleDateFormat.parse(commandEvent.getArgs()) != null) {
                    commandEvent.reply(NASABot.apiClient.getPictureOfTheDay(commandEvent.getArgs()));
                } else {
                    commandEvent.reply(String.format("Unable to get POTD, please check your formatting: %s", this.getArgumentsString()));
                }
            } catch (ParseException e) {
                System.out.println(String.format("[POTD] Unable to parse date: %s", commandEvent.getArgs()));
                commandEvent.reply(String.format("Unable to get POTD, please check your formatting: %s", this.getArgumentsString()));
            }
        }
    }
}
