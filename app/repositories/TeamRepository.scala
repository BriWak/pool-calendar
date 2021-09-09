package repositories

import com.google.inject.Inject
import models.Team
import play.api.libs.json._
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json.compat.json2bson._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TeamRepository @Inject()(
                      val reactiveMongoApi: ReactiveMongoApi
                     ) extends IndexesManager(reactiveMongoApi) with ReactiveMongoComponents {

  override val collectionName: String = "Teams"

  override val cacheTtl: Option[Int] = None

  override val lastUpdatedIndexName: String = "teams-created-at-index"

  def create(value: Team): Future[Boolean] = {
    collection.flatMap(_.insert.one(value)).map(_.writeConcernError.isEmpty)
  }

  def createAll(value: List[Team]): Future[Boolean] = {
    collection.flatMap(_.insert.many(value)).map(_.writeConcernError.isEmpty)
  }

  def findTeamByName(value: String): Future[Option[Team]] = {
    collection.flatMap(_.find(Json.obj("name" -> value), Some(Json.obj())).one[Team])
  }

  def findAllTeams(): Future[List[Team]] = {
    val cursor = collection.map(_.find(Json.obj(), Some(Json.obj())).cursor[Team]())
    cursor.flatMap(_.collect[List](-1, Cursor.FailOnError[List[Team]]()))
  }

  def updateTeam(value: Team, newValue: Team): Future[Boolean] ={
    collection.flatMap(_.update.one(value, newValue)).map(_.writeConcernError.isEmpty)
  }

  def deleteTeam(value: Team): Future[Boolean] = {
    collection.flatMap(_.delete.one(value, Some(1))).map(_.writeConcernError.isEmpty)
  }

  def flush: Future[Boolean] = {
    collection.flatMap(_.drop(false))
  }
}