package w.spring.extras.generator.model;

import javax.annotation.Resource;

public class B implements Marker{

	@Resource 
	C c;
	
	String name;
	
	public B() {}
	public B(String name) {setName(name);}
	public void setName(String name) {this.name=name;}
	
	public C getC() {
		return c;
	}
	@Override
	public String toString() {
		return "B [" + name + "," + hashCode() + "," + c + "]";
	}
}
