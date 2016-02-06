package w.utils;

import org.junit.Assert;
import org.junit.Test;

public class NestedBeanUtilsTest {

	static NestedBeanUtils nestedBeanUtils = new NestedBeanUtils();
	
	static class A {		
		B b;
		public A(){}
		public A(B b) {this.b = b;}
		public B getB() {return b;}
		public void setB(B b) {this.b = b;}
    }
	static class B {
    	C c;
    	public B(){}
		public B(C c) {this.c = c;}
		public C getC() {return c;}
		public void setC(C c) {this.c = c;}
    }
    static class C {
    	int value;
    	public C(){}
		public C(int value) {this.value = value;}
		public int getValue() {return value;}
		public void setValue(int value) {this.value = value;}
    }
    
    @Test
    public void testGetNestedType() {
    	Assert.assertEquals(B.class,nestedBeanUtils.getPropertyType(A.class, "b"));

    	Assert.assertEquals(C.class,nestedBeanUtils.getPropertyType(A.class, "b.c"));

    	Assert.assertEquals(Integer.TYPE,nestedBeanUtils.getPropertyType(A.class, "b.c.value"));
    }
    
    @Test
    public void testGetNestedValue() {
    	A a = new A(new B(new C(3)));
    	
    	Assert.assertEquals(3,nestedBeanUtils.getPropertyValue(a, "b.c.value"));
    }

    @Test
    public void testGetNullNestedValue() {
    	A a = new A(null);
    	
    	Assert.assertNull(a.getB());
    	Assert.assertNull(nestedBeanUtils.getPropertyValue(a, "b.c"));

    	// it was not created when getting c 
    	Assert.assertNull(a.getB());
    }
    
    @Test
    public void testSetNestedValue() {
    	A a = new A(new B(new C()));
    	
    	nestedBeanUtils.setPropertyValue(a, "b.c.value", 4);
    	
    	Assert.assertEquals(4,nestedBeanUtils.getPropertyValue(a, "b.c.value"));
    }
    
    @Test
    public void testSetNestedNullValue() {
    	A a = new A(new B());
    	
    	Assert.assertNull(a.getB().getC());
    	
    	nestedBeanUtils.setPropertyValue(a, "b.c.value", 4);
    	
    	Assert.assertNotNull(a.getB().getC());
    	Assert.assertEquals(4,a.getB().getC().getValue());
    	Assert.assertEquals(4,nestedBeanUtils.getPropertyValue(a, "b.c.value"));
    }
}
