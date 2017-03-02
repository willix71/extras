package w.utils;

import java.util.Calendar;
import java.util.Date;

public class DateBuilder {

	private Calendar instance = Calendar.getInstance();
	
	public DateBuilder() {}
	
	public DateBuilder(Date date) {
		this.instance.setTime(date);
	}

	public DateBuilder yesterday() {
		instance.add(Calendar.DAY_OF_MONTH, -1);
		return this;
	}
	
	public DateBuilder tomorrow() {
		instance.add(Calendar.DAY_OF_MONTH, +1);
		return this;
	}
	
	public DateBuilder addDay(int i) {
		instance.add(Calendar.DAY_OF_MONTH, i);
		return this;
	}
	
	public DateBuilder addMonth(int i) {
		instance.add(Calendar.MONTH, i);
		return this;
	}
	
	public DateBuilder addYear(int i) {
		instance.add(Calendar.YEAR, i);
		return this;
	}
	
	public DateBuilder startOfWeek() {
		instance.setFirstDayOfWeek(Calendar.MONDAY);
		instance.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return this;
	}
	
	public DateBuilder endOfWeek() {
		instance.setFirstDayOfWeek(Calendar.MONDAY);
		instance.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return this;
	}
	
	public DateBuilder startOfYear() {
		instance.set(Calendar.MONTH, Calendar.JANUARY);
		instance.set(Calendar.DAY_OF_MONTH, 1);
		return this;
	}
	
	public DateBuilder endOfYear() {
		instance.set(Calendar.MONTH, Calendar.DECEMBER);
		instance.set(Calendar.DAY_OF_MONTH, 31);
		return this;
	}
	
	public DateBuilder startOfMonth() {
		instance.set(Calendar.DAY_OF_MONTH, 1);
		return this;
	}
	
	public DateBuilder endOfMonth() {
		instance.set(Calendar.DAY_OF_MONTH,instance.getActualMaximum(Calendar.DAY_OF_MONTH));
		return this;
	}
	
	public DateBuilder startOfDay() {
		instance.set(Calendar.MILLISECOND,0);
		instance.set(Calendar.SECOND,0);
		instance.set(Calendar.MINUTE,0);
		instance.set(Calendar.HOUR_OF_DAY,0);
		return this;
	}
	
	public DateBuilder endOfDay() {
		instance.set(Calendar.MILLISECOND,999);
		instance.set(Calendar.SECOND,59);
		instance.set(Calendar.MINUTE,59);
		instance.set(Calendar.HOUR_OF_DAY,23);
		return this;
	}
	
	public Date toDate() {
		return instance.getTime();
	}
	
	public static DateBuilder from(Date d) {
		return new DateBuilder(d);
	}
}
