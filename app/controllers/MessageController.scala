package controllers

import models.Cards
import play.api.Play.current
import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import play.api.Routes
import play.api.Logger
import play.api.db.slick.DBAction
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import scala.slick.lifted.Query

case class Message(value: String)

object MessageController extends Controller {

  implicit val fooWrites = Json.writes[Message]

  val Cards = new Cards

  Logger.debug(Cards.ddl.createStatements.mkString)

  def getMessage = DBAction {
    implicit rs =>
      val cards = Query(Cards)
      Logger.debug(cards.selectStatement)
      Ok(Json.toJson(Message("Backbone Hello World: " + cards.list.length)))
  }
}