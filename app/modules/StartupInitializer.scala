package modules

import play.api.inject.{SimpleModule, bind}
import repositories._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class StartupModule extends SimpleModule(bind[StartupInitializer].toSelf.eagerly())

@Singleton
class StartupInitializer @Inject()(
                                    leagueRepo: LeagueRepository,
                                    teamRepo: TeamRepository,
                                    sessionRepo: SessionRepository,
                                    fixtureRepo: FixtureRepository
                                  )(implicit ec: ExecutionContext) {

  private def withRetry[T](f: => Future[T], retries: Int = 5, delay: FiniteDuration = 5.seconds): Future[T] = {
    f.recoverWith {
      case ex if retries > 0 =>
        println(s"Mongo not ready, retrying in ${delay.toSeconds} seconds: ${ex.getMessage}")
        Thread.sleep(delay.toMillis)
        withRetry(f, retries - 1, delay)
    }
  }

  private val allCollections: Seq[Future[_]] = Seq(
    withRetry(leagueRepo.collectionF),
    withRetry(teamRepo.collectionF),
    withRetry(sessionRepo.collectionF),
    withRetry(fixtureRepo.collectionF)
  )

  Future.sequence(allCollections)
    .map(_ => ())
    .andThen {
      case Success(_) => println("All Mongo collections initialized with indexes")
      case Failure(ex) => println(s"Error initializing Mongo collections after retries: ${ex.getMessage}")
    }
}
