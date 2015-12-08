package w.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

   private static final int[] FIELDS = {
	   	Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR, // date fields 
        Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, // time fields 
			Calendar.MILLISECOND };

	private static final int[] MAX = { 31, 12, Integer.MAX_VALUE, 23, 59, 59, 999 };
	private static final int[] MIN = { 1, 1, 0, 0, 0, 0, 0 };

	/**
	 * @deprecated use toDate
	 */
	@Deprecated
	public static Date getDate(int... values) {
		return getDate(values);
	}

	/**
	 * Returns a date according to passed in date and time values.
	 * 
	 * @param values
	 *            (day, month, year, hour, minutes, seconds)
	 * @return
	 */
	public static Date toDate(int... values) {
		// perform quick checks
		for (int i = 0; i < values.length && i < MAX.length; i++) {
			if (values[i] < MIN[i] || values[i] > MAX[i]) {
				throw new IllegalArgumentException("Value " + i + " is out of range");
			}
		}

		Calendar cal = Calendar.getInstance();

		// do not overwrite date fields if no values are passed
		// i.e. use today's date
		int index = 3;

		// Set the passed in fields
		if (values.length > 0) {
			cal.set(FIELDS[0], values[0]); // start with the day of month
			if (values.length > 1) {
            cal.set(FIELDS[1], values[1] - 1); // Month is zero based in Calendar!!!
				if (values.length > 2) {
					index = 2;

					// Copy other fields
					for (; index < values.length && index < FIELDS.length; index++) {
						cal.set(FIELDS[index], values[index]);
					}
				}
			}
		}

		// Clear the remaining fields
		for (; index < FIELDS.length; index++) {
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

		Calendar now = Calendar.getInstance();
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
