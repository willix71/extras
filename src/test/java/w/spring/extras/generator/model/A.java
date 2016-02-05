package w.spring.extras.generator.model;

import java.util.Collection;

import javax.annotation.Resource;

public class A implements Marker{

	@Resource
	Collection<B> bs;
	
	public Collection<B> getBs() {
		return bs;
	}

	public void setBs(Collection<B> b) {
		this.bs = b;
	}

	@Override
	public String toString() {
		return "A [" + hashCode() + ", " + bs + "]";
	}
}
