package manu.tron.service.impl

import manu.tron.service.MinimaxService
import manu.tron.common.Vocabulary._


/**
 * Created by manu on 2/6/14.
 */
trait MinimaxServiceComponent {

  val minimaxService: MinimaxService

  class MinimaxServiceImpl extends MinimaxService {

    override def minimax(status: GameStatus,
                        childsFinder: (GameStatus => Map[Direction, GameStatus]),
                        heuristic: ((GameStatus, PlayerId) => Int),
                        depth: Int): Direction = {
      val pointOfView = status.nextPlayerToPlay match {
        case None => throw new IllegalArgumentException(
            "Couldn't decide a move, no player was expected to play"
          )
        case Some(p) => p
      }

      sealed trait Res{
        val rate: Int
      }
      case class ResWithMove(rate: Int, move: Direction) extends Res
      case class ResWithoutMove(rate: Int) extends Res

      def recurse(status: GameStatus, depth: Int): Res = {
        if(depth == 0)
          ResWithoutMove(heuristic(status, pointOfView))
        else
          childsFinder(status) match {
            case Seq() => ResWithoutMove(heuristic(status, pointOfView))
            case childs => {
              val childsAndRates = childs.map {  case (move, child) =>
                (move, recurse(child, depth - 1).rate)
              }
              val pick =
                if(status.nextPlayerToPlay.head == pointOfView)
                  childsAndRates.maxBy(_._2)
                else
                  childsAndRates.minBy(_._2)
              ResWithMove(pick._2, pick._1)
            }
          }
      }

      recurse(status, depth) match {
        case ResWithMove(_, move) => move
        case ResWithoutMove(_) => throw new IllegalArgumentException(
          "Couldn't decide a move, you either provided a depth of 0 or gave a status where the game was already over without any child nodes"
        )
      }

    }












  }


}
