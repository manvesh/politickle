package controllers

import util.{CardApiHelper, GoogleChartHelper}
import play.api.Play.current
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.api.db.slick.Config.driver.simple._
import models._
import play.api.data._
import play.api.data.Forms._
import java.sql.Timestamp
import java.util.Date
import play.api.db.slick.DBAction
import play.api.Logger
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{StatusUpdate, TwitterFactory}
import models.Response
import controllers.ResponseData
import play.api.libs.json.JsArray
import models.Response
import controllers.ResponseData
import models.Choice
import play.api.libs.json.JsObject

object ResponsesController extends Controller with securesocial.core.SecureSocial {

  val responseForm = Form(
    mapping(
      "twitter_id" -> text(0, 20),
      "poll_id" -> longNumber,
      "choice_id" -> longNumber,
      "explanation" -> optional(text(0, 2000))
    )(ResponseData.apply)(ResponseData.unapply)
  )

  def create(id: Long, isCard: Boolean) = Action {
    implicit request =>
      Logger.debug("isCard is: " + isCard)
      val responseData = responseForm.bindFromRequest.get
      play.api.db.slick.DB.withSession {
        val response = Response(
          None,
          responseData.twitterUserId,
          responseData.pollId,
          responseData.choiceId,
          responseData.explanation
        )

        Logger.debug("bound the form data")

        implicit session: Session =>
        // TODO: catch duplicate exceptions and ignore
          val choice = Choices.findById(response.choiceId)
          if (!choice.isDefined || choice.get.pollId != response.pollId) {
            BadRequest("Poll/Choice is invalid!")
          } else {
            val poll = Polls.findById(response.pollId)
            Responses.upsert(response)
            val tweetDescription = poll.get.description + "My answer: " + choice.get.description
            val tweetString = ellipse(tweetDescription, 115) + pollUrl(response.pollId)
            val choiceCounts: Seq[(Long, Int, String)] = Choices.findByPollId(response.pollId).map { choice: Choice =>
              (choice.id.get, Responses.countsPerPollIdAndChoiceId(response.pollId, choice.id.get), choice.description) }
            Logger.info("ResponseData : " + responseData.toString)
             
            val jsonResponse = 
              if (isCard) {
                  Json.obj(
                    "totweet" -> CardApiHelper.getStringBinding(tweetString),
                    "chart_image" -> GoogleChartHelper.getChartUrlImageBinding(choiceCounts))
              } else {
                  Json.obj(
                    "success" -> "true",
                    "data" -> Json.arr(choiceCounts.map { choiceCount =>
                      Json.obj(choiceCount._1.toString -> Json.obj("count" -> JsNumber(choiceCount._2), "description" -> choiceCount._3))
                    }))
              }

            Logger.debug("Json response : " + jsonResponse)

            Ok(jsonResponse).as("application/json")
          }
      }
  }

  val ELLIPSE = "..."

  def ellipse(original: String, maxLength: Int): String = {
    if (original.length <= maxLength) {
      original
    } else {
      original.substring(0, maxLength - ELLIPSE.length) + ELLIPSE;
    }
  }

  def pollUrl(pollId: Long): String = {
    "https://getpolitickle.com/polls/" + pollId
  }
}

case class ResponseData(twitterUserId: String, pollId: Long, choiceId: Long, explanation: Option[String])
