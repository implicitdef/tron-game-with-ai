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



  def minimax(node: GameNode, childsFinder: ChildsFinder, heuristic: EvaluationHeuristic, depth: Int): Move


}
