package w.cron;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * A simplified cron that finds a next calendar day based on dayOfMonth, monthInYear and dayOdWeek.
 * 
 * @author willy
 *
 */
public class DayCron {

	public static final String LAST = "LAST";

	private static final int LAST_VALUE = -1;

	public static final String[] DAYS_OF_WEEK = { null, "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN" };

	public static final String[] MONTH_OF_YEAR = { null, "JAN", "FEB", "MAR", "APR", "MAI", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

	private final int daysInWeek[];

	private final int daysInMonth[];

	private final int month[];

	private final String expression;

	public DayCron(int[] daysInMonth, int[] month, int[] daysInWeek) {
		super();

		this.daysInMonth = daysInMonth;
		if (this.daysInMonth != null) {
			Arrays.sort(this.daysInMonth);
		}

		this.month = month;
		if (this.month != null) {
			Arrays.sort(this.month);
		}

		this.daysInWeek = daysInWeek;
		if (this.daysInWeek != null) {
			Arrays.sort(this.daysInWeek);
		}

		this.expression = format();
	}

	public DayCron(String expression) {
		if (expression == null || expression.trim().length() == 0) {
			throw new IllegalArgumentException("DayCron should have at least one field {daysInMonth[, month[, daysInWeek]]}");
		}

		String[] fields = expression.split(" ");

		this.daysInMonth = fields.length > 0 ? parseExpression(fields[0], 1, 31, null) : null;
		this.month = fields.length > 1 ? parseExpression(fields[1], 1, 12, MONTH_OF_YEAR) : null;
		this.daysInWeek = fields.length > 2 ? parseExpression(fields[2], 1, 7, DAYS_OF_WEEK) : null;

		this.expression = expression;
	}

	public int[] getDaysInWeek() {
		return this.daysInWeek;
	}

	public int[] getDaysInMonth() {
		return this.daysInMonth;
	}

	public int[] getMonth() {
		return this.month;
	}

	public String getExpression() {
		return this.expression;
	}

	public Date next(Date input) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(input);

		for (int i = 0; i < 3654; i++) { // just over ten years
			cal.add(Calendar.DAY_OF_MONTH, 1);

			if (this.month != null && !contains(cal.get(Calendar.MONTH) + 1, this.month)) {
				continue;
			}

			if (this.daysInWeek != null) {
				int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
				if (day == 0) {
					day = 7; // SUNDAY is 7, not 0
				}
				if (!contains(day, this.daysInWeek)) {
					continue;
				}
			}

			if (this.daysInMonth != null) {
				if (!contains(cal.get(Calendar.DAY_OF_MONTH), this.daysInMonth)) {
					if (!contains(LAST_VALUE, this.daysInMonth)) {
						continue;
					}
					int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
					int lastInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					if ((this.daysInWeek == null && dayOfMonth != lastInMonth) || (this.daysInWeek != null && dayOfMonth < lastInMonth - 6)) {
						continue;
					}
				}
			}

			return cal.getTime();
		}

		return null;
	}
	
	private boolean contains(int value, int[] values) {
		// same as guava Ints.contains(values, value)
		for (int value2 : values) {
			if (value2 == value) {
				return true;
			}
		}
		return false;
	}

	private int[] parseExpression(String field, int min, int max, String[] text) {
		if (field.equals("*")) {
			return null;
		}

		if (field.contains(",")) {
			String[] list = field.split(",");
			int[] values = new int[list.length];
			for (int i = 0; i < list.length; i++) {
				values[i] = parseValue(list[i], min, max, text);
			}
			Arrays.sort(values);
			return values;
		}

		if (!field.contains("-") && !field.contains("/")) {
			// single value
			return new int[] { parseValue(field, min, max, text) };
		}

		int step = 1;
		if (field.contains("/")) {
			String[] divider = field.split("/");
			if (divider.length != 2) {
				throw new IllegalArgumentException("Could not parse " + field);
			}
			try {
				step = Integer.parseInt(divider[1]);
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException("Could not parse " + field, nfe);
			}
			field = divider[0];
		}

		int from = min;
		int to = max;
		if (!field.contains("-")) {
			if (!field.equals("*")) {
				from = parseValue(field, min, max, text);
			}
		} else {
			String[] range = field.split("-");
			if (range.length != 2) {
				throw new IllegalArgumentException("Could not parse " + field);
			}
			if (!range[0].isEmpty()) {
				from = parseValue(range[0], min, max, text);
			}
			if (!range[1].isEmpty()) {
				to = parseValue(range[1], min, max, text);
			}
		}

		if (min > max) {
			throw new IllegalArgumentException(min + " is bigger than " + max);
		}

		// create values
		int index = 0;
		int[] values = new int[(to - from) / step + 1];
		for (int i = from; i <= to; i += step) {
			values[index++] = i;
		}
		return values;
	}

	private int parseValue(String value, int min, int max, String[] text) {
		try {
			int v = Integer.parseInt(value);
			if (v == LAST_VALUE && text == null) {
				return LAST_VALUE;
			} else if (v >= min && v <= max) {
				return v;
			}

		} catch (NumberFormatException nfe) {
			if (LAST.equals(value)) {
				return LAST_VALUE;
			} else if (text != null) {
				// lookup index of text
				for (int i = 0; i < text.length; i++) {
					if (value.equals(text[i])) {
						return i;
					}
				}
			}
		}

		throw new IllegalArgumentException("Illegal value " + value);
	}

	private String format() {
		StringBuilder sb = new StringBuilder();
		format(sb, this.daysInMonth, null);
		sb.append(" ");
		format(sb, this.month, MONTH_OF_YEAR);
		sb.append(" ");
		format(sb, this.daysInWeek, DAYS_OF_WEEK);
		return sb.toString();
	}

	private void format(StringBuilder sb, int[] values, String[] text) {
		if (values == null) {
			sb.append("*");
		} else {
			sb.append(format(values[0], text));
			if (values.length > 2 && (values[values.length - 1] - values[0] + 1) == values.length) {
				sb.append("-").append(format(values[values.length - 1], text));
			} else {
				for (int i = 1; i < values.length; i++) {
					sb.append(",").append(format(values[i], text));
				}
			}
		}
	}

	private String format(int index, String[] text) {
		if (index == LAST_VALUE) {
			return LAST;
		} else if (text != null) {
			return text[index];
		} else {
			return String.valueOf(index);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(this.daysInMonth);
		result = prime * result + Arrays.hashCode(this.daysInWeek);
		result = prime * result + Arrays.hashCode(this.month);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DayCron other = (DayCron) obj;
		if (!Arrays.equals(this.daysInMonth, other.daysInMonth)) {
			return false;
		}
		if (!Arrays.equals(this.daysInWeek, other.daysInWeek)) {
			return false;
		}
		if (!Arrays.equals(this.month, other.month)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.expression;
	}
}
