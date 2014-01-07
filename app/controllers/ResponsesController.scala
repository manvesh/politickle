package controllers

import play.api.Play.current
import play.api.mvc.Controller
import play.api.libs.json.Json
import play.api.db.slick.Config.driver.simple._
import models.Polls


object ResponsesController extends Controller with securesocial.core.SecureSocial {
  def create(id: Long) = SecuredAction {
    implicit request =>

      play.api.db.slick.DB.withSession {
        implicit session: Session =>
          Ok(Json.toJson(Map("binding_values" -> Polls.count))).as("application/json")
      }
  }
}
