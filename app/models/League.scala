package models

import play.api.libs.json.{Json, OFormat}

case class League(name: String)

object League {
  implicit val fmts: OFormat[League]= Json.format[League]
}
