package services

import com.google.inject.Inject
import models.{FixtureList, Team}
import repositories.{MongoFixtureRepository, MongoTeamRepository}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoService @Inject()(mongoFixtureRepository: MongoFixtureRepository,
                             mongoTeamRepository: MongoTeamRepository) {

  def uploadAllFixturesForAllTeams(fixtureList: List[FixtureList]): Future[Boolean] = {
    mongoFixtureRepository.flush
    mongoFixtureRepository.createAll(fixtureList)
  }

  def uploadAllTeams(teams: List[Team]): Future[List[Team]] = {
    for {
      _ <- mongoTeamRepository.flush
      _ <- mongoTeamRepository.createAll(teams)
    } yield teams
  }
}
