package net.schwagereit.t1j;

/**
 * @author Erich Eichinger
 * @since 03/01/2016
 */
public final class BoardLabels {

   /**
    * Array of column headers.
    */
   private static final String[] COL_NAMES = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
      "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA",
      "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ"};

   /**
    * get the name of given hole as String, e.g. D4
    *
    * @param x         x
    * @param y         y
    * @param withSpace print Move with spaces
    * @return Name of Hole
    */
   public static String holeName(final int x, final int y, final boolean withSpace) {
      if (x < 0 || y < 0) {
         return "";
      }
      return "" + COL_NAMES[x] + (withSpace ? " " : "") + (y + 1);
   }

   public static String xLabel(int x) {
        return COL_NAMES[x];
   }
}
