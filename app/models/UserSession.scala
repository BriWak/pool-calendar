package models

import play.api.libs.json.{Json, OFormat}

case class UserSession(username: String, uuid: String)

object UserSession {
  implicit val fmts: OFormat[UserSession]= Json.format[UserSession]
}
