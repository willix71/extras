package w.dao.populator.entity;

import java.util.concurrent.atomic.AtomicLong;

import w.dao.populator.entity.AbstractEntityPopulator.FieldGenerator;

public class SimpleFieldGenerator<T> implements FieldGenerator<T> {
   AtomicLong counter = new AtomicLong();
   public  Object generate(Object t) {
     return counter.addAndGet(1);
   }
}
