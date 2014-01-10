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
import util.TwitterHelper
import java.util.List
import twitter4j.User
import play.api.libs.json.Json
import java.util.{List => JList}
import scala.collection.JavaConverters._


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

  def newPoll = SecuredAction {
    implicit session =>
      Ok(views.html.Polls.newPoll(pollForm))
  }

  def show(id: Long) = UserAwareAction {
    implicit request =>
      DB withSession {
        implicit s: Session =>
          val currentUrl = routes.PollsController.show(id).absoluteURL(true)
          val pollFromDB = Polls.findById(id)
          val userFromDB = request.user flatMap {
            userIdentity => Users.findByTwitterId(userIdentity.identityId.userId)
          }
          pollFromDB match {
            case Some(poll) => {
              val ownerUser = Users.findById(poll.ownerId).get
              val choices = Choices.findByPollId(poll.id.get)
              val targets = PollTargets.findByPollId(poll.id.get)

              if (userFromDB.isEmpty) {
                val session = request.session +("original-url", request.uri)
                Ok(views.html.Polls.show(poll, choices, Nil, ownerUser, None, None, currentUrl)).withSession(session)
              } else {
                val twitterOption = TwitterHelper.getTwitter4JInstance(userFromDB.get)

                val handles = twitterOption map {
                  twitter =>
                    twitter.lookupUsers(targets.map(_.twitterUserId).toArray)
                      .asInstanceOf[JList[User]].asScala map {
                      user =>
                        user.getScreenName()
                    }
                } getOrElse (Nil).map("@" + _)

                val userResponse = Responses.findByIdAndTwitterUserId(poll.id.get, userFromDB.get.twitterId)
                Logger.info("UserResponse is " + userResponse.toString + " for poll.id.get" + poll.id.get.toString + " and user " + userFromDB.get.twitterId)
                Ok(views.html.Polls.show(poll, choices, handles, ownerUser, userFromDB, userResponse, currentUrl))
              }
            }
            case _ => NotFound
          }
      }
  }

  def create = SecuredAction {
    implicit request =>
      val pollFormData = pollForm.bindFromRequest.get
      Logger.info(pollFormData.toString)

      play.api.db.slick.DB.withTransaction {
        implicit s: Session =>
          val pollId = Polls.insert(pollFormData)
          val choices = pollFormData.choices.map {
            choiceFormData =>
              Choice(None, pollId, choiceFormData.description)
          }
          Choices.insertAll(choices: _*)
          pollFormData.pollTargets map {
            pollTargetFormData =>
              val pollTargets = pollTargetFormData.map {
                pollTargetFormDatum =>
                  PollTarget(None, pollTargetFormDatum.twitterUserId, pollId)
              }
              PollTargets.insertAll(pollTargets: _*)
          }

          Redirect(routes.PollsController.show(pollId))
      }
  }
}
