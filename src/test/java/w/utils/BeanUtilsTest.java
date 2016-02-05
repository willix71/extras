package w.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.annotation.Order;

import w.junit.extras.OrderedJUnit4ClassRunner;

@RunWith(OrderedJUnit4ClassRunner.class)
public class BeanUtilsTest {

	interface I {}
	
	interface II extends I {}
	
    class A implements II {
    }

    class B extends A {
    }

    class C extends B {
    }
    
    class Bean {
        String name;
        A a;
        A a2;
        A a3;
        I i;
        
        public Bean(String name) {
            super();
            this.name = name;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public A getA() { return a;}
        public void setA(A a) { this.a = a; }

        public A getA2() { return a2;}
        public void setA2(A a2) { this.a2 = a2; }
        
        public A getA3() { return a3;}
        public void setA3(A a3) { this.a3 = a3; }

        public I getI() { return i;}
        public void setI(I i) { this.i = i; } 
        //public void setI(II i) { this.i = i; } 
    }

    class SubBean extends Bean {
    	boolean setA2Called = false;
    	boolean setA3Called = false;
        public SubBean(String name) {
            super(name);
        }

        @Override
        public void setA2(A a) {
            super.setA(a);
            setA2Called = true;
        }
        
        // overloading
        public void setA3(B b) {
            super.setA(b);
            setA3Called = true;
        }
    }

    // --- Getter
    @Test
    public void testGetter() {
        Bean b = new Bean("William");

        Assert.assertEquals("William", BeanUtils.getPropertyValue(b, "name"));
    }

    @Test
    public void testGetterA() {
        Bean b = new Bean("William");

        Assert.assertEquals("William", BeanUtils.getPropertyValue(b, "name"));
    }

    @Test
    public void testGetterSubBean() {
        SubBean b = new SubBean("William");

        Assert.assertEquals("William", BeanUtils.getPropertyValue(b, "name"));
    }

    // --- Setter v1
    @Test
    @Order(1)
    public void testSimpleSetter() {
        Bean b = new Bean("William");

        BeanUtils.setPropertyValue(b, "name", "William2");

        Assert.assertEquals("William2", b.getName());
    }

    // TODO this only works because the method has already been cached
    @Test
    @Order(2)
    public void testNullSetter() {
        Bean b = new Bean("William");

        BeanUtils.setPropertyValue(b, "name", null);

        Assert.assertNull(b.getName());
    }

    @Test
    public void testSimpleSetterSubBean() {
        SubBean b = new SubBean("William");

        BeanUtils.setPropertyValue(b, "name", "William2");

        Assert.assertEquals("William2", b.getName());
    }

    @Test
    public void testSetterOfSubClass() {
        Bean bean = new Bean("William");
        B b = new B();

        BeanUtils.setPropertyValue(bean, "a", b);

        Assert.assertEquals(b, bean.getA());
    }

    // --- Setter v2
    @Test
    public void testNullSetter2() {
        Bean b = new Bean("William");

        BeanUtils.setPropertyValue2(b, "name", null);

        Assert.assertNull(b.getName());
    }

    @Test
    public void testSimpleSetter2() {
        Bean b = new Bean("William");

        BeanUtils.setPropertyValue2(b, "name", "William2");

        Assert.assertEquals("William2", b.getName());
    }

    @Test
    public void testSimpleSetterSubBean2() {
        SubBean b = new SubBean("William");

        BeanUtils.setPropertyValue2(b, "name", "William2");

        Assert.assertEquals("William2", b.getName());
    }

    @Test
    public void testSetterOfSubClass2() {
        Bean bean = new Bean("William");
        B b = new B();

        BeanUtils.setPropertyValue2(bean, "a", b);

        Assert.assertEquals(b, bean.getA());
    }
    
    @Test
    public void testSetterOfSubBeanWithSubClass2() {
    	SubBean bean = new SubBean("William");
        B b = new B();

        BeanUtils.setPropertyValue2(bean, "a", b);

        Assert.assertEquals(b, bean.getA());
    }
    
    @Test
    public void testOverridenSetterOfSubBeanWithSubSubClass2() {
    	SubBean bean = new SubBean("William");
        C c = new C();

        BeanUtils.setPropertyValue2(bean, "a2", c);

        Assert.assertEquals(c, bean.getA());
        
        Assert.assertTrue(bean.setA2Called);
    }
    
    @Test
    public void testOverloadedSetterOfSubBeanWithSubClass() {
    	SubBean bean = new SubBean("William");
    	C c = new C();

        BeanUtils.setPropertyValue2(bean, "a3", c);

        Assert.assertEquals(c, bean.getA());
        
        Assert.assertTrue(bean.setA3Called);
    }
    
    @Test
    public void testInterfacedArguments() {
    	SubBean bean = new SubBean("William");
    	II i = new C();

        BeanUtils.setPropertyValue2(bean, "i", i);

        // succeeds because there is only ONE methed getI()
        Assert.assertEquals(i, bean.getI());
        Assert.assertNull(bean.getA());
        Assert.assertNull(bean.getA2());
        Assert.assertNull(bean.getA3());
    }
}
