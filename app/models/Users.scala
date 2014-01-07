package models

import java.sql.Date
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._


case class User(
  id: Option[Long],
  twitterId: Long,
  twitterName: String,
  twitterHandle: Option[String],
  twitterAvatarUrl: Option[String],
  createdAt: Date,
  updatedAt: Date,
  accessToken: Option[String])

trait UsersComponent {
  val Users: Users

  class Users extends Table[User]("USERS") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def twitterId = column[Long]("twitter_id", O.NotNull)

    def twitterName = column[String]("twitter_name", O.NotNull)

    def twitterHandle = column[String]("twitter_handle", O.Nullable)

    def twitterAvatarUrl = column[String]("twitter_avatar_url", O.Nullable)

    def createdAt = column[Date]("created_at", O.NotNull)

    def updatedAt = column[Date]("updated_at", O.NotNull)

    def accessToken = column[String]("access_token", O.Nullable)

    def * = id.? ~ twitterId ~ twitterName ~ twitterHandle.? ~ twitterAvatarUrl.? ~ createdAt ~ updatedAt ~ accessToken.? <>(User.apply _, User.unapply _)

    val byId = createFinderBy(_.id)

    def autoInc = * returning id
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
