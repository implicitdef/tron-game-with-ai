package manu.tron.common

object Vocabulary {

  case class Pos(x: Int, y: Int)

  case class Board(width: Int, height: Int)

  type PlayerId = Int

  sealed trait Direction
  object West  extends Direction
  object North extends Direction
  object East  extends Direction
  object South extends Direction

  case class GameStatus(
    board: Board,
    playersPos: Map[PlayerId, Pos],
    playersPreviousPos: Map[PlayerId, Seq[Pos]],
    deadPlayers: Seq[PlayerId],
    nextPlayerToPlay: Option[PlayerId]
  )

}
