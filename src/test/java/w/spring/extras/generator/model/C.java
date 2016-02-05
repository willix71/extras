package w.spring.extras.generator.model;

import javax.annotation.Resource;

public class C implements Marker{

	@Resource 
	D d;
	
	public D getD() {
		return d;
	}

	@Override
	public String toString() {
		return "C [" + hashCode() +  "," + d +"]";
	}
}
