package manu.tron.web

import com.twitter.finatra._
import manu.tron.bots.impl._
import manu.tron.service.impl._
import manu.tron.common.Vocabulary.Board
import manu.tron.common.Direction2String._

trait IndexControllerComponent {

  this: GameOperatorServiceComponent
    with BotDefinitionComponent =>

  val indexController: Controller

  class IndexController extends Controller with ControllerUtil {

    // Conf
    private val templateFileName = "index.mustache"
    private val aPlayerId = 1
    private val bPlayerId = 2
    private val board = Board(20, 20)
    // Bots
    private val bots = Map(
      aPlayerId -> botDefinition,
      bPlayerId -> botDefinition
    )
    // Mutable status
    private var currentStatus = buildInitialStatus()

    get("/") { request =>
      currentStatus = buildInitialStatus()
      render.view(new View {
        override val template = templateFileName
        val initialMap = asSeqOfSeq(board)
        val aPos = currentStatus.playersPos.get(aPlayerId).get
        val bPos = currentStatus.playersPos.get(bPlayerId).get
      }).toFuture
    }


    get("/next") { request =>
      //who's playing ?
      val playerId = currentStatus.nextPlayerToPlay.getOrElse(throw new RuntimeException("Game is over already"))
      //what's his move ?
      val dir = bots.get(playerId).get.nextMove(currentStatus)
      //process it
      currentStatus = gameOperatorService.applyPlayerMove(currentStatus, playerId, dir)
      val response = BotVsBotControllerResponse(
        playerId,
        dir,
        isDead(currentStatus, playerId)
      )
      render.json(response).toFuture
    }


    private def buildInitialStatus() =
      gameOperatorService.buildInitialStatus(
        board,
        randomInitialPoses(aPlayerId, bPlayerId, board)
      )



  }

}
