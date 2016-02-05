package w.utils;

import org.junit.Assert;
import org.junit.Test;

public class SimpleBeanUtilsTest {

	static SimpleBeanUtils simpleBeanUtils = new SimpleBeanUtils();

	interface I {}
	interface II extends I {}
	
    class A implements II {}
    class B extends A {}
    class C extends B {}
    
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

    @Test
    public void testGetType() {
        Assert.assertEquals(String.class, simpleBeanUtils.getPropertyType(Bean.class, "name"));
        Assert.assertEquals(A.class, simpleBeanUtils.getPropertyType(Bean.class, "a"));
    }
    
    @Test
    public void testGetTypeSubBean() {
        Assert.assertEquals(String.class, simpleBeanUtils.getPropertyType(SubBean.class, "name"));
        Assert.assertEquals(A.class, simpleBeanUtils.getPropertyType(SubBean.class, "a"));
    }
    
    // --- Getter
    @Test
    public void testGetter() {
        Bean b = new Bean("William");
        Assert.assertEquals("William", simpleBeanUtils.getPropertyValue(b, "name"));
    }
    @Test
    public void testGetterSubBean() {
        SubBean b = new SubBean("William");
        Assert.assertEquals("William", simpleBeanUtils.getPropertyValue(b, "name"));
    }

    // --- Setter v1
    @Test
    public void testSimpleSetter() {
        Bean b = new Bean("William");
        simpleBeanUtils.setPropertyValue(b, "name", "William2");
        Assert.assertEquals("William2", b.getName());
    }

    @Test
    public void testNullSetter() {
        Bean b = new Bean("William");
        simpleBeanUtils.setPropertyValue(b, "name", null);
        Assert.assertNull(b.getName());
    }

    @Test
    public void testSimpleSetterSubBean() {
        SubBean b = new SubBean("William");
        simpleBeanUtils.setPropertyValue(b, "name", "William2");
        Assert.assertEquals("William2", b.getName());
    }

    @Test
    public void testSetterOfSubClass() {
        Bean bean = new Bean("William");
        B b = new B();

        simpleBeanUtils.setPropertyValue(bean, "a", b);
        Assert.assertEquals(b, bean.getA());
    }

    // --- Setter v2

    
    @Test
    public void testSetterOfSubBeanWithSubClass2() {
    	SubBean bean = new SubBean("William");
        B b = new B();

        simpleBeanUtils.setPropertyValue(bean, "a", b);
        Assert.assertEquals(b, bean.getA());
    }
    
    @Test
    public void testOverridenSetterOfSubBeanWithSubSubClass2() {
    	SubBean bean = new SubBean("William");
        C c = new C();
        
        Assert.assertFalse(bean.setA2Called);
        simpleBeanUtils.setPropertyValue(bean, "a2", c);

        Assert.assertEquals(c, bean.getA());
        Assert.assertTrue(bean.setA2Called);
    }
    
    @Test
    public void testOverloadedSetterOfSubBeanWithSubClass() {
    	SubBean bean = new SubBean("William");
    	C c = new C();

        Assert.assertFalse(bean.setA3Called);
        simpleBeanUtils.setPropertyValue(bean, "a3", c); 
        
        // does not call the sub-class method because the setter is match with the return type of the getter!!! 

        Assert.assertEquals(c, bean.getA()); 
        Assert.assertTrue(bean.setA3Called);
    }
    
    @Test
    public void testInterfacedArguments() {
    	SubBean bean = new SubBean("William");
    	II i = new C();

        simpleBeanUtils.setPropertyValue(bean, "i", i);

        // succeeds because there is only ONE methed getI()
        Assert.assertEquals(i, bean.getI());
        
        Assert.assertNull(bean.getA());
        Assert.assertNull(bean.getA2());
        Assert.assertNull(bean.getA3());
    }
    
}
