package net.schwagereit.t1j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Erich Eichinger
 * @since 31/12/2015
 */
public class GenerateMoveContext {
   private static final int INITIAL_CAPACITY = 30;

   private final List<ValuedMove> valuedMoves = new ArrayList<ValuedMove>(INITIAL_CAPACITY);

   // 1st for X, 2nd for Y
   private final Map<Move, Number>[] killerMoves = newArray(new HashMap<Move, Number>(), new HashMap<Move, Number>());

   @SafeVarargs
   private static <T> T[] newArray(T... items) {
      return items;
   }

   public boolean hasValuedMoves() {
      return valuedMoves.size() > 0;
   }

   public List<Move> getSortedMoves(int player) {
      List<Move> orderedMoves = new ArrayList<>(valuedMoves.size());
      Collections.sort(valuedMoves, new Comparator<ValuedMove>() {
         public int compare(ValuedMove o1, ValuedMove o2) {
            return ((o1).value - (o2).value) * player;
         }
      });
      for (Iterator<ValuedMove> iterator = valuedMoves.iterator(); iterator.hasNext(); ) {
         ValuedMove valuedMove = iterator.next();
         orderedMoves.add(valuedMove.move);
      }
      valuedMoves.clear();
      return orderedMoves;
   }

   /**
    * Add a move the list.
    *
    * @param move  the move
    * @param value its value
    */
   public void addValuedMove(Move move, int value) {
      valuedMoves.add(new ValuedMove(move, value));
   }

   /**
    * In the map of killerMoves found, a move-counter is incremented.
    *
    * @param move   the move
    * @param player the player
    */
   public void addKiller(Move move, int player) {
      int ref = (player == Board.XPLAYER) ? 0 : 1;

      int val = getHits(move, ref) + 1;
      killerMoves[ref].put(move, val);
   }

   /**
    * Compute sort-value for new moves. Value is equal to number of hits as killermoves before.
    *
    * @param move move
    * @param ref  0 for xplayer, 1 for yplayer
    * @return value
    */
   private int getHits(Move move, int ref) {
      Number countObject = killerMoves[ref].get(move);
      return (countObject == null) ? 0 : countObject.intValue();
   }

   /**
    * Sort moves.
    *
    * @param moves  the moves to sort
    * @param player next Player
    * @return sorted list
    */
   public List<Move> sortMoves(Set<Move> moves, int player) {
      final int ref = (player == Board.XPLAYER) ? 0 : 1;
      List<Move> list = new ArrayList<Move>(moves);
      Collections.sort(list, new Comparator<Move>() {
         public int compare(Move oOne, Move oTwo) {
            return (getHits(oTwo, ref) - getHits(oOne, ref));
         }
      });
      return list;
   }

   private static final class ValuedMove {
      final int value;
      final Move move;

      /**
       * Cons'tor.
       *
       * @param moveIn  the moveIn
       * @param valueIn its valueIn
       */
      public ValuedMove(Move moveIn, int valueIn) {
         this.move = moveIn;
         this.value = valueIn;
      }

      /**
       * Print object as String.
       *
       * @return Representation as String
       */
      public String toString() {
         return "(" + move + ": " + value + ")";
      }

   }
}
