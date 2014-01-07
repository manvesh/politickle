package controllers

import play.api.mvc.Controller
import play.api.db.slick.DBAction
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.Play.current
import models.Polls

object PollsController extends Controller with securesocial.core.SecureSocial {
  def show(id: Long) = DBAction { implicit session =>
    Polls.findById(id) map { poll =>
      Ok(views.html.Polls.show(poll))
    } getOrElse(NotFound)
  }

  def create = SecuredAction { implicit request =>
    // TODO: create Poll
    Redirect(routes.PollsController.show(0))
  }
}
