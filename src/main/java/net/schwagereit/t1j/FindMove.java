/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package net.schwagereit.t1j;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import lombok.NonNull;


/**
 * Generate all moves and try to find best one. This class is a singleton.
 *
 * @author Johannes Schwagereit (mail(at)johannes-schwagereit.de)
 */
public final class FindMove
{
   private static final int INITIAL_CAPACITY = 10000;
   private static final int WAIT_MILLIS = 20;
   private static final int MILLI_PER_SEC = 1000;
   private static final int GAMEOVER = MILLI_PER_SEC;

   @NonNull
   private final Match match;

   /** the bestMove found yet */
   private Move bestMove;

   private int maxTime;

   private int currentPlayer;

   private final Map<Integer,Integer> zobristMap = new HashMap<>(INITIAL_CAPACITY);
   private Stopwatch clock;
   /** use alphabeta for highest ply. */
   private boolean usealphabeta;
   @NonNull
   private final GeneralSettings generalSettings;
   @NonNull
   private final MoveGenerator moveGenerator;
   @NonNull
   private final InitialMoves initialMoveGenerator;

   /**
    * Cons'tor - no external instance.
    */
   public FindMove(Match match)
   {
      this(match, GeneralSettings.getInstance());
   }

   public FindMove(Match match, GeneralSettings generalSettings)
   {
      this.match = match;
      this.generalSettings = generalSettings;
      this.moveGenerator = new MoveGenerator(match);
      this.initialMoveGenerator = InitialMoves.getInstance();
   }

   /**
    * Find best move for computerplayer.
    *
    * @param player X- or Y-player, the next player
    * @return a computermove
    */
   public Move computeMove(final int player)
   {
      currentPlayer = player;
      // the first pins are set by simple rules
      Move initMove = initialMoveGenerator.initialMove(match, player);
      if (initMove != null)
      {
         // if move was found
         try
         {
            Thread.sleep(WAIT_MILLIS);
         }
         catch (InterruptedException e)
         {
            System.out.println("Sleep Interrupted");
         }
         return initMove;
      }

      clock = new Stopwatch();

      internalComputeMove(player);

      System.out.println("Elapsed: " + clock.getElapsedMillis() + " msec.");

      return bestMove;
   }

   /**
    * Find best move for computerplayer by using alpha-beta.
    *
    * @param player X- or Y-player, the next player
    */
   private void internalComputeMove(final int player)
   {
      int maxPly;
      if (generalSettings.mdFixedPly)
      {
         maxPly = generalSettings.mdPly;
         maxTime = -1;
      }
      else
      {
         //move moves with time-limit
         maxPly = Integer.MAX_VALUE; // will never be reached
         maxTime = generalSettings.mdTime;
      }

      // use alpha-beta
      ComputeMoveContext computeMoveContext = new ComputeMoveContext();
      usealphabeta = false;
      for (int currentMaxPly = 3; currentMaxPly <= maxPly; currentMaxPly++)
      {
         if (currentMaxPly != 4 || currentMaxPly == maxPly)
         {
            zobristMap.clear();
            alphaBeta(computeMoveContext, player, currentMaxPly, currentMaxPly, -Integer.MAX_VALUE, Integer.MAX_VALUE);
            usealphabeta = true;
            if (isThinkingTimeExceeded())
            {
               break;
            }
         }
      }
   }

   /**
    * Evaluate situation.
    * @param player player who has next turn
    * @return value of current situation on board - the higher the better for y-player
    */
   private int evaluatePosition(int player)
   {
      int val1, val2;

      // evaluate
      val1 = match.getBoardY().getEval().evaluateY(false, player);
      val2 = match.getBoardX().getEval().evaluateY(false, -player);


      // at the first move only defensive moves are good
      if (match.getMoveNr() < 8)
      {
         if (currentPlayer == Board.YPLAYER)
         {
            val1 = 0;
         }
         else
         {
            val2 = 0;
         }

      }
      return val2 - val1;

      //on equal moves, takes the one nearest to midth
      //vals = (vals << 7) + Math.abs(ownBoard.getXsize() / 2 - element.getX())
      //+ Math.abs(ownBoard.getYsize() / 2 - element.getY());
   }

   /**
    * Make a move on both boards.
    * @param move the move
    * @param player the player
    */
   private void makeMove(Move move, int player)
   {
      match.getBoardY().setPin(move.getX(), move.getY(), player);
      match.getBoardX().setPin(move.getY(), move.getX(), -player);
   }


