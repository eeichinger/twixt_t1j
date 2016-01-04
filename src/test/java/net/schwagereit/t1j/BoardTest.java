package net.schwagereit.t1j;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest
{
   Board bo; 

   @Before
   public void setUp()
   {
      bo = new Board();
      bo.setSize(24, 24);
   }
   
   /*
    * Test method for 'net.schwagereit.t1j.Board.setPin(int, int, int)'
    */
   @Test
   public void testSetPin()
   {
      assertTrue(bo.setPin(7, 8, Board.YPLAYER));
   }

   @Test
   public void testSetPinExists()
   {
      assertTrue(bo.setPin(4, 4, Board.YPLAYER));
      
      assertTrue(bo.getPin(4, 4) == Board.YPLAYER);
   }
   
   /*
    * Test method for 'net.schwagereit.t1j.Board.removePin(int, int, int)'
    */
   @Test
   public void testRemovePin()
   {
      assertTrue(bo.setPin(2, 2, Board.YPLAYER));
      assertTrue(bo.getPin(2, 2) == Board.YPLAYER);
      assertTrue(bo.removePin(2, 2, Board.YPLAYER));
      
      assertTrue(bo.getPin(2, 2) == 0);
   }

   @Test
   public void testRemovePinWrongPlayer()
   {
      bo.setPin(2, 2, Board.XPLAYER);
      assertFalse(bo.removePin(2, 2, Board.YPLAYER));
   }

   @Test
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

   @Test
   public void testDisconnected()
   {
      bo.setPin(7, 7, Board.YPLAYER);
      bo.setPin(9, 8, Board.YPLAYER);
      assertTrue(bo.isConnected(7, 7, 9, 8));

      bo.removePin(7,7, Board.YPLAYER);

      assertFalse(bo.isConnected(7, 7, 9, 8));
   }

   @Test
   public void testCrossingConnected()
   {
      bo.setPin(17, 7, Board.YPLAYER);
      bo.setPin(19, 8, Board.YPLAYER);
      bo.setPin(18, 7, Board.YPLAYER);
      bo.setPin(17, 9, Board.YPLAYER);

      assertTrue(bo.isConnected(17, 7, 19, 8));
      assertFalse(bo.isConnected(18, 7, 17, 9));
   }

   @Test
   public void testBridgeNotAllowed()
   {
      bo.setPin(17, 7, Board.YPLAYER);
      bo.setPin(19, 8, Board.YPLAYER);
      bo.setPin(18, 7, Board.YPLAYER);
      bo.setPin(17, 9, Board.YPLAYER);

      assertFalse(bo.isBridgeAllowed(18, 7, 17, 9));
   }

}
