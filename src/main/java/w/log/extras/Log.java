package w.log.extras;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Autowire a slf4j logger for the surrounding class.
 *
 */
@Retention(RUNTIME) 
@Target(FIELD) 
@Documented 
public @interface Log {}
