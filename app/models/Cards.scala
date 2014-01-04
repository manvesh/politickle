package models

import java.sql.Date
import play.api.db.slick.Config.driver.simple._

case class Card(id: Long, accountId: Long, cardType: Int, createdAt: Date, updatedAt: Date, name: String)

class Cards extends Table[Card]("CARDS") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def accountId = column[Long]("account_id", O.NotNull)

  def cardType = column[Int]("card_type", O.NotNull)

  def createdAt = column[Date]("created_at", O.NotNull)

  def updatedAt = column[Date]("updated_at", O.NotNull)

  def name = column[String]("name", O.NotNull)

  def * = id ~ accountId ~ cardType ~ createdAt ~ updatedAt ~ name <>(Card.apply _, Card.unapply _)
}
