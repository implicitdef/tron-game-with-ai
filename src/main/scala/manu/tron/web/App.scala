package manu.tron.web

import com.twitter.finatra._
import manu.tron.bot.impl._
import manu.tron.service.impl._

object App extends FinatraServer {

  object ComponentRegistry extends BotDefinitionComponent
                              with GameBasicLogicServiceComponent
                              with GameOperatorServiceComponent
                              with VoronoiServiceComponent
                              with IndexControllerComponent
                              with MinimaxServiceComponent {

    override val botDefinition = new BotDefinitionImpl
    override val gameBasicLogicService = new GameBasicLogicServiceImpl
    override val gameOperatorService = new GameOperatorServiceImpl
    override val voronoiService = new VoronoiServiceImpl
    override val indexController = new IndexController
    override val minimaxService = new MinimaxServiceImpl

  }

  register(ComponentRegistry.indexController)

}
