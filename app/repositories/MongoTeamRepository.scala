package repositories

import com.google.inject.Inject
import models.Team
import play.api.libs.json.Json
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json._
import reactivemongo.play.json.collection._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoTeamRepository @Inject()(
                      val reactiveMongoApi: ReactiveMongoApi
                     ) extends ReactiveMongoComponents {

  def collection: Future[JSONCollection] = {
    reactiveMongoApi.database.map(_.collection[JSONCollection]("Teams"))
  }

  def create(value: Team): Future[Boolean] = {

    val futureResult = collection.flatMap(_.insert.one(value))

    futureResult.map(_.ok)
  }

  def createAll(value: List[Team]): Future[Boolean] = {

    val futureResult = collection.flatMap(_.insert.many(value))

    futureResult.map(_.ok)
  }

  def findTeamByName(value: String) = {
    val cursor: Future[Cursor[Team]] = collection.map(_.find(Json.obj("name" -> value))
      .sort(Json.obj("number" -> -1)).cursor[Team]())

    val futureTeamList = cursor.flatMap(_.collect[List](-1, Cursor.FailOnError[List[Team]]()))

    futureTeamList.map(_.headOption)
  }

  def findAllTeams() = {
    val cursor= collection.map(_.find(Json.obj()).cursor[Team]())

    val futureTeamList = cursor.flatMap(_.collect[List](-1, Cursor.FailOnError[List[Team]]()))

    futureTeamList
  }

  def updateTeam(value: Team, newValue: Team) ={
    collection.flatMap(_.update.one(value, newValue))
  }

  def deleteTeam(value: Team) = {
    collection.flatMap(_.delete.one(value, Some(1)))
  }

  def flush = {
    collection.flatMap(_.drop(false))
  }
}