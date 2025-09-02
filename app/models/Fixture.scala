package models

import java.time.LocalDate

import play.api.libs.json.{Json, OFormat}

case class Fixture(date: LocalDate, homeTeam: Team, awayTeam: Team, venue: String, address: Option[String])

object Fixture {

  implicit val fmts: OFormat[Fixture]= Json.format[Fixture]

  def apply(date: LocalDate, homeTeam: Team, awayTeam:Team): Fixture = {
    Fixture(date: LocalDate, homeTeam: Team, awayTeam:Team, homeTeam.venue, homeTeam.address)
  }

}
