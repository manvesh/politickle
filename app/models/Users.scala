package models

import java.sql.Date
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{ Join, MappedTypeMapper }
import scala.slick.lifted.ForeignKeyAction

case class User(
  id: Long,
  twitterId: Long,
  twitterHandle: String,
  name: String,
  createdAt: Date,
  updatedAt: Date,
  authToken: String,
  userSecret: String)

trait UsersComponent {
  val Users: Users

  class Users extends Table[User]("USERS") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def twitterId = column[Long]("twitter_id", O.NotNull)

    def twitterHandle = column[String]("twitter_handle", O.NotNull)

    def name = column[String]("name", O.NotNull)

    def createdAt = column[Date]("created_at", O.NotNull)

    def updatedAt = column[Date]("updated_at", O.NotNull)

    def authToken = column[String]("auth_token", O.Nullable)

    def userSecret = column[String]("user_secret", O.Nullable)

    def * = id ~ twitterId ~ twitterHandle ~ name ~ createdAt ~ updatedAt ~ authToken ~ userSecret <>(User.apply _, User.unapply _)

    def autoInc = * returning id

    val byId = createFinderBy(_.id)
  }

}

object Users extends DAO {

  def ddl = Users.ddl

  def findById(id: Long)(implicit s: Session): Option[User] =
    Users.byId(id).firstOption

  def count(implicit s: Session): Int =
    Query(Users.length).first


  def insert(user: User)(implicit s: Session) {
    Users.autoInc.insert(user)
  }

  def update(id: Long, user: User)(implicit s: Session) {
    val UserToUpdate: User = user.copy(id)
    Users.where(_.id === id).update(UserToUpdate)
  }

  def delete(id: Long)(implicit s: Session) {
    Users.where(_.id === id).delete
  }
}