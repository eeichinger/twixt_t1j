/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package net.schwagereit.t1j;

/**
 * Representation of playing boards. Board is implemented as singleton
 * 
 * @author Johannes Schwagereit (mail(at)johannes-schwagereit.de)
 *
 */
public final class Board
{
   public static class BoardState {
      private final Node[][] field = new Node[MAXBOARDSIZE][MAXBOARDSIZE];

      public BoardState(Board board) {
         // TODO: do we really need to copy the "max" board size or is xsize/ysize enough
         for (int i = 0; i < MAXBOARDSIZE; i++)
         {
            for (int j = 0; j < MAXBOARDSIZE; j++)
            {
               this.field[i][j] = new Node(board.field[i][j]);
            }
         }
      }

      public boolean equals(BoardState other)
      {
         for (int i = 0; i < MAXBOARDSIZE; i++)
         {
            for (int j = 0; j < MAXBOARDSIZE; j++)
            {
               if (!this.field[i][j].equals(other.field[i][j])) {
                  return false;
               }
            }
         }
         return true;
      }
   }

   public BoardState captureState()
   {
      return new BoardState(this);
   }

   public interface BoardListener
   {
      void addForY(int x, int y);
      void removeForY(int x, int y, int player);
   }

   final static private class Node
   {
      private int player; //0 or XPLAYER or YPLAYER

      private final int[] bridge = new int[4];

      public Node() {
      }

      public Node(Node other) {
         this.copyFrom(other);
      }

      /**
       * Initialize a node.
       */
      void clear()
      {
         player = 0;
         bridge[0] = 0;
         bridge[1] = 0;
         bridge[2] = 0;
         bridge[3] = 0;
      }

      public int hashCode() {
         throw new UnsupportedOperationException("node is not meant to be used as a key");
      }

      public boolean equals(Object o) {
         throw new UnsupportedOperationException("should never get here");
      }

      /**
       * Copy all values from a source-node to another node.
       * @param source the source
       */
      public void copyFrom(Node source)
      {
         player = source.player;
         bridge[0] = source.bridge[0];
         bridge[1] = source.bridge[1];
         bridge[2] = source.bridge[2];
         bridge[3] = source.bridge[3];
      }
   }

   /** perform various validations & checks */
   public static final boolean DEBUG = false;

   /** playing East-West. */
   public static final int XPLAYER = 1;

   /** playing North-South. */
   public static final int YPLAYER = -1;

   private static final Board BOARD_Y = new Board(); // this is the real board
   private static final Board BOARD_X = new Board(); // this board is mirrored on x/y-axis
   private static final Board BOARD_DISP = new Board(); // this board is displayed

   /** min. dimension the board should have. */
   public static final int MINDIM = 12;

   /** max. dimension the board could have. */
   public static final int MAXDIM = 36;

   /** default dimension of the board. */
   public static final int DEFAULTDIM = 24;

   /** border around field. */
   private static final int MARGIN = 3;

   /** max required field array size */
   private static final int MAXBOARDSIZE = MAXDIM + MARGIN * 2;

   /** value of bridge if bridge was layed. */
   private static final int BRIDGED = 10;

   /** the Zobrist value generator */
   private static final Zobrist ZOBRIST = Zobrist.getInstance();

   /**
    * Get one of the two existing board.
    * (Sort of factory pattern.)
    *
    * @param player orientation of the board
    * @return the Board
    */
   public static Board getBoard(final int player)
   {
      if (player == XPLAYER)
      {
         return BOARD_X; // mirrored board
      }
      else
      {
         return BOARD_Y; // main board
      }
   }

   /**
    * get board to display.
    * @return board
    */
   public static Board getBoardDisplay()
   {
      return BOARD_DISP;
   }

   /**
    * Copy all data from the y-board to the display-board.
    */
   public static void copyYtoDisplay()
   {
      BOARD_DISP.copyFrom(BOARD_Y);
   }

   /** This board used for zobrist-hashing. */
   private boolean zobristEnabled;


