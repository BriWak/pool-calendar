package services

import java.util.Date

import com.google.inject.Inject
import conf.ApplicationConfig
import connectors.FixtureFileConnector
import models.{Fixture, Team}
import repositories.{MongoFixtureRepository, MongoTeamRepository}
import utils.DateHelper._

import scala.concurrent.Future

class UploadService @Inject()(fixtureFileConnector: FixtureFileConnector,
                              appConfig: ApplicationConfig,
                              mongoTeamRepository: MongoTeamRepository,
                              mongoFixtureRepository: MongoFixtureRepository) {

  private def createFixture(date: Date, homeTeam: Int, awayTeam: Int): Fixture = {
    val teams = fixtureFileConnector.getTeams()
    val home = teams.find(_.number == homeTeam)
    val away = teams.find(_.number == awayTeam)

    if (home.isDefined && away.isDefined) {
      Fixture(date, home.get, away.get)
    } else {
      throw new Exception("Team number does not exist")
    }
  }

  def uploadAllFixturesForTeam(team: Team): Future[Boolean] = {
    val allFixtures = fixtureFileConnector.getFixtureWeeks().flatMap {
      fixtureWeek =>
        val fixture: Option[(Int, Int)] = fixtureWeek.fixtures.find(fixture => fixture._1 == team.number || fixture._2 == team.number)

        fixture match {
          case Some((home, away)) =>
            Seq(
              createFixture(convertStringToDate(fixtureWeek.date1), home, away),
              createFixture(convertStringToDate(fixtureWeek.date2), away, home)
            )
          case _ =>
            throw new Exception("Team does not exist")
        }
    }
    mongoFixtureRepository.flush(team)
    mongoFixtureRepository.createAll(team, allFixtures.sortBy(_.date))
  }

  def uploadAllTeams: List[Team] = {
    val allTeams = fixtureFileConnector.getTeams()
    mongoTeamRepository.flush
    mongoTeamRepository.createAll(allTeams)
    allTeams
  }

}
