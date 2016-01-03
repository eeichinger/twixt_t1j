package puzzles;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.schwagereit.t1j.Board;
import net.schwagereit.t1j.CheckPattern;
import net.schwagereit.t1j.FindMove;
import net.schwagereit.t1j.GeneralSettings;
import net.schwagereit.t1j.LoadSave;
import net.schwagereit.t1j.Match;
import net.schwagereit.t1j.Move;
import net.schwagereit.t1j.Zobrist;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Erich Eichinger
 * @since 30/12/2015
 */
public class Puzzle01Test {

   @Test
   public void solves_puzzle01() {
      BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("puzzle01.T1")));
      CheckPattern.getInstance().loadPattern();

      Match match = new Match();
      GeneralSettings settings = new GeneralSettings();
      LoadSave.loadGameData(match, reader);

      settings.setMdFixedPly(true);
      settings.setMdPly(10);

      final FindMove findMove = new FindMove(match, settings);

      Move move = findMove.computeMove(Board.YPLAYER);

      System.out.println("found move " + move);

      // TODO: not the correct solution yet, but serves as regression test for structural refactoring
      assertThat(move.getX(), equalTo(3));
      assertThat(move.getY(), equalTo(4));
   }
}
