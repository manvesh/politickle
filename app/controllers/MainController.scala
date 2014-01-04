package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import play.api.Routes

object MainController extends Controller with securesocial.core.SecureSocial{

  def index = SecuredAction { implicit request =>
    Ok(views.html.index(request.user))
  }
}