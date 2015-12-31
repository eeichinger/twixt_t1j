/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package net.schwagereit.t1j;

import java.util.*;

import lombok.NonNull;


/**
 * Created by IntelliJ IDEA.
 *
 * @author Johannes Schwagereit (mail(at)johannes-schwagereit.de)
 */
final class MoveGenerator
{
   @NonNull
   private final CheckPattern checkpattern;
   @NonNull
   private final Match match;

   /**
    * Cons'tor.
    * @param inMatch matchdata
    */
   MoveGenerator(Match inMatch)
   {
      match = inMatch;
      checkpattern = CheckPattern.getInstance();
   }

   /**
    * Find all relevant moves for player. If the returned list is empty, the game is over.
    *
    * @param player X- or Y-player, the next player
    * @param isMaxPly true if current ply is starting ply
    * @return list of promising moves for player in order of descending expectation value. if empty -> game over
    */
   public final List<Move> generateMoves(ComputeMoveContext generateMoveContext, final int player, boolean isMaxPly)
   {
      List<Move> orderedMoves;
      if ( isMaxPly && generateMoveContext.hasValuedMoves())
      {
         orderedMoves = generateMoveContext.getSortedMoves(player);
      }
      else
      {
         Set<Move> generatedMoves = generateNewMoves(player);
         orderedMoves = generateMoveContext.sortMoves(generatedMoves, player);
      }
      return orderedMoves;
   }

   /**
    * Find all relevant moves for player.
    *
    * @param player X- or Y-player, the next player
    */
   private Set<Move> generateNewMoves(final int player)
   {
      //currentPlayer = player;

      Set<Move> moves = new HashSet<Move>();
      Board ownBoard = match.getBoard(player);
      Evaluation ownEval = ownBoard.getEval();
      Board oppBoard = match.getBoard(-player);
      Evaluation oppEval = oppBoard.getEval();

      // eval for opponent
      int oppVal = oppEval.evaluateY(true, Board.YPLAYER);

      // gameover?
      if (oppVal == 0)
      {
         return moves;
      }

      // own eval
      ownEval.evaluateY(true, Board.YPLAYER);

      // check own critical points
      Set<Evaluation.CritPos> s = ownEval.getCritical();
      if (s.isEmpty())
      {
         //there are some situations where the last pin set has to be
         //   taken as last hope to find a good move
         Move lastMove = match.getLastMove();
         int xc = lastMove.getX();
         int yc = lastMove.getY();
         s.add(new Evaluation.CritPos(xc, yc, Evaluation.CritPos.DOWN));
         s.add(new Evaluation.CritPos(xc, yc, Evaluation.CritPos.UP));
      }

      // iterate over all own critical points
      for (Iterator<Evaluation.CritPos> iter = s.iterator(); iter.hasNext();)
      {
         Evaluation.CritPos element = iter.next();

         int xe = element.getX();
         int ye = element.getY();
         if (!element.isDir())
         {
            // put pin (player is always yPlayer on own board)
            if (ownBoard.bridgeAllowed(xe, ye, 1)
                  && ownBoard.pinAllowed(xe - 1, ye - 2, Board.YPLAYER))
            {
               moves.add(new Move(xe - 1, ye - 2, player));
            }
            else if (ownBoard.bridgeAllowed(xe, ye, 0)
                  && ownBoard.pinAllowed(xe - 2, ye - 1, Board.YPLAYER))
            {
               moves.add(new Move(xe - 2, ye - 1, player));
            }

            if (ownBoard.bridgeAllowed(xe, ye, 2)
                  && ownBoard.pinAllowed(xe + 1, ye - 2, Board.YPLAYER))
            {
               moves.add(new Move(xe + 1, ye - 2, player));
            }
            else if (ownBoard.bridgeAllowed(xe, ye, 3)
                  && ownBoard.pinAllowed(xe + 2, ye - 1, Board.YPLAYER))
            {
               moves.add(new Move(xe + 2, ye - 1, player));
            }
         }
         else
         // (elements.isDir())
         {
            if (ownBoard.bridgeAllowed(xe - 1, ye + 2, 2)
                  && ownBoard.pinAllowed(xe - 1, ye + 2, Board.YPLAYER))
            {
               moves.add(new Move(xe - 1, ye + 2, player));
            }
            else if (ownBoard.bridgeAllowed(xe - 2, ye + 1, 3)
                  && ownBoard.pinAllowed(xe - 2, ye + 1, Board.YPLAYER))
            {
               moves.add(new Move(xe - 2, ye + 1, player));
            }

            if (ownBoard.bridgeAllowed(xe + 1, ye + 2, 1)
                  && ownBoard.pinAllowed(xe + 1, ye + 2, Board.YPLAYER))
            {
               moves.add(new Move(xe + 1, ye + 2, player));
            }
            else if (ownBoard.bridgeAllowed(xe + 2, ye + 1, 0)
                  && ownBoard.pinAllowed(xe + 2, ye + 1, Board.YPLAYER))
            {
               moves.add(new Move(xe + 2, ye + 1, player));
            }
         }
         // try to find additional defensive moves using patterns
         // TODO: clarify, if this is a bug? comment says "defensive" but sets "offense"=true
         moves.addAll(
               checkpattern.findPatternMoves(true, ownBoard, element, player));
      }

      // moves against opponent
      s = oppEval.getCritical();

      //iterate over all critical point of opponent
      for (Iterator iter = s.iterator(); iter.hasNext();)
      {
         Evaluation.CritPos element = (Evaluation.CritPos) iter.next();

         int ye = element.getX(); // CAUTION: swapped
         int xe = element.getY();
         xe += (element.isDir() ? 4 : -4);
         ye += ((ye > match.getYsize() / 2 + 3) ? -1 :
               (ye < match.getYsize() / 2 - 3) ? 1 : 0);
         // put pin (player is always yPlayer)
         if (ownBoard.pinAllowed(xe, ye, Board.YPLAYER))
         {
            moves.add(new Move(xe, ye, player));
         }
         // try to find additional defensive moves using patterns
         moves.addAll(
               checkpattern.findPatternMoves(false, oppBoard, element, player));

      }

      //no moves found, desperately try some random move
      if (moves.isEmpty())
      {
         moves.add(randomMove(player));
      }

      return moves;
   }

   /**
    * Find a random legal Move.
    * @return a random Move
    * @param player next player
    */
   private Move randomMove(int player)
   {
      int bx, by;
      Random rand = new Random();
      do
      {
         bx = rand.nextInt(match.getXsize());
         by = rand.nextInt(match.getYsize());
      } while (!match.getBoard(Board.YPLAYER).pinAllowed(bx, by, player));
      return new Move(bx, by);
   }

}
