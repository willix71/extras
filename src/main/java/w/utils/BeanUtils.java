package w.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class BeanUtils {
	static final IBeanUtils simpleBeanUtils = new SimpleBeanUtils();
	static final IBeanUtils fullBeanUtils = new NestedBeanUtils(true, new CollectionBeanUtils(true, simpleBeanUtils));
	
    public static Object getSimpleValue(Object o, String propertyName) {    	
    	return simpleBeanUtils.getPropertyValue(o, propertyName);
    }
       
    public static void setNestedCollectionValue(Object o, String propertyName, Object propertyValue) {
    	simpleBeanUtils.setPropertyValue(o, propertyName, propertyValue);    	
    }
    
    public static Object getPropertyValue(Object o, String propertyName) {
    	return fullBeanUtils.getPropertyValue(o, propertyName); 
    }

    public static void setPropertyValue(Object o, String propertyName, Object propertyValue) {
    	fullBeanUtils.setPropertyValue(o, propertyName, propertyValue);
    }


    /**
     * Returns a method that looks like a getter method for the passed field. To do so, the method must be public and
     * the name must be 'get' or 'is' + the field's name (case insensitive).
     */
    protected Method getGetterMethod(final Class<?> clazz, final Field field) {
       String fieldName = field.getName().toUpperCase();
       boolean isBoolean = field.getType() == Boolean.class || field.getType() == Boolean.TYPE;
       for (Method m : clazz.getMethods()) {
          if (Modifier.isPublic(m.getModifiers())) {
             String mName = m.getName().toUpperCase();
             if (mName.equals("GET" + fieldName) || (isBoolean && mName.equals("IS" + fieldName))) {
                return m;
             }
          }
       }

       return null;
    }
    
    /**
     * Returns a method that looks like a setter method for the passed field. To do so, the method must be public and
     * the name must be 'set' + the field's name (case insensitive).
     */
    protected Method getSetterMethod(final Class<?> clazz, final Field field) {
       String fieldName = field.getName().toUpperCase();
       for (Method m : clazz.getMethods()) {
          if (Modifier.isPublic(m.getModifiers())) {
             String mName = m.getName().toUpperCase();
             if (mName.equals("SET" + fieldName)) {
                return m;
             }
          }
       }

       return null;
    }
}
