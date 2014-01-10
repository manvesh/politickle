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
import play.api.libs.json.Json
import scala.collection.JavaConverters._
import java.util.{List => JList}
import twitter4j.{TwitterException, User}


object TwitterActionsController extends Controller with securesocial.core.SecureSocial {

  case class HandleList(handles: List[String])

  case class Tweet(tweet: String)

  val handlesForm = Form(
    mapping(
      "handles" -> list(text)
    )(HandleList.apply)(HandleList.unapply)
  )

  val tweetForm = Form(
    mapping(
      "tweet" -> text
    )(Tweet.apply)(Tweet.unapply)
  )

  def lookupUser() = SecuredAction(ajaxCall = true) {
    implicit request =>
      val handles = handlesForm.bindFromRequest.get
      Logger.debug("handles here: " + handles.handles.mkString(", "))
      DB withSession {
        implicit s: Session =>
          val userFromDB = Users.findByTwitterId(request.user.identityId.userId)
          userFromDB map {
            user =>
              val twitterOption = TwitterHelper.getTwitter4JInstance(user)

              twitterOption map {
                twitter =>
                  val users = twitter.lookupUsers(handles.handles.toArray)
                    .asInstanceOf[JList[User]].asScala map {
                    user =>
                      user.getId()
                  }
                  Ok(Json.toJson(users))
              } getOrElse (Unauthorized("Don't have no twitter creds."))

          } getOrElse (Unauthorized("User not logged in"))
      }
  }

  def postTweet() = SecuredAction(ajaxCall = true) {
    implicit request =>
      val tweet = tweetForm.bindFromRequest.get

      DB withSession {
        implicit s: Session =>
          val userFromDB = Users.findByTwitterId(request.user.identityId.userId)
          userFromDB map {
            user =>
              val twitterOption = TwitterHelper.getTwitter4JInstance(user)

              twitterOption map {
                twitter =>
                  twitter.updateStatus(tweet.tweet)
                  Ok(Json.toJson(
                    Map(
                      "success" -> "true"
                    )
                  ))
              } getOrElse (Unauthorized("Don't have no twitter creds."))

          } getOrElse (Unauthorized("User not logged in"))
      }
  }

}
