package w.junit.extras;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * use org.springframework.core.annotation.Order to order your test case
 * 
 * @author gekeysew
 *
 */
public class OrderedSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {

   public OrderedSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
      super(clazz);
   }

   @Override
   protected List<FrameworkMethod> getChildren() {
      List<FrameworkMethod> children = super.getChildren();
      
      Collections.sort(children, new Comparator<FrameworkMethod>() {
         @Override
         public int compare(FrameworkMethod fm1, FrameworkMethod fm2) {
            Order o1 = fm1.getAnnotation(Order.class);
            Order o2 = fm2.getAnnotation(Order.class);
            if (o1 == null) {
               if (o2 == null) {
                  // none of the methods are ordered, sort them by name
                  return fm1.getName().compareTo(fm2.getName());
               } else {
                  // ordered test come first
                  return 1;
               }
            } else {
               if (o2 == null) {
                  // ordered test come first
                  return -1;
               } else {
                  // order the ordered methods
                  return o1.value() - o2.value();
               }
            }
         }
      });
      
      return children;
   }

   
}
