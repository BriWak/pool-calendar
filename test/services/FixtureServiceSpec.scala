package services

import models.{Fixture, FixtureList, FixtureWeek, Team}
import org.scalatest.MustMatchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import utils.DateHelper.convertStringToDate

class FixtureServiceSpec extends PlaySpec with MustMatchers with ScalaFutures{

  val fixtureService = new FixtureService {
    override val fixtureTable = Seq(
      FixtureWeek(Seq((1, 16), (2, 15), (3, 14), (4, 13), (5, 12), (6, 11), (7, 10), (8, 9)), "05/09/19", "09/01/20"),
      FixtureWeek(Seq((15, 1), (14, 2), (13, 3), (12, 4), (11, 5), (10, 6), (9, 7), (16, 8)), "12/09/19", "16/01/20")
    )
  }

  "createFixture" must {
    "create a fixture when a valid date, home team number, and away team number is given" in {
      val result = fixtureService.createFixture(convertStringToDate("01/01/2019"), 1, 2)
      println(result)
      result mustEqual Fixture(convertStringToDate("01/01/2019"), Team("Newsham Side Club", 1), Team("Seahorse", 2))
    }

    "throw an exception when an invalid home team number is given" in {
      lazy val result = fixtureService.createFixture(convertStringToDate("01/01/2019"), 100, 2)

      an[Exception] mustBe thrownBy(result)
    }
  }

  "createAllFixturesForTeam" must {
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

}