   /**
    * Unmake a move on both boards.
    * @param move the move
    * @param player the player
    */
   private void unmakeMove(Move move, int player)
   {
      match.getBoardY().removePin(move.getX(), move.getY(), player);
      match.getBoardX().removePin(move.getY(), move.getX(), -player);
   }

   /**
    * Recursive minimax with alpha-beta pruning to find best move.
    * @param player player who has next turn
    * @param maxPly max depth
    * @param ply current depth, decreasing
    * @param alpha alpha-value
    * @param beta beta-value
    * @return value computed
    */
   private int alphaBeta(ComputeMoveContext computeMoveContext, int player, int maxPly, int ply, int alpha, int beta)
   {
      int val;
      Move move;

      if (isThinkingTimeExceeded())
      {
         return 0;
      }

      if (isLeafPly(ply))
      {
         return positionValue(player);
      }

      List<Move> orderedMoves = moveGenerator.generateMoves(computeMoveContext, player, ply == maxPly);

      // a check for game over
      if (orderedMoves.isEmpty())
      {
         // the earlier the better
         return (GAMEOVER + ply) * player;
      }

      // minimizing node
      if (player == Board.XPLAYER)
      {
         return evaluateMoveSetX(computeMoveContext, Board.XPLAYER, maxPly, ply, alpha, beta, orderedMoves);
      }
      else
      //maximizing node
      {
         return evaluateMoveSetY(computeMoveContext, player, maxPly, ply, alpha, beta, orderedMoves);
      }
   }

   private int evaluateMoveSetY(ComputeMoveContext computeMoveContext, int player, int maxPly, int ply, int alpha, int beta, List<Move> orderedMoves) {
      int val;
      for(Move move:orderedMoves)
      {
         makeMove(move, player);
         val = alphaBeta (computeMoveContext, -player, maxPly, ply - 1, alpha, beta);
         // zobristMap.put(new Integer(match.getBoardY().getZobristValue()), new Integer(val));
         if (ply == maxPly)
         {
            computeMoveContext.addValuedMove(move, val);
         }
         if (val > alpha)
         {
            if (ply == maxPly)
            {
               // a new best move is only accepted if time is not over
               if (!isThinkingTimeExceeded())
               {
                  bestMove = move;
               }

//                  if (currentMaxPly < maxPly)
//                  {
//                     OrderedMoves.addValuedMove(move, val);
//                  }

               if (usealphabeta)
               {
                  alpha = val;
               }
            }
            else
            {
               alpha = val;
               computeMoveContext.addKiller(move, player);
            }
         }

         unmakeMove(move, player);

         if (beta <= alpha)
         {
            return beta;
         }
      }
      return alpha;
   }

   private int evaluateMoveSetX(ComputeMoveContext computeMoveContext, int player, int maxPly, int ply, int alpha, int beta, List<Move> orderedMoves) {
      int val;
      for(Move move:orderedMoves)
      {
         makeMove(move, player);
         val = alphaBeta (computeMoveContext, -player, maxPly, ply - 1, alpha, beta);
         //zobristMap.put(new Integer(match.getBoardY().getZobristValue()), new Integer(val));

         if (ply == maxPly)
         {
            computeMoveContext.addValuedMove(move, val);
         }

         if (val < beta)
         {
            //
            //
            //
            if (ply == maxPly)
            {
               // a new best move is only accepted if time is not over
               if (!isThinkingTimeExceeded())
               {
                  bestMove = move;
               }

               //if (currentMaxPly < maxPly)
               //{
               //   OrderedMoves.addValuedMove(move, val);
               //}

               if (usealphabeta)
               {
                  beta = val;
               }
            }
            else
            {
               beta = val;
               computeMoveContext.addKiller(move, player);
            }
         }
         unmakeMove(move, player);

         if (beta <= alpha)
         {
            return alpha;
         }
      }
      return beta;
   }

   private boolean isLeafPly(int ply) {
      return ply == 0;
   }

   private boolean isThinkingTimeExceeded() {
      return maxTime > 0 && maxTime * MILLI_PER_SEC <= clock.getElapsedMillis();
   }

   private int positionValue(int player) {
      int val;
      Integer zobristVal=zobristMap.get(match.getBoardY().getZobristValue());
      if (zobristVal != null)
      {
         //System.out.println("Treffer bei ply = " + ply + " Hashsize:" + zobristMap.size());
         return zobristVal;
      }
      //return evaluatePosition(player);
      val = evaluatePosition(player);
      zobristMap.put(match.getBoardY().getZobristValue(), val);
      return val;
   }

}
