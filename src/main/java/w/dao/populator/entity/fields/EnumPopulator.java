package w.dao.populator.entity.fields;

import java.lang.reflect.Field;
import java.sql.Types;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import w.dao.populator.entity.FieldPopulator;

@FieldPopulator.AssignableFrom(type = Enum.class)
public class EnumPopulator<T> extends AbstractFieldPopulator<T> {

	private final int length;

	public EnumPopulator(Class<?> clazz, Field field, String name) {
		super(clazz, field, name, getEnumType(field));
		
		Column column = field.getAnnotation(Column.class);
		this.length = column==null?-1:column.length();
	}

	@Override
	public Object getValue(T entity) {
		Enum<?> value = (Enum<?>) super.getValue(entity);
		if (value == null) {
			return null;
		} else if (getSqlType() == Types.NUMERIC) {
			return getNumericValue(value);
		} else {
			String text = getStringValue(value);
			if (this.length < 0 || text.length() <= this.length) {
				return text;
			} else {
				return text.subSequence(0, this.length);
			}
		}
	}

	public Number getNumericValue(Enum<?> value) {
		return value.ordinal();
	}

	public String getStringValue(Enum<?> value) {
		return value.name();
	}

	private static int getEnumType(Field field) {
		Enumerated enumerated = field.getAnnotation(Enumerated.class);
		return enumerated == null || enumerated.value() == EnumType.ORDINAL ? Types.NUMERIC : Types.VARCHAR;
	}
}
