package w.utils;

import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;

public class DateBuilderTest {

	@Test
	public void testStartOf() {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss,SSS");
		DateBuilder b = new DateBuilder(DateUtils.toDate(12, 2, 2017, 13, 45, 1));
		Assert.assertEquals("12/02/2017 13:45:01,000", sf.format(b.toDate()));
		
		Assert.assertEquals("01/02/2017 13:45:01,000", sf.format(b.startOfMonth().toDate()));
		
		Assert.assertEquals("01/02/2017 00:00:00,000", sf.format(b.startOfDay().toDate()));
	}
	
	@Test
	public void testStartOfYear() {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss,SSS");
		DateBuilder b = new DateBuilder(DateUtils.toDate(12, 2, 2017, 13, 45, 1));
		Assert.assertEquals("01/01/2017 00:00:00,000", sf.format(b.startOfDay().startOfYear().toDate()));
	}
		
	@Test
	public void testEndOf() {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss,SSS");
		DateBuilder b = new DateBuilder(DateUtils.toDate(12, 6, 2017, 13, 45, 1));
		
		Assert.assertEquals("30/06/2017 13:45:01,000", sf.format(b.endOfMonth().toDate()));
		
		Assert.assertEquals("30/06/2017 23:59:59,999", sf.format(b.endOfDay().toDate()));
		
		Assert.assertEquals("31/12/2017 23:59:59,999", sf.format(b.endOfYear().toDate()));
	}
	
	@Test
	public void testEndOfFeburary() {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss,SSS");
		DateBuilder b = new DateBuilder(DateUtils.toDate(12, 2, 2017));
		
		Assert.assertEquals("28/02/2017 00:00:00,000", sf.format(b.endOfMonth().toDate()));
		
		b = new DateBuilder(DateUtils.toDate(12, 2, 2016));
		Assert.assertEquals("29/02/2016 00:00:00,000", sf.format(b.endOfMonth().toDate()));
	}
	
	@Test
	public void testStartAndEndOfWeek() {
		SimpleDateFormat sf = new SimpleDateFormat("E dd/MM/yyyy HH:mm:ss,SSS");
		DateBuilder b = new DateBuilder(DateUtils.toDate(14, 6, 2017, 13, 45, 1));
		
		Assert.assertEquals("Wed 14/06/2017 13:45:01,000", sf.format(b.toDate()));
		
		Assert.assertEquals("Mon 12/06/2017 13:45:01,000", sf.format(b.startOfWeek().toDate()));
		
		Assert.assertEquals("Sun 18/06/2017 13:45:01,000", sf.format(b.endOfWeek().toDate()));
		
		Assert.assertEquals("Mon 12/06/2017 13:45:01,000", sf.format(b.startOfWeek().toDate()));
	}
}
