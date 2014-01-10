package util

import play.api.libs.json._

object CardApiHelper {

  def getStringBinding(string_value: String) = {
    Json.obj("type" -> "STRING",
             "string_value" -> string_value)
  }

  def getBooleanBinding(boolean_value: Boolean) = {
    Json.obj("type" -> "BOOLEAN",
             "boolean_value" -> boolean_value)
  }

  def getImageBinding(url: String, width: Int, height: Int) = {
    Json.obj("type" -> "IMAGE",
             "image_value" -> Json.obj("url" -> url, "width" -> width, "height" -> height))

  }
}