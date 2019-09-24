package services

import com.google.inject.Inject
import models.{FixtureList, Team}
import repositories.{FixtureRepository, TeamRepository}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoService @Inject()(fixtureRepository: FixtureRepository,
                             teamRepository: TeamRepository) {

  def uploadAllFixturesForAllTeams(fixtureList: List[FixtureList]): Future[Boolean] = {
    fixtureRepository.flush.flatMap { _ =>
      fixtureRepository.createAll(fixtureList)
    }
  }

  def uploadAllTeams(teams: List[Team]): Future[List[Team]] = {
    for {
      _ <- teamRepository.flush
      _ <- teamRepository.createAll(teams)
    } yield teams
  }
}
