package repositories

import com.google.inject.Inject
import models.{League, Team}
import play.api.libs.json.Json
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json._
import reactivemongo.play.json.collection._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LeagueRepository @Inject()(
                      val reactiveMongoApi: ReactiveMongoApi
                     ) extends ReactiveMongoComponents {

  def collection: Future[JSONCollection] = {
    reactiveMongoApi.database.map(_.collection[JSONCollection]("Leagues"))
  }

  def create(value: League): Future[Boolean] = {
    collection.flatMap(_.insert.one(value)).map(_.ok)
  }

  def findTeamByName(value: String): Future[Option[Team]] = {
    collection.flatMap(_.find(Json.obj("teams.name" -> value), Some(Json.obj())).one[Team])
  }

  def findLeague(league: String): Future[Option[League]] = {
    collection.flatMap(_.find(Json.obj("name" -> league), Some(Json.obj())).one[League])
  }

  def updateLeague(value: League, newValue: League): Future[Boolean] ={
    collection.flatMap(_.update.one(value, newValue)).map(_.ok)
  }

  def deleteLeague(value: League): Future[Boolean] = {
    collection.flatMap(_.delete.one(value, Some(1))).map(_.ok)
  }

  def flush(league: String): Future[Boolean] = {
    collection.flatMap(_.drop(false))
  }
}