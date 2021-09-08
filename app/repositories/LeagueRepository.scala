package repositories

import com.google.inject.Inject
import models.League
import play.api.libs.json._
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LeagueRepository @Inject()(
                      val reactiveMongoApi: ReactiveMongoApi
                     ) extends IndexesManager(reactiveMongoApi) with ReactiveMongoComponents {

  override val collectionName: String = "Leagues"

  override val cacheTtl: Option[Int] = None

  override val lastUpdatedIndexName: String = "leagues-created-at-index"

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