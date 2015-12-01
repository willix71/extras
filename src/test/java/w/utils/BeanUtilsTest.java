package w.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.annotation.Order;

import junit.framework.Assert;
import w.junit.extras.OrderedJUnit4ClassRunner;

@RunWith(OrderedJUnit4ClassRunner.class)
public class BeanUtilsTest {

    class A {
    }

    class B extends A {
    }

    class Bean {
        String name;
        A a;

        public Bean(String name) {
            super();
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }
    }

    class SubBean extends Bean {
        public SubBean(String name) {
            super(name);
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
}
