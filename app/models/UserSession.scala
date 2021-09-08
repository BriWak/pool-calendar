package models

import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json.{JsValue, Json, OFormat, Reads, Writes, __}

case class UserSession(username: String, uuid: String, updatedAt: DateTime = DateTime.now)

object UserSession {

  implicit val dateTimeRead: Reads[DateTime] =
    (__ \ "$date").read[Long].map { dateTime =>
      new DateTime(dateTime, DateTimeZone.UTC)
    }

  implicit val dateTimeWrite: Writes[DateTime] = new Writes[DateTime] {
    def writes(dateTime: DateTime) : JsValue = Json.obj(
      "$date" -> dateTime.getMillis
    )
  }

  implicit val fmts: OFormat[UserSession]= Json.format[UserSession]
}
