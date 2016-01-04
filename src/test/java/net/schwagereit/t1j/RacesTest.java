package net.schwagereit.t1j;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * test some races and blocking condition
 *
 * @author Johannes Schwagereit (mail(at)johannes-schwagereit.de)
 */
public class RacesTest
{
   final static Races races = new Races();

   Board bo;

   @Before
   public void setUp()
   {
      bo = new Board();
      bo.setSize(24, 24);
   }

   @Test
   public void testCheck4_1()
   {
      bo.reset();
      
      assertTrue(bo.setPin(21, 18, Board.YPLAYER));
      assertTrue(bo.setPin(21, 19, Board.XPLAYER));
      assertTrue(bo.setPin(20, 17, Board.XPLAYER));
      
      assertFalse(races.checkBottom(21, 18, bo, Board.XPLAYER));
      assertFalse(races.checkBottom(21, 18, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck4_2()
   {
      bo.reset();
      
      assertTrue(bo.setPin(11, 18, Board.YPLAYER));
      assertTrue(bo.setPin(11, 19, Board.XPLAYER));
      assertTrue(bo.setPin(10, 17, Board.XPLAYER));
      
      assertFalse(races.checkBottom(11, 18, bo, Board.XPLAYER));
      assertTrue(races.checkBottom(11, 18, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck4_3()
   {
      bo.reset();
      
      assertTrue(bo.setPin(21, 18, Board.YPLAYER));
      assertTrue(bo.setPin(20, 17, Board.XPLAYER));
      assertTrue(bo.setPin(19, 15, Board.XPLAYER));
      
      assertFalse(races.checkBottom(21, 18, bo, Board.XPLAYER));
      assertTrue(races.checkBottom(21, 18, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck5_1()
   {
      bo.reset();
      
      assertTrue(bo.setPin(18, 21, Board.YPLAYER));
      assertTrue(bo.setPin(17, 21, Board.XPLAYER));
      assertTrue(bo.setPin(19, 22, Board.XPLAYER));
      
      assertFalse(races.checkBottom(18, 21, bo, Board.XPLAYER));
      assertTrue(races.checkBottom(18, 21, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck5_2()
   {
      bo.reset();
      
      assertTrue(bo.setPin(18, 11, Board.YPLAYER));
      assertTrue(bo.setPin(17, 11, Board.XPLAYER));
      assertTrue(bo.setPin(19, 12, Board.XPLAYER));
      
      assertFalse(races.checkBottom(18, 11, bo, Board.XPLAYER));
      assertFalse(races.checkBottom(18, 11, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck5_3()
   {
      bo.reset();
      
      assertTrue(bo.setPin(20, 12, Board.YPLAYER));
      assertTrue(bo.setPin(17, 11, Board.XPLAYER));
      assertTrue(bo.setPin(19, 12, Board.XPLAYER));

      assertTrue(bo.setPin(20, 17, Board.XPLAYER));
      assertTrue(bo.setPin(19, 15, Board.XPLAYER));
      
      assertFalse(races.checkBottom(20, 12, bo, Board.XPLAYER));
      assertTrue(races.checkBottom(20, 12, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck6_1()
   {
      bo.reset();

      assertTrue(bo.setPin(18, 21, Board.YPLAYER));
      assertTrue(bo.setPin(19, 21, Board.XPLAYER));
      assertTrue(bo.setPin(17, 22, Board.XPLAYER));
      
      assertFalse(races.checkBottom(18, 21, bo, Board.XPLAYER));
      assertTrue(races.checkBottom(18, 21, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck6_2()
   {
      bo.reset();

      assertTrue(bo.setPin(18, 11, Board.YPLAYER));
      assertTrue(bo.setPin(19, 11, Board.XPLAYER));
      assertTrue(bo.setPin(17, 12, Board.XPLAYER));
      
      assertFalse(races.checkBottom(18, 11, bo, Board.XPLAYER));
      assertFalse(races.checkBottom(18, 11, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck7_1()
   {
      bo.reset();

      assertTrue(bo.setPin(21, 18, Board.YPLAYER));
      assertTrue(bo.setPin(21, 19, Board.XPLAYER));
      assertTrue(bo.setPin(22, 17, Board.XPLAYER));
      
      assertFalse(races.checkBottom(21, 18, bo, Board.XPLAYER));
      assertTrue(races.checkBottom(21, 18, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck7_2()
   {
      bo.reset();

      assertTrue(bo.setPin(4, 8, Board.YPLAYER));
      assertTrue(bo.setPin(4, 9, Board.XPLAYER));
      assertTrue(bo.setPin(5, 7, Board.XPLAYER));
      
      assertFalse(races.checkBottom(4, 8, bo, Board.XPLAYER));
      assertFalse(races.checkBottom(4, 8, bo, Board.YPLAYER));
   }

   @Test
   public void testBlockedBottom()
   {
      bo.reset();

      assertTrue(bo.setPin(10, 12, Board.YPLAYER));
      assertTrue(bo.setPin(10, 14, Board.XPLAYER));
      assertTrue(bo.setPin( 8, 13, Board.XPLAYER));
      
      assertFalse(races.checkBottom(10, 12, bo, Board.XPLAYER));
      
      //other board
      bo.reset();

      assertTrue(bo.setPin(10, 12, Board.YPLAYER));
      assertTrue(bo.setPin(10, 14, Board.XPLAYER));
      assertTrue(bo.setPin( 8, 13, Board.XPLAYER));
      
      assertFalse(races.checkBottom(10, 12, bo, Board.XPLAYER));

   }

   @Test
   public void testCheck3_1()
   {
      bo.reset();

      assertTrue(bo.setPin(21, 18, Board.YPLAYER));
      assertTrue(bo.setPin(21, 19, Board.XPLAYER));
      assertTrue(bo.setPin(20, 17, Board.XPLAYER));
      
      assertFalse(races.checkBottom(21, 18, bo, Board.XPLAYER));
      assertFalse(races.checkBottom(21, 18, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck3_2()
   {
      bo.reset();
      
      assertTrue(bo.setPin(11, 18, Board.YPLAYER));
      assertTrue(bo.setPin(11, 19, Board.XPLAYER));
      assertTrue(bo.setPin(10, 17, Board.XPLAYER));
      
      assertFalse(races.checkBottom(11, 18, bo, Board.XPLAYER));
      assertTrue(races.checkBottom(11, 18, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck3_3()
   {
      bo.reset();
      
      assertTrue(bo.setPin(21, 18, Board.YPLAYER));
      assertTrue(bo.setPin(20, 17, Board.XPLAYER));
      assertTrue(bo.setPin(19, 15, Board.XPLAYER));
      
      assertFalse(races.checkBottom(21, 18, bo, Board.XPLAYER));
      assertTrue(races.checkBottom(21, 18, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck2_1()
   {
      bo.reset();
      
      assertTrue(bo.setPin(18, 21, Board.YPLAYER));
      assertTrue(bo.setPin(17, 21, Board.XPLAYER));
      assertTrue(bo.setPin(19, 22, Board.XPLAYER));
      
      assertFalse(races.checkBottom(18, 21, bo, Board.XPLAYER));
      assertTrue(races.checkBottom(18, 21, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck2_2()
   {
      bo.reset();
      
      assertTrue(bo.setPin(18, 11, Board.YPLAYER));
      assertTrue(bo.setPin(17, 11, Board.XPLAYER));
      assertTrue(bo.setPin(19, 12, Board.XPLAYER));
      
      assertFalse(races.checkBottom(18, 11, bo, Board.XPLAYER));
      assertFalse(races.checkBottom(18, 11, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck2_3()
   {
      bo.reset();
      
      assertTrue(bo.setPin(20, 12, Board.YPLAYER));
      assertTrue(bo.setPin(17, 11, Board.XPLAYER));
      assertTrue(bo.setPin(19, 12, Board.XPLAYER));

      assertTrue(bo.setPin(20, 17, Board.XPLAYER));
      assertTrue(bo.setPin(19, 15, Board.XPLAYER));
      
      assertFalse(races.checkBottom(20, 12, bo, Board.XPLAYER));
      assertTrue(races.checkBottom(20, 12, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck1_1()
   {
      bo.reset();
      
      assertTrue(bo.setPin(18, 21, Board.YPLAYER));
      assertTrue(bo.setPin(19, 21, Board.XPLAYER));
      assertTrue(bo.setPin(17, 22, Board.XPLAYER));
      
      assertFalse(races.checkBottom(18, 21, bo, Board.XPLAYER));
      assertTrue(races.checkBottom(18, 21, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck1_2()
   {
      bo.reset();
      
      assertTrue(bo.setPin(18, 11, Board.YPLAYER));
      assertTrue(bo.setPin(19, 11, Board.XPLAYER));
      assertTrue(bo.setPin(17, 12, Board.XPLAYER));
      
      assertFalse(races.checkBottom(18, 11, bo, Board.XPLAYER));
      assertFalse(races.checkBottom(18, 11, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck0_1()
   {
      bo.reset();
      
      assertTrue(bo.setPin(21, 18, Board.YPLAYER));
      assertTrue(bo.setPin(21, 19, Board.XPLAYER));
      assertTrue(bo.setPin(22, 17, Board.XPLAYER));
      
      assertFalse(races.checkBottom(21, 18, bo, Board.XPLAYER));
      assertTrue(races.checkBottom(21, 18, bo, Board.YPLAYER));
   }

   @Test
   public void testCheck0_2()
   {
      bo.reset();
      
      assertTrue(bo.setPin(4, 8, Board.YPLAYER));
      assertTrue(bo.setPin(4, 9, Board.XPLAYER));
      assertTrue(bo.setPin(5, 7, Board.XPLAYER));
      
      assertFalse(races.checkBottom(4, 8, bo, Board.XPLAYER));
      assertFalse(races.checkBottom(4, 8, bo, Board.YPLAYER));
   }

   @Test
   public void testBlockedTop1()
   {
      bo.reset();
      
      assertTrue(bo.setPin(11, 12, Board.XPLAYER));
      assertTrue(bo.setPin(13, 11, Board.XPLAYER));
      assertTrue(bo.setPin(13, 14, Board.YPLAYER));

      assertFalse(races.checkTop(13, 14, bo, Board.XPLAYER));
   }

   @Test
   public void testBlockedTop2()
   {
      bo.reset();

      assertTrue(bo.setPin(11, 12, Board.XPLAYER));
      assertTrue(bo.setPin(13, 11, Board.XPLAYER));
      assertTrue(bo.setPin(12, 12, Board.YPLAYER));

      assertFalse(races.checkTop(12, 12, bo, Board.XPLAYER));

      //other board
      bo.reset();

      assertTrue(bo.setPin(11, 12, Board.XPLAYER));
      assertTrue(bo.setPin(13, 11, Board.XPLAYER));
      assertTrue(bo.setPin(13, 13, Board.YPLAYER));

      assertFalse(races.checkTop(13, 13, bo, Board.XPLAYER));
   }

}
