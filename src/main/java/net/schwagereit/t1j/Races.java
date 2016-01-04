/***************************************************************************
 * *
 * This program is free software; you can redistribute it and/or modify  *
 * it under the terms of the GNU General Public License as published by  *
 * the Free Software Foundation; either version 2 of the License, or     *
 * (at your option) any later version.                                   *
 * *
 ***************************************************************************/
package net.schwagereit.t1j;

/**
 * Check for races. The 8 relevant diagonal lines are numbered clockwise 0 to 7,
 * starting with the steep one at B2.
 *
 * @author Johannes Schwagereit (mail(at)johannes-schwagereit.de)
 */
public final class Races
{
   /**
    * The singleton-instance.
    */
   private static final Races RACES = new Races();

   /**
    * Return the Races-Object. (Singleton)
    *
    * @return Races-Object
    */
   public static Races getRaces()
   {
      return RACES;
   }

   /**
    * Cons'tor
    */
   public Races()
   {
   }

   /**
    * Check for race 0 (upper-left to right, steep).
    *
    * @return true if no blocking race exists
    */
   private static boolean checkFor0(int pinX, int pinY, Board board, int nextPlayer)
   {
      boolean ret = true;
      int vx = (pinX - 1) * 2;
      int vy = (pinY - 1);

//      System.out.println("0 fuer " + GuiBoard.getHoleName(pinX, pinY, false) + ":"
//            + ((vx >= vy) ? " save" : " not save"));

      if (board.getPin(pinX + 1, pinY + 1) == Board.XPLAYER && vx < vy
         && (board.isConnected(pinX + 1, pinY + 1, pinX, pinY - 1)
         || (
         board.isConnected(pinX + 1, pinY + 1, pinX + 2, pinY + 3)
            && board.bridgeAllowed(pinX + 1, pinY + 1, 1)
            && nextPlayer == Board.XPLAYER
      )
      )
         )
      {
         ret = false;
      }

//      System.out.println("Check: " + (ret ? "okay" : "not okay") +
//            " NextPlayer: " + (nextPlayer == Board.XPLAYER ? "X" : "Y"));
      return ret;
   }

   /**
    * Check for race 1 (upper-left to right, gentle).
    *
    * @return true if no blocking race exists
    */
   private static boolean checkFor1(int pinX, int pinY, Board board, int nextPlayer)
   {
      boolean ret = true;
      int vx = (pinX - 1);
      int vy = (pinY - 1) * 2;

//      System.out.println("1 fuer " + GuiBoard.getHoleName(pinX, pinY, false) + ":"
//            + ((vx >= vy) ? " save" : " not save"));

      if (board.getPin(pinX + 1, pinY) == Board.XPLAYER && vx < vy
         && (board.isConnected(pinX + 1, pinY, pinX - 1, pinY - 1)
         || (board.isConnected(pinX + 1, pinY, pinX + 3, pinY + 1)
         && nextPlayer == Board.XPLAYER)))
      {
         ret = false;
      }

//      System.out.println("Check: " + (ret ? "okay" : "not okay") +
//            " NextPlayer: " + (nextPlayer == Board.XPLAYER ? "X" : "Y"));
      return ret;
   }

   /**
    * Check for race 2 (upper-right to left, gentle).
    *
    * @return true if no blocking race exists
    */
   private static boolean checkFor2(int pinX, int pinY, Board board, int nextPlayer)
   {
      boolean ret = true;
      int vx = (board.getXsize() - 2 - pinX);
      int vy = (pinY - 1) * 2;

//      System.out.println("2 fuer " + GuiBoard.getHoleName(pinX, pinY, false) + ":"
//            + ((vx >= vy) ? " save" : " not save"));

      if (board.getPin(pinX - 1, pinY) == Board.XPLAYER && vx < vy
         && (board.isConnected(pinX - 1, pinY, pinX + 1, pinY - 1)
         || (board.isConnected(pinX - 1, pinY, pinX - 3, pinY + 1)
         && nextPlayer == Board.XPLAYER)))
      {
         ret = false;
      }

//      System.out.println("Check: " + (ret ? "okay" : "not okay") +
//            " NextPlayer: " + (nextPlayer == Board.XPLAYER ? "X" : "Y"));
      return ret;
   }

