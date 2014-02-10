package manu.tron.web

import com.twitter.finatra._
import manu.tron.bot.impl._
import manu.tron.service.impl._
import manu.tron.common.Vocabulary.{Direction, GameStatus, Board}
import manu.tron.common.Direction2String._

trait IndexControllerComponent {

  this: GameOperatorServiceComponent
    with BotDefinitionComponent =>

  val indexController: Controller

  class IndexController extends Controller with ControllerUtil {

    private val templateFileName = "index.mustache"
    private val playerPlayerId = 1
    private val botPlayerId = 2
    private val board = Board(20, 20)
    private var currentStatus: GameStatus = buildInitialStatus

    get("/") { request =>
      currentStatus = buildInitialStatus
      render.view(new View {
        override val template = templateFileName
        val initialMap = asSeqOfSeq(board)
        val aPos = currentStatus.playersPos.get(playerPlayerId).get
        val bPos = currentStatus.playersPos.get(botPlayerId).get
      }).toFuture
    }

    get("/next") { request =>
      val dir: Direction = readParam(request, "move")
      currentStatus = gameOperatorService.applyPlayerMove(currentStatus, playerPlayerId, dir)
      val response =
        if (gameOperatorService.isGameOver(currentStatus))
          PlayerDiedResponse
        else {
          val botDir: Direction = botDefinition.nextMove(currentStatus)
          currentStatus = gameOperatorService.applyPlayerMove(currentStatus, botPlayerId, botDir)
          if (gameOperatorService.isGameOver(currentStatus))
            BotMovedAndDiedResponse(botDir)
          else
            BotMovedResponse(botDir)
        }
      render.json(response).toFuture
    }

    object PlayerDiedResponse {
      val playerDied = true
    }
    case class BotMovedResponse(botMove: String) {
      val playerDied = false
      val botDied = false
    }
    case class BotMovedAndDiedResponse(botMove: String) {
      val playerDied = false
      val botDied = true
    }

    private def buildInitialStatus =
      gameOperatorService.buildInitialStatus(
        board,
        randomInitialPoses(playerPlayerId, botPlayerId, board)
      )

  }

}
