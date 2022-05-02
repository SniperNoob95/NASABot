package commands;

import java.text.MessageFormat;
import java.time.LocalDate;

import com.jagrosh.jdautilities.command.CommandEvent;

import utils.AstronomyCalc;

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
	
	public static enum MoonType {
		NEW("New"),
		WANING_CRES("Waning Crescent"),
		THIRD_QUART("Third Quarter"),
		WANING_GIBB("Waning Gibbous"),
		FULL("Full"),
		WAXING_GIBB("Waxing Gibbous"),
		FIRST_QUART("First Quarter"),
		WAXING_CRES("Waxing Crescent");
		
		private String phaseString;
		
		private MoonType(String phaseString) {
			this.phaseString = phaseString;
		}
		
		@Override
		public String toString() {
			return phaseString;
		}
		
	}
}
