package services

import com.google.inject.Inject
import models.{FixtureList, League, Team}
import repositories.{FixtureRepository, LeagueRepository, TeamRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoService @Inject()(fixtureRepository: FixtureRepository,
                             teamRepository: TeamRepository,
                             leagueRepository: LeagueRepository) {

  def uploadLeague(league: League): Future[Boolean] = {
    leagueRepository.create(league)
  }

  def getLeagues: Future[List[League]] = {
    leagueRepository.findAllLeagues
  }

  def uploadAllFixturesForAllTeams(fixtureList: List[FixtureList]): Future[Boolean] = {
    fixtureRepository.createAll(fixtureList)
  }

  def uploadAllTeams(teams: List[Team]): Future[List[Team]] = {
    for {
      _ <- teamRepository.createAll(teams)
    } yield teams
  }

  def dropDatabase: Future[Boolean] = {
    teamRepository.flush.flatMap { _ =>
      fixtureRepository.flush.flatMap { _ =>
        leagueRepository.flush
      }
    }
  }

}
