package services

import conf.ApplicationConfig
import connectors.FileConnector
import models.{FixtureWeek, Team}
import org.scalatest.MustMatchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class FixtureFileConnectorSpec extends PlaySpec with GuiceOneAppPerSuite with MustMatchers with MockitoSugar with ScalaFutures {

  val mockAppConfig = mock[ApplicationConfig]

  private val fixtureFileConnector = new FileConnector(app.environment, mockAppConfig)

  val processedCsv = List(
    "Super League,,,,,,,,,,",
    "1,1-16,2-15,3-14,4-13,5-12,6-11,7-10,8-9,05/09/19,09/01/20",
    "2,15-1,14-2,13-3,12-4,11-5,10-6,9-7,16-8,12/09/19,16/01/20",
    "1 Newsham Side Club,,,,,,9 Kings Arms,,,,",
    "2 Seahorse,,,,,,10 Comrades B,,,,",
    "3 Comrades A,,,,,,11 Services B,,,,",
    "4 Bedlington Station,,,,,,12 Market Tavern ,,,,",
    "5 Sports Club,,,,,,13 Breakers C ,,,,",
    "6 Breakers A,,,,,,14 Annitsford Irish B,,,,",
    "7 Annitsford Irish A,,,,,,15  Isabella A,,,,",
    "8 Breakers E,,,,,,16 Charltons,,,,"
  )

  val fixtureTable: List[FixtureWeek] = List(
      FixtureWeek(List((1, 16), (2, 15), (3, 14), (4, 13), (5, 12), (6, 11), (7, 10), (8, 9)), "05/09/19", "09/01/20"),
      FixtureWeek(List((15, 1), (14, 2), (13, 3), (12, 4), (11, 5), (10, 6), (9, 7), (16, 8)), "12/09/19", "16/01/20")
    )

  val teams: List[Team] = List(
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

  "getFixtureWeeks" should {

    "create a list of fixtureWeeks when a valid csv file has been processed" in {
      val result = fixtureFileConnector.getFixtureWeeks(processedCsv)

      result mustEqual fixtureTable
    }
  }

  "getTeams" should {

    "create a list of teams when a valid csv file has been processed" in {
      val result = fixtureFileConnector.getTeams(processedCsv)

      result mustEqual teams
    }
  }

}
