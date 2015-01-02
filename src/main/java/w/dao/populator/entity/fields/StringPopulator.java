package w.dao.populator.entity.fields;

import java.lang.reflect.Field;
import java.sql.Types;

import javax.persistence.Column;

import w.dao.populator.entity.FieldPopulator;

@FieldPopulator.AssignableFrom(type = CharSequence.class)
public class StringPopulator<T> extends AbstractFieldPopulator<T> {

   private final int length;

   public StringPopulator(Class<?> clazz, Field field, String name) {
      super(clazz, field, name, Types.VARCHAR);

      Column column = field.getAnnotation(Column.class);
      this.length = column == null ? -1 : column.length();
   }

   @Override
   public Object getValue(T entity) {
      CharSequence value = (CharSequence) super.getValue(entity);
      if (value == null || this.length < 0 || value.length() <= this.length) {
         return value;
      } else {
         return value.subSequence(0, this.length);
      }
   }
}
