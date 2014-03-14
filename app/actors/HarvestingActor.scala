package actors

import akka.actor.{Actor, Props}
import models.{SailingsToHarvest, CategoryInfoRequest}
import scala.concurrent.Future
import play.api.libs.ws.{WS, Response}
import scala.util.{Failure, Success}
import play.Logger
import play.api.http.Status
import harvester.HarvestingStatus

/**
 * Actor for the harvester
 */
object HarvestingActor{
  def props(sailing:SailingsToHarvest, url:String): Props = Props(new HarvestingActor(sailing,url))
}

class HarvestingActor(sailing:SailingsToHarvest,url:String) extends Actor {
  private val harvestingStatus = new HarvestingStatus()
  def receive = {
    {
      val request: CategoryInfoRequest = CategoryInfoRequest.build(sailing)
      val futureResponse: Future[Response] = WS
        .url(url)
        .post(CategoryInfoRequest.toXml(request))

      futureResponse onComplete {
        case Success(response: Response) =>
          response.status match {
            case Status.OK => harvestingStatus.increaseSuccess()
            case x => {
              harvestingStatus.increaseFailure()
              Logger.error(response.xml.text)
            }
          }
        case Failure(response: Response) => {
          harvestingStatus.increaseFailure()
          Logger.error(response.xml.text)
        }
      }
    }
  }
}
