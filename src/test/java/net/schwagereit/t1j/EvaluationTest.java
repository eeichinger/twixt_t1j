package net.schwagereit.t1j;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Erich Eichinger
 * @since 04/01/2016
 */
public class EvaluationTest
{
   @Test
   public void evals_empty() {
      Board board = new Board();
      Evaluation eval = new Evaluation(board);

      int scoreY = eval.evaluateY(Board.YPLAYER);
      assertEquals(990, scoreY);
      int scoreX = eval.evaluateY(Board.XPLAYER);
      assertEquals(990, scoreX);
   }

   @Test
   public void setup_correctly() {
      Board board = new Board();
      Evaluation eval = new Evaluation(board);

      board.setPin(2, 1, Board.YPLAYER);
      board.setPin(17, 2, Board.YPLAYER);
      board.setPin(12, 2, Board.XPLAYER);

      int score1 = eval.evaluateY(Board.YPLAYER);

      // clear boardlistener
      board.setBoardListener(null);
      Evaluation eval2 = new Evaluation(board);
      int score2 = eval2.evaluateY(Board.YPLAYER);

      assertEquals(score1, score2);
   }
}
