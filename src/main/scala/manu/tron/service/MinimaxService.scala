package manu.tron.service

/**
 * Created by manu on 2/6/14.
 */
trait MinimaxService {


  trait GameNode {
    def nextPlayer: Player
  }

  trait Player

  trait Move

  trait EvaluationHeuristic extends ((GameNode, Player) => Int)

  trait ChildsFinder extends (GameNode => Map[Move, GameNode])


  /**
   * Generic implem of the mini-max algorithm for a 2 player game
   * @param node the state of the game, indicating the next player who is playing
   *             and needs the decision
   * @param childsFinder a function giving all immediatly following child states,
   *                     and the moves to get there
   * @param heuristic An arbitrary function to rate how good a state is for a given player.
   *                  The better a state is for that player, the higher the rate should be.
   * @param depth The number of recursive look-ahead to make. The higher it is, the more time the
   *              algorithm will take
   * @return the move the next player should play
   */
  def minimax(node: GameNode, childsFinder: ChildsFinder, heuristic: EvaluationHeuristic, depth: Int): Move


}
