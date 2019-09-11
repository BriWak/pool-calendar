package services

import models.{Fixture, FixtureList, FixtureWeek, Team}
import org.scalatest.MustMatchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import utils.DateHelper.convertStringToDate

class FixtureServiceSpec extends PlaySpec with MustMatchers with ScalaFutures{

  private val fixtureService = new FixtureService {
    override val fixtureTable: Seq[FixtureWeek] = Seq(
      FixtureWeek(Seq((1, 16), (2, 15), (3, 14), (4, 13), (5, 12), (6, 11), (7, 10), (8, 9)), "05/09/19", "09/01/20"),
      FixtureWeek(Seq((15, 1), (14, 2), (13, 3), (12, 4), (11, 5), (10, 6), (9, 7), (16, 8)), "12/09/19", "16/01/20")
    )
    override val teams: Seq[Team] = Seq(
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
      Team("Charltons", 16)
    )
  }

  "createFixture" should {

    "create a fixture when a valid date, home team number, and away team number is given" in {
      val result = fixtureService.createFixture(convertStringToDate("01/01/2019"), 1, 2)

      result mustEqual Fixture(convertStringToDate("01/01/2019"), Team("Newsham Side Club", 1), Team("Seahorse", 2))
    }

    "throw an exception when an invalid home team number is given" in {
      lazy val result = fixtureService.createFixture(convertStringToDate("01/01/2019"), 100, 2)

      an[Exception] mustBe thrownBy(result)
    }
  }

  "createAllFixturesForTeam" should {

    "generate a sorted FixtureList with all available fixtures for a team" in {
      val result = fixtureService.createAllFixturesForTeam(Team("Annitsford Irish B", 14))

      val expectedResult = FixtureList(
        Fixture(convertStringToDate("05/09/19"), Team("Comrades A", 3), Team("Annitsford Irish B", 14)),
        Fixture(convertStringToDate("12/09/19"), Team("Annitsford Irish B", 14), Team("Seahorse", 2)),
        Fixture(convertStringToDate("09/01/20"), Team("Annitsford Irish B", 14), Team("Comrades A", 3)),
        Fixture(convertStringToDate("16/01/20"), Team("Seahorse", 2), Team("Annitsford Irish B", 14))
      )

      result mustEqual expectedResult
    }
  }

  "getTeamFromName" should {

    "get a Team when given a team name" in {
      val result = fixtureService.getTeamFromName("Annitsford Irish B")

      result mustBe Some(Team("Annitsford Irish B", "Annitsford Irish", 14))
    }
  }

  "createCalendar" should {

    "return a string with calendar information when given a valid team" in {
      val result = fixtureService.createCalendar(Team("Annitsford Irish B", 14))

      result must include("BEGIN:VCALENDAR")
      result must include("SUMMARY:Comrades A v Annitsford Irish B")
      result must include("END:VCALENDAR")
    }
  }
}
