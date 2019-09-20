package repositories

import com.google.inject.Inject
import models.{Fixture, Team}
import play.api.libs.json.Json
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json._
import reactivemongo.play.json.collection._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoFixtureRepository @Inject()(
                      val reactiveMongoApi: ReactiveMongoApi
                     ) extends ReactiveMongoComponents {

  def collection(name: String): Future[JSONCollection] = {
    reactiveMongoApi.database.map(_.collection[JSONCollection](name))
  }

  def create(team: Team, value: Fixture): Future[Boolean] = {
    collection(team.name).flatMap(_.insert.one(value)).map(_.ok)
  }

  def createAll(team: Team, value: List[Fixture]): Future[Boolean] = {
    collection(team.name).flatMap(_.insert.many(value)).map(_.ok)
  }

  def findAllFixtures(team: Team): Future[List[Fixture]] = {
    val cursor= collection(team.name).map(_.find(Json.obj()).cursor[Fixture]())
    cursor.flatMap(_.collect[List](-1, Cursor.FailOnError[List[Fixture]]()))
  }

  def updateFixture(team: Team, value: Fixture, newValue: Fixture): Future[Boolean] ={
    collection(team.name).flatMap(_.update.one(value, newValue)).map(_.ok)
  }

  def deleteFixture(team: Team, value: Fixture): Future[Boolean] = {
    collection(team.name).flatMap(_.delete.one(value, Some(1))).map(_.ok)
  }

  def flush(team: Team): Future[Boolean] = {
    collection(team.name).flatMap(_.drop(false))
  }
}