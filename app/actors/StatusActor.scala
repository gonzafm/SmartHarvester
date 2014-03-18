package actors

import play.api.Play.current
import akka.actor.{Props, Actor}
import harvester.HarvestingStatus
import play.api.Logger

/**
 * Created by Gonza on 3/14/14.
 */
object StatusActor{
  def props(): Props = Props(new StatusActor(new HarvestingStatus()))
  def props(harvestingStatus:HarvestingStatus): Props = Props(new StatusActor(harvestingStatus))
}
class StatusActor(val harvestingStatus: HarvestingStatus) extends Actor {

  def receive = {
    case "Success" => {
      Logger.info("I received a success " + harvestingStatus.success)
      harvestingStatus.increaseSuccess()
    }
    case "Failure" => {
       Logger.info("I received a failure " + harvestingStatus.failure)
       harvestingStatus.increaseFailure()
    }
    case "Status" => {
      Logger.info("I received a status request ")
      sender ! harvestingStatus
    }

    case totalValue:Int => harvestingStatus.total = totalValue

    case _ => {
      Logger.info("I received a crazy ")
    }
  }
}