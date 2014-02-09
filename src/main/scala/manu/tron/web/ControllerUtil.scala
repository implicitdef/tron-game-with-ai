package manu.tron.web

import manu.tron.common.Vocabulary._
import com.twitter.finatra.Request
import scala.util.Random

trait ControllerUtil {

  protected def asSeqOfSeq(board: Board): Seq[Seq[Any]] =
    (0 until board.height).map( _ => 0 until board.width)

  protected def topLeft(board: Board): Pos =
    Pos(0, 0)

  protected def bottomRight(board: Board): Pos =
    Pos(board.width - 1, board.height - 1)

  protected def randomPos(board: Board): Pos =
    Pos(Random.nextInt(board.width), Random.nextInt(board.height))

  protected def randomPosWithExclusion(board: Board, excluded: Pos): Pos = {
    val p = randomPos(board)
    if(p == excluded)
      randomPosWithExclusion(board, excluded)
    else
      p
  }

  protected def randomInitialPoses(playerIdFirst: PlayerId, playerIdSecond: PlayerId, board: Board): Map[PlayerId, Pos] = {
    val p = randomPos(board)
    Map(
      playerIdFirst  -> p,
      playerIdSecond -> randomPosWithExclusion(board, p)
    )
  }

  protected def readParam(request: Request, key: String): String =
    request.params.get(key).getOrElse(throw new RuntimeException(s"The parameter $key is missing"))

  protected def isDead(status: GameStatus, playerId: PlayerId) =
    status.deadPlayers.contains(playerId)

  protected case class HumanVsBotControllerResponse(
    isPlayerDead: Boolean = false,
    isServerDead: Boolean = false,
    serverMove: Option[String] = None
  )

  protected case class BotVsBotControllerResponse(
     movingPlayerId: PlayerId,
     move: String,
     died: Boolean
   )


}
