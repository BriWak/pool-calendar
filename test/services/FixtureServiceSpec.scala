package services

import base.SpecBase
import conf.ApplicationConfig
import models.{Fixture, FixtureList, FixtureWeek, Team}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import repositories.{FixtureRepository, TeamRepository}
import utils.DateHelper.convertStringToDate

import scala.concurrent.Future

class FixtureServiceSpec extends SpecBase {

  private val mockTeamRepository = mock[TeamRepository]
  private val mockFixtureRepository = mock[FixtureRepository]
  private val appConfig = inject[ApplicationConfig]

  private val fixtureService = new FixtureService(appConfig, mockTeamRepository, mockFixtureRepository)

  lazy val fixtureTable: List[FixtureWeek] = List(
    FixtureWeek(List((1, 16), (2, 15), (3, 14), (4, 13), (5, 12), (6, 11), (7, 10), (8, 9)), "05/09/19", "09/01/20"),
    FixtureWeek(List((15, 1), (14, 2), (13, 3), (12, 4), (11, 5), (10, 6), (9, 7), (16, 8)), "12/09/19", "16/01/20")
  )

  lazy val teams: List[Team] = List(
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

  lazy val fixtures = FixtureList( Team("Annitsford Irish B", 14), List(
    Fixture(convertStringToDate("05/09/19"), Team("Comrades A", 3), Team("Annitsford Irish B", 14)),
    Fixture(convertStringToDate("12/09/19"), Team("Annitsford Irish B", 14), Team("Seahorse", 2)),
    Fixture(convertStringToDate("09/01/20"), Team("Annitsford Irish B", 14), Team("Comrades A", 3)),
    Fixture(convertStringToDate("16/01/20"), Team("Seahorse", 2), Team("Annitsford Irish B", 14))
  ))

  "getAllFixturesForTeam" should {

    "generate a sorted FixtureList with all available fixtures for a team" in {
      when(mockFixtureRepository.findAllFixtures(any())).thenReturn(Future.successful(Some(fixtures)))

      val result = fixtureService.getAllFixturesForTeam(Team("Annitsford Irish B", 14))

      result.futureValue mustEqual fixtures.fixtures
    }
  }

  "getTeams" should {

    "create a list of teams when a valid csv file has been processed" in {
      when(mockTeamRepository.findAllTeams()).thenReturn(Future.successful(teams))

      val result = fixtureService.getAllTeams

      result.futureValue mustEqual teams.sorted
    }
  }

  "getTeamFromName" should {

    "get a Team when given a team name" in {
      when(mockTeamRepository.findTeamByName(any()))
        .thenReturn(Future.successful(Some(Team("Annitsford Irish B", "Annitsford Irish", 14))))

      val result = fixtureService.getTeamFromName("Annitsford Irish B")

      result.futureValue mustBe Some(Team("Annitsford Irish B", "Annitsford Irish", 14))
    }
  }

  "createCalendar" should {

    "return a string with calendar information when given a valid team" in {
      when(mockFixtureRepository.findAllFixtures(any())).thenReturn(Future.successful(Some(fixtures)))

      val result: Future[String] = fixtureService.createCalendar(Team("Annitsford Irish B", 14))

      result.futureValue must include("BEGIN:VCALENDAR")
      result.futureValue must include("SUMMARY:Comrades A v Annitsford Irish B")
      result.futureValue must include("END:VCALENDAR")
    }
  }
}
