/**
 * 
 * Created by IntelliJ IDEA.
 * @author Johannes Schwagereit (mail(at)johannes-schwagereit.de)
 */
package net.schwagereit.t1j;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;

/**
 * This class implements Zobrist hashing.
 */
public class Zobrist
{
   final private static class Node
   {
      private int valueX; //value if pin set by XPlayer
      private int valueY; //value if pin set by YPlayer

      private final int[] bridge = new int[4];
   }

   final private static class UniqueRng {
      private final Random rand;

      /** Set of already drawn numbers - avoid duplicates. */
      private final Set<Integer> alreadyDrawn;

      public UniqueRng(long seed) {
         rand = new Random(seed);
         alreadyDrawn = new HashSet<Integer>(700);
      }

      /**
       * Get a random number. Avoid duplicates.
       * @return int
       */
      int nextInt()
      {
         int val;
         do
         {
            val = rand.nextInt();
         } while (alreadyDrawn.contains(val));

         alreadyDrawn.add(val);

         return val;
      }
   }

   private static final Zobrist ourInstance = new Zobrist();

   /**
    * Return the Zobrist-Object.
    *
    * @return Zobrist-Object
    */
   public static Zobrist getInstance()
   {
      return ourInstance;
   }

   private final Node[][] field = new Node[Board.MAXDIM][Board.MAXDIM];

   /**
    * Constructor - no external instance.
    */
   private Zobrist()
   {
      initialize();
   }

   /**
    * Initialize the data structure.
    */
   private void initialize()
   {
      UniqueRng uniqueRng = new UniqueRng(0);

      for (int j = 0; j < field.length; j++)
      {
         for (int i = 0; i < field.length; i++)
         {
            fill(i,j, uniqueRng);
         }
      }
   }

   /**
    * Fill one of the positions.
    * @param i x
    * @param j y
    */
   private void fill(int i, int j, UniqueRng uniqueRng)
   {
      Node node = new Node();
      node.valueX = uniqueRng.nextInt();
      node.valueY = uniqueRng.nextInt();
      node.bridge[0] = uniqueRng.nextInt();
      node.bridge[1] = uniqueRng.nextInt();
      node.bridge[2] = uniqueRng.nextInt();
      node.bridge[3] = uniqueRng.nextInt();
      field[i][j] = node;
   }

   /**
    * Return the value for a pin set or removed.
    * @param x x
    * @param y y
    * @param player player
    * @return int
    */
   public int getPinValue(int x, int y, int player)
   {
      if (player == Board.XPLAYER)
      {
         return field[x][y].valueX;
      }
      else
      {
         return field[x][y].valueY;
      }
   }
   /**
    * Return the value for a link set or removed.
    * @param x x
    * @param y y
    * @param link no of link (0-3)
    * @return int
    */
   public int getLinkValue(int x, int y, int link)
   {
      return field[x][y].bridge[link];
   }
}
