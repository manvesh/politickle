package models

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

private[models] class DAO extends UsersComponent with PollsComponent with ChoicesComponent with ResponseComponent {
  val Polls: Polls = new Polls
  val Users: Users = new Users
  val Choices: Choices = new Choices
  val Responses: Responses = new Responses

  val PollsComponent: PollsComponent = this
  val ChoicesComponent: ChoicesComponent = this
}
