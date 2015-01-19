package figlet;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FigletUtil {

   public static final String DEFAULT_PATH = "figlet/";
   
   public static enum Figlet {
      BIG, BLOCK, BULBHEAD, CHUNKY, DOH, EPIC;
   }
   
   static Map<Figlet, FigletFont> fonts = new HashMap<Figlet, FigletFont>();
  
   public static FigletFont getFigletFont(Figlet figlet) {
      FigletFont ff = fonts.get(figlet);
      if (ff == null) {
         String resource = DEFAULT_PATH+ figlet.toString().toLowerCase() + ".flf" ;
         ff = new FigletFont(FigletUtil.class.getClassLoader().getResourceAsStream(resource));
         fonts.put(figlet, ff);
      }
      return ff;
   }
   
   public static String[] getMessageLines(String message, Figlet figlet) {
      return getMessageLines(message, getFigletFont(figlet));
   }
   
   public static String getMessage(String message, String separator, Figlet figlet) {
      return getMessage(message, separator, getFigletFont(figlet));
   }
   
   public static String getMessage(String message, Figlet figlet) {
      return getMessage(message, getFigletFont(figlet));
   }
   
   public static String[] getMessageLines(String message, FigletFont font) {
      String[] result= new String[font.height];      
      for(int l=0;l<font.height; l++) {
         StringBuilder line = new StringBuilder();
         for(char c: message.toCharArray()) {         
            line.append(font.getCharLineString((int) c, l));
         }
         result[l]=line.toString();
      }
      return result;
   }
   
   public static String getMessage(String message, String separator, FigletFont font) {
      StringBuilder line = new StringBuilder();

      for(int l=0;l<font.height; l++) {
          for(char c: message.toCharArray()) {         
            line.append(font.getCharLineString((int) c, l));
         }
          line.append(separator);
      }
      return line.toString();
   }
   
   public static String getMessage(String message, FigletFont font) {
      return getMessage(message, "\n", font);
   }
   
   public static void main(String[] args) throws Exception {
      String message;
      FigletFont font;
      int argi = 0;
      if (args.length > 2 && args[0].startsWith("-")) {
         if ("-url".equals(args[0])) {
            font = new FigletFont(new URL(args[1]));
         } else if ("-file".equals(args[0])) {
            font = new FigletFont(new FileInputStream(args[1]));
         } else if ("-figlet".equals(args[0])) {
            InputStream is = FigletUtil.class.getClassLoader().getResourceAsStream(DEFAULT_PATH+args[1]);
            font = new FigletFont(is);
         } else {
            font = null;
            throw new IllegalArgumentException("Unkown option " + args[0]);
         }
         message = args[1];
         argi=2;
      } else {
         font = getFigletFont(Figlet.CHUNKY);
         message = "Figlets are cool!";
      }
      if (args.length>argi) {
         message = args[argi];
      }

      System.out.println(getMessage(message, font));

   }

}
