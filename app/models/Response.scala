package models

import java.sql.Timestamp
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}
import scala.slick.lifted.ForeignKeyAction
import scala.slick.lifted.ColumnOption.DBType
import play.Logger

case class Response(
  id: Option[Long],
  twitterUserId: String,
  pollId: Long,
  choiceId: Long,
  explanationText: Option[String],
  createdAt: Option[Timestamp] = None,
  updatedAt: Option[Timestamp] = None
  )

trait ResponseComponent {

  val PollsComponent: PollsComponent
  val ChoicesComponent: ChoicesComponent

  val Responses: Responses

  class Responses extends Table[Response]("RESPONSES") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def twitterUserId = column[String]("twitter_user_id", O.NotNull)

    def pollId = column[Long]("poll_id", O.NotNull)

    def poll = foreignKey("RESPONSES_POLL_FK", pollId, PollsComponent.Polls)(_.id)

    def choiceId = column[Long]("choice_id", O.NotNull)

    def choice = foreignKey("RESPONSES_CHOICE_FK", choiceId, ChoicesComponent.Choices)(_.id)

    def explanationText = column[String]("explanation", O.Nullable, DBType("VARCHAR(2000)"))

    def createdAt = column[Timestamp]("created_at", O.NotNull)

    def updatedAt = column[Timestamp]("updated_at", O.Nullable)

    def idx = index("IDX_UNIQUE_RESPONSE", (twitterUserId, pollId), unique = true)

    def * = id.? ~ twitterUserId ~ pollId ~ choiceId ~ explanationText.? ~ createdAt.? ~ updatedAt.? <>(Response.apply _, Response.unapply _)

    def autoInc = twitterUserId ~ pollId ~ choiceId ~ explanationText.? ~ createdAt.? ~ updatedAt.? returning id

    val byId = createFinderBy(_.id)

    val byTwitterUserId = createFinderBy(_.twitterUserId)
  }

}

object Responses extends DAO {

  def ddl = Responses.ddl

  def findById(id: Long)(implicit s: Session): Option[Response] =
    Responses.byId(id).firstOption

  def findByIdAndTwitterUserId(pollId: Long, twitterUserId: String)(implicit s: Session): Option[Response] =
    Query(Responses).filter(_.pollId === pollId).filter(_.twitterUserId === twitterUserId).firstOption

  def count(implicit s: Session): Int =
    Query(Responses.length).first

  def findAllByPollId(pollId: Long, page: Int = 0, pageSize: Int = 10, orderBy: Int = 1)(implicit s: Session): Page[Response] = {

    val offset = pageSize * page

    val query =
      (for {
        (responses) <- Responses.where(_.pollId === pollId)
      } yield (responses))
        .drop(offset)
        .take(pageSize)

    val result = query.list

    Page(result, page, offset, result.size)
  }

  def countsPerPollIdAndChoiceId(pollId: Long, choiceId: Long)(implicit s: Session): Int =
    Query(Responses).filter(_.pollId === pollId).filter(_.choiceId === choiceId).list.length

  def insert(response: Response)(implicit s: Session) = {
    val responseToInsert = response.copy(createdAt = Some(currentTimestamp))
    Responses.autoInc.insert(
      responseToInsert.twitterUserId,
      responseToInsert.pollId,
      responseToInsert.choiceId,
      responseToInsert.explanationText,
      responseToInsert.createdAt,
      responseToInsert.updatedAt
    ).asInstanceOf[Int]
  }

  def update(id: Long, response: Response)(implicit s: Session) {
    val responseToUpdate: Response = response.copy(Some(id), updatedAt = Some(currentTimestamp))
    Responses.where(_.id === id).update(responseToUpdate)
  }

  def upsert(response: Response)(implicit s: Session) {
    findByIdAndTwitterUserId(response.pollId, response.twitterUserId) match {
      case Some(existingResponse) => update(existingResponse.id.get, response)
      case None => insert(response)
    }
  }

  def delete(id: Long)(implicit s: Session) {
    Responses.where(_.id === id).delete
  }
}