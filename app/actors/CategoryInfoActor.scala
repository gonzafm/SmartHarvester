package actors

import play.api.Play.current
import akka.actor.{Actor, Props}
import models.{SailingsToHarvest, CategoryInfoRequest}
import scala.concurrent.Future
import play.api.libs.ws.{WS, Response}
import scala.util.{Failure, Success}
import play.Logger
import play.api.http.Status
import play.api.libs.concurrent.Akka

/**
 * Actor for the harvester
 */
object CategoryInfoActor {
  def props(url: String): Props = Props(new CategoryInfoActor(url))
}

class CategoryInfoActor(url: String) extends Actor {
  import context._

  def receive = {
    case aSailing: SailingsToHarvest => {
      val statusActor = actorSelection("akka://harvesterSystem/user/statusActor")
      val request: CategoryInfoRequest = CategoryInfoRequest.build(aSailing)
      val futureResponse: Future[Response] = WS
        .url(url)
        .post(CategoryInfoRequest.toXml(request))

      futureResponse onComplete {
        case Success(response: Response) =>
          response.status match {
            case Status.OK => {
              Logger.info("Sending a success message for sailing " + aSailing.externalSailingId
                + " for date " + aSailing.sailingDate)
              statusActor ! "Success"
            }
            case x => {
              statusActor ! "Failure"
              Logger.error(CategoryInfoRequest.toXml(request).toString())
              Logger.error(response.body)
            }
          }
        case Failure(response: Response) => {
          statusActor !"Failure"
          Logger.error(CategoryInfoRequest.toXml(request).toString())
          Logger.error(response.body)
        }
      }
    }
    case _ => Logger.error("Unnexpected error")
  }
}
