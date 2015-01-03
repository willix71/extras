package w.dao.populator.entity.fields;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class PrimitiveFieldPopulator<T> extends AbstractFieldPopulator<T> {

   /**
    * Default SQL type for the basic Java types
    */
   public static final Map<Class<?>, Integer> DEFAULT_SQL_TYPES = ImmutableMap.<Class<?>, Integer> builder() //
           .put(Boolean.class, Types.NUMERIC) //
           .put(Boolean.TYPE, Types.NUMERIC) //
           .put(Byte.class, Types.NUMERIC) //
           .put(Byte.TYPE, Types.NUMERIC) //
           .put(Short.class, Types.NUMERIC) //
           .put(Short.TYPE, Types.NUMERIC) //
           .put(Integer.class, Types.NUMERIC) //
           .put(Integer.TYPE, Types.NUMERIC) //
           .put(Long.class, Types.NUMERIC) //
           .put(Long.TYPE, Types.NUMERIC) //
           .put(Float.class, Types.REAL) //
           .put(Float.TYPE, Types.REAL) // no not use DECIMAL because INFINITY can't be converted to DECIMAL but REAL can
           .put(Double.class, Types.DOUBLE) //
           .put(Double.TYPE, Types.DOUBLE) // no not use DECIMAL because INFINITY can't be converted to DECIMAL but DOUBLE can
           .put(Character.class, Types.CHAR) //
           .put(Character.TYPE, Types.CHAR) //
           .put(BigInteger.class, Types.NUMERIC) //
           .put(BigDecimal.class, Types.DECIMAL) //
           .build();
   
   public static final int getPrimitiveType(Class<?> clazz, Field field, String name) {
      Integer type = DEFAULT_SQL_TYPES.get(field.getType());
      if (type == null) {
         throw new IllegalArgumentException("Can't get primitive type for field " + field + " of class "+ clazz);
      }
      return type.intValue();
   }
   public PrimitiveFieldPopulator(Class<?> clazz, Field field, String name) {
      super(clazz, field, name, getPrimitiveType(clazz, field, name));
   }
}
