package net.schwagereit.t1j;

import junit.framework.TestCase;
//import net.schwagereit.t1j.Board;
//import net.schwagereit.t1j.Races;


//test some races and blocking condition
public class RacesTest extends TestCase
{
   Board bo;

   protected void setUp()
   {
      bo = Board.getBoard(Board.YPLAYER);
      bo.setSize(24, 24);
   }

   
   public void testCheck4_1()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(21, 18, Board.YPLAYER));
      assertTrue(bo.setPin(21, 19, Board.XPLAYER));
      assertTrue(bo.setPin(20, 17, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(21, 18, bo, Board.XPLAYER));
      assertFalse(Races.getRaces().checkBottom(21, 18, bo, Board.YPLAYER));
   }

   public void testCheck4_2()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(11, 18, Board.YPLAYER));
      assertTrue(bo.setPin(11, 19, Board.XPLAYER));
      assertTrue(bo.setPin(10, 17, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(11, 18, bo, Board.XPLAYER));
      assertTrue(Races.getRaces().checkBottom(11, 18, bo, Board.YPLAYER));
   }

   public void testCheck4_3()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(21, 18, Board.YPLAYER));
      assertTrue(bo.setPin(20, 17, Board.XPLAYER));
      assertTrue(bo.setPin(19, 15, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(21, 18, bo, Board.XPLAYER));
      assertTrue(Races.getRaces().checkBottom(21, 18, bo, Board.YPLAYER));
   }
   
   public void testCheck5_1()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(18, 21, Board.YPLAYER));
      assertTrue(bo.setPin(17, 21, Board.XPLAYER));
      assertTrue(bo.setPin(19, 22, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(18, 21, bo, Board.XPLAYER));
      assertTrue(Races.getRaces().checkBottom(18, 21, bo, Board.YPLAYER));
   }

   public void testCheck5_2()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(18, 11, Board.YPLAYER));
      assertTrue(bo.setPin(17, 11, Board.XPLAYER));
      assertTrue(bo.setPin(19, 12, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(18, 11, bo, Board.XPLAYER));
      assertFalse(Races.getRaces().checkBottom(18, 11, bo, Board.YPLAYER));
   }

   public void testCheck5_3()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(20, 12, Board.YPLAYER));
      assertTrue(bo.setPin(17, 11, Board.XPLAYER));
      assertTrue(bo.setPin(19, 12, Board.XPLAYER));

      assertTrue(bo.setPin(20, 17, Board.XPLAYER));
      assertTrue(bo.setPin(19, 15, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(20, 12, bo, Board.XPLAYER));
      assertTrue(Races.getRaces().checkBottom(20, 12, bo, Board.YPLAYER));
   }

   public void testCheck6_1()
   {
      bo.clearBoard();

      assertTrue(bo.setPin(18, 21, Board.YPLAYER));
      assertTrue(bo.setPin(19, 21, Board.XPLAYER));
      assertTrue(bo.setPin(17, 22, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(18, 21, bo, Board.XPLAYER));
      assertTrue(Races.getRaces().checkBottom(18, 21, bo, Board.YPLAYER));
   }
   
   public void testCheck6_2()
   {
      bo.clearBoard();

      assertTrue(bo.setPin(18, 11, Board.YPLAYER));
      assertTrue(bo.setPin(19, 11, Board.XPLAYER));
      assertTrue(bo.setPin(17, 12, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(18, 11, bo, Board.XPLAYER));
      assertFalse(Races.getRaces().checkBottom(18, 11, bo, Board.YPLAYER));
   }
   public void testCheck7_1()
   {
      bo.clearBoard();

      assertTrue(bo.setPin(21, 18, Board.YPLAYER));
      assertTrue(bo.setPin(21, 19, Board.XPLAYER));
      assertTrue(bo.setPin(22, 17, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(21, 18, bo, Board.XPLAYER));
      assertTrue(Races.getRaces().checkBottom(21, 18, bo, Board.YPLAYER));
   }

   public void testCheck7_2()
   {
      bo.clearBoard();

      assertTrue(bo.setPin(4, 8, Board.YPLAYER));
      assertTrue(bo.setPin(4, 9, Board.XPLAYER));
      assertTrue(bo.setPin(5, 7, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(4, 8, bo, Board.XPLAYER));
      assertFalse(Races.getRaces().checkBottom(4, 8, bo, Board.YPLAYER));
   }


   public void testBlockedBottom()
   {
      bo.clearBoard();

      assertTrue(bo.setPin(10, 12, Board.YPLAYER));
      assertTrue(bo.setPin(10, 14, Board.XPLAYER));
      assertTrue(bo.setPin( 8, 13, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(10, 12, bo, Board.XPLAYER));
      
      //other board
      bo.clearBoard();

      assertTrue(bo.setPin(10, 12, Board.YPLAYER));
      assertTrue(bo.setPin(10, 14, Board.XPLAYER));
      assertTrue(bo.setPin( 8, 13, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(10, 12, bo, Board.XPLAYER));

   }
   
   public void testCheck3_1()
   {
      bo.clearBoard();

      assertTrue(bo.setPin(21, 18, Board.YPLAYER));
      assertTrue(bo.setPin(21, 19, Board.XPLAYER));
      assertTrue(bo.setPin(20, 17, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(21, 18, bo, Board.XPLAYER));
      assertFalse(Races.getRaces().checkBottom(21, 18, bo, Board.YPLAYER));
   }

   public void testCheck3_2()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(11, 18, Board.YPLAYER));
      assertTrue(bo.setPin(11, 19, Board.XPLAYER));
      assertTrue(bo.setPin(10, 17, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(11, 18, bo, Board.XPLAYER));
      assertTrue(Races.getRaces().checkBottom(11, 18, bo, Board.YPLAYER));
   }

   public void testCheck3_3()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(21, 18, Board.YPLAYER));
      assertTrue(bo.setPin(20, 17, Board.XPLAYER));
      assertTrue(bo.setPin(19, 15, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(21, 18, bo, Board.XPLAYER));
      assertTrue(Races.getRaces().checkBottom(21, 18, bo, Board.YPLAYER));
   }
   
   public void testCheck2_1()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(18, 21, Board.YPLAYER));
      assertTrue(bo.setPin(17, 21, Board.XPLAYER));
      assertTrue(bo.setPin(19, 22, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(18, 21, bo, Board.XPLAYER));
      assertTrue(Races.getRaces().checkBottom(18, 21, bo, Board.YPLAYER));
   }

   public void testCheck2_2()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(18, 11, Board.YPLAYER));
      assertTrue(bo.setPin(17, 11, Board.XPLAYER));
      assertTrue(bo.setPin(19, 12, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(18, 11, bo, Board.XPLAYER));
      assertFalse(Races.getRaces().checkBottom(18, 11, bo, Board.YPLAYER));
   }

   public void testCheck2_3()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(20, 12, Board.YPLAYER));
      assertTrue(bo.setPin(17, 11, Board.XPLAYER));
      assertTrue(bo.setPin(19, 12, Board.XPLAYER));

      assertTrue(bo.setPin(20, 17, Board.XPLAYER));
      assertTrue(bo.setPin(19, 15, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(20, 12, bo, Board.XPLAYER));
      assertTrue(Races.getRaces().checkBottom(20, 12, bo, Board.YPLAYER));
   }

   public void testCheck1_1()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(18, 21, Board.YPLAYER));
      assertTrue(bo.setPin(19, 21, Board.XPLAYER));
      assertTrue(bo.setPin(17, 22, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(18, 21, bo, Board.XPLAYER));
      assertTrue(Races.getRaces().checkBottom(18, 21, bo, Board.YPLAYER));
   }
   
   public void testCheck1_2()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(18, 11, Board.YPLAYER));
      assertTrue(bo.setPin(19, 11, Board.XPLAYER));
      assertTrue(bo.setPin(17, 12, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(18, 11, bo, Board.XPLAYER));
      assertFalse(Races.getRaces().checkBottom(18, 11, bo, Board.YPLAYER));
   }
   public void testCheck0_1()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(21, 18, Board.YPLAYER));
      assertTrue(bo.setPin(21, 19, Board.XPLAYER));
      assertTrue(bo.setPin(22, 17, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(21, 18, bo, Board.XPLAYER));
      assertTrue(Races.getRaces().checkBottom(21, 18, bo, Board.YPLAYER));
   }

   public void testCheck0_2()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(4, 8, Board.YPLAYER));
      assertTrue(bo.setPin(4, 9, Board.XPLAYER));
      assertTrue(bo.setPin(5, 7, Board.XPLAYER));
      
      assertFalse(Races.getRaces().checkBottom(4, 8, bo, Board.XPLAYER));
      assertFalse(Races.getRaces().checkBottom(4, 8, bo, Board.YPLAYER));
   }


   public void testBlockedTop1()
   {
      bo.clearBoard();
      
      assertTrue(bo.setPin(11, 12, Board.XPLAYER));
      assertTrue(bo.setPin(13, 11, Board.XPLAYER));
      assertTrue(bo.setPin(13, 14, Board.YPLAYER));

      assertFalse(Races.getRaces().checkTop(13, 14, bo, Board.XPLAYER));
   }

   public void testBlockedTop2()
   {
      bo.clearBoard();

      assertTrue(bo.setPin(11, 12, Board.XPLAYER));
      assertTrue(bo.setPin(13, 11, Board.XPLAYER));
      assertTrue(bo.setPin(12, 12, Board.YPLAYER));

      assertFalse(Races.getRaces().checkTop(12, 12, bo, Board.XPLAYER));

      //other board
      bo.clearBoard();

      assertTrue(bo.setPin(11, 12, Board.XPLAYER));
      assertTrue(bo.setPin(13, 11, Board.XPLAYER));
      assertTrue(bo.setPin(13, 13, Board.YPLAYER));

      assertFalse(Races.getRaces().checkTop(13, 13, bo, Board.XPLAYER));
   }

}
