package w.cron;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import w.utils.DateUtils;

public class DayCronTest {

	@Test
	public void twoDaysOfMonth() {
		DayCron cron = new DayCron(new int[] { 1, 4 }, null, null);
		Assert.assertEquals("1,4 * *", cron.getExpression());

		Date next = cron.next(DateUtils.getDate(15,1,2014));
		Assert.assertEquals(DateUtils.getDate(1,2,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(4,2,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(1,3,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(4,3,2014), next);
	}

	@Test
	public void twoDaysOfWeek() {
		DayCron cron = new DayCron(null, null, new int[] { 1, 4 }); // MON and THU
		Assert.assertEquals("* * MON,THU", cron.getExpression());

		Date next = cron.next(DateUtils.getDate(15,1,2014)); // WED
		Assert.assertEquals(DateUtils.getDate(16,1,2014), next); // THU

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(20,1,2014), next); // MON

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(23,1,2014), next); // THU
	}

	@Test
	public void weekEnds() {
		DayCron cron = new DayCron(null, null, new int[] { 6, 7 });
		Assert.assertEquals("* * SAT,SUN", cron.getExpression());

		Date next = cron.next(DateUtils.getDate(15,1,2014)); // WED
		Assert.assertEquals(DateUtils.getDate(18,1,2014), next); // SAT

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(19,1,2014), next); // SUN

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(25,1,2014), next); // SAT
	}

	@Test
	public void twoMonthOfYear() {
		DayCron cron = new DayCron(null, new int[] { 1, 4 }, null); // JAN and APR
		Assert.assertEquals("* JAN,APR *", cron.getExpression());

		Date next = cron.next(DateUtils.getDate(29,1,2014));
		Assert.assertEquals(DateUtils.getDate(30,1,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(31,1,2014), next);

		for (int i = 1; i < 31; i++) {
			next = cron.next(next);
			Assert.assertEquals(DateUtils.getDate(i, 4, 2014), next);
		}

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(1,1,2015), next);
	}

	@Test
	public void lastDayOfMonth() {
		DayCron cron = new DayCron(new int[] { -1 }, null, null);
		Assert.assertEquals("LAST * *", cron.getExpression());

		Date next = cron.next(DateUtils.getDate(15,1,2014));
		Assert.assertEquals(DateUtils.getDate(31,1,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(28,2,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(31,3,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(30,4,2014), next);
	}

	@Test
	public void lastFirdayOfMonth() {
		DayCron cron = new DayCron(new int[] { -1 }, new int[] { 1, 2, 3, 4, 5, 6, 7 }, new int[] { 5 });
		Assert.assertEquals("LAST JAN-JUL FRI", cron.getExpression());

		Date next = cron.next(DateUtils.getDate(15,1,2014));
		Assert.assertEquals(DateUtils.getDate(31,1,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(28,2,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(28,3,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(25,4,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(30,5,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(27,6,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(25,7,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(30,1,2015), next);
	}

	@Test
	public void lastAndMidDayOfOtherMonth() {
		DayCron cron = new DayCron(new int[] { 15, -1 }, new int[] { 1, 4, 7, 10 }, null);
		Assert.assertEquals("LAST,15 JAN,APR,JUL,OCT *", cron.getExpression());

		Date next = cron.next(DateUtils.getDate(15,1,2014));
		Assert.assertEquals(DateUtils.getDate(31,1,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(15,4,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(30,4,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(15,7,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(31,7,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(15,10,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(31,10,2014), next);
	}

	@Test
	public void nextFriay13th() {
		DayCron cron = new DayCron(new int[] { 13 }, null, new int[] { 5 });
		Assert.assertEquals("13 * FRI", cron.getExpression());

		Date next = cron.next(DateUtils.getDate(15,1,2014));
		Assert.assertEquals(DateUtils.getDate(13,6,2014), next);

		next = cron.next(next);
		Assert.assertEquals(DateUtils.getDate(13,2,2015), next);
	}

	@Test
	public void impossible() {
		DayCron cron = new DayCron(new int[] { 31 }, new int[] { 2 }, null);
		Assert.assertEquals("31 FEB *", cron.getExpression());

		Date next = cron.next(DateUtils.getDate(15,1,2014));
		Assert.assertNull(next);
	}

	@Test
	public void testEquals() {
		DayCron c1 = new DayCron(new int[] { 15, -1 }, new int[] { 1, 2, 3, 4 }, null);
		DayCron c2 = new DayCron("-1,15 1,2,3,4 *");
		DayCron c3 = new DayCron("15,LAST JAN-APR *");
		Assert.assertEquals(c1, c2);
		Assert.assertEquals(c1, c3);
		Assert.assertEquals(c2, c3);
	}
}
