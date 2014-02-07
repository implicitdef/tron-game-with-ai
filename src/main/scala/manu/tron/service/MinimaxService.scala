package manu.tron.service

import manu.tron.common.Vocabulary._

/**
 * Created by manu on 2/6/14.
 */
trait MinimaxService {


  /**
   * Generic implem of the mini-max algorithm for a 2 player game
   * @param status the state of the game
   * @param childsFinder a function giving all immediatly following child states,
   *                     and the moves to get there
   * @param heuristic An arbitrary function to rate how good a state is for a given player.
   *                  The better a state is for that player, the higher the rate should be.
   * @param depth The number of recursive look-ahead to make. The higher it is, the more time the
   *              algorithm will take
   * @return the move the next player to play should play
   */
  def minimax(status: GameStatus,
              childsFinder: (GameStatus => Map[Direction, GameStatus]),
              heuristic: ((GameStatus, PlayerId) => Int),
              depth: Int): Direction

}