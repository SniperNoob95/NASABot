package commands;

import java.text.MessageFormat;
import java.time.LocalDate;

import com.jagrosh.jdautilities.command.CommandEvent;

import utils.AstronomyCalc;
import utils.AstronomyCalc.MoonType;

/**
 * Command to calculate the current moonphase.
 * 
 * @author Kyle Smith (kjsmita6)
 */
public class Moonphase extends NASACommand {

    public Moonphase() {
        super();
        this.name = "moonphase";
        this.help = "Displays the current phase of the moon.";
    }

    @Override
    protected void execute(CommandEvent event) {
        insertCommand(event);
        LocalDate now= LocalDate.now();

        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        MoonType phase = AstronomyCalc.getMoonphase(year, month, day);
        double days = AstronomyCalc.getDaysSinceNewMoon(year, month, day);
        event.reply(MessageFormat.format("The current moon phase is {0} ({1} days until new moon).", phase, (int) AstronomyCalc.MOON_PHASE_LENGTH - (int) days));
    }
}