   /**
    * Check for race 3 (upper-right to left, steep).
    *
    * @return true if no blocking race exists
    */
   private static boolean checkFor3(int pinX, int pinY, Board board, int nextPlayer)
   {
      boolean ret = true;
      int vx = (board.getXsize() - 2 - pinX) * 2;
      int vy = (pinY - 1);

//      System.out.println("3 fuer " + GuiBoard.getHoleName(pinX, pinY, false) + ":"
//            + ((vx >= vy) ? " save " : " not save ") + vx + "," + vy + "-" + (board.getYsize() - 2 - pinY));

      if (board.getPin(pinX - 1, pinY + 1) == Board.XPLAYER && vx < vy
         && (board.isConnected(pinX - 1, pinY + 1, pinX, pinY - 1)
         || (board.isConnected(pinX - 1, pinY + 1, pinX - 2, pinY + 3)
         && board.bridgeAllowed(pinX - 1, pinY + 1, 2)
         && nextPlayer == Board.XPLAYER)))
      {
         ret = false;
      }

//      System.out.println("Check: " + (ret ? "okay" : "not okay") +
//            " NextPlayer: " + (nextPlayer == Board.XPLAYER ? "X" : "Y"));
      return ret;
   }

   /**
    * Check for race 4 (down-right to left, steep).
    *
    * @return true if no blocking race exists
    */
   private static boolean checkFor4(int pinX, int pinY, Board board, int nextPlayer)
   {
      boolean ret = true;
      int vx = (board.getXsize() - 2 - pinX) * 2;
      int vy = (board.getYsize() - 2 - pinY);

//      System.out.println("4 fuer " + GuiBoard.getHoleName(pinX, pinY, false) + ":"
//            + ((vx >= vy) ? " save " : " not save ") + vx + "," + vy + "-" + (board.getYsize() - 2 - pinY));

      if (board.getPin(pinX - 1, pinY - 1) == Board.XPLAYER && vx < vy
         && (board.isConnected(pinX - 1, pinY - 1, pinX, pinY + 1)
         || (board.isConnected(pinX - 1, pinY - 1, pinX - 2, pinY - 3)
         && board.bridgeAllowed(pinX, pinY + 1, 1)
         && nextPlayer == Board.XPLAYER)))
      {
         ret = false;
      }

//      System.out.println("Check: " + (ret ? "okay" : "not okay") +
//            " NextPlayer: " + (nextPlayer == Board.XPLAYER ? "X" : "Y"));
      return ret;
   }


   /**
    * Check for race 5 (down-right to left, gentle).
    *
    * @return true if no blocking race exists
    */
   private static boolean checkFor5(int pinX, int pinY, Board board, int nextPlayer)
   {
      boolean ret = true;
      int vx = (board.getXsize() - 2 - pinX);
      int vy = (board.getYsize() - 2 - pinY) * 2;

//      System.out.println("5 fuer " + GuiBoard.getHoleName(pinX, pinY, false) + ":"
//            + ((vx >= vy) ? " save" : " not save"));

      if (board.getPin(pinX - 1, pinY) == Board.XPLAYER && vx < vy
         && (board.isConnected(pinX - 1, pinY, pinX + 1, pinY + 1)
         || (board.isConnected(pinX - 1, pinY, pinX - 3, pinY - 1)
         && nextPlayer == Board.XPLAYER)))
      {
         ret = false;
      }

//      System.out.println("Check: " + (ret ? "okay" : "not okay") +
//            " NextPlayer: " + (nextPlayer == Board.XPLAYER ? "X" : "Y"));
      return ret;
   }

