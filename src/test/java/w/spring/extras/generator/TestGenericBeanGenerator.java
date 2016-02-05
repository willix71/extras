package w.spring.extras.generator;

import javax.xml.ws.Holder;

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
import w.spring.extras.generator.model.E;
import w.spring.extras.generator.model.GenericHolder;

@Configuration
class GenericBeanConfig {

	@Bean
	public A main() {
		return new A();
	}
	
	@Bean
	public B b() {
		return new B();
	}
	
	@Bean
	public C c() {
		return new C();
	}

	@Bean
	public D d() {
		return new D();
	}
	
	@Bean
	public E e() {
		return new E();
	}
	
	// is static needed? 
	
	@Bean
	public GenericBeanGenerator<GenericHolder, Class> BpostProcessor() {
		Class[] markers = new Class[]{A.class, B.class, C.class};
		return new GenericBeanGenerator<GenericHolder, Class>(GenericHolder.class, markers) {
			
			public String getBeanName(Class generic) {
				return generic.getSimpleName() + "holder";
			}

			public void initiliaze(String beanName, Class generic, GenericHolder instance) {
				instance.setClazz(generic);
			}
		};
	}	
}

/**
 *
 */
public class TestGenericBeanGenerator {
	
	@Test
	public void testGenericWithAnnotationConfig() {
		try(AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(GenericBeanConfig.class)) {
			GenericHolder<A> aHolder = (GenericHolder<A>) context.getBean("Aholder");
			testHolder(aHolder, A.class);
			
			GenericHolder bHolder = (GenericHolder) context.getBean("Bholder");
			testHolder(bHolder, B.class);
			
			testHolder((GenericHolder<C>) context.getBean("Cholder"), C.class);
			
			A a = (A) context.getBean("main");
			aHolder.setHolding(a);
			
			// this is bad but it's java; Generics are only checked at compile time
			bHolder.setHolding(a);
		}
	}
	
	private void testHolder(GenericHolder h, Class c) {
		Assert.assertNotNull(h);
		Assert.assertNotNull(h.getClazz());
		Assert.assertEquals(c, h.getClazz());
	}
}
