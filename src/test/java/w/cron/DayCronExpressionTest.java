package w.cron;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class DayCronExpressionTest {

	@Test
	public void testFirstDay() {
		DayCron cron = new DayCron("1 * *");

		equals(cron.getDaysInMonth(), 1);
		Assert.assertNull(cron.getMonth());
		Assert.assertNull(cron.getDaysInWeek());
	}

	@Test
	public void testLastDay() {
		DayCron cron = new DayCron("LAST * *");

		equals(cron.getDaysInMonth(), -1);
		Assert.assertNull(cron.getMonth());
		Assert.assertNull(cron.getDaysInWeek());
	}

	@Test
	public void testLastFriday() {
		DayCron cron = new DayCron("LAST * 5");

		equals(cron.getDaysInMonth(), -1);
		equals(cron.getDaysInWeek(), 5);
		Assert.assertNull(cron.getMonth());
	}

	@Test
	public void testDays1to5() {
		DayCron cron = new DayCron("1-5 * *");

		equals(cron.getDaysInMonth(), 1, 2, 3, 4, 5);
		Assert.assertNull(cron.getMonth());
		Assert.assertNull(cron.getDaysInWeek());
	}

	@Test
	public void testMonday() {
		DayCron cron = new DayCron("* * MON");

		equals(cron.getDaysInWeek(), 1);
		Assert.assertNull(cron.getMonth());
		Assert.assertNull(cron.getDaysInMonth());
	}

	@Test
	public void testTuesdayToFriday() {
		DayCron cron = new DayCron("* * TUE-FRI");

		equals(cron.getDaysInWeek(), 2, 3, 4, 5);
		Assert.assertNull(cron.getMonth());
		Assert.assertNull(cron.getDaysInMonth());
	}

	@Test
	public void testWeekEnds() {
		DayCron cron = new DayCron("* * SAT,SUN");

		equals(cron.getDaysInWeek(), 6, 7);
		Assert.assertNull(cron.getMonth());
		Assert.assertNull(cron.getDaysInMonth());
	}

	@Test
	public void testEveryOtherDay() {
		DayCron cron = new DayCron("* * */2");

		equals(cron.getDaysInWeek(), 1, 3, 5, 7);
		Assert.assertNull(cron.getMonth());
		Assert.assertNull(cron.getDaysInMonth());
	}

	@Test
	public void testMondayToSunday() {
		DayCron cron = new DayCron("* * 1/2");

		equals(cron.getDaysInWeek(), 1, 3, 5, 7);
		Assert.assertNull(cron.getMonth());
		Assert.assertNull(cron.getDaysInMonth());
	}

	@Test
	public void testTuesdayToSarturday() {
		DayCron cron = new DayCron("* * 2/2");

		equals(cron.getDaysInWeek(), 2, 4, 6);
		Assert.assertNull(cron.getMonth());
		Assert.assertNull(cron.getDaysInMonth());
	}

	@Test
	public void testOtheFirstHalfMonth() {
		DayCron cron = new DayCron("* JAN-JUN/2 *");

		equals(cron.getMonth(), 1, 3, 5);
		Assert.assertNull(cron.getDaysInWeek());
		Assert.assertNull(cron.getDaysInMonth());
	}

	@Test
	public void testQuaterly() {
		DayCron cron = new DayCron("* 2/3 *");

		equals(cron.getMonth(), 2, 5, 8, 11);
		Assert.assertNull(cron.getDaysInWeek());
		Assert.assertNull(cron.getDaysInMonth());
	}

	@Test
	public void testEmpty() {
		DayCron cron = new DayCron("*");
		DayCron cron2 = new DayCron("* * *");

		Assert.assertEquals(cron2, cron);
	}

	public void equals(int actual[], int... expected) {
		Assert.assertEquals(Arrays.toString(expected), Arrays.toString(actual));
	}
}
