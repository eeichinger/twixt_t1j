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
import lombok.Value;


/**
 * Generate all moves and try to find best one. This class is a singleton.
 *
 * @author Johannes Schwagereit (mail(at)johannes-schwagereit.de)
 */
public final class FindMove
{
   public static final boolean DEBUG = false;

   private static final int INITIAL_CAPACITY = 10000;
   private static final int WAIT_MILLIS = 20;
   private static final int MILLI_PER_SEC = 1000;
   private static final int GAMEOVER = MILLI_PER_SEC;

   @NonNull
   private final Match match;

   private int maxTime;

   private int currentPlayer;

   // replace with specialized int2int map: http://java-performance.info/implementing-world-fastest-java-int-to-int-hash-map/
   private final Map<Integer,Integer> evaluatedBoardPositionCache = new HashMap<>(INITIAL_CAPACITY);
   private int cacheAccessCount = 0;
   private int cacheHitCount = 0;

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

   @Value
   public static class ComputeMoveResult {
      Move move;
      int analysedPositions;
   }

   /**
    * Find best move for computerplayer.
    *
    * @param player X- or Y-player, the next player
    * @return a computermove
    */
   public ComputeMoveResult computeMove(final int player)
   {
      currentPlayer = player;
      // the first pins are set by simple rules
      Move initMove = initialMoveGenerator.initialMove(match, player);
      if (initMove != null)
      {
         // TODO: why?
         releaseThreadTimeSlice();
         return new ComputeMoveResult(initMove, 0);
      }

      clock = new Stopwatch();

      ComputeMoveResult bestMove = internalComputeMove(player);

      System.out.println("Elapsed: " + clock.getElapsedMillis() + " msec.");

      return bestMove;
   }

   private void releaseThreadTimeSlice() {
      try
      {
         Thread.sleep(WAIT_MILLIS);
      }
      catch (InterruptedException e)
      {
         System.out.println("Sleep Interrupted");
      }
   }

   /**
    * Find best move for computerplayer by using alpha-beta.
    *
    * @param player X- or Y-player, the next player
    */
   private ComputeMoveResult internalComputeMove(final int player)
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
      evaluatedBoardPositionCache.clear();
      cacheAccessCount = 0;
      cacheHitCount = 0;
      for (int currentMaxPly = 3; currentMaxPly <= maxPly; currentMaxPly++)
      {
         if (currentMaxPly == 4 && currentMaxPly != maxPly) continue;

         System.out.println("refining ply " + currentMaxPly);

         // TODO: use windowing?
         alphaBeta(computeMoveContext, player, currentMaxPly, currentMaxPly, -Integer.MAX_VALUE, Integer.MAX_VALUE);
         usealphabeta = true;
         if (isThinkingTimeExceeded())
         {
            break;
         }

         System.out.println("analysed board positions " + evaluatedBoardPositionCache.size());
      }

      return new ComputeMoveResult(computeMoveContext.getBestMove(), evaluatedBoardPositionCache.size());
   }

   /**
    * Evaluate situation.
    * @param player player who has next turn
    * @return value of current situation on board - the higher the better for y-player
    */
   private int evaluatePosition(int player)
   {
      int positionValY, positionValX;

      // evaluate
      positionValY = match.getEvalY().evaluateY(false, player);
      positionValX = match.getEvalX().evaluateY(false, -player);


      // at the first move only defensive moves are good
      if (match.getMoveNr() < 8)
      {
         if (currentPlayer == Board.YPLAYER)
         {
            positionValY = 0;
         }
         else
         {
            positionValX = 0;
         }

      }
      return positionValX - positionValY;

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
      if (isThinkingTimeExceeded())
      {
         return 0;
      }

      if (ply == 0)
      {
         return positionValue(player);
      }

      // generate list of move candidates, most promising first
      // TODO: convert List to Collection
      List<Move> promisingMoves = moveGenerator.generateMoves(computeMoveContext, player, ply == maxPly);

      // a check for game over
      if (promisingMoves.isEmpty())
      {
         // the earlier the better
         return (GAMEOVER + ply) * player;
      }

      if (player == Board.XPLAYER)
      // minimizing node
      {
         return evaluatePromisingMovesX(computeMoveContext, Board.XPLAYER, maxPly, ply, alpha, beta, promisingMoves);
      }
      else
      //maximizing node
      {
         return evaluatePromisingMovesY(computeMoveContext, Board.YPLAYER, maxPly, ply, alpha, beta, promisingMoves);
      }
   }

   private int evaluatePromisingMovesY(ComputeMoveContext computeMoveContext, int player, int maxPly, int ply, int alpha, int beta, List<Move> orderedMoves) {
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
                  computeMoveContext.setBestMove(move);
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

   private int evaluatePromisingMovesX(ComputeMoveContext computeMoveContext, int player, int maxPly, int ply, int alpha, int beta, List<Move> orderedMoves) {
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
                  computeMoveContext.setBestMove(move);
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

   private boolean isThinkingTimeExceeded() {
      return maxTime > 0 && maxTime * MILLI_PER_SEC <= clock.getElapsedMillis();
   }

   private int positionValue(int player) {
      // TODO: sadly despite a 70% cache hit ratio, we only get a performance improvement of ~10%
      cacheAccessCount++;
      if (cacheAccessCount % 10000 == 0) {
         System.out.println(String.format("analysed distinct positions, cache hit ratio: %s/%s ~ %s%%", cacheHitCount, cacheAccessCount, (cacheHitCount*100/cacheAccessCount)));
      }
      final Board boardY = match.getBoardY();
      Integer cacheKey = boardY.getZobristValue();
      Integer cachedScore = evaluatedBoardPositionCache.get(cacheKey);
      if (cachedScore != null)
      {
         cacheHitCount++;
         //System.out.println("Treffer bei ply = " + ply + " Hashsize:" + zobristMap.size());
         if (!DEBUG) return cachedScore;
      }
      int calculatedScore = evaluatePosition(player);
      evaluatedBoardPositionCache.put(cacheKey, calculatedScore);

      if (DEBUG && cachedScore != null && cachedScore != calculatedScore) {
         // TODO: figure out why re-evaluating the board sometimes results in different scores?!?
         System.err.println("cached/calculated score differ for same board position!");
//         throw new IllegalStateException("cached/calculated score differ for same board position!");
      }
      return calculatedScore;
   }

}
