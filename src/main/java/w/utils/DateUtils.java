package w.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

   private static final int[] FIELDS = {
	   	Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR, // date fields 
        Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, // time fields 
			Calendar.MILLISECOND };

	/**
	 * Returns a date according to passed in date and time values.
	 * 
	 * @param values
	 *            (day, month, year, hour, minutes, seconds)
	 * @return
	 */
	public static Date toDate(int... values) {
		Calendar cal = Calendar.getInstance();
		
		if (values.length > 1) values[1]--; // Month is zero based in Calendar!!!

		// perform simple validations	
		for (int index = 0; index < values.length && index < FIELDS.length; index++) {
			if (values[index] < cal.getMinimum(FIELDS[index]) || values[index] > cal.getMaximum(FIELDS[index])) {
				throw new IllegalArgumentException("Value " + index + " is out of range");
			}
		}

		// Set the passed in fields
		for (int index = 0; index < values.length && index < FIELDS.length; index++) {
			cal.set(FIELDS[index], values[index]);
		}

		// Clears the remaining time fields
		for (int index = Math.max(3, values.length); index < FIELDS.length; index++) {
			cal.set(FIELDS[index], 0);
		}

		return cal.getTime();
	}

	/**
	 * Returns a date according to passed in string. 
	 * 
	 * It matches a format dd.MM.YYYY HH:mm:ss however any parts can be missing and default values are used.
	 * Date and time should be separated by a space
	 * Date components should be separated by one of ./-
	 * Time components should be separated by one of :.
	 * Default values come from the current day for the date components and a 0 for time components.
	 * 
	 * @param dateString
	 * @return
	 * @throws IllegalArgumentException
	 * @throws NumberFormatException
	 */
	public static Date toDate(String dateString) throws IllegalArgumentException, NumberFormatException {
		if (dateString == null || dateString.length() == 0) {
			throw new IllegalArgumentException("invalid date (can not parse empty date)");
		}

		int hour = 0;
		int minute = 0;
		int second = 0;
		String[] parts = dateString.split(" ");
		if (parts.length > 1) {
			try {
				// parsing time
				String fields[] = parts[1].split("[:.]");
				if (fields.length > 0) {
					hour = Integer.parseInt(fields[0]);
					if (fields.length > 1) {
						minute = Integer.parseInt(fields[1]);
						if (fields.length > 2) {
							second = Integer.parseInt(fields[2]);
						}
					}
				}
			} catch (NumberFormatException e) {
				throw new NumberFormatException("invalid time (Can not parse " + parts[1] + ")");
			}
		}

		String fields[] = parts.length == 0 ? new String[] {} : parts[0].split("[/.-]");
		try {
			Calendar now = Calendar.getInstance();
			int d = fields.length > 0 && fields[0].length()>0 ? Integer.parseInt(fields[0]) : now.get(Calendar.DAY_OF_MONTH);
			int m = fields.length > 1 && fields[1].length()>0 ? Integer.parseInt(fields[1]) - 1 : now.get(Calendar.MONTH);
			int y = fields.length > 2 && fields[2].length() > 0 ? Integer.parseInt(fields[2]) : now.get(Calendar.YEAR);
			GregorianCalendar c = new GregorianCalendar(y, m, d, hour, minute, second);
			c.set(Calendar.MILLISECOND, 0);
			return c.getTime();
		} catch (NumberFormatException e) {
			throw new NumberFormatException("invalid date (Can not parse " + parts[0] + ")");
		}
	}
}
