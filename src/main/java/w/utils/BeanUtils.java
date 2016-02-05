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

    public static Object getNestedValue(Object o, String propertyName) {    	
    	if (propertyName.contains(".")) {
    		for(String name : propertyName.split("\\.")) {
    			o = getPropertyValue(o, name);
    			if (o==null) return null;
    		}
    		return o;
    	} else {
    		return getPropertyValue(o, propertyName);
    	}
    }
       
    public static void setNestedValue2(Object o, String propertyName, Object propertyValue) {
    	if (propertyName.contains(".")) {
    		String[] names = propertyName.split("\\.");
    		int last = names.length-1;
    		for(int i=0;i<last;i++) {
    			o = getPropertyValue(o, names[i]);
    		}
    		propertyName = names[last];
    	}
    	
   		setPropertyValue2(o, propertyName, propertyValue);
    }
    
    public static Object getPropertyValue(Object o, String propertyName) {
    	if (o==null) return null;
    	
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

    public static void setPropertyValue(Object o, String propertyName, Object propertyValue) {
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
            m.invoke(o, propertyValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke setter for " + name, e);
        }
    }

    public static void setPropertyValue2(Object o, String propertyName, Object propertyValue) {
        String name = o.getClass().getName() + "." + propertyName;
        try {
            Map<Class<?>, Method> ms = SETTERS2.get(name);
            if (ms == null) {
            	ms = new HashMap<Class<?>, Method>();
            	SETTERS2.put(name, ms);
            	
            	// fill the methods map
                String mName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                for (Method m : o.getClass().getMethods()) {
                    if (mName.equals(m.getName()) && m.getParameterTypes().length == 1) {
                        ms.put(m.getParameterTypes()[0], m);
                    }
                }
            }

            if (ms.size() == 0) {
                throw new NoSuchMethodException("Failed to find setter for " + name);
            } else if (ms.size() == 1) {
                ms.values().iterator().next().invoke(o, propertyValue);
            } else {
                if (propertyValue == null) {
                    throw new RuntimeException("Failed to choose setter for " + name + " because argument is null");
                }
                for (Class<?> vc = propertyValue.getClass(); vc != null; vc = vc.getSuperclass()) {
                    Method m = ms.get(vc);
                    if (m!=null) {
                        m.invoke(o, propertyValue);
                        return;
                    }
                }
                throw new NoSuchMethodException("Failed to match single setter for " + name + " to " + propertyValue.getClass());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke setter for " + name, e);
        }

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
