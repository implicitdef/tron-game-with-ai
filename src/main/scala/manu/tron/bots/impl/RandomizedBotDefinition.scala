package manu.tron.bots.impl

import scala.util.Random
import manu.tron.common.Vocabulary._
import manu.tron.service.GameBasicLogicService
import manu.tron.bots.BotDefinition

class RandomizedBotDefinition extends BotDefinition {

  this: GameBasicLogicService =>

  override def nextMove(status: GameStatus, selfId: PlayerId) = {
    randomPick(validDirections(status, selfId)).getOrElse(anyDirection)
  }

  private def randomPick[T](seq: Seq[T]) : Option[T] =
    if (seq.isEmpty) None
    else Some(Random.shuffle(seq).head)
}
