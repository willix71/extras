package w.dao.populator.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public interface FieldPopulator<T> {
   
   @Target({ ElementType.TYPE })
   @Retention(RetentionPolicy.RUNTIME)
   public static @interface AssignableFrom {
      Class<?> type();
   }
   
   String getName();

   int getSqlType();

   Field getField();

   Object getValue(T entity);
   
   void setValue(T entity, Object o);
}