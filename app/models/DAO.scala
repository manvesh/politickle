package models

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

class DAO extends UsersComponent with PollsComponent with ChoicesComponent {
  val Polls: Polls = new Polls
  val Users: Users = new Users
  val Choices: Choices = new Choices
}