   /** Value of board for zobrist-hashing. */
   private int zobristValue;

   /** size of board. */
   private int xsize, ysize;

   private final Node[][] field = new Node[MAXBOARDSIZE][MAXBOARDSIZE];
   
   private BoardListener boardListener;

   /**
    * Cons'tor - no external instance.
    */
   Board()
   { //some initialization
      int i, j;
      for (i = 0; i < field.length; i++)
         for (j = 0; j < field[i].length; j++)
         {
            field[i][j] = new Node();
         }
      zobristEnabled = false;
      zobristValue = 0;
      setSize(24, 24);
   }

   public void setBoardListener(BoardListener listener) {
      if (listener != null && this.boardListener != null) {
         throw new IllegalStateException("boardListener already set");
      }
      this.boardListener = listener;
   }

   /**
    * Copy all data from the y-board to the display-board.
    */
   public void copyFrom(Board other)
   {
      this.xsize = other.xsize;
      this.ysize = other.ysize;
      // TODO: do we really need to copy the "max" board size or is xsize/ysize enough
      for (int i = 0; i < MAXBOARDSIZE; i++)
         for (int j = 0; j < MAXBOARDSIZE; j++)
         {
            this.field[i][j].copyFrom(other.field[i][j]);
         }
   }

   /**
    * Set size of current board (not allowed during game).
    *
    * @param xSize
    *           x-Size
    * @param ySize
    *           y-Size
    */
   public void setSize(final int xSize, final int ySize)
   {
      rejectIf(
         xSize > MAXDIM || ySize > MAXDIM || xSize < MINDIM || ySize < MINDIM
         , "Size has to be between " + MINDIM + " and "+ MAXDIM + "."
      );

      xsize = xSize;
      ysize = ySize;
   }

   /**
    * Get x-size of the board.
    * @return x-size of the board
    */
   public int getXsize()
   {
      return xsize;
   }

   /**
    * Get y-size of the board.
    * @return y-size of the board
    */
   public int getYsize()
   {
      return ysize;
   }

   /**
    * Clear the board between two games.
    */
   public void clearBoard()
   {
      int i, j;
      for (i = 0; i < MAXBOARDSIZE; i++)
         for (j = 0; j < MAXBOARDSIZE; j++)
         {
            field[i][j].clear();
         }
      zobristValue = 0;
   }

   /**
    * Add a bridge. Works with corrected data (+3)
    *
    * @param x
    *           x
    * @param y
    *           y
    * @param direction
    *           Allowed values are 0 to 3
    */
   private void setBridge(final int x, final int y, final int direction)
   {
      rejectIf(
         direction > 3 || direction < 0
         , "direction " + direction + " is not allowed."
      );

      if (field[x][y].bridge[direction] > 0)
      {
         return; //bridge cannot be layed because something is blocking me
      }
      field[x][y].bridge[direction] = BRIDGED;
      zobristValue ^= ZOBRIST.getLinkValue(x, y, direction);

      //now mark all the 9 crossing bridges as illegal
      switch (direction)
      {
      case 0:
         field[x    ][y + 1].bridge[1]++;
         field[x - 1][y + 1].bridge[2]++;
         field[x - 2][y + 1].bridge[2]++;
         field[x - 1][y    ].bridge[3]++;
         field[x - 1][y    ].bridge[2]++;
         field[x - 1][y    ].bridge[1]++;
         field[x - 2][y    ].bridge[3]++;
         field[x - 2][y    ].bridge[2]++;
         field[x - 3][y    ].bridge[3]++;
         break;
      case 1:
         field[x - 1][y + 1].bridge[2]++;
         field[x + 1][y    ].bridge[0]++;
         field[x - 1][y    ].bridge[3]++;
         field[x - 1][y    ].bridge[2]++;
         field[x - 2][y    ].bridge[3]++;
         field[x    ][y - 1].bridge[0]++;
         field[x - 1][y - 1].bridge[3]++;
         field[x - 1][y - 1].bridge[2]++;
         field[x - 2][y - 1].bridge[3]++;
         break;
      case 2:
         field[x + 1][y + 1].bridge[1]++;
         field[x - 1][y    ].bridge[3]++;
         field[x + 1][y    ].bridge[0]++;
         field[x + 1][y    ].bridge[1]++;
         field[x + 2][y    ].bridge[0]++;
         field[x    ][y - 1].bridge[3]++;
         field[x + 1][y - 1].bridge[0]++;
         field[x + 1][y - 1].bridge[1]++;
         field[x + 2][y - 1].bridge[0]++;
         break;
      case 3:
         field[x    ][y + 1].bridge[2]++;
         field[x + 1][y + 1].bridge[1]++;
         field[x + 2][y + 1].bridge[1]++;
         field[x + 1][y    ].bridge[0]++;
         field[x + 1][y    ].bridge[1]++;
         field[x + 1][y    ].bridge[2]++;
         field[x + 2][y    ].bridge[0]++;
         field[x + 2][y    ].bridge[1]++;
         field[x + 3][y    ].bridge[0]++;
         break;
      default:
         throw new IllegalArgumentException("direction illegal");
      }
   }

