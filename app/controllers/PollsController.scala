package controllers

import play.api.mvc.Controller
import play.api.db.slick.DBAction
import play.api.Play.current

object PollsController extends Controller with securesocial.core.SecureSocial {
  def show(id: Long) = DBAction { implicit request =>
    // TODO:
    /*
    Polls.findById(id) map { poll =>
      Ok(views.html.Polls.show(poll)
    } getOrlElse(NotFound)

     */
    val poll = 0
    Ok(views.html.Polls.show(id))
  }

  def create = SecuredAction { implicit request =>
    // TODO: create Poll
    Redirect(routes.PollsController.show(0))
  }
}
