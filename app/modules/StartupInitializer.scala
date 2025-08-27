package modules

import play.api.inject.{SimpleModule, bind}
import repositories._
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

class StartupModule extends SimpleModule(bind[StartupInitializer].toSelf.eagerly())

@Singleton
class StartupInitializer @Inject()(
                                    leagueRepo: LeagueRepository,
                                    teamRepo: TeamRepository,
                                    sessionRepo: SessionRepository,
                                    fixtureRepo: FixtureRepository
                                  )(implicit ec: ExecutionContext) {

  private val allCollections: Seq[Future[_]] = Seq(
    leagueRepo.collectionF,
    teamRepo.collectionF,
    sessionRepo.collectionF,
    fixtureRepo.collectionF
  )

  Future.sequence(allCollections)
    .map(_ => ())
    .andThen {
      case scala.util.Success(_) => println("✅ All Mongo collections initialized with indexes")
      case scala.util.Failure(ex) => println(s"❌ Error initializing Mongo collections: ${ex.getMessage}")
    }
}
