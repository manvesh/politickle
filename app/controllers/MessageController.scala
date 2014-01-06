package controllers

import models.{Choices, Polls, Users}
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

  Logger.debug("==========DDL statements begin==========")
  Logger.debug(Users.ddl.createStatements.mkString)
  Logger.debug(Polls.ddl.createStatements.mkString)
  Logger.debug(Choices.ddl.createStatements.mkString)
  Logger.debug("==========DDL statements end==========")

  def getMessage = DBAction {
    implicit rs =>
      Ok(Json.toJson(Message("Backbone Hello World: " + Users.count)))
  }
}