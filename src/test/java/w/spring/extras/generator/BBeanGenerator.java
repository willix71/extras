package w.spring.extras.generator;

import java.util.Collection;

import w.spring.extras.GenericBeanGenerator;
import w.spring.extras.generator.model.B;

public class BBeanGenerator extends GenericBeanGenerator<B, String> {

	public BBeanGenerator(Class<B> type, Collection<String> generics) {
		super(type, generics);
	}

	@Override
	public void initiliaze(String beanName, String generic, B instance) {
		instance.setName(beanName);
	}
}
