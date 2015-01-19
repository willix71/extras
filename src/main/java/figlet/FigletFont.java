package figlet;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * FigletFont Java
 * 
 * @author Benoit Rigaut CERN July 96 www.rigaut.com benoit@rigaut.com released
 *         with GPL the 13th of november 2000 (my birthday!)
 *
 */
public class FigletFont {
   public char hardblank;
   public int height = -1;
   public int heightWithoutDescenders = -1;
   public int maxLine = -1;
   public int smushMode = -1;
   public char font[][][] = null;
   public String fontName = null;

   public char[][][] getFont() {
      return font;
   }

   public char[][] getChar(int c) {
      return font[c];
   }

   public String getCharLineString(int c, int l) {
      if (font[c][l] == null) return null;
      else return new String(font[c][l]);
   }

   public FigletFont(URL url) {
      try {
         InputStream conn = url.openStream();
         init(new BufferedReader(new InputStreamReader(conn)));
         conn.close();
      } catch (IOException ioe) {
         throw new IllegalArgumentException(ioe);
      }
   }

   public FigletFont(InputStream inStrean) {
      try {
         init(new BufferedReader(new InputStreamReader(inStrean)));
         inStrean.close();
      } catch (IOException ioe) {
         throw new IllegalArgumentException(ioe);
      }
   }

   public FigletFont(BufferedReader data) {
      try {
         init(data);
         data.close();
      } catch (IOException ioe) {
         throw new IllegalArgumentException(ioe);
      }
   }

   private void init(BufferedReader data) throws IOException {
      font = new char[256][][];

      String dummyS = data.readLine();
      StringTokenizer st = new StringTokenizer(dummyS, " ");
      String s = st.nextToken();
      hardblank = s.charAt(s.length() - 1);
      height = Integer.parseInt(st.nextToken());
      heightWithoutDescenders = Integer.parseInt(st.nextToken());
      maxLine = Integer.parseInt(st.nextToken());
      smushMode = Integer.parseInt(st.nextToken());
      int dummyI = Integer.parseInt(st.nextToken());

      /*
       * try to read the font name as the first word of the first comment line,
       * but this is not standardized !
       */
      st = new StringTokenizer(data.readLine(), " ");
      if (st.hasMoreElements()) fontName = st.nextToken();
      else fontName = "";

      for (int i = 0; i < dummyI - 1; i++) {
         // skip the comments
         dummyS = data.readLine();
      }

      for (int i = 32; i < 256; i++) { // for all the characters
         //System.out.print(i+":");
         for (int h = 0; h < height; h++) {
            dummyS = data.readLine();
            
            if (dummyS != null) {
               int iNormal = i;
               
               boolean abnormal = true;
               if (h == 0) {
                  try {
                     i = Integer.parseInt(dummyS);
                  } catch (NumberFormatException e) {
                     abnormal = false;
                  }
                  if (abnormal) {
                     dummyS = data.readLine();
                  } else {
                     i = iNormal;
                  }
               }

               if (h == 0) {
                  font[i] = new char[height][];
               }
               int t = dummyS.length() - 1 - ((h == height - 1) ? 1 : 0);
               if (height == 1) {
                  t++;
               }

               font[i][h] = new char[t];
               for (int l = 0; l < t; l++) {
                  char a = dummyS.charAt(l);
                  font[i][h][l] = (a == hardblank) ? ' ' : a;
               }
            }
         }
      }
   }
}