package w.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionBeanUtils implements IBeanUtils {

	private final IBeanUtils delegate;
	
	/**
	 * Expand the collection up to the index value when SETTING a value
	 */
	private final boolean expandCollection;
	
	public CollectionBeanUtils() {
		this(true, new SimpleBeanUtils());
	}

	public CollectionBeanUtils(boolean expandCollection) {
		this(expandCollection, new SimpleBeanUtils());
	}
	
	public CollectionBeanUtils(boolean expandCollection, IBeanUtils delegate) {
		this.expandCollection = expandCollection;
		this.delegate = delegate;
	}

	public Class<?> getPropertyTypeFor(Class<?> clazz, String propertyName, int startLimit) {
		if (startLimit < 0) {
			return delegate.getPropertyType(clazz, propertyName);
		}
		
		String[] parts = split(propertyName, startLimit);
		
		Class<?> propertyClass = delegate.getPropertyType(clazz, parts[0]);
		if (propertyClass == null) {
			throwError("Can't extract the generic type of a null collection type", null);
			
		} else if (propertyClass.isArray()) {
			return propertyClass.getComponentType();
		}
		
		return null;
	}
	
	@Override
	public Class<?> getPropertyType(Class<?> clazz, String propertyName) {
		int startLimit = propertyName.indexOf("[");
		Class<?> propertyType = getPropertyTypeFor(clazz, propertyName, startLimit);
		if (propertyType == null) {
			throwError("Can't extract the generic type of a generic colletion", null);
		}
		return propertyType;
	}
	
	@Override
	public Class<?> getPropertyType(Object o, String propertyName) {
		int startLimit = propertyName.indexOf("[");
		Class<?> propertyType = getPropertyTypeFor(o.getClass(), propertyName, startLimit);
		if (propertyType != null) {
			return propertyType;
		}
		
		// we will retrieve the first element of the collection and create a new instance
		String[] parts = split(propertyName, startLimit);
		Object values = delegate.getPropertyValue(o, parts[0]);
		if (values == null) {		
			throwError("Can't know the generic type of a null colletion", null);
		} else if (values instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String,?> m = (Map<String,?>) values;
			
			if (m.size()==0) {
				throwError("Can't know the generic type of an empty map", null);
			} else {
				return m.values().iterator().next().getClass();
			}
		} else if (values instanceof Iterable) {
			Iterator<?> iter = ((Iterable<?>) values).iterator();
			if (!iter.hasNext()) {
				throwError("Can't know the generic type of an empty iterable", null);
			} else {
				return iter.next().getClass();
			}
		}
		
		throwError("Can't know the generic type of an unknown collection " + values.getClass(), null);
		
		return null;
    }
	
	@Override
	public Object getPropertyValue(Object o, String propertyName) {
		int startLimit = propertyName.indexOf("[");
		if (startLimit < 0) {
			return delegate.getPropertyValue(o, propertyName);
		}
		String[] parts = split(propertyName, startLimit);
		Object values = delegate.getPropertyValue(o, parts[0]);
		if (values == null) {
			return null;			
		} else if (values instanceof Map) {
			return ((Map<?,?>) values).get(parts[1]);
		} else {
			int index = Integer.parseInt(parts[1]);
			if (values instanceof Collection) {
				Collection<?> collection = (Collection<?>) values;
				if (index >= collection.size()) {
					// index too big
					return null;
				} else if (collection instanceof List) {
					return ((List<?>) collection).get(index);
				} else {
					Iterator<?> iter = ((Iterable<?>) collection).iterator();
					for(int i=0; i<index;i++) {
						iter.next();
					}
					return iter.next();
				}
			} else if (values instanceof Iterable) {
				int i = 0;
				for (Iterator<?> iter = ((Iterable<?>) values).iterator(); iter.hasNext(); ) {
					if (i==index) return iter.next();
				}
				// index too big
				return null; 
			} else {
				int length = getArrayLengthOrError(values);	
				if (index >= length) {
					 // index too big	
					return null;
				} else {
					return Array.get(values, index);
				}
			}
		}   
	}

	@Override
	public void setPropertyValue(Object o, String propertyName, Object propertyValue) {
		int startLimit = propertyName.indexOf("[");
		if (startLimit < 0) {
			delegate.setPropertyValue(o, propertyName, propertyValue);
			return;
		}
		
		String[] parts = split(propertyName, startLimit);
		Object values = delegate.getPropertyValue(o, parts[0]);
		
		if (values == null) {
			if (!expandCollection) 
				throwError("Can't set property on a null collection for property " + parts[0] + " on " + o.getClass(), null);

			// create the collection
			values = newCollection(o.getClass(), parts[0], propertyValue, null);
			delegate.setPropertyValue(o, parts[0], values);
		}
		
		boolean emptyIndex = parts[1].length()==0;
		Object newValue = emptyIndex?appendValue(values, propertyValue):setValue(values, parts[1], propertyValue);
		if (newValue != null) {
			delegate.setPropertyValue(o, parts[0], newValue);
		}
	}

	protected Object appendValue(Object values, Object propertyValue) {
		if (values instanceof Collection) {
			@SuppressWarnings("unchecked")
			Collection<Object> collection = (Collection<Object>) values;
			collection.add(propertyValue);
			return null;
			
		}  else {
			int length = getArrayLengthOrError(values);			
			if (!expandCollection) {
				throwError("Can't append value, array is too small", null);
			}
			
			return appendValueOnArray(values,length,propertyValue);
		}
	}

	protected Object setValue(Object values, String at, Object propertyValue) {
		if (values instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>) values;
			map.put(at, propertyValue);
		} else {
			int index = Integer.parseInt(at);
			if (values instanceof Collection) {
				@SuppressWarnings("unchecked")
				Collection<Object> collection = (Collection<Object>) values;
				if (index >= collection.size()) {
					if (!expandCollection) {
						throwError("Can't set value, collection is too small", null);
					}
					for(int i=collection.size();i<index;i++) {
						collection.add(null); // this will not really work on sets 
					}
					collection.add(propertyValue);
					
				} else if (collection instanceof List) {
					((List<Object>) collection).set(index, propertyValue);
				} else {
					throwError("Can't set value on a non-list collection of type " + values.getClass(), null);
				}
			}  else {
				int length = getArrayLengthOrError(values);
				if (index < length) {
					Array.set(values, index, propertyValue);
				} else {
					if (!expandCollection) {
						throwError("Can't set value, array is too small", null);
					}

					return appendValueOnArray(values, index, propertyValue);
				}
			}
		}
		return null; // 
	}
      
    protected Object appendValueOnArray(Object array, int index, Object value) {
    	Class<?> t = array.getClass().getComponentType();
    	if (Boolean.TYPE==t) {
			boolean[] arr = Arrays.copyOf((boolean[])array, index+1);
			arr[index] = value==null? true : (boolean) value;
			return arr;
		} else if (Byte.TYPE==t) {
			byte[] arr = Arrays.copyOf((byte[])array, index+1);
			arr[index] = value==null? 0 : ((Number) value).byteValue();
			return arr;
		} else if (Short.TYPE==t) {
			short[] arr = Arrays.copyOf((short[])array, index+1);
			arr[index] = value==null? 0 : ((Number) value).shortValue();
			return arr;
		} else if (Integer.TYPE==t) {
			int[] arr = Arrays.copyOf((int[])array, index+1);
			arr[index] = value==null? 0 : ((Number) value).intValue();
			return arr;
		} else if (Long.TYPE==t) {
			long[] arr = Arrays.copyOf((long[])array, index+1);
			arr[index] = value==null? 0 : ((Number) value).longValue();
			return arr;
		} else if (Float.TYPE==t) {
			float[] arr = Arrays.copyOf((float[])array, index+1);
			arr[index] = value==null? 0 : ((Number) value).floatValue();
			return arr;
		} else if (Double.TYPE==t) {
			double[] arr = Arrays.copyOf((double[])array, index+1);
			arr[index] = value==null? 0 : ((Number) value).doubleValue();
			return arr;
		} else if (Character.TYPE==t) {
			char[] arr = Arrays.copyOf((char[])array, index+1);
			arr[index] = (char) (value==null?0:value);
			return arr;
		} else {
			Object[] arr = Arrays.copyOf((Object[])array, index+1);
			arr[index] = value;
			return arr;
		}
    }
    
    protected int getArrayLengthOrError(Object array) {
    	if (!array.getClass().isArray()) {	
    		throwError("Can't do that on collection of type " + array.getClass(), null);
    	}
		
    	return Array.getLength(array);
    }
    
	protected void throwError(String message, Exception e) {
		throw new IllegalArgumentException(message);
	}
	
    @SuppressWarnings("rawtypes")
	protected Object newCollection(Class containerClass, String propertyName, Object properyValue, Class collectionClass) {
    	if (collectionClass == null) {
    		collectionClass = delegate.getPropertyType(containerClass, propertyName);
    	}
		if (collectionClass.isInterface()) {
	    	if (List.class.isAssignableFrom(collectionClass)) {
				return new ArrayList();
			} else if (Set.class.isAssignableFrom(collectionClass)) {
				return new HashSet();
			} else if (Map.class.isAssignableFrom(collectionClass)) {
				return new HashMap();
			}
		} else if (collectionClass.isArray()) {
			Class t = collectionClass.getComponentType();
			return Array.newInstance(t, 0);
		}
		
		// try instantiate the implementation
    	try { 
			return collectionClass.newInstance();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    protected String[] split(String propertyName, int delimiter) {
		return new String[] {propertyName.substring(0,delimiter), propertyName.substring(delimiter+1,propertyName.length()-1)};
	}
}