   /**
    * Remove bridge from board.
    *
    * @param x
    *           x
    * @param y
    *           y
    * @param direction
    *           direction of bridge
    */
   private void removeBridge(final int x, final int y, final int direction)
   {
      if (field[x][y].bridge[direction] < BRIDGED)
      {
         return; //there was no bridge
      }
      field[x][y].bridge[direction] = 0;
      zobristValue ^= ZOBRIST.getLinkValue(x, y, direction);
      
      //now remove mark for the 9 crossing
      switch (direction)
      {
      case 0:
         field[x    ][y + 1].bridge[1]--;
         field[x - 1][y + 1].bridge[2]--;
         field[x - 2][y + 1].bridge[2]--;
         field[x - 1][y    ].bridge[3]--;
         field[x - 1][y    ].bridge[2]--;
         field[x - 1][y    ].bridge[1]--;
         field[x - 2][y    ].bridge[3]--;
         field[x - 2][y    ].bridge[2]--;
         field[x - 3][y    ].bridge[3]--;
         break;
      case 1:
         field[x - 1][y + 1].bridge[2]--;
         field[x + 1][y    ].bridge[0]--;
         field[x - 1][y    ].bridge[3]--;
         field[x - 1][y    ].bridge[2]--;
         field[x - 2][y    ].bridge[3]--;
         field[x    ][y - 1].bridge[0]--;
         field[x - 1][y - 1].bridge[3]--;
         field[x - 1][y - 1].bridge[2]--;
         field[x - 2][y - 1].bridge[3]--;
         break;
      case 2:
         field[x + 1][y + 1].bridge[1]--;
         field[x - 1][y    ].bridge[3]--;
         field[x + 1][y    ].bridge[0]--;
         field[x + 1][y    ].bridge[1]--;
         field[x + 2][y    ].bridge[0]--;
         field[x    ][y - 1].bridge[3]--;
         field[x + 1][y - 1].bridge[0]--;
         field[x + 1][y - 1].bridge[1]--;
         field[x + 2][y - 1].bridge[0]--;
         break;
      case 3:
         field[x    ][y + 1].bridge[2]--;
         field[x + 1][y + 1].bridge[1]--;
         field[x + 2][y + 1].bridge[1]--;
         field[x + 1][y    ].bridge[0]--;
         field[x + 1][y    ].bridge[1]--;
         field[x + 1][y    ].bridge[2]--;
         field[x + 2][y    ].bridge[0]--;
         field[x + 2][y    ].bridge[1]--;
         field[x + 3][y    ].bridge[0]--;
         break;
      default:
         throw new IllegalArgumentException("direction illegal");
      }
   }

