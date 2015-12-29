package net.schwagereit.t1j;

import junit.framework.TestCase;
// import net.schwagereit.t1j.Board;

// test some boad-methods
public class BoardTest extends TestCase
{

   Board bo; 
   
   protected void setUp()
   {
      bo = Board.getBoard(Board.YPLAYER);
      bo.setSize(24, 24);
      Zobrist.getInstance().initialize();
   }
   
   /*
    * Test method for 'net.schwagereit.t1j.Board.setPin(int, int, int)'
    */
   public void testSetPin()
   {
      assertTrue(bo.setPin(7, 8, Board.YPLAYER));
   }

   public void testSetPinExists()
   {
      assertTrue(bo.setPin(4, 4, Board.YPLAYER));
      
      assertTrue(bo.getPin(4, 4) == Board.YPLAYER);
   }
   
   /*
    * Test method for 'net.schwagereit.t1j.Board.removePin(int, int, int)'
    */
   public void testRemovePin()
   {
      assertTrue(bo.setPin(2, 2, Board.YPLAYER));
      assertFalse(bo.getPin(2, 2) == 0);
      bo.removePin(2, 2, Board.YPLAYER);
      
      assertTrue(bo.getPin(2, 2) == 0);
   }

   public void testConnected()
   {
      assertTrue(bo.setPin(7, 7, Board.YPLAYER));
      assertTrue(bo.setPin(9, 8, Board.YPLAYER));
      assertTrue(bo.isConnected(7, 7, 9, 8));
      assertFalse(bo.isConnected(7, 7, 8, 9));
      //also test switch of arguments
      assertTrue(bo.isConnected(9, 8, 7, 7));
      assertFalse(bo.isConnected(8, 9, 7, 7));
   }

   public void testCrossingConnected()
   {
      assertTrue(bo.setPin(17, 7, Board.YPLAYER));
      assertTrue(bo.setPin(19, 8, Board.YPLAYER));
      assertTrue(bo.setPin(18, 7, Board.YPLAYER));
      assertTrue(bo.setPin(17, 9, Board.YPLAYER));
      assertTrue(bo.isConnected(17, 7, 19, 8));
      assertFalse(bo.isConnected(18, 7, 17, 9));
   }

}