   /**
    * Check for race 6 (down-left to right, gentle).
    *
    * @return true if no blocking race exists
    */
   private static boolean checkFor6(int pinX, int pinY, Board board, int nextPlayer)
   {
      boolean ret = true;
      int vx = (pinX - 1);
      int vy = (board.getYsize() - 2 - pinY) * 2; // check for 6

//      System.out.println("6 fuer " + GuiBoard.getHoleName(pinX, pinY, false) + ":"
//            + ((vx >= vy) ? " save" : " not save"));

      if (board.getPin(pinX + 1, pinY) == Board.XPLAYER && vx < vy
         && (board.isConnected(pinX + 1, pinY, pinX - 1, pinY + 1)
         || (board.isConnected(pinX + 1, pinY, pinX + 3, pinY - 1)
         && nextPlayer == Board.XPLAYER)))
      {
         ret = false;
      }

//      System.out.println("Check: " + (ret ? "okay" : "not okay") +
//            " NextPlayer: " + (nextPlayer == Board.XPLAYER ? "X" : "Y"));
      return ret;
   }

   /**
    * Check for race 7 (down-left to right, steep).
    *
    * @return true if no blocking race exists
    */
   private static boolean checkFor7(int pinX, int pinY, Board board, int nextPlayer)
   {
      boolean ret = true;
      int vx = (pinX - 1) * 2;
      int vy = (board.getYsize() - 2 - pinY);

//      System.out.println("7 fuer " + GuiBoard.getHoleName(pinX, pinY, false) + ":"
//            + ((vx >= vy) ? " save" : " not save"));

      if (board.getPin(pinX + 1, pinY - 1) == Board.XPLAYER && vx < vy
         && (board.isConnected(pinX + 1, pinY - 1, pinX, pinY + 1)
         || (board.isConnected(pinX + 1, pinY - 1, pinX + 2, pinY - 3)
         && board.bridgeAllowed(pinX, pinY + 1, 2)
         && nextPlayer == Board.XPLAYER)))
      {
         ret = false;
      }

//      System.out.println("Check: " + (ret ? "okay" : "not okay") +
//            " NextPlayer: " + (nextPlayer == Board.XPLAYER ? "X" : "Y"));
      return ret;
   }

   /**
    * Check for blocking pins if opponent (XPlayer) has next turn.
    * Method looks to south.
    *
    * @return false if blocking pin was found
    */
   private static boolean blockingBottom(int pinX, int pinY, Board board, int nextPlayer)
   {
      // everything okay if my turn or nothing is blocking
      return nextPlayer == Board.YPLAYER
         || checkBottomPin(board, pinX, pinY + 1)
         && checkBottomPin(board, pinX, pinY + 2)
         && checkBottomPin(board, pinX + 1, pinY + 1)
         && checkBottomPin(board, pinX - 1, pinY + 1)
         && checkBottomPinTwo(board, pinX, pinY + 3)
         && checkBottomPinTwo(board, pinX, pinY + 4);
   }

   /**
    * Check for blocking pins if opponent (XPlayer) has next turn.
    * Method looks to north.
    *
    * @return false if blocking pin was found
    */
   private boolean blockingTop(int pinX, int pinY, Board board, int nextPlayer)
   {
      // everything okay if my turn or nothing is blocking
      return (nextPlayer == Board.YPLAYER)
         || checkTopPin(board, pinX, pinY - 1)
         && checkTopPin(board, pinX, pinY - 2)
         && checkTopPin(board, pinX + 1, pinY - 1)
         && checkTopPin(board, pinX - 1, pinY - 1)
         && checkTopPinTwo(board, pinX, pinY - 3)
         && checkTopPinTwo(board, pinX, pinY - 4);
   }

