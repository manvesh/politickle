package controllers

import play.api.mvc.Controller
import play.api.db.slick.DBAction
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Mapping
import play.api.data.Forms._
import play.api.Play.current
import models.{Poll, Choice, Polls}


object PollsController extends Controller with securesocial.core.SecureSocial {
//  case class PollFormData(description: String, hashTag: Option[String], choices: Seq[ChoiceFormData])
//  case class ChoiceFormData(description: String)
  import mappings.CustomMappings._

  val pollForm = Form(
    mapping(
      "description" -> nonEmptyText,
      "hashTag" -> optional(text),
      "choices" -> seq(
        mapping(
          "id" -> optional(longNumber),
          "pollId" -> longNumber,
          "description" -> nonEmptyText
        )(Choice.apply)(Choice.unapply)
      )
    )(Poll.apply)(Poll.unapply)
  )

  def newPoll = DBAction { implicit session =>
    Ok(views.html.Polls.newPoll(pollForm))
  }

  def show(id: Long) = DBAction { implicit session =>
    Polls.findById(id) map { poll =>
      Ok(views.html.Polls.show(poll))
    } getOrElse NotFound
  }

  def create = SecuredAction { implicit request =>
    val form = pollForm.bindFromRequest.get
    // TODO: create Poll
    Redirect(routes.PollsController.show(0))
  }
}
