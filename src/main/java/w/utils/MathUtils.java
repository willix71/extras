package w.utils;

public class MathUtils {

   public static int sum(int...values) {
      int sum = 0;
      for(int i:values) sum+=i;
      return sum;
   }
   
   public static long sum(long...values) {
      long sum = 0;
      for(long i:values) sum+=i;
      return sum;
   }
   
   public static float sum(float...values) {
      float sum = 0;
      for(float i:values) sum+=i;
      return sum;
   }
   
   public static double sum(double...values) {
      double sum = 0;
      for(double i:values) sum+=i;
      return sum;
   }
}
