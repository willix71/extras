package w.utils;

public class NestedBeanUtils implements IBeanUtils {
	
	private class Holder {
		Object target;
		String propertyName;
	}
	
	private IBeanUtils delegate = new SimpleBeanUtils();
	
	private boolean createPath = true;
	
	public NestedBeanUtils() {}

	public NestedBeanUtils(boolean createPath) {
		this.createPath = createPath;
	}
	
	public NestedBeanUtils(boolean createPath, IBeanUtils delegate) {
		this.createPath = createPath;
		this.delegate = delegate;
	}
	
	private Holder getLastElementInPath(Object o, String propertyName) {
		String[] names = propertyName.split("\\.");
		int last = names.length-1;
		for(int i=0;i<last;i++) {
			Object target = delegate.getPropertyValue(o, names[i]);
			if (target == null) {
				if (createPath) {
					target = newPathElement(delegate.getPropertyType(o.getClass(), names[i]));
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
	public Object getPropertyValue(Object o, String propertyName) {
		if (propertyName.contains(".")) {
			Holder h = getLastElementInPath(o, propertyName);
			if (h == null) { return null; }
			return delegate.getPropertyValue(h.target, h.propertyName);
		} else {
			return delegate.getPropertyValue(o, propertyName);
		}
	}

	@Override
	public void setPropertyValue(Object o, String propertyName, Object propertyValue) {
		if (propertyName.contains(".")) {
			Holder h = getLastElementInPath(o, propertyName);
			delegate.setPropertyValue(h.target, h.propertyName, propertyValue);
		} else {
			delegate.setPropertyValue(o, propertyName, propertyValue);
		}
	}
	
}
