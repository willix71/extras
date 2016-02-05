package w.spring.extras.generator;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import w.spring.extras.GenericBeanGenerator;
import w.spring.extras.generator.model.A;
import w.spring.extras.generator.model.B;
import w.spring.extras.generator.model.C;
import w.spring.extras.generator.model.D;

@Configuration
class DynamicBeanConfig {

	@Bean
	public A main() {return new A();}
	@Bean
	public C c() {return new C();}

	// is static needed? 
	
	@Bean
	public GenericBeanGenerator<B, String> BpostProcessor() {
		return new GenericBeanGenerator<B, String>(B.class, Arrays.asList("b10","b11","b12")) {
			@Override
			public void initiliaze(String beanName, String generic, B instance) {
				instance.setName(beanName);
			}
		};
	}
	
	@Bean
	public GenericBeanGenerator<D, String> DpostProcessor() {
		return new GenericBeanGenerator<D, String>(D.class, Arrays.asList("d"));
	}
}

/**
 * Testing dynamically generating beans
 */
public class TestDynamicBeanGenerator {
	
	@Test
	public void testWithAnnotationConfig() {
		try(AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DynamicBeanConfig.class)) {
			testContext(context);
		}
	}
	
	@Test
	public void testWithXmlConfig() {
		try(ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dynamic-context.xml")) {
			testContext(context);
		}
	}
	
	private void testContext(ApplicationContext context) {
		Object main = context.getBean("main");
		Assert.assertNotNull(main);

		A mainA = (A) main;
		Assert.assertEquals(3, mainA.getBs().size());
		
		B b1 = mainA.getBs().iterator().next();
		Assert.assertNotNull("b has no C", b1.getC());
		Assert.assertNotNull("b has no D", b1.getC().getD());
		
		Assert.assertNotNull(context.getBean("b10"));
	}
}
