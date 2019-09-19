package models

import java.util.Date

import play.api.libs.json.{Json, OFormat}

case class Fixture(date: Date, homeTeam: Team, awayTeam:Team, venue: String)

object Fixture {

  implicit val fmts: OFormat[Fixture]= Json.format[Fixture]

  def apply(date: Date, homeTeam: Team, awayTeam:Team): Fixture = {
    Fixture(date: Date, homeTeam: Team, awayTeam:Team, homeTeam.venue)
  }

}
