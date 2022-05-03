import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import utils.AstronomyCalc;
import utils.AstronomyCalc.MoonType;

class MoonphaseTest {

	@Test
	void testCres() {
		int year = 2022;
		int month = 5;
		int day = 7;
		
		MoonType phase = AstronomyCalc.getMoonphase(year, month, day);
		assertEquals(MoonType.FIRST_QUART, phase);
	}
	
	@Test
	void testFull() {
		int year = 2028;
		int month = 5;
		int day = 9;
		MoonType phase = AstronomyCalc.getMoonphase(year, month, day);
		assertEquals(MoonType.FULL, phase);
	}
	
}
