package manu.tron.service.impl

import manu.tron.service.MinimaxService


/**
 * Created by manu on 2/6/14.
 */
trait MinimaxServiceComponent {

  val minimaxService: MinimaxService

  class MinimaxServiceImpl extends MinimaxService {

    override def minimax(node: GameNode, childsFinder: ChildsFinder, heuristic: EvaluationHeuristic, depth: Int) = {

      val pointOfView = node.nextPlayer

      sealed trait Res{
        val rate: Int
      }
      case class ResWithMove(rate: Int, move: Move) extends Res
      case class ResWithoutMove(rate: Int) extends Res

      def recurse(node: GameNode, depth: Int): Res = {
        if(depth == 0)
          ResWithoutMove(heuristic(node, pointOfView))
        else
          childsFinder(node) match {
            case Seq() => ResWithoutMove(heuristic(node, pointOfView))
            case childs => {
              val childsAndRates = childs.map {  case (move, child) =>
                (move, recurse(child, depth - 1).rate)
              }
              val pick =
                if(node.nextPlayer == pointOfView)
                  childsAndRates.maxBy(_._2)
                else
                  childsAndRates.minBy(_._2)
              ResWithMove(pick._2, pick._1)
            }
          }
      }

      recurse(node, depth) match {
        case ResWithMove(_, move) => move
        case ResWithoutMove(_) => throw new IllegalArgumentException(
          "Couldn't decide a move, you either provided a depth of 0 or gave a node without any child nodes"
        )
      }

    }












  }


}
