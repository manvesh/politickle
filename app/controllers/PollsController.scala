package controllers

import play.api.mvc.Controller
import play.api.db.slick.DBAction
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Mapping
import play.api.data.Forms._
import play.api.Play.current
import models.{Users, Poll, Choice, Polls}
import play.Logger


object PollsController extends Controller with securesocial.core.SecureSocial {
  import mappings.CustomMappings._

  case class PollFormData(description: String,
                          hashTag: Option[String],
                          choices: Seq[ChoiceFormData],
                          pollTargets: Option[Seq[PollTargetFormData]])
  case class ChoiceFormData(description: String)
  case class PollTargetFormData(handle: String)

  val pollForm = Form(
    mapping(
      "description" -> nonEmptyText,
      "hashTag" -> optional(text),
      "choices" -> seq(
        mapping(
          "description" -> nonEmptyText
        )(ChoiceFormData.apply)(ChoiceFormData.unapply)
      ),
      "pollTargets" -> optional(seq(
        mapping(
          "handle" -> text
        )(PollTargetFormData.apply)(PollTargetFormData.unapply)
      ))
    )(PollFormData.apply)(PollFormData.unapply)
  )

  //case class SecuredDBAction extends Action with SecuredAction with DBAction

  def newPoll = SecuredAction { implicit session =>
    Ok(views.html.Polls.newPoll(pollForm))
  }

  def show(id: Long) = UserAwareAction { implicit request =>
    DB withSession { implicit s: Session =>
      val pollFromDB = Polls.findById(id)
      val userFromDB = request.user flatMap { userIdentity => Users.findByTwitterId(userIdentity.identityId.userId) }
      pollFromDB match {
        case Some(poll) => Ok(views.html.Polls.show(poll, userFromDB))
        case _ => NotFound
      }
    }
  }

  def create = SecuredAction { implicit request =>
    val form = pollForm.bindFromRequest.get

    Logger.info(form.toString)
    // TODO: create Poll
    Redirect(routes.PollsController.show(0))
  }
}
