package manu.tron

import manu.tron.common.Vocabulary._
import manu.tron.service.impl.{GameBasicLogicServiceImpl, VoronoiServiceImpl}
import scala.Predef._
import scala.Some
import manu.tron.common.Vocabulary.Board
import manu.tron.common.Vocabulary.Pos
import scala.Some
import manu.tron.common.Vocabulary.GameStatus

/**
 * Created by manu on 2/3/14.
 */
object QuickDebugger {


  def main(args: Array[String]): Unit = {

    val service = new VoronoiServiceImpl with GameBasicLogicServiceImpl {}

    val gameStatus = GameStatus(
      Board(10, 10),
      Map(
        1 -> Pos(0, 0),
        2 -> Pos(9, 9)
      ),
      Map(
        1 -> Seq(
          Pos(1, 0),
          Pos(1, 1)
        ),
        2 -> Seq(

        )
      ),
      Seq(),
      Some(1)
    )


    print(display(gameStatus))

    println()
    println()
    println()


    print(displayRegions(gameStatus.board, service.computeVoronoiRegions(gameStatus, None)))

  }

  private def display(board: Board) =
    (0 until board.height).map( x =>
      (0 until board.width).map( y =>
        "."
      ) mkString " "
    ) mkString "\n"

  private def display(status: GameStatus): String = {
//    case class GameStatus(
//   board: Board,
//   playersPos: Map[PlayerId, Pos],
//   playersPreviousPos: Map[PlayerId, Seq[Pos]],
//   deadPlayers: Seq[PlayerId],
//   nextPlayerToPlay: Option[PlayerId]
//   )

    val p1 = status.playersPos.keys.head
    val p2 = status.playersPos.keys.filterNot(_ == p1).head

    """----------GameStatus----------
board : """ + status.board.width + " x " + status.board.height + """
players: """ + status.playersPos.keys.mkString("(", ", ", ")") + """
positions: """ + status.playersPos + """
previous positions: """ + status.playersPreviousPos + """
dead: """ + status.deadPlayers + """
next to play: """ + status.nextPlayerToPlay + """

""" +
      display(
        status.board,
        Map(
          p1.toString -> Seq(status.playersPos(p1)),
          p2.toString -> Seq(status.playersPos(p2)),
          "X" -> status.playersPreviousPos.values.flatten.toSeq
        )
      ) + """

---------------------------------"""


  }

  private def displayRegions(board: Board, regions: Map[PlayerId, Seq[Pos]]): String = {
    val p1 = regions.keys.head
    val p2 = regions.keys.filterNot(_ == p1).head
    "----------Regions----------\n" +
    "\n" +
    display(
      board,
      Map(
        p1.toString -> regions(p1),
        p2.toString -> regions(p2)
      )
    ) +
    "\n" +
    "---------------------------"
  }


  private def display(board: Board, posSeq: Seq[Pos]): String =
    display(board, posSeq, 'X')

  private def display(board: Board, posSeq: Seq[Pos], char: Char): String =
    (0 until board.height).map( y =>
      (0 until board.width).map( x =>
        if (posSeq.contains(Pos(x, y)))
          char
        else
          "."
      ) mkString " "
    ) mkString "\n"

  private def display(board: Board, positions: Map[String, Seq[Pos]]): String =
    (0 until board.height).map( y =>
      (0 until board.width).map( x => {
        val matchingPosSeqs = positions.filter { case (_, posSeq) =>
          posSeq.contains(Pos(x, y))
        }
        matchingPosSeqs.size match {
          case 0 => "."
          case 1 => matchingPosSeqs.head._1
          case _ => matchingPosSeqs.keys.mkString("/")
        }
      }) mkString " "
    ) mkString "\n"


}
