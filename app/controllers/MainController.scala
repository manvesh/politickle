package controllers

import play.api.mvc.{Action, Controller}

object MainController extends Controller with securesocial.core.SecureSocial{

  def index = SecuredAction { implicit request =>
    Ok(views.html.Main.index(request.user))
  }
}