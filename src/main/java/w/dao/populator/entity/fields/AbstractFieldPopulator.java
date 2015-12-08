package w.dao.populator.entity.fields;

import java.lang.reflect.Field;

import w.dao.populator.entity.FieldPopulator;

public class AbstractFieldPopulator<T> implements FieldPopulator<T> {

	private final Field field;
	private final String name;
	private final int type;

	public AbstractFieldPopulator(Class<?> clazz, Field field, String name, int type) {
		this.name = name;
		this.type = type;
		this.field = field;

		this.field.setAccessible(true); // force accessibility
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getSqlType() {
		return this.type;
	}

	@Override
	public Field getField() {
		return field;
	}

	@Override
	public Object getValue(T entity) {
		try {
			return field.get(entity);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(name + " getValue error for " + entity, e);
		}
	}

	@Override
	public void setValue(T entity, Object o) {
		try {
			field.set(entity, o);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(name + " setValue error for " + entity, e);
		}
	}

}
