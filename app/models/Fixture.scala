package models

import java.util.Date

case class Fixture(date: Date, homeTeam: Team, awayTeam:Team, venue: String)

object Fixture {

  def apply(date: Date, homeTeam: Team, awayTeam:Team): Fixture = {
    Fixture(date: Date, homeTeam: Team, awayTeam:Team, homeTeam.venue)
  }

}
