package mappings

import java.sql.Timestamp
import play.api.data.{FormError, Mapping, Form}
import play.api.data.format.Formatter

object CustomMappings {
  implicit val timestampFormatter = new Formatter[Timestamp] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Timestamp] = {
      data.get(key).map { value =>
        try {
          Right(Timestamp.valueOf(value))
        } catch {
          case e: IllegalArgumentException => error(key, value + " is not a valid timestamp")
        }
      } getOrElse error(key, "No timestamp provided")
    }

    private def error(key: String, msg: String) = Left(List(new FormError(key, msg)))

    override def unbind(key: String, value: Timestamp): Map[String, String] = {
      Map(key -> value.toString())
    }
  }

  def timestamp: Mapping[Timestamp] = Form.of[Timestamp]
}
