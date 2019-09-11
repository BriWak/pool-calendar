package services

import java.util.{Calendar, Date}

import utils.DateHelper._
import models.{Fixture, FixtureList, FixtureWeek, Team}
import java.io.File
import java.io.PrintWriter
import java.time.LocalDate

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


  def getTeamFromName(name: String): Option[Team] = {
    teams.find(_.name == name)
  }

  def createCalendar(team: Team) = {

    val fixtureList = createAllFixturesForTeam(team)
        val fileObject = new File(s"${team.name} Fixtures.ics" )

        val printWriter = new PrintWriter(fileObject)

        printWriter.write(
          "BEGIN:VCALENDAR\n" +
          "VERSION:2.0\n" +
          "PRODID:-//http://fixtures.thismonkey.com/cgi-bin/build-football-fixture.pl//NONSGML v1.0//EN\n" +
          "X-WR-CALNAME:Pool Fixtures\n" +
          "CALSCALE:GREGORIAN\n" +
          "BEGIN:VTIMEZONE\n" +
          "TZID:Europe/London\n" +
          "TZURL:http://tzurl.org/zoneinfo-outlook/Europe/London\n" +
          "X-LIC-LOCATION:Europe/London\n" +
          "BEGIN:DAYLIGHT\n" +
          "TZOFFSETFROM:+0000\n" +
          "TZOFFSETTO:+0100\n" +
          "TZNAME:BST\n" +
          "DTSTART:19700329T010000\n" +
          "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU\n" +
          "END:DAYLIGHT\n" +
          "BEGIN:STANDARD\n" +
          "TZOFFSETFROM:+0100\n" +
          "TZOFFSETTO:+0000\n" +
          "TZNAME:GMT\n" +
          "DTSTART:19701025T020000\n" +
          "RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU\n" +
          "END:STANDARD\n" +
          "END:VTIMEZONE\n"
        )

    fixtureList.fixtures.zipWithIndex.foreach {
      data =>
        val (fixture, index) = data

        printWriter.write(
          "BEGIN:VEVENT\n" +
          s"DTSTAMP:${getDateAsString(Calendar.getInstance().getTime)}T${getTimeAsString(Calendar.getInstance().getTime)}Z\n" +
          s"UID:${getDateAsString(Calendar.getInstance().getTime)}T${getTimeAsString(Calendar.getInstance().getTime)}Z-${index}\n" +
          s"DTSTART;TZID=Europe/London:${getDateAsString(fixture.date)}T${getTimeAsString(fixture.date)}\n" +
          s"DTEND;TZID=Europe/London:${getDateAsString(fixture.date)}T220000\n" +
          s"SUMMARY:${fixture.homeTeam.name} v ${fixture.awayTeam.name}\n" +
          s"LOCATION:${fixture.venue}\n" +
          "END:VEVENT\n"
      )
    }
    printWriter.write("END:VCALENDAR")
    printWriter.close()

  }
}
