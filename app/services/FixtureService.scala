package services

import java.util.Date
import utils.DateHelper._
import models.{Fixture, FixtureList, FixtureWeek, Team}

class FixtureService {

  val teams = Seq(
    Team("Newsham Side Club", 1),
    Team("Seahorse", 2),
    Team("Comrades A", 3),
    Team("Bedlington Station", 4),
    Team("Sports Club", 5),
    Team("Breakers A", 6),
    Team("Annitsford Irish A", 7),
    Team("Breakers E", 8),
    Team("Kings Arms", 9),
    Team("Comrades B", 10),
    Team("Services B", 11),
    Team("Market Tavern", 12),
    Team("Breakers C", 13),
    Team("Annitsford Irish B", 14),
    Team("Isabella A", 15),
    Team("Charltons", 16),
  )

  val fixtureTable = Seq(
    FixtureWeek(Seq((1, 16), (2, 15), (3, 14), (4, 13), (5, 12), (6, 11), (7, 10), (8, 9)), "05/09/19", "09/01/20"),
    FixtureWeek(Seq((15, 1), (14, 2), (13, 3), (12, 4), (11, 5), (10, 6), (9, 7), (16, 8)), "12/09/19", "16/01/20"),
    FixtureWeek(Seq((1, 14), (2, 13), (3, 12), (4, 11), (5, 10), (6, 9), (7, 8), (15, 16)), "19/09/19", "23/01/20")
  )

  def createFixture(date: Date, homeTeam: Int, awayTeam: Int): Fixture = {
    val home = teams.find(_.number == homeTeam)
    val away = teams.find(_.number == awayTeam)

    if (home.isDefined && away.isDefined) {
      Fixture(date, home.get, away.get)
    } else {
      throw new Exception("Team number does not exist")
    }
  }

  def createAllFixturesForTeam(team: Team): FixtureList = {
    val allFixtures = fixtureTable.flatMap {
      fixtureWeek =>
        val fixture: Option[(Int, Int)] = fixtureWeek.fixtures.find(fixture => fixture._1 == team.number || fixture._2 == team.number)

        fixture match {
          case Some((home, away)) =>
            Seq(
              createFixture(convertStringToDate(fixtureWeek.date1), home, away),
              createFixture(convertStringToDate(fixtureWeek.date2), away, home)
            )
        }
    }
    FixtureList(allFixtures).sort()
  }

}
