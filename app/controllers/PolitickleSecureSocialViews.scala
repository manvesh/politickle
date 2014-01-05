package controllers

import play.api.data.Form
import play.api.mvc.Request
import play.api.templates.Html
import play.api.Application
import securesocial.controllers.DefaultTemplatesPlugin

class PolitickleSecureSocialViews(application: Application) extends DefaultTemplatesPlugin(application) {
  override def getLoginPage[A](implicit request: Request[A], form: Form[(String, String)], msg: Option[String] = None): Html = {
    views.html.login(form, msg)
  }
}
