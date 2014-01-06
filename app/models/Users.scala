package models

import java.sql.Date
import play.api.db.slick.Config.driver.simple._

case class User(
  id: Long,
  twitterId: Long,
  twitterHandle: String,
  twitterName: String,
  twitterAvatarUrl: Option[String],
  createdAt: Date,
  updatedAt: Date,
  authToken: Option[String],
  userSecret: Option[String])

class Users extends Table[User]("USERS") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def twitterId = column[Long]("twitter_id", O.NotNull)

  def twitterHandle = column[String]("twitter_handle", O.NotNull)

  def twitterName = column[String]("twitter_name", O.NotNull)

  def twitterAvatarUrl = column[String]("twitter_avatar_url", O.Nullable)

  def createdAt = column[Date]("created_at", O.NotNull)

  def updatedAt = column[Date]("updated_at", O.NotNull)

  def authToken = column[String]("auth_token", O.Nullable)

  def userSecret = column[String]("user_secret", O.Nullable)

  def * = id ~ twitterId ~ twitterHandle ~ twitterName ~ twitterAvatarUrl.? ~ createdAt ~ updatedAt ~ authToken.? ~ userSecret.? <> (User.apply _, User.unapply _)
}