package models

import java.sql.Timestamp
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}
import scala.slick.lifted.ForeignKeyAction

case class PollTarget(
  id: Option[Long],
  twitterUserId: Long,
  pollId: Long,
  createdAt: Option[Timestamp] = None,
  updatedAt: Option[Timestamp] = None)

trait PollTargetsComponent {
  self: PollsComponent =>

  val PollTargets: PollTargets

  class PollTargets extends Table[PollTarget]("TARGETS") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def twitterUserId = column[Long]("twitter_user_id", O.NotNull)

    def pollId = column[Long]("poll_id", O.NotNull)

    def poll = foreignKey("POLLTARGETS_POLL_FK", pollId, Polls)(_.id)

    def createdAt = column[Timestamp]("created_at", O.NotNull)

    def updatedAt = column[Timestamp]("updated_at", O.NotNull)

    def * = id.? ~ twitterUserId ~ pollId ~ createdAt.? ~ updatedAt.? <>(PollTarget.apply _, PollTarget.unapply _)

    def autoInc = * returning id

    val byId = createFinderBy(_.id)
  }

}

object PollTargets extends DAO {

  def ddl = PollTargets.ddl


  def findById(id: Long)(implicit s: Session): Option[PollTarget] =
    PollTargets.byId(id).firstOption

  def count(implicit s: Session): Int =
    Query(PollTargets.length).first

  def findByPollId(pollId: Long)(implicit s: Session): Seq[PollTarget] = {
    val query =
      (for {
        (responses) <- PollTargets.where(_.pollId === pollId)
      } yield (responses))

    query.list
  }

  def insert(target: PollTarget)(implicit s: Session) {
    val targetToUpdate = target.copy(createdAt = Some(currentTimestamp))
    PollTargets.autoInc.insert(targetToUpdate)
  }

  def update(id: Long, target: PollTarget)(implicit s: Session) {
    val targetToUpdate = target.copy(Some(id), updatedAt = Some(currentTimestamp))
    PollTargets.where(_.id === id).update(targetToUpdate)
  }

  def delete(id: Long)(implicit s: Session) {
    PollTargets.where(_.id === id).delete
  }
}