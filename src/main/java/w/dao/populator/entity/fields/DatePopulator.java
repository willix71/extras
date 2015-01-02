package w.dao.populator.entity.fields;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Date;

import javax.persistence.Temporal;

import w.dao.populator.entity.FieldPopulator;

@FieldPopulator.AssignableFrom(type = Date.class)
public class DatePopulator<T> extends AbstractFieldPopulator<T> {

	public DatePopulator(Class<?> clazz, Field field, String name) {
		super(clazz, field, name,getDateType(field));
	}

	@Override
	public Object getValue(T entity) {
		Date d = (Date) super.getValue(entity);
		return d == null ? null : new java.sql.Timestamp(d.getTime());
	}

	private static int getDateType(Field field) {
		int type = Types.DATE;
		Temporal temporal = field.getAnnotation(Temporal.class);
		if (temporal != null) {
			switch (temporal.value()) {
			case TIME:
				type = Types.TIME;
				break;
			case TIMESTAMP:
				type = Types.TIMESTAMP;
				break;
			default:
			}
		}
		return type;
	}
}