   /**
    * set a pin with boundary-checks. Input data is corrected (+MARGIN).
    *
    * @param xin
    *           x
    * @param yin
    *           y
    * @param player
    *           XPLAYER or YPLAYER
    * @return setting pin was successful
    */
   public boolean setPin(final int xin, final int yin, final int player)
   {
      if (!pinAllowed(xin, yin, player))
      {
         return false;
      }

      if (zobristEnabled)
      {
         zobristValue ^= ZOBRIST.getPinValue(xin, yin, player);
      }

      int x = xin + MARGIN; //the input data is corrected to reflect the margin
      int y = yin + MARGIN;
      //checks are done, so lets move on
      field[x][y].player = player;
      //checks for bridges if the other pin has the same color
      if (field[x - 2][y - 1].player == player)
      {
         setBridge(x, y, 0);
      }
      if (field[x - 1][y - 2].player == player)
      {
         setBridge(x, y, 1);
      }
      if (field[x + 1][y - 2].player == player)
      {
         setBridge(x, y, 2);
      }
      if (field[x + 2][y - 1].player == player)
      {
         setBridge(x, y, 3);
      }
      if (field[x + 2][y + 1].player == player)
      {
         setBridge(x + 2, y + 1, 0);
      }
      if (field[x + 1][y + 2].player == player)
      {
         setBridge(x + 1, y + 2, 1);
      }
      if (field[x - 1][y + 2].player == player)
      {
         setBridge(x - 1, y + 2, 2);
      }
      if (field[x - 2][y + 1].player == player)
      {
         setBridge(x - 2, y + 1, 3);
      }

      if (boardListener != null) {
         boardListener.addForY(xin, yin);
      }
      return true; //set was okay
   }

   /**
    * remove a set pin.
    *
    * @param xin
    *           x
    * @param yin
    *           y
    * @param player player
    * @return true if okay
    */
   public boolean removePin(final int xin, final int yin, final int player)
   {
      int x = xin + MARGIN; //the input data is corrected to reflect the margin
      int y = yin + MARGIN;
      final int fieldPlayer = field[x][y].player;
      if (fieldPlayer != player) //empty field or opponent pin?
      {
         return false;
      }

      if (zobristEnabled)
      {
         zobristValue ^= ZOBRIST.getPinValue(xin, yin, fieldPlayer);
      }

      //checks are done, so lets move on
      field[x][y].player = 0;
      //checks for bridges if the other pin has the same color
      removeBridge(x, y, 0);
      removeBridge(x, y, 1);
      removeBridge(x, y, 2);
      removeBridge(x, y, 3);
      removeBridge(x + 2, y + 1, 0);
      removeBridge(x + 1, y + 2, 1);
      removeBridge(x - 1, y + 2, 2);
      removeBridge(x - 2, y + 1, 3);

      if (boardListener != null) {
         boardListener.removeForY(xin, yin, player);
      }
      return true; //remove was okay
   }

   /**
    * Check if it would be allowed to set specified pin.
    *
    * @param xin
    *           x
    * @param yin
    *           y
    * @param player
    *           player
    * @return true, if pin allowed
    */
   public boolean pinAllowed(final int xin, final int yin, final int player)
   {
      return !((player == XPLAYER && (yin < 1 || yin > ysize - 2 || xin < 0 || xin > xsize - 1))
            || (player == YPLAYER && (yin < 0 || yin > ysize - 1 || xin < 1 || xin > xsize - 2))
            || (field[xin + 3][yin + 3].player != 0));
      // simplified from
//      return (player == XPLAYER && (yin < 1 || yin > ysize - 2 || xin < 0 || xin > xsize - 1))
//            || (player == YPLAYER && (yin < 0 || yin > ysize - 1 || xin < 1 || xin > xsize - 2))
//            || (field[xin + 3][yin + 3].value != 0) ? false : true;

   }

