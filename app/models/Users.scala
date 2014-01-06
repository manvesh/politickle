package models

import java.sql.Date
import play.api.db.slick.Config.driver.simple._
import securesocial.core.OAuth2Info

case class User(
  id: Long,
  twitterId: Long,
  twitterHandle: String,
  twitterName: String,
  twitterAvatarUrl: Option[String],
  createdAt: Date,
  updatedAt: Date,
  accessToken: Option[String])

object Users extends Table[User]("USERS") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def twitterId = column[Long]("twitter_id", O.NotNull)

  def twitterHandle = column[String]("twitter_handle", O.NotNull)

  def twitterName = column[String]("twitter_name", O.NotNull)

  def twitterAvatarUrl = column[String]("twitter_avatar_url", O.Nullable)

  def createdAt = column[Date]("created_at", O.NotNull)

  def updatedAt = column[Date]("updated_at", O.NotNull)

  def accessToken = column[String]("access_token", O.Nullable)

  implicit def tuple2OAuth2Info(tuple: (Option[String])): Option[OAuth2Info] = {
    tuple match {
      case (Some(accessToken)) => Some(OAuth2Info(accessToken, Some("bearer"), None, None))
      case _ => None
    }
  }

  def uniqueTwitterId = index("unique_twitter_id", twitterId, unique = true)

  def uniqueTwitterHandle = index("unique_twitter_handle", twitterHandle, unique = true)

  def * = id ~ twitterId ~ twitterHandle ~ twitterName ~ twitterAvatarUrl.? ~ createdAt ~ updatedAt ~ accessToken.? <> (User.apply _, User.unapply _)
}
