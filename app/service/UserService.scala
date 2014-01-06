package service

import play.api.Application
import securesocial.core.{Identity, UserServicePlugin}
import models.Users

class UserService(application: Application) {  //extends UserServicePlugin(application) {
  def find(id: Long) = Users.findById(id)

  //def save(user: Identity) = Users.save(user)
  def findByEmailAndProvider(email: String, providerId: String) = None

  def save(token: String) {}

  def findToken(token: String) = None

  def deleteToken(token: String) {}

  def deleteExpiredTokens() {}
}
