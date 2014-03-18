package controllers

import play.api.Play.current
import akka.pattern.ask
import akka.util.Timeout
import scala.util._
import scala.concurrent.{Future}
import scala.concurrent.duration._
import harvester.{HarvestingStatus, Criteria, SailingDAOImpl}
import scala.xml.{Elem, NodeSeq}
import play.libs.Json._
import akka.actor.{ActorSystem, Props}
import play.api.mvc._
import play.Logger
import play.api.libs.json.Json
import actors.{StatusActor, CategoryInfoActor}

object Application extends Controller {

  val actorSystem = ActorSystem("harvesterSystem")

  implicit val ec = actorSystem.dispatcher

  private val url = "http://devtsl.dev.sabre.com:9001/tvly-css-service-1.0/remote-service/categoryInfo/0"


  val statusActor = actorSystem.actorOf(Props(classOf[StatusActor], new HarvestingStatus()), "statusActor")

  implicit val timeout = Timeout(30 seconds)

  def index = Action {
    val dao = new SailingDAOImpl
    val sailings = dao.getSailing(Criteria.loadCriteria())

    statusActor ! sailings.size
    var startTime: Double = 0
    sailings.foreach(sailing => {
      val harvestingActor = actorSystem.actorOf(Props(classOf[CategoryInfoActor], url))
      startTime = startTime + (math.random * 10)
      Logger.info("Start time" + startTime)
      actorSystem.scheduler.scheduleOnce(startTime.seconds, harvestingActor, sailing)
    }
    )
    Ok(views.html.index())
  }

  def status = Action.async {
    val future = statusActor ? "Status"
    future map {
      harvestingStatus => harvestingStatus match {
        case hs: HarvestingStatus => hs
      }
    }
    val timeoutFuture = play.api.libs.concurrent.Promise.timeout("Oops", 20.second)
    Future.firstCompletedOf(Seq(future, timeoutFuture)).map {
      case hs: HarvestingStatus => Logger.info("received a harvesting status")
        Ok(Json.obj(
          "total" -> hs.total,
          "success" -> hs.success,
          "failure" -> hs.failure,
          "isComplete" -> hs.isComplete
        ))
      case t: String => InternalServerError(t)
    }
  }

}