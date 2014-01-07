package service

import _root_.java.sql.Timestamp
import _root_.java.util.Date
import play.api.Application
import securesocial.core._
import models.{UserSecureSocialHelper, User, Users}
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import securesocial.core.providers.Token
import play.api.Play.current
import org.joda.time.DateTime

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
          Some(user.firstName),
          user.avatarUrl,
          None, None,
          accessToken = user.oAuth1Info.map { _.token },
          secret = user.oAuth1Info.map { _.secret }
        )
      Users.findByTwitterId(user.identityId.userId) match {
        case Some(userInDB) => Users.update(userInDB.id.get, userToSave)
        case _ => Users.insert(userToSave)
      }
      user
    }
  }

  def findByEmailAndProvider(email: String, providerId: String) = None

  def findToken(token: String) = {
    play.api.db.slick.DB.withSession { implicit s: Session =>
      Users.findByToken(token).map { user =>
        Token(
          user.accessToken.getOrElse(""),
          user.twitterHandle.getOrElse(""),
          new DateTime(user.createdAt.getOrElse()),
          new DateTime(user.createdAt.getOrElse()),
          isSignUp = true) }
    }
  }

  def deleteToken(token: String) {
    play.api.db.slick.DB.withSession { implicit s: Session =>
      val twitterId = token.split("-").headOption
      Users.findByTwitterId(twitterId.get) foreach { user =>
        Users.update(user.id.get, user.copy(accessToken = None))
      }
    }
  }

  def deleteExpiredTokens() {}

  def save(token: Token): Unit = {
    play.api.db.slick.DB.withSession { implicit s: Session =>
      val twitterId = token.uuid.split("-").headOption
      Users.findByTwitterId(twitterId.get) foreach { user =>
        Users.update(user.id.get, user.copy(accessToken = Some(token.uuid)))
      }
    }
  }
}
