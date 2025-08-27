package modules

import play.api.inject.{SimpleModule, bind}

import javax.inject._
import repositories._

import scala.concurrent.{ExecutionContext, Future}

class StartupModule extends SimpleModule(bind[StartupInitializer].toSelf.eagerly())

@Singleton
class StartupInitializer @Inject()(
                                    leagueRepo: LeagueRepository,
                                    teamRepo: TeamRepository,
                                    sessionRepo: SessionRepository,
                                    fixtureRepo: FixtureRepository
                                  )(implicit ec: ExecutionContext) {

  // Trigger collectionF for all repositories at startup
  private val init: Future[Unit] = {
    val allCollections = Seq(
      leagueRepo.collectionF,
      teamRepo.collectionF,
      sessionRepo.collectionF,
      fixtureRepo.collectionF
    )

    Future.sequence(allCollections).map(_ => ())
  }

  // Eager initialization
  init.onComplete(_ => println("✅ All Mongo collections initialized with indexes"))
}
