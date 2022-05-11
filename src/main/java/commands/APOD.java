package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;
import utils.ErrorLogging;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class APOD extends NASACommand {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public APOD() {
        super();
        this.name = "APOD";
        this.help = "Displays the Picture of the Day from yesterday, or the specified date.";
        this.arguments = "[date (yyyy-mm-dd)]";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        this.insertCommand(commandEvent);

        if (commandEvent.getArgs().isEmpty()) {
            commandEvent.reply(NASABot.NASAClient.getPictureOfTheDay(simpleDateFormat.format(System.currentTimeMillis() - 86400000)));
        } else {
            try {
                if (simpleDateFormat.parse(commandEvent.getArgs()) != null) {
                    commandEvent.reply(NASABot.NASAClient.getPictureOfTheDay(commandEvent.getArgs()));
                } else {
                    commandEvent.reply(String.format("Unable to get APOD, please check your formatting: %s", this.getArgumentsString()));
                }
            } catch (ParseException e) {
                ErrorLogging.handleError("APOD", "execute", String.format("Unable to parse date: %s", commandEvent.getArgs()), e);
                commandEvent.reply(String.format("Unable to get APOD, please check your formatting: %s", this.getArgumentsString()));
            }
        }
    }
}
