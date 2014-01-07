package controllers

import models.{Responses, Choices, Polls, Users}
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

  def getMessage = DBAction {
    implicit rs =>
      Ok(views.html.Message.getMessage(List(
        Users.ddl.createStatements.mkString(";\n"),
        Polls.ddl.createStatements.mkString(";\n"),
        Choices.ddl.createStatements.mkString(";\n"),
        Responses.ddl.createStatements.mkString(";\n")
      )))
  }
}