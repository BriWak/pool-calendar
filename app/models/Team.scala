package models

import play.api.libs.json.{Json, OFormat}

case class Team(name: String, venue: String, number: Int)

object Team {

  implicit val fmts: OFormat[Team]= Json.format[Team]

  def apply(name: String, number: Int): Team = {
    val venue = if (name.charAt(name.length-2) == ' ')
      name.substring(0, name.length-2)
    else
      name

    Team(name, venue, number)
  }
}

