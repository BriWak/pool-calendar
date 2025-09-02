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
    Team(1, "Newsham Side Club"),
    Team(2, "Seahorse"),
    Team(3, "Comrades A"),
    Team(4, "Bedlington Station"),
    Team(5, "Sports Club"),
    Team(6, "Breakers A"),
    Team(7, "Annitsford Irish A"),
    Team(8, "Breakers E"),
    Team(9, "Kings Arms"),
    Team(10, "Comrades B"),
    Team(11, "Services B"),
    Team(12, "Market Tavern"),
    Team(13, "Breakers C"),
    Team(14, "Annitsford Irish B"),
    Team(15, "Isabella A"),
    Team(16, "Charltons")
  )

  lazy val fixtures = FixtureList( Team(14, "Annitsford Irish B"), List(
    Fixture(convertStringToDate("05/09/19"), Team(3, "Comrades A"), Team(14, "Annitsford Irish B")),
    Fixture(convertStringToDate("12/09/19"), Team(14, "Annitsford Irish B"), Team(2, "Seahorse")),
    Fixture(convertStringToDate("09/01/20"), Team(14, "Annitsford Irish B"), Team(3, "Comrades A")),
    Fixture(convertStringToDate("16/01/20"), Team(2, "Seahorse"), Team(14, "Annitsford Irish B"))
  ))

  "getAllFixturesForTeam" should {

    "generate a sorted FixtureList with all available fixtures for a team" in {
      when(mockFixtureRepository.findAllFixtures(any())).thenReturn(Future.successful(Some(fixtures)))

      val result = fixtureService.getAllFixturesForTeam(Team(14, "Annitsford Irish B"))

      result.futureValue mustEqual fixtures.fixtures
    }
  }

  "getTeams" should {

    "create a list of teams when a valid csv file has been processed" when {
      "no location is provided teams" in {
        when(mockTeamRepository.findAllTeams()).thenReturn(Future.successful(teams))

        val result = fixtureService.getAllTeams

        result.futureValue mustEqual teams.sorted
      }

      "a location is provided for teams" in {
        when(mockTeamRepository.findAllTeams()).thenReturn(Future.successful(teams.map(_.copy(address = Some("line 1, city, postcode")))))

        val result = fixtureService.getAllTeams

        result.futureValue mustEqual teams.map(_.copy(address = Some("line 1, city, postcode"))).sorted
      }
    }
  }

  "getTeamFromName" should {

    "get a Team when given a team name" in {
      when(mockTeamRepository.findTeamByName(any()))
        .thenReturn(Future.successful(Some(Team(14, "Annitsford Irish B", "Annitsford Irish", None))))

      val result = fixtureService.getTeamFromName("Annitsford Irish B")

      result.futureValue mustBe Some(Team(14, "Annitsford Irish B", "Annitsford Irish", None))
    }
  }

  "createCalendar" should {

    "return a string with calendar information when given a valid team" in {
      when(mockFixtureRepository.findAllFixtures(any())).thenReturn(Future.successful(Some(fixtures)))

      val result: Future[String] = fixtureService.createCalendar(Team(14, "Annitsford Irish B"))

      result.futureValue must include("BEGIN:VCALENDAR")
      result.futureValue must include("SUMMARY:Comrades A v Annitsford Irish B")
      result.futureValue must include("END:VCALENDAR")
    }
  }
}
