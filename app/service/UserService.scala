package service

import _root_.java.sql.Timestamp
import _root_.java.util.Date
import play.api.Application
import securesocial.core._
import models.{UserSecureSocialHelper, User, Users}
import play.api.db.slick.DBAction
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import securesocial.core.providers.Token
import securesocial.core.PasswordInfo
import play.api.Play.current

class UserService(application: Application) extends UserServicePlugin(application) {

  def find(id: IdentityId): Option[Identity] = {
    play.api.db.slick.DB.withSession{ implicit s: Session =>
      val twitterId = id.userId
      Users.findByTwitterId(twitterId) map { UserSecureSocialHelper.getIdentity }
    }
  }

  def save(user: Identity): Identity = {
    val time = new Timestamp(new Date().getTime)
    play.api.db.slick.DB.withSession{ implicit s: Session =>
      val userToSave = User(
          None,
          user.identityId.userId,
          user.fullName,
          Some(user.identityId.userId),
          user.avatarUrl,
          time,
          time,
          accessToken = user.oAuth2Info.map {
            _.accessToken
          }
        )
      Users.insert(userToSave)
      user
    }
  }

  def findByEmailAndProvider(email: String, providerId: String) = None

  def findToken(token: String) = None

  def deleteToken(token: String) {}

  def deleteExpiredTokens() {}

  def save(token: Token): Unit = {}
}
