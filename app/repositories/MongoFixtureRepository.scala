package repositories

import com.google.inject.Inject
import models.{FixtureList, Team}
import play.api.libs.json.Json
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json._
import reactivemongo.play.json.collection._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoFixtureRepository @Inject()(
                      val reactiveMongoApi: ReactiveMongoApi
                     ) extends ReactiveMongoComponents {

  def collection: Future[JSONCollection] = {
    reactiveMongoApi.database.map(_.collection[JSONCollection]("Fixtures"))
  }

  def create(value: FixtureList): Future[Boolean] = {
    collection.flatMap(_.insert.one(value)).map(_.ok)
  }

  def createAll(value: List[FixtureList]): Future[Boolean] = {
    collection.flatMap(_.insert.many(value)).map(_.ok)
  }

  def findAllFixtures(team: Team): Future[Option[FixtureList]] = {
    collection.flatMap(_.find(Json.obj("team.name" -> team.name)).one[FixtureList])
  }

  def flush: Future[Boolean] = {
    collection.flatMap(_.drop(false))
  }
}