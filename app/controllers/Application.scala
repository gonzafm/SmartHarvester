package controllers

import scala.concurrent.{Await, Future}
import scala.concurrent
import scala.util._
import play.api.Play.current
import scala.concurrent.duration._
import play.api.libs.ws.Response
import play.api.mvc._
import harvester.{HarvestingStatus, Criteria, SailingDAOImpl}
import play.api.libs.ws.WS
import models.CategoryInfoRequest
import scala.xml.{Elem, NodeSeq}
import play.libs.Json._
import play.api.libs.json.Json
import play.Logger

object Application extends Controller {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  private val url = "http://devtsl.dev.sabre.com:9001/tvly-css-service-1.0/remote-service/categoryInfo/0"

  private var harvestingStatus: HarvestingStatus = null

  def index = Action {
    val dao = new SailingDAOImpl
    val sailings = dao.getSailing(Criteria.loadCriteria())
    var request: CategoryInfoRequest = null

    harvestingStatus = new HarvestingStatus(total = sailings.size)
    sailings.foreach(sailing => {
      request = CategoryInfoRequest.build(sailing)
      val futureResponse: Future[Response] = WS
        .url(url)
        .post(CategoryInfoRequest.toXml(request))

      futureResponse onComplete {
        case Success(response: Response) =>
          response.status match {
            case OK => harvestingStatus.increaseSuccess()
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
    )
    Ok(views.html.index())
  }

  def status = Action {
    Ok(Json.obj(
      "total" -> harvestingStatus.total,
      "success" -> harvestingStatus.success,
      "failure" -> harvestingStatus.failure,
      "isComplete" -> harvestingStatus.isComplete
    ))
  }
}