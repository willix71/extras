package w.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class DateUtilsTest {
   final String DAY;
   final String MONTH;
   final String YEAR;
   final String TIME = " 00:00:00.000";
   public DateUtilsTest() {
      Calendar c = Calendar.getInstance();
      int i = c.get(Calendar.YEAR);
      YEAR = i + "";
      i = c.get(Calendar.MONTH) + 1;
      MONTH = i < 10 ? "0" + i: "" + i;
      i = c.get(Calendar.DAY_OF_MONTH);
      DAY = i < 10 ? "0" + i: "" + i;
   }
   
   @Test
   public void testDate() {
      SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
      
      // no args
      Assert.assertEquals(YEAR+MONTH+DAY+TIME, sf.format(DateUtils.getDate()));
      
      Assert.assertEquals(YEAR+MONTH+"15"+TIME, sf.format(DateUtils.getDate(15)));
      Assert.assertEquals(YEAR+"1215"+TIME, sf.format(DateUtils.getDate(15,12)));
      Assert.assertEquals("20130401"+TIME, sf.format(DateUtils.getDate(1, 4,2013)));
      Assert.assertEquals("20000229"+TIME, sf.format(DateUtils.getDate(29,2,2000)));
      Assert.assertEquals("20001231"+TIME, sf.format(DateUtils.getDate(31,12,2000)));
      
      Assert.assertEquals("20130601 23:00:00.000", sf.format(DateUtils.getDate(1,6,2013,23)));
      Assert.assertEquals("20130601 00:59:00.000", sf.format(DateUtils.getDate(1,6,2013,0,59)));
      Assert.assertEquals("20130601 23:00:59.000", sf.format(DateUtils.getDate(1,6,2013,23,0,59)));
   }
   
   @Test(expected=IllegalArgumentException.class)
   @Ignore // we only doing simple validations...
   public void test30OfFeb() {
      DateUtils.getDate(2013,2,30); // illegal date
   }
   
   @Test(expected=IllegalArgumentException.class)
   public void badYearDate() {
      DateUtils.getDate(-1);
   }

   @Test(expected=IllegalArgumentException.class)
   public void badMinMonthDate() {
      DateUtils.getDate(1,0);
   }
   
   @Test(expected=IllegalArgumentException.class)
   public void badMaxMonthDate() {
      DateUtils.getDate(1,13);
   }
   
   @Test(expected=IllegalArgumentException.class)
   public void badMinDayDate() {
      DateUtils.getDate(0);
   }
   
   @Test(expected=IllegalArgumentException.class)
   public void badMaxDayDate() {
      DateUtils.getDate(32);
   }
   
   @Test(expected=IllegalArgumentException.class)
   public void badMinHour() {
      DateUtils.getDate(31,1,2012,-1);
   }
   
   @Test(expected=IllegalArgumentException.class)
   public void badMaxHour() {
      DateUtils.getDate(31,1,2012,24);
   }
   
   @Test(expected=IllegalArgumentException.class)
   public void badMinMinute() {
      DateUtils.getDate(2012,1,31,12,-1);
   }
   
   @Test(expected=IllegalArgumentException.class)
   public void badMaxMinute() {
      DateUtils.getDate(2012,1,31,12,60);
   }   
}
