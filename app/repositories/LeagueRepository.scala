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

  def findLeagueByName(value: String): Future[Option[League]] = {
    collection.flatMap(_.find(Json.obj("name" -> value), Some(Json.obj())).one[League])
  }

  def findAllLeagues: Future[List[League]] = {
    val cursor = collection.map(_.find(Json.obj(), Some(Json.obj())).cursor[League]())
    cursor.flatMap(_.collect[List](-1, Cursor.FailOnError[List[League]]()))
  }

  def flush: Future[Boolean] = {
    collection.flatMap(_.drop(false))
  }
}