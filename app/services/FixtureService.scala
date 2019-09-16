package services

import java.util.{Calendar, Date}

import com.google.inject.Inject
import conf.ApplicationConfig
import connectors.FixtureFileConnector
import models.{Fixture, FixtureList, FixtureWeek, Team}
import utils.DateHelper._

class FixtureService @Inject()(fixtureFileConnector: FixtureFileConnector, appConfig: ApplicationConfig){

  lazy val teams: List[Team] = fixtureFileConnector.getTeams()

  lazy val fixtureTable: List[FixtureWeek] = fixtureFileConnector.getFixtureWeeks()

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
            List(
              createFixture(convertStringToDate(fixtureWeek.date1), home, away),
              createFixture(convertStringToDate(fixtureWeek.date2), away, home)
            )
          case _ =>
            throw new Exception("Team does not exist")
        }
    }
    FixtureList(allFixtures).sort()
  }


  def getTeamFromName(name: String): Option[Team] = {
    teams.find(_.name == name)
  }

  def createCalendar(team: Team): String = {

    val startTime: String = appConfig.fixtureStartTime
    val endTime: String = appConfig.fixtureEndTime

    val calendarStart = List(
      "BEGIN:VCALENDAR",
      "VERSION:2.0",
      "PRODID:-//https://bdpl-fixtures.herokuapp.com//NONSGML v1.0//EN",
      "X-WR-CALNAME:Pool Fixtures",
      "CALSCALE:GREGORIAN",
      "BEGIN:VTIMEZONE",
      "TZID:Europe/London",
      "TZURL:http://tzurl.org/zoneinfo-outlook/Europe/London",
      "X-LIC-LOCATION:Europe/London",
      "BEGIN:DAYLIGHT",
      "TZOFFSETFROM:+0000",
      "TZOFFSETTO:+0100",
      "TZNAME:BST",
      "DTSTART:19700329T010000",
      "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU",
      "END:DAYLIGHT",
      "BEGIN:STANDARD",
      "TZOFFSETFROM:+0100",
      "TZOFFSETTO:+0000",
      "TZNAME:GMT",
      "DTSTART:19701025T020000",
      "RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU",
      "END:STANDARD",
      "END:VTIMEZONE",
    )

    val calenderFixtures = createAllFixturesForTeam(team).fixtures.zipWithIndex.flatMap {
      data =>
        val (fixture, index) = data
        List(
          "BEGIN:VEVENT",
          s"DTSTAMP:${getDateAsString(Calendar.getInstance().getTime)}T${getTimeAsString(Calendar.getInstance().getTime)}Z",
          s"UID:${getDateAsString(Calendar.getInstance().getTime)}T${getTimeAsString(Calendar.getInstance().getTime)}Z-${index}",
          s"DTSTART;TZID=Europe/London:${getDateAsString(fixture.date)}T$startTime",
          s"DTEND;TZID=Europe/London:${getDateAsString(fixture.date)}T$endTime",
          s"SUMMARY:${fixture.homeTeam.name} v ${fixture.awayTeam.name}",
          s"LOCATION:${fixture.venue}",
          "END:VEVENT"
        )
    }

    val calenderEnd = List("END:VCALENDAR")

    (calendarStart ++ calenderFixtures ++ calenderEnd).mkString("\n")
  }
}
