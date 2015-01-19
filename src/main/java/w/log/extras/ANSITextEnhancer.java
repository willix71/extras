package w.log.extras;

public class ANSITextEnhancer {
   public final static String ESC_START = "\033[";
   
   public final static String ESC_END = "m";

   public final static String BOLD = "1;";

   public final static int DEFAULT_FG = 39;

   public final static int BLACK_FG = 30;

   public final static int RED_FG = 31;

   public final static int GREEN_FG = 32;

   public final static int YELLOW_FG = 33;

   public final static int BLUE_FG = 34;

   public final static int MAGENTA_FG = 35;

   public final static int CYAN_FG = 36;

   public final static int WHITE_FG = 37;

   public final static String ESC_START_BOLD = ESC_START + BOLD;
   
   public final static String END = ESC_START + "0;" + DEFAULT_FG + ESC_END;
   
   static public String stripANSIEscapeChars(String str) {
      return str.replaceAll("\033\\[\\d;\\d\\dm", "");
   }

   static public String toANSIColor(String str, int color) {
      return toANSIColor(str, color, false);
   }
   static public String toANSIColor(String str, int color, boolean bold) {
      //return ESC_START + (bold ? BOLD : "") + color + ESC_END + str + ESC_START + "0;" + DEFAULT_FG + ESC_END;
      return (bold? ESC_START_BOLD :ESC_START) + color + ESC_END + str + END;      
   }

   static public String toBlack(String str) {
      return toANSIColor(str, BLACK_FG, true);
   }

   static public String toBlue(String str) {
      return toANSIColor(str, BLUE_FG, true);
   }

   static public String toCyan(String str) {
      return toANSIColor(str, CYAN_FG, true);
   }

   static public String toMagenta(String str) {
      return toANSIColor(str, MAGENTA_FG, true);
   }

   static public String toRed(String str) {
      return toANSIColor(str, RED_FG, true);
   }

   static public String toYellow(String str) {
      return toANSIColor(str, YELLOW_FG, true);
   }


}