   /**
    * Check if this pin is an opponent-pin and has connection.
    * The two connection down are not checked.
    *
    * @param oppX x of pin to check
    * @param oppY y of pin to check
    * @return true if pin is not blocking
    */
   private static boolean checkBottomPin(Board board, final int oppX, final int oppY)
   {
      return !(board.getPin(oppX, oppY) == Board.XPLAYER
         && (board.isBridged(oppX, oppY, 0)
         || board.isBridged(oppX, oppY, 1)
         || board.isBridged(oppX, oppY, 2)
         || board.isBridged(oppX, oppY, 3)
         || board.isBridged(oppX + 2, oppY + 1, 0)
         || board.isBridged(oppX - 2, oppY + 1, 3)
      ));
   }

   /**
    * Check if this pin is an opponent-pin and has connection.
    * The two connection up are not checked.
    *
    * @param oppX x of pin to check
    * @param oppY y of pin to check
    * @return true if pin is not blocking
    */
   private static boolean checkTopPin(Board board, final int oppX, final int oppY)
   {
      return !(board.getPin(oppX, oppY) == Board.XPLAYER
         && (board.isBridged(oppX, oppY, 0)
         || board.isBridged(oppX, oppY, 3)
         || board.isBridged(oppX + 2, oppY + 1, 0)
         || board.isBridged(oppX + 1, oppY + 2, 1)
         || board.isBridged(oppX - 1, oppY + 2, 2)
         || board.isBridged(oppX - 2, oppY + 1, 3)
      ));
   }

   /**
    * Check if this pin is an opponent-pin and has connection.
    * Only two connection are checked.
    *
    * @param oppX x of pin to check
    * @param oppY y of pin to check
    * @return true if pin is not blocking
    */
   private static boolean checkBottomPinTwo(Board board, final int oppX, final int oppY)
   {
      return oppY >= board.getYsize()
         || !(board.getPin(oppX, oppY) == Board.XPLAYER
         && (board.isBridged(oppX, oppY, 0)
         || board.isBridged(oppX, oppY, 3)));
   }

   /**
    * Check if this pin is an opponent-pin and has connection.
    * Only two connection are checked.
    *
    * @param oppX x of pin to check
    * @param oppY y of pin to check
    * @return true if pin is not blocking
    */
   private static boolean checkTopPinTwo(Board board, final int oppX, final int oppY)
   {
      return oppY <= 0
         || !(board.getPin(oppX, oppY) == Board.XPLAYER
         && (board.isBridged(oppX - 2, oppY + 1, 3)
         || board.isBridged(oppX + 2, oppY + 1, 0)));
   }

   /**
    * Check if pin can be used as connection to bottom.
    *
    * @param fx           x-pos of pin
    * @param fy           y-pos of pin
    * @param board      the board
    * @param nextPlayer who's next?
    * @return true, if allowed
    */
   public boolean checkBottom(final int fx, final int fy, final Board board,
                              final int nextPlayer)
   {
      //check for direct block or blocking races
      return blockingBottom(fx, fy, board, nextPlayer)
         && checkFor5(fx, fy, board, nextPlayer)
         && checkFor6(fx, fy, board, nextPlayer)
         && checkFor4(fx, fy, board, nextPlayer)
         && checkFor7(fx, fy, board, nextPlayer);
   }

   /**
    * Check if pin can be used as connection to top.
    *
    * @param fx           x-pos of pin
    * @param fy           y-pos of pin
    * @param board        the board
    * @param nextPlayer   who's next?
    * @return             true, if allowed
    */
   public boolean checkTop(final int fx, final int fy, final Board board,
                           final int nextPlayer)
   {
      //check for direct block or blocking races
      return blockingTop(fx, fy, board, nextPlayer)
         && checkFor1(fx, fy, board, nextPlayer)
         && checkFor2(fx, fy, board, nextPlayer)
         && checkFor0(fx, fy, board, nextPlayer)
         && checkFor3(fx, fy, board, nextPlayer);
   }

}
