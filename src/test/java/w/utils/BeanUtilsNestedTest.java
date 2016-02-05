package w.utils;

import org.junit.Assert;
import org.junit.Test;

public class BeanUtilsNestedTest {

	class A {
		B b;
		public A(){}
		public A(B b) {this.b = b;}
		public B getB() {return b;}
		public void setB(B b) {this.b = b;}
    }
    class B {
    	C c;
    	public B(){}
		public B(C c) {this.c = c;}
		public C getC() {return c;}
		public void setC(C c) {this.c = c;}
    }
    class C {
    	int value;
    	public C(){}
		public C(int value) {this.value = value;}
		public int getValue() {return value;}
		public void setValue(int value) {this.value = value;}
    }
    
    @Test
    public void testGetNestedValue() {
    	A a = new A(new B(new C(3)));
    	
    	Assert.assertEquals(3,BeanUtils.getNestedValue(a, "b.c.value"));
    }

    @Test
    public void testGetNullNestedValue() {
    	A a = new A(null);
    	
    	Assert.assertNull(BeanUtils.getNestedValue(a, "b.c.value"));
    }
    
    @Test
    public void testSetNestedValue() {
    	A a = new A(new B(new C()));
    	
    	BeanUtils.setNestedValue2(a, "b.c.value", 4);
    	
    	Assert.assertEquals(4,BeanUtils.getNestedValue(a, "b.c.value"));
    }
    
    @Test
    public void testSetNestedNullValue() {
    	A a = new A(new B());
    	
    	BeanUtils.setNestedValue2(a, "b.c.value", 4);
    	
    	Assert.assertEquals(4,BeanUtils.getNestedValue(a, "b.c.value"));
    }
}