   /**
    * Check if two points are connected by bridge.
    * 
    * @param xa
    *           x of first pin
    * @param ya
    *           y of first pin
    * @param xb
    *           x of second pin
    * @param yb
    *           y of second pin
    * @return true, if connected
    */
   public boolean isConnected(final int xa, final int ya, final int xb, final int yb)
   {
      rejectIf(
         Math.abs(xa - xb) >= 3 || Math.abs(ya - yb) >= 3
            || Math.abs(xa - xb) + Math.abs(ya - yb) != 3
         , "Wrong distance"
      );

      //data is NOT corrected (margin!) here because isBridged corrects them
      //put lower first
      if (ya < yb)
      {
         return isBridged(xb, yb, (xa < xb) ? xa - xb + 2 : xa - xb + 1);
      } else
      {
         return isBridged(xa, ya, (xb < xa) ? xb - xa + 2 : xb - xa + 1);
      }
   }

   /**
    * Check if bridge allowed between two points.
    *
    * @param xa x of first pin
    * @param ya y of first pin
    * @param xb x of second pin
    * @param yb y of second pin
    * @return true, if connected
    */
   public boolean isBridgeAllowed(final int xa, final int ya, final int xb, final int yb)
   {
      rejectIf(
         Math.abs(xa - xb) >= 3 || Math.abs(ya - yb) >= 3
            || Math.abs(xa - xb) + Math.abs(ya - yb) != 3
         , "Wrong distance"
      );

      //data is NOT corrected (margin!) here because isBridged corrects them
      //put lower first
      if (ya < yb)
      {
         return bridgeAllowed(xb, yb, (xa < xb) ? xa - xb + 2 : xa - xb + 1);
      } else
      {
         return bridgeAllowed(xa, ya, (xb < xa) ? xb - xa + 2 : xb - xa + 1);
      }
   }

   /**
    * get Value of pin a (x,y).
    * 
    * @param x
    *           x
    * @param y
    *           y
    * @return 0 (==empty) or owner of pin
    */
   public int getPin(final int x, final int y)
   {
      //the input data is corrected to reflect the margin
      return field[x + MARGIN][y + MARGIN].player;
   }

   /**
    * Check if specified bridge exists.
    * 
    * @param x
    *           x
    * @param y
    *           y
    * @param direction
    *           direction
    * @return true, if bridge exists
    */
   public boolean isBridged(final int x, final int y, final int direction)
   {
      //the input data is corrected to reflect the margin
      return field[x + MARGIN][y + MARGIN].bridge[direction] >= Board.BRIDGED;
   }

   /**
    * Check if specified bridge is allowed, but does not exists.
    * 
    * @param x
    *           x
    * @param y
    *           y
    * @param direction
    *           direction
    * @return true, if bridge allowed
    */
   public boolean bridgeAllowed(final int x, final int y, final int direction)
   {
      //the input data is corrected to reflect the margin
      return field[x + MARGIN][y + MARGIN].bridge[direction] == 0;
   }

   /**
    * A Pin is strong if it is connected to any other pin.
    * @param hx x
    * @param hy y
    * @return true if pin is strong
    */
   public boolean isStrong(int hx, int hy)
   {
      return isBridged(hx, hy, 0) || isBridged(hx, hy, 1) || isBridged(hx, hy, 2)
            || isBridged(hx, hy, 3) || isBridged(hx + 2, hy + 1, 0) ||
            isBridged(hx + 1, hy + 2, 1) || isBridged(hx - 1, hy + 2, 2) ||
            isBridged(hx - 2, hy + 1, 3);
   }

   /**
    * En/disable zobrist hashing.
    * @param zobristEnabledIn Value
    */
   public void setZobristEnabled(boolean zobristEnabledIn)
   {
      this.zobristEnabled = zobristEnabledIn;
   }

   /**
    * get current zobrist value.
    * @return int
    */
   public int getZobristValue()
   {
      return zobristValue;
   }

   @Override
   public int hashCode() {
      throw new UnsupportedOperationException();
   }

   public boolean equals(Object o) {
      throw new UnsupportedOperationException();
   }

   @SuppressWarnings("PointlessBooleanExpression")
   private static void rejectIf(boolean condition, String message) {
      if (!DEBUG) return;
      if (condition)
      {
         throw new IllegalArgumentException(message);
      }
   }
}
