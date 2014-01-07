package service

import play.api.Application
import securesocial.core.{Identity, UserServicePlugin}
import models.{User, Users}

class UserService(application: Application) extends UserServicePlugin(application) {
  def find(id: Long) = Users.findById(id)

  def save(user: Identity): User = {
    val time = new java.sql.Date(new java.util.Date().getTime())
    Users.insert(
      User(
        None,
        user.identityId.userId.toLong,
        None,
        user.avatarUrl,
        time,
        time,
        user.oAuth2Info.map {
          _.accessToken
        }))
  }

  def findByEmailAndProvider(email: String, providerId: String) = None

  def save(token: String) {}

  def findToken(token: String) = None

  def deleteToken(token: String) {}

  def deleteExpiredTokens() {}
}
