package controllers

import play.api.mvc.{Action, Request, SimpleResult, Controller}
import play.api.db.slick.DBAction
import play.api.db.slick._
import play.api.data._
import play.api.data.Mapping
import play.api.data.Forms._
import play.api.Play.current
import models._
import play.Logger
import scala.concurrent.Future
import securesocial.core.{SecureSocial, SecuredRequest}
import scala.Some
import models.Choice
import securesocial.core.SecuredRequest
import models.Poll


object PollsController extends Controller with securesocial.core.SecureSocial {
  import mappings.CustomMappings._

  case class PollFormData(description: String,
                          hashTag: Option[String],
                          choices: Seq[ChoiceFormData],
                          pollTargets: Option[Seq[PollTargetFormData]])
  case class ChoiceFormData(description: String)
  case class PollTargetFormData(twitterUserId: Long, handle: Option[String])

  private val pollForm = Form(
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
          "twitterUserId" -> longNumber,
          "handle" -> optional(text)
        )(PollTargetFormData.apply)(PollTargetFormData.unapply)
      ))
    )(PollFormData.apply)(PollFormData.unapply)
  )

  private implicit def pollFormToPoll(pollForm: PollFormData)(implicit request: SecuredRequest[_], session: Session): Poll = {
    val user = Users.findByTwitterId(request.user.identityId.userId).get
    Poll(id = None,
      ownerId = user.id.get,
      description = pollForm.description,
      hashTag = pollForm.hashTag)
  }

  def newPoll = SecuredAction { implicit session =>
    Ok(views.html.Polls.newPoll(pollForm))
  }

  def show(id: Long) = UserAwareAction { implicit request =>
    DB withSession { implicit s: Session =>
      val pollFromDB = Polls.findById(id)
      val userFromDB = request.user flatMap { userIdentity => Users.findByTwitterId(userIdentity.identityId.userId) }
      pollFromDB match {
        case Some(poll) => {
          val ownerUser = Users.findById(poll.ownerId).get
          val choices = Choices.findByPollId(poll.id.get)
          if (userFromDB.isEmpty) {
            val session = request.session + ("original-url", request.uri)
            Ok(views.html.Polls.show(poll, choices, ownerUser, None, None)).withSession(session)
          } else {
            val userResponse = Responses.findByIdAndTwitterUserId(poll.id.get, userFromDB.get.twitterId)
            Logger.info("UserResponse is " + userResponse.toString + " for poll.id.get" + poll.id.get.toString + " and user " + userFromDB.get.twitterId)
            Ok(views.html.Polls.show(poll, choices, ownerUser, userFromDB, userResponse))
          }
        }
        case _ => NotFound
      }
    }
  }

  def create = SecuredAction { implicit request =>
    val pollFormData = pollForm.bindFromRequest.get
    Logger.info(pollFormData.toString)

    play.api.db.slick.DB.withTransaction { implicit s: Session =>
      val pollId = Polls.insert(pollFormData)
      val choices = pollFormData.choices.map { choiceFormData =>
        Choice(None, pollId, choiceFormData.description)
      }
      Choices.insertAll(choices: _*)
      pollFormData.pollTargets map { pollTargetFormData =>
        val pollTargets = pollTargetFormData.map { pollTargetFormDatum =>
          PollTarget(None, pollTargetFormDatum.twitterUserId, pollId)
        }
        PollTargets.insertAll(pollTargets: _*)
      }

      Redirect(routes.PollsController.show(pollId))
    }
  }
}
