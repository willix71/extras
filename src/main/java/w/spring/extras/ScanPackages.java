package w.spring.extras;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

public class ScanPackages {

	public static String[] getImplementations(Class<?> c, String ...path) {
		BeanDefinitionRegistry bdr = new SimpleBeanDefinitionRegistry();
		ClassPathBeanDefinitionScanner s = new ClassPathBeanDefinitionScanner(bdr);

		TypeFilter tf = new AssignableTypeFilter(c);
		s.addIncludeFilter(tf);
		s.scan(path);       
		
		return bdr.getBeanDefinitionNames();
	}
}
