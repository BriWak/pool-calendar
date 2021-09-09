package repositories

import com.google.inject.Inject
import models.{FixtureList, Team}
import play.api.libs.json._
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json.compat.json2bson._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FixtureRepository @Inject()(
                      val reactiveMongoApi: ReactiveMongoApi
                     ) extends IndexesManager(reactiveMongoApi) with ReactiveMongoComponents {

  override val collectionName: String = "Fixtures"

  override val cacheTtl: Option[Int] = None

  override val lastUpdatedIndexName: String = "fixtures-created-at-index"

  def create(value: FixtureList): Future[Boolean] = {
    collection.flatMap(_.insert.one(value)).map(_.writeConcernError.isEmpty)
  }

  def createAll(value: List[FixtureList]): Future[Boolean] = {
    collection.flatMap(_.insert.many(value)).map(_.ok)
  }

  def findAllFixtures(team: Team): Future[Option[FixtureList]] = {
    collection.flatMap(_.find(Json.obj("team.name" -> team.name), Some(Json.obj())).one[FixtureList])
  }

  def flush: Future[Boolean] = {
    collection.flatMap(_.drop(false))
  }
}