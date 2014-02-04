package manu.tron.web

import com.twitter.finatra._
import manu.tron.common.Vocabulary._
import manu.tron.common.Direction2String._
import manu.tron.bots.impl._
import manu.tron.service.impl._
import manu.tron.common.Vocabulary.Board
import manu.tron.common.Vocabulary.Pos

object IndexController extends Controller with ControllerUtil {


  // Conf
  private val templateFileName = "index.mustache"
  private val aPlayerId = 1
  private val bPlayerId = 2
  private val board = Board(20, 20)
  // Injections
  private val bots = Map(
    aPlayerId -> new MinimaxBotDefinition with GameBasicLogicServiceImpl with GameOperatorServiceImpl with VoronoiServiceImpl,
    bPlayerId -> new MinimaxBotDefinition with GameBasicLogicServiceImpl with GameOperatorServiceImpl with VoronoiServiceImpl
  )
  private val service = new GameOperatorServiceImpl  with GameBasicLogicServiceImpl
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
    val dir = bots.get(playerId).get.nextMove(currentStatus, playerId)
    //process it
    currentStatus = service.applyPlayerMove(currentStatus, playerId, dir)
    val response = BotVsBotControllerResponse(
      playerId,
      dir,
      isDead(currentStatus, playerId)
    )
    render.json(response).toFuture
  }


  protected def buildInitialStatus() =
    service.buildInitialStatus(
      board,
      randomInitialPoses(aPlayerId, bPlayerId, board)
    )



}
