package models

import java.sql.Timestamp
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}
import scala.slick.lifted.ForeignKeyAction

case class Choice(
  id: Option[Long],
  pollId: Long,
  description: String,
  createdAt: Option[Timestamp] = None,
  updatedAt: Option[Timestamp] = None)

trait ChoicesComponent {
  self: PollsComponent =>

  val Choices: Choices

  class Choices extends Table[Choice]("CHOICES") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def pollId = column[Long]("poll_id", O.NotNull)

    def poll = foreignKey("CHOICES_POLL_FK", pollId, Polls)(_.id)

    def description = column[String]("description", O.NotNull)

    def createdAt = column[Timestamp]("created_at", O.Nullable)

    def updatedAt = column[Timestamp]("updated_at", O.Nullable)

    def * = id.? ~ pollId ~ description ~ createdAt.? ~ updatedAt.? <>(Choice.apply _, Choice.unapply _)

    def autoInc = pollId ~ description ~ createdAt.? ~ updatedAt.? returning id

    val byId = createFinderBy(_.id)
  }

}

object Choices extends DAO {

  def ddl = Choices.ddl

  def findById(id: Long)(implicit s: Session): Option[Choice] =
    Choices.byId(id).firstOption

  def findByPollId(pollId: Long)(implicit s: Session): Seq[Choice] = {
    val query =
      (for {
        (responses) <- Choices.where(_.pollId === pollId)
      } yield (responses))
    query.list
  }

  def count(implicit s: Session): Int =
    Query(Choices.length).first


  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1)(implicit s: Session): Page[(Choice, Poll)] = {

    val offset = pageSize * page
    val query =
      (for {
        (choice, poll) <- Choices innerJoin Polls on (_.pollId === _.id)
      } yield (choice, poll))
        .drop(offset)
        .take(pageSize)

    val totalRows = count
    val result = query.list.map(row => (row._1, row._2))

    Page(result, page, offset, totalRows)
  }

  def insert(choice: Choice)(implicit s: Session): Int = {
    val choiceToInsert = choice.copy(createdAt = Some(currentTimestamp))
    Choices.autoInc.insert(
      choiceToInsert.pollId,
      choiceToInsert.description,
      choiceToInsert.createdAt,
      choiceToInsert.updatedAt
    ).asInstanceOf[Int]
  }

  def insertAll(choices: Choice*)(implicit s: Session) = {
    val toInsert = choices.map(_.copy(createdAt = Some(currentTimestamp)))
    Choices.autoInc.insertAll(
      toInsert.map(c =>
        (c.pollId,
          c.description,
          c.createdAt,
          c.updatedAt
          )): _*
    )
  }

  def update(id: Long, choice: Choice)(implicit s: Session) {
    val ChoiceToUpdate: Choice = choice.copy(Some(id), updatedAt = Some(currentTimestamp))
    Choices.where(_.id === id).update(ChoiceToUpdate)
  }

  def delete(id: Long)(implicit s: Session) {
    Choices.where(_.id === id).delete
  }

}