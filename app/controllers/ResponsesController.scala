package controllers

import play.api.mvc.Controller
import play.api.libs.json.Json

object ResponsesController extends Controller with securesocial.core.SecureSocial {
  def create(id: Long) = SecuredAction { implicit request =>
    Ok(Json.toJson(Map("binding_values" -> "go_here"))).as("application/json")
  }
}
