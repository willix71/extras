package w.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SimpleBeanUtils implements IBeanUtils {

	private class BeanProperty {
		Class<?> type;		
		Method getter;
		// there might be more than one setter method
		Map<Class<?>, Method> setters = new HashMap<Class<?>, Method>();
	}
	
	private Map<String, BeanProperty> beanProperties = new HashMap<String, BeanProperty>();
	 	
	private BeanProperty getBeanPropertyFor(Object o, String propertyName) throws NoSuchMethodException {
		return getBeanProperty(o.getClass(), propertyName);
	}
	
	private BeanProperty getBeanProperty(Class<?> clazz, String propertyName) throws NoSuchMethodException {
		String name = clazz.getName() + "." + propertyName; 
		BeanProperty bp = beanProperties.get(name);
		if (bp == null) {
			bp = new BeanProperty();
			String methodSuffix = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
			try {
				bp.getter = clazz.getMethod("get" + methodSuffix);
				bp.type = bp.getter.getReturnType();			
			} catch(NoSuchMethodException e) {
				// no getter for methodSuffix
			}
			
        	// fill the setter map
            String mName = "set" + methodSuffix;
            for (Method m : clazz.getMethods()) {
                if (mName.equals(m.getName()) && m.getParameterTypes().length == 1) {
                	bp.setters.put(m.getParameterTypes()[0], m);
                }
            }
            if (bp.setters.size() == 1 && bp.type == null) {
            	// set the default type if there is no getter and only one setter
            	bp.type = bp.setters.keySet().iterator().next();
            }
		}
		return bp;
	}
	
	@Override
	public Class<?> getPropertyType(Class<?> clazz, String propertyName) {
        try {
        	BeanProperty pn = getBeanProperty(clazz, propertyName);
        	return pn.type;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find property " + propertyName + " for bean " + clazz, e);
        }
    }
	
	@Override
	public Class<?> getPropertyType(Object o, String propertyName) {
        try {
        	BeanProperty pn = getBeanPropertyFor(o, propertyName);
        	return pn.type;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find property " + propertyName + " for bean " + (o==null?null:o.getClass()), e);
        }
    }
	
	@Override
    public Object getPropertyValue(Object o, String propertyName) {
        try {
        	BeanProperty pn = getBeanPropertyFor(o, propertyName);
        	if (pn.getter == null) {
        		throw new RuntimeException("No getter for property " + propertyName + " for bean " + (o==null?null:o.getClass()));
        	}
        	return pn.getter.invoke(o);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke getter for " + propertyName + " for bean " + (o==null?null:o.getClass()), e);
        }
    }

	@Override
    public void setPropertyValue(Object o, String propertyName, Object propertyValue) {
        try {
        	BeanProperty pn = getBeanPropertyFor(o, propertyName);
        	Method m = null;
        	if (pn.setters.size()==1) {
        		// if there is only one setter, well use that one
        		m = pn.setters.values().iterator().next();
        	} else if (propertyValue == null) {
        		// if the value is null, use the setter which corresponds to the getter's return type (if it exists)
        		m = pn.setters.get(pn.type);
        	} else {
        		// else loop over the value's class hierarchy to find the best match
        		for (Class<?> vc = propertyValue.getClass(); m==null && vc != null; vc = vc.getSuperclass()) {
        			m = pn.setters.get(vc);
        		}
        	}       	
        	if (m == null) {
        		throw new RuntimeException("No setter for property " + propertyName + " for bean " + (o==null?null:o.getClass()) + " and value " + propertyValue);
        	} 
        	m.invoke(o, propertyValue);
        } catch (Exception e) {
        	throw new RuntimeException("Failed to invoke setter for " + propertyName + " for bean " + (o==null?null:o.getClass()) + " and value " + propertyValue, e);
        }
    }

}
