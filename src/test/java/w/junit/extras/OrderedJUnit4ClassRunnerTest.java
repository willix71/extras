package w.junit.extras;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrderedJUnit4ClassRunner.class)
@ContextConfiguration
public class OrderedJUnit4ClassRunnerTest {

   @Test
   public void testBBBB() {

   }

   @Test
   public void testXXXX() {

   }
   
   @Test
   @Order(10)
   public void test10() {

   }

   @Test
   @Order(9)   
   public void testBefore10() {

   }

   @Test
   @Order(11)
   public void testAfter10() {

   }


   @Test
   public void testZZZZ() {

   }

   @Test
   public void testAAAAA() {

   }

}
