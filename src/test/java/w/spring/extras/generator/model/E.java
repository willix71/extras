package w.spring.extras.generator.model;

import javax.annotation.Resource;

public class E {
	
	@Resource
	GenericHolder<A> Aholder; // matches by name

	public GenericHolder<A> getAholder() {
		return Aholder;
	}

	@Override
	public String toString() {
		return "E [holderA=" + Aholder + "]";
	}
}
