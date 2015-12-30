package puzzles;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

import net.schwagereit.t1j.Board;
import net.schwagereit.t1j.FindMove;
import net.schwagereit.t1j.GeneralSettings;
import net.schwagereit.t1j.LoadSave;
import net.schwagereit.t1j.Match;
import net.schwagereit.t1j.MatchData;
import net.schwagereit.t1j.Move;
import net.schwagereit.t1j.Zobrist;
import org.junit.Test;

/**
 * @author Erich Eichinger
 * @since 30/12/2015
 */
public class Puzzle01Test {

   @Test
   public void testGame() {
      BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("puzzle01.T1")));
      Zobrist.getInstance().initialize();

      Match match = new Match();
      GeneralSettings settings = new GeneralSettings();
      LoadSave.loadGameData(match, reader);

      settings.setMdFixedPly(true);
      settings.setMdPly(10);

      final FindMove findMove = new FindMove(match, settings);

      Move move = findMove.computeMove(Board.YPLAYER);
   }
}
