package net.schwagereit.t1j;

import net.schwagereit.t1j.Evaluation.CritPos;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Erich Eichinger
 * @since 04/01/2016
 */
public class EvaluationTest
{
   Board board;

   @Before
   public void setup() {
      /* Puzzle-01: D9, F6, F8, H7, E12, I9 */
      board = new Board(13);
      board.setPin(3, 8, Board.YPLAYER);
      board.setPin(5, 5, Board.XPLAYER);
      board.setPin(5, 7, Board.YPLAYER);
      board.setPin(7, 6, Board.XPLAYER);
      board.setPin(4, 11, Board.YPLAYER);
      board.setPin(8, 8, Board.XPLAYER);
   }

   @Test
   public void evals_empty() {
      Evaluation eval = new Evaluation(board);

      int scoreY = eval.evaluateY(Board.YPLAYER);
      assertEquals(111, scoreY);
   }

   @Test
   public void criticalPositions() {
      Evaluation eval = new Evaluation(board);

      Set<CritPos> critPos = eval.computeCriticalY();
      assertEquals(critPos.size(), 4);
      assertTrue(critPos.contains(new CritPos(4, 11, true)));
      assertTrue(critPos.contains(new CritPos(4, 11, false)));
      assertTrue(critPos.contains(new CritPos(3, 8, true)));
      assertTrue(critPos.contains(new CritPos(5, 7, false)));
   }
}
