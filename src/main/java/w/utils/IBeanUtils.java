package w.utils;

public interface IBeanUtils {
	
	Class<?> getPropertyType(Class<?> clazz, String propertyName);

	Class<?> getPropertyType(Object o, String propertyName);
	
	Object getPropertyValue(Object o, String propertyName);

	void setPropertyValue(Object o, String propertyName, Object propertyValue);
}
