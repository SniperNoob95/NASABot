package utils;

import java.time.LocalDate;

import commands.Moonphase.MoonType;

/**
 * Utility class for miscellaneous astronomy calculations.
 * 
 * @author Kyle Smith (kjsmita6)
 *
 */
public final class AstronomyCalc {

	/** There was a new moon on January 10th, 2020. Use this as a reference. */
	public static final long MOON_REF = LocalDate.of(2022, 4, 30).toEpochDay();

	/** Moon cycles every 29.53 days */
	public static final double MOON_PHASE_LENGTH = 29.53059;

	/**
	 * Gets the moon phase for the given date. This date must be after 4/30/2022. Note that
	 * this calculation is not 100% accurate and may be off by a few days.
	 * 
	 * @param year The year
	 * @param month The month
	 * @param day The day
	 * @return Moon phase
	 */
	public static MoonType getMoonphase(int year, int month, int day) {
		int daysSinceNew = (int) getDaysSinceNewMoon(year, month, day);

		// Crescent/Gibbous moons
		if (daysSinceNew > 0 && daysSinceNew < 7) {
			return MoonType.WAXING_CRES;
		} else if (daysSinceNew > 7 && daysSinceNew < 15) {
			return MoonType.WAXING_GIBB;
		} else if (daysSinceNew > 15 && daysSinceNew < 22) {
			return MoonType.WANING_GIBB;
		} else if (daysSinceNew > 22 && daysSinceNew < 29.5) {
			return MoonType.WANING_CRES;
		}

		// Quarter moons
		if (daysSinceNew == 7) {
			return MoonType.FIRST_QUART;
		} else if (daysSinceNew == 22) {
			return MoonType.THIRD_QUART;
		}

		// Full and new
		if (daysSinceNew == 15) {
			return MoonType.FULL;
		} else {
			return MoonType.NEW;
		}
	}

	/**
	 * Gets the number of days since the last new moon. This can be used to figure
	 * out what the current phase is (new moon occurs every 29.53 days)
	 * 
	 * @param year Current year
	 * @param month Current month (1-12)
	 * @param day Current day (1-28/29/30/31)
	 * @return The number of days since the last new moon
	 */
	public static double getDaysSinceNewMoon(int year, int month, int day) {
		double refDiff = LocalDate.of(year, month, day).toEpochDay() - MOON_REF;
		assert refDiff > 0: "Must use a day after 4/30/2022";
		double cycleCount = refDiff / MOON_PHASE_LENGTH;
		return (cycleCount % 1) * MOON_PHASE_LENGTH;
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
