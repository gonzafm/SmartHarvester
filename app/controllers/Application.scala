package controllers

import play.api.Play.current
import akka.pattern.ask
import akka.util.Timeout
import scala.util._
import scala.concurrent.{Future}
import harvester.{HarvestingStatus, Criteria, SailingDAOImpl}
import scala.concurrent.duration._
import scala.xml.{Elem, NodeSeq}
import play.libs.Json._
import akka.actor.{ActorSystem, Props}
import play.api.mvc._
import play.Logger

import play.api.libs.json.Json
import actors.{HarvestingActor, StatusActor, CategoryInfoActor}
import play.Play

object Application extends Controller {

  val actorSystem = ActorSystem("harvesterSystem")

  implicit val ec = actorSystem.dispatcher

  private val url = Play.application.configuration.getString("cruise.service.url")

  val statusActor = actorSystem.actorOf(Props(classOf[StatusActor], new HarvestingStatus()), "statusActor")

  implicit val timeout = Timeout(30 seconds)

  var started = false

  def index(vendorCode:String,minDays:String,maxDays:String) = Action {
    if (!started) {
      val criteria = new Criteria(vendorCode.split(",").toList,minDays.toInt,maxDays.toInt)
      val harvestingActor = actorSystem.actorOf(Props(classOf[HarvestingActor], url, criteria), "harvestingActor")
      actorSystem.scheduler.schedule(0.millisecond, 12.hours, harvestingActor, "start")
      started = true
    }
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

  def shutdown = {
    actorSystem.shutdown()
  }

}