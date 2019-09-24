package services

import java.util.Calendar

import com.google.inject.Inject
import conf.ApplicationConfig
import models.{Fixture, Team}
import repositories.{FixtureRepository, TeamRepository}
import utils.DateHelper._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FixtureService @Inject()(appConfig: ApplicationConfig,
                               teamRepository: TeamRepository,
                               fixtureRepository: FixtureRepository) {

  def getAllFixturesForTeam(team: Team): Future[List[Fixture]] = {
    fixtureRepository.findAllFixtures(team).map(fixtureList =>
      fixtureList.get.fixtures)
  }

  def getAllTeams: Future[List[Team]] = {
    teamRepository.findAllTeams()
  }

  def getTeamFromName(name: String): Future[Option[Team]] = {
    teamRepository.findTeamByName(name)
  }

  def createCalendar(team: Team): Future[String] = {

    val startTime: String = appConfig.fixtureStartTime
    val endTime: String = appConfig.fixtureEndTime

    getAllFixturesForTeam(team).map { allFixturesForTeam =>

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

      val calenderFixtures = allFixturesForTeam.zipWithIndex.flatMap {
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
}
