package w.utils;

public class NestedBeanUtils implements IBeanUtils {
	
	private class Holder {
		Object target;
		String propertyName;
	}
	
	private final IBeanUtils delegate;
	
	/**
	 * Create the path when GETTING sub elements
	 */
	private final boolean createPath;
	
	public NestedBeanUtils() {
		this(true, new SimpleBeanUtils());
	}

	public NestedBeanUtils(boolean createPath) {
		this(createPath, new SimpleBeanUtils());
	}
	
	public NestedBeanUtils(boolean createPath, IBeanUtils delegate) {
		this.createPath = createPath;
		this.delegate = delegate;
	}

	private Holder getLastElementInPath(Object o, String propertyName, boolean createIfNull) {
		String[] names = propertyName.split("\\.");
		int last = names.length-1;
		for(int i=0;i<last;i++) {
			Object target = delegate.getPropertyValue(o, names[i]);
			if (target == null) {
				if (createIfNull) {
					// create the path up to the last object
					target = newPathElement(delegate.getPropertyType(o, names[i]));
					delegate.setPropertyValue(o, names[i], target);
				} else {
					return null;
				}
			}
			o = target;
		}
		
		Holder holder = new Holder();
		holder.target = o;
		holder.propertyName = names[last];
		return holder;
    }
    
    protected <T> T newPathElement(Class<T> clazz) {
    	try { 
			return clazz.newInstance();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
    }
    
	@Override
	public Class<?> getPropertyType(Class<?> clazz, String propertyName) {
		if (propertyName.contains(".")) {
			for(String name: propertyName.split("\\.")) {
				clazz = delegate.getPropertyType(clazz, name);
			}
			return clazz;
		} else {
			return delegate.getPropertyType(clazz, propertyName);
		}
	}

	@Override
	public Class<?> getPropertyType(Object o, String propertyName) {
		if (propertyName.contains(".")) {
			// retrieve the type on the instance for as long as possible
			// if one instance is null, fall back to fetching the type by class
			Class<?> clazz = o.getClass();
			for(String name: propertyName.split("\\.")) {
				if (o==null) {
					clazz = delegate.getPropertyType(clazz, propertyName);
				} else {
					o = delegate.getPropertyType(o, name);
					if (o==null) {
						clazz = delegate.getPropertyType(clazz, propertyName);
					}
				}
			}
			return clazz;
		} else {
			return delegate.getPropertyType(o, propertyName);
		}
	}
	
	@Override
	public Object getPropertyValue(Object o, String propertyName) {
		if (propertyName.contains(".")) {
			Holder h = getLastElementInPath(o, propertyName, false);
			if (h == null) { return null; }
			return delegate.getPropertyValue(h.target, h.propertyName);
		} else {
			return delegate.getPropertyValue(o, propertyName);
		}
	}

	@Override
	public void setPropertyValue(Object o, String propertyName, Object propertyValue) {
		if (propertyName.contains(".")) {
			Holder h = getLastElementInPath(o, propertyName, createPath);
			delegate.setPropertyValue(h.target, h.propertyName, propertyValue);
		} else {
			delegate.setPropertyValue(o, propertyName, propertyValue);
		}
	}
	
}
