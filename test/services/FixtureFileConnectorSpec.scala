package services

import connectors.FixtureFileConnector
import models.{Fixture, FixtureList, FixtureWeek, Team}
import org.scalatest.MustMatchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import utils.DateHelper.convertStringToDate

class FixtureFileConnectorSpec extends PlaySpec with MustMatchers with ScalaFutures{

  private val fileReaderService = new FixtureFileConnector


  val fixtureTable: Seq[FixtureWeek] = Seq(
      FixtureWeek(Seq((1, 16), (2, 15), (3, 14), (4, 13), (5, 12), (6, 11), (7, 10), (8, 9)), "05/09/19", "09/01/20"),
      FixtureWeek(Seq((15, 1), (14, 2), (13, 3), (12, 4), (11, 5), (10, 6), (9, 7), (16, 8)), "12/09/19", "16/01/20")
    )

  val teams: Seq[Team] = Seq(
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

  "processCsvFile" should {

    "create a fixture when a valid date, home team number, and away team number is given" in {
      val result = fileReaderService.getFixtureWeeks

      true
    }


  }

}
