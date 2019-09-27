package services

import com.google.inject.Inject
import models.{FixtureList, League, Team}
import repositories.{FixtureRepository, LeagueRepository, TeamRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoService @Inject()(fixtureRepository: FixtureRepository,
                             teamRepository: TeamRepository,
                             leagueRepository: LeagueRepository) {

  def uploadAllFixturesForAllTeams(fixtureList: List[FixtureList]): Future[Boolean] = {
      fixtureRepository.createAll(fixtureList)
  }

//  def uploadAllTeams(league: String, teams: List[Team]): Future[List[Team]] = {
//    for {
//      _ <- teamRepository.flush(league)
//      _ <- teamRepository.createAll(league, teams)
//    } yield teams
//  }

  def uploadLeague(league: League): Future[League] = {
    for {
      _ <- leagueRepository.create(league)
    } yield league
  }
}
