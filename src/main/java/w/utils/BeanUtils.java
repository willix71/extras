package w.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class BeanUtils {

    private static Map<String, Method> GETTERS = new HashMap<String, Method>();
    private static Map<String, Method> SETTERS = new HashMap<String, Method>();
    private static Map<String, Map<Class<?>, Method>> SETTERS2 = new HashMap<String, Map<Class<?>, Method>>();

    public static Object getPropertyValue(Object o, String propertyName) {
        String name = o.getClass().getName() + "." + propertyName;
        try {
            Method m = GETTERS.get(name);
            if (m == null) {
                m = o.getClass().getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
                GETTERS.put(name, m);
            }
            return m.invoke(o);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke getter for " + name, e);
        }
    }

    public static Object setPropertyValue(Object o, String propertyName, Object propertyValue) {
        String name = o.getClass().getName() + "." + propertyName;
        try {
            Method m = SETTERS.get(name);
            if (m == null) {
                String mName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                for (Class<?> vc = propertyValue.getClass(); m == null && vc != null; vc = vc.getSuperclass()) {
                    try {
                        m = o.getClass().getMethod(mName, vc);
                    } catch (NoSuchMethodException e) {
                        // loop
                    }
                }
                if (m == null) {
                    throw new NoSuchMethodException("Failed to find method named " + mName + " on " + o.getClass().getName());
                }
                SETTERS.put(name, m);
            }
            return m.invoke(o, propertyValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke setter for " + name, e);
        }
    }

    public static void setPropertyValue2(Object o, String propertyName, Object propertyValue) {
        String name = o.getClass().getName() + "." + propertyName;
        try {
            Map<Class<?>, Method> ms = SETTERS2.get(name);
            if (ms == null) {
                ms = fillSetters2(o, propertyName);
            }

            if (ms.size() == 0) {
                throw new NoSuchMethodException("Failed to find setter for " + name);
            }
            if (ms.size() == 1) {
                ms.values().iterator().next().invoke(o, propertyValue);
            } else {
                if (propertyValue == null) {
                    throw new RuntimeException("Failed to choose setter for " + name + " because argument is null");
                }
                for (Class<?> vc = propertyValue.getClass(); vc != null; vc = vc.getSuperclass()) {
                    Method m = ms.get(vc);
                    if (m!=null) {
                        m.invoke(o, propertyValue);
                    }
                }
                throw new NoSuchMethodException("Failed to match setter for " + name + " to " + propertyValue.getClass());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke setter for " + name, e);
        }

    }

    private static Map<Class<?>, Method> fillSetters2(Object o, String propertyName) {
        String mName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Map<Class<?>, Method> ms = new HashMap<Class<?>, Method>();
        for (Method m : o.getClass().getMethods()) {
            if (mName.equals(m.getName()) && m.getParameterTypes().length == 1) {
                ms.put(m.getParameterTypes()[0], m);
            }
        }
        String name = o.getClass().getName() + "." + propertyName;
        SETTERS2.put(name, ms);
        return ms;
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
