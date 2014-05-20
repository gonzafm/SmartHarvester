package actors

import akka.actor.{Props, Actor}
import harvester.{Criteria, SailingDAOImpl}
import play.Logger
import scala.concurrent.duration._
import models.SailingsToHarvest

/**
 * Created by Gonza on 3/18/14.
 */
object HarvestingActor {
  def props(url: String,criteria:Criteria): Props = Props(new HarvestingActor(url,criteria))
}

class HarvestingActor(url: String, criteria:Criteria) extends Actor {

  import context._

  val TWELVE_HOURS = (12 * 60 * 60)

  def receive = {

    case "start" => val dao = new SailingDAOImpl
      val sailings = dao.getSailing(criteria)
      if (sailings.size > 0) {
        schedule(sailings)
      } else {
        Logger.error("The query returned 0 sailings")
      }
  }

  def schedule(sailings: List[SailingsToHarvest]) {
    Logger.info("Amount of sailings to process: " + sailings.size)
    val statusActor = actorSelection("akka://harvesterSystem/user/statusActor")
    statusActor ! sailings.size
    val timeDispersion = TWELVE_HOURS / sailings.size
    var startTime: Double = 0
    sailings.foreach(sailing => {
      val harvestingActor = actorOf(Props(classOf[CategoryInfoActor], url))
      startTime = startTime + (math.random * timeDispersion)
      context.system.scheduler.scheduleOnce(startTime.seconds, harvestingActor, sailing)
    }
    )
  }
}
