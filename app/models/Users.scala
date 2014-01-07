package models

import java.sql.Timestamp
import java.util.Date
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import securesocial.core._
import securesocial.core.OAuth2Info
import securesocial.core.OAuth1Info
import securesocial.core.IdentityId
import securesocial.core.providers.TwitterProvider


case class User(
  id: Option[Long],
  twitterId: String,
  twitterName: String,
  twitterHandle: Option[String],
  twitterAvatarUrl: Option[String],
  createdAt: Timestamp,
  updatedAt: Timestamp,
  accessToken: Option[String])

trait UsersComponent {
  val Users: Users

  class Users extends Table[User]("USERS") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def twitterId = column[String]("twitter_id", O.NotNull)

    def twitterName = column[String]("twitter_name", O.NotNull)

    def twitterHandle = column[String]("twitter_handle", O.Nullable)

    def twitterAvatarUrl = column[String]("twitter_avatar_url", O.Nullable)

    def createdAt = column[Timestamp]("created_at", O.NotNull)

    def updatedAt = column[Timestamp]("updated_at", O.NotNull)

    def accessToken = column[String]("access_token", O.Nullable)

    def * = id.? ~ twitterId ~ twitterName ~ twitterHandle.? ~ twitterAvatarUrl.? ~ createdAt ~ updatedAt ~ accessToken.? <>(User.apply _, User.unapply _)

    val byId = createFinderBy(_.id)
    val byTwitterId = createFinderBy(_.twitterId)

    def autoInc = * returning id
  }
}

object Users extends DAO {

  def ddl = Users.ddl

  def findById(id: Long)(implicit s: Session): Option[User] =
    Users.byId(id).firstOption

  def findByTwitterId(twId: String)(implicit s: Session): Option[User] =
    Users.byTwitterId(twId).firstOption

  def count(implicit s: Session): Int =
    Query(Users.length).first


  def insert(user: User)(implicit s: Session) {
    val userToInsert: User = user.copy(createdAt = new Timestamp(new Date().getTime))
    Users.autoInc.insert(userToInsert)
  }

  def update(id: Long, user: User)(implicit s: Session) {
    val UserToUpdate: User = user.copy(id = Some(id), updatedAt = new Timestamp(new Date().getTime))
    Users.where(_.id === id).update(UserToUpdate)
  }

  def delete(id: Long)(implicit s: Session) {
    Users.where(_.id === id).delete
  }
}

object UserSecureSocialHelper {

  def getIdentity(user: User): Identity = new Identity {
      def identityId: IdentityId = IdentityId(user.twitterId, TwitterProvider.Id)
      def firstName: String = ""
      def lastName: String = ""
      def fullName: String = user.twitterName
      def email: Option[String] = None
      def avatarUrl: Option[String] = user.twitterAvatarUrl
      def authMethod: AuthenticationMethod = AuthenticationMethod.OAuth2
      def oAuth1Info: Option[OAuth1Info] = None
      def oAuth2Info: Option[OAuth2Info] = user.accessToken.map {accessToken =>
        OAuth2Info(accessToken, Some("bearer"), None, None)
      }
      def passwordInfo: Option[PasswordInfo] = None
    }
}
