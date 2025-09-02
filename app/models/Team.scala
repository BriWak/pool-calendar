package models

import play.api.libs.json.{Json, OFormat}

case class Team(number: Int, name: String, venue: String, address: Option[String])

object Team {

  implicit val fmts: OFormat[Team]= Json.format[Team]

  def apply(number: Int, name: String, address: Option[String] = None): Team = {
    val venue = if (name.charAt(name.length-2) == ' ')
      name.substring(0, name.length-2)
    else
      name

    Team(number, name, venue, address)
  }

  implicit val ordering: Ordering[Team] = Ordering.by(_.name)

}

