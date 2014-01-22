package manu.tron.web

import com.twitter.finatra._
import manu.tron.bots.impl._
import manu.tron.common.Direction2String._
import manu.tron.common.Vocabulary._
import manu.tron.service.impl._

object HumanVsBotController extends Controller with ControllerUtil {

  // Conf
  private val templateFileName = "humanVsBot.mustache"
  private val humanPlayerId = 1
  private val botPlayerId = 2
  private val board = Board(30, 10)
  private val initialPoses =  Map(
    humanPlayerId -> topLeft(board),
    botPlayerId   -> bottomRight(board)
  )
  // Injections
  private val bot     = new WallHuggingBotDefinition with GameBasicLogicServiceImpl
  private val service = new GameOperatorServiceImpl with GameBasicLogicServiceImpl
  // Mutable status
  private var currentStatus = buildInitialStatus()

  get("/") { request =>
    currentStatus = buildInitialStatus()
    render.view(new View {
      override val template = templateFileName
      val initialMap = asSeqOfSeq(board)
      val playerPos = initialPoses.get(humanPlayerId).get
      val serverPos = initialPoses.get(botPlayerId).get
    }).toFuture
  }

  get("/move") { request =>
    //do the human player's move
    currentStatus = service.applyPlayerMove(currentStatus, humanPlayerId, readParam(request, "d"))
    val response =
      //might have ended the game
      if(service.isGameOver(currentStatus))
        HumanVsBotControllerResponse(
          isDead(currentStatus, humanPlayerId),
          isDead(currentStatus, botPlayerId)
        )
      else {
        //game continues, do the bot's move
        val botDirection = bot.nextMove(currentStatus, botPlayerId)
        currentStatus = service.applyPlayerMove(currentStatus, botPlayerId, botDirection)
        HumanVsBotControllerResponse(
          isDead(currentStatus, humanPlayerId),
          isDead(currentStatus, botPlayerId),
          Some(botDirection)
        )
      }
    render.json(response).toFuture
  }


  protected def buildInitialStatus() =
    service.buildInitialStatus(
      board,
      initialPoses
    )

}