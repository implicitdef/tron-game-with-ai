package manu.tron.service.impl

import manu.tron.common.Vocabulary._
import manu.tron.service._


trait GameOperatorServiceComponent {

  this: GameBasicLogicServiceComponent =>

  val gameOperatorService: GameOperatorService

  class GameOperatorServiceImpl extends GameOperatorService {


    def buildInitialStatus(board: Board, playersPos: Map[PlayerId, Pos]): GameStatus =
      GameStatus(
        board,
        playersPos,
        playersPos.mapValues(_ => Nil),
        Nil,
        Some(playersPos.keys.min)
      )

    def applyPlayerMove(status: GameStatus, Id: PlayerId, d: Direction): GameStatus =
      //the call might be illegitimate
      status.nextPlayerToPlay match {
        case Some(Id) => {
          val oldPos = status.playersPos.get(Id).get
          val newPos = gameBasicLogicService.applyDirection(oldPos, d)
          incrementNextPlayerToPlay(
            if (gameBasicLogicService.isPosValid(newPos, status))
            //standard move
            registerChangeOfPos(Id, oldPos, newPos, status)
            //bumped into something
            else registerDeathOfPlayer(Id, status)
          )
        }
        case Some(otherId) => throw new RuntimeException("The player " + otherId + " is not expected to play right now")
        case None          => throw new RuntimeException("The game is finished")
      }

    def isGameOver(status: GameStatus): Boolean =
      (status.playersPos.keySet -- status.deadPlayers).size < 2


    private def registerChangeOfPos(Id: PlayerId, oldPos: Pos, newPos: Pos, status: GameStatus) =
      GameStatus(
        status.board,
        status.playersPos.map {
          case (Id, _) => (Id, newPos)
          case other   => other
        },
        status.playersPreviousPos.map {
          case (Id, seqOfPreviousPos) => (Id, seqOfPreviousPos :+ oldPos)
          case other   => other
        },
        status.deadPlayers,
        status.nextPlayerToPlay
      )

    private def registerDeathOfPlayer(id: PlayerId, status: GameStatus) =
      GameStatus(
        status.board,
        status.playersPos,
        status.playersPreviousPos,
        status.deadPlayers :+ id,
        status.nextPlayerToPlay
      )

    private def incrementNextPlayerToPlay(status: GameStatus) = {
      val playersIds = status.playersPos.keySet.toSeq
      val alivePlayersIds = playersIds.sorted.filterNot(status.deadPlayers.contains(_))
      val newNextPlayerId = alivePlayersIds.size match {
        //if 0 or 1 players left, no more move is expected
        case 0 => None
        case 1 => None
        //otherwise
        case _ => Some(
          //the next in the list, or the first one
          alivePlayersIds.find( _ > status.nextPlayerToPlay.get).getOrElse(alivePlayersIds.head)
        )
      }
      GameStatus(
        status.board,
        status.playersPos,
        status.playersPreviousPos,
        status.deadPlayers,
        newNextPlayerId
      )
    }
  }
}
