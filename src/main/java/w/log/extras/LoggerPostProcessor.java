package w.log.extras;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 * Autowire an Slf4j logger for the surrounding class.
 * 
 * <pre>
 * class XYZ {
 *    @Log
 *    Logger logger;
 *    
 * }
 * </pre>
 */
public class LoggerPostProcessor implements BeanPostProcessor {

   @Override
   public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
      return bean;
   }

   @Override
   public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
      final Class<?> beanClass = bean.getClass();
      
      ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {

           @Override
         public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

            ReflectionUtils.makeAccessible(field);

            // Check if the field is annoted with @Log
            if (field.getAnnotation(Log.class) != null) {
               Logger logger = LoggerFactory.getLogger(beanClass);
               field.set(bean, logger);
            }
         }
      });

      return bean;

   }

}
