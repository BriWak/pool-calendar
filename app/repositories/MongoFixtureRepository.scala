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

    val futureResult = collection(team.name).flatMap(_.insert.one(value))

    futureResult.map(_.ok)
  }

  def createAll(team: Team, value: List[Fixture]): Future[Boolean] = {

    val futureResult = collection(team.name).flatMap(_.insert.many(value))

    futureResult.map(_.ok)
  }

//  def findTeamByName(value: String) = {
//    val cursor: Future[Cursor[Team]] = collection.map(_.find(Json.obj("name" -> value))
//      .sort(Json.obj("number" -> -1)).cursor[Team]())
//
//    val futureTeamList = cursor.flatMap(_.collect[List](-1, Cursor.FailOnError[List[Team]]()))
//
//    futureTeamList.map(_.headOption)
//  }

  def findAllFixtures(team: Team) = {
    val cursor= collection(team.name).map(_.find(Json.obj()).cursor[Fixture]())

    val futureTeamList = cursor.flatMap(_.collect[List](-1, Cursor.FailOnError[List[Fixture]]()))

    futureTeamList
  }

  def updateFixture(team: Team, value: Fixture, newValue: Fixture) ={
    collection(team.name).flatMap(_.update.one(value, newValue))
  }

  def deleteFixture(team: Team, value: Fixture) = {
    collection(team.name).flatMap(_.delete.one(value, Some(1)))
  }

  def flush(team: Team)  = {
    collection(team.name).flatMap(_.drop(false))
  }
}