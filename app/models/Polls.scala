package models

import java.sql.Timestamp
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}
import scala.slick.lifted.ForeignKeyAction

case class Poll(
  id: Option[Long],
  ownerId: Long,
  description: String,
  hashTag: Option[String],
  createdAt: Option[Timestamp] = None,
  updatedAt: Option[Timestamp] = None)

trait PollsComponent {
  self: UsersComponent =>

  val Polls: Polls

  class Polls extends Table[Poll]("POLLS") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def ownerId = column[Long]("owner_id", O.NotNull)

    def owner = foreignKey("POLL_USER_FK", ownerId, Users)(_.id)

    def description = column[String]("description", O.NotNull)

    def hashTag = column[String]("hash_tag", O.Nullable)

    def createdAt = column[Timestamp]("created_at", O.Nullable)

    def updatedAt = column[Timestamp]("updated_at", O.Nullable)

    def * = id.? ~ ownerId ~ description ~ hashTag.? ~ createdAt.? ~ updatedAt.? <>(Poll.apply _, Poll.unapply _)

    def autoInc = * returning id

    val byId = createFinderBy(_.id)
  }

}

object Polls extends DAO {

  def ddl = Polls.ddl


  def findById(id: Long)(implicit s: Session): Option[Poll] =
    Polls.byId(id).firstOption

  def count(implicit s: Session): Int =
    Query(Polls.length).first


  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1)(implicit s: Session): Page[(Poll, User)] = {

    val offset = pageSize * page
    val query =
      (for {
        (poll, user) <- Polls innerJoin Users on (_.ownerId === _.id)
      } yield (poll, user))
        .drop(offset)
        .take(pageSize)

    val totalRows = count
    val result = query.list.map(row => (row._1, row._2))

    Page(result, page, offset, totalRows)
  }

  def insert(poll: Poll)(implicit s: Session): Int = {
    val pollToInsert = poll.copy(createdAt = Some(currentTimestamp))
    Polls.autoInc.insert(pollToInsert).asInstanceOf[Int]
  }

  def update(id: Long, poll: Poll)(implicit s: Session) {
    val PollToUpdate: Poll = poll.copy(Some(id), updatedAt = Some(currentTimestamp))
    Polls.where(_.id === id).update(PollToUpdate)
  }

  def delete(id: Long)(implicit s: Session) {
    Polls.where(_.id === id).delete
  }
}