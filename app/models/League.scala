package models

import play.api.libs.json.{Json, OFormat}

case class League(name: String, teams: List[Team])

object League {

  implicit val fmts: OFormat[League]= Json.format[League]

}



