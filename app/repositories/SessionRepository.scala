package repositories

import com.google.inject.Inject
import conf.ApplicationConfig
import models.UserSession
import play.api.libs.json.Json
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import reactivemongo.play.json.collection._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionRepository @Inject()(val reactiveMongoApi: ReactiveMongoApi,
                                  config: ApplicationConfig
                                 ) extends ReactiveMongoComponents {

  def collection: Future[JSONCollection] = {
    reactiveMongoApi.database.map(_.collection[JSONCollection]("Session"))
  }

  def create(session: UserSession): Future[Boolean] = {
    collection.flatMap(_.indexesManager.ensure(Index(Seq("createdAt" -> IndexType.Ascending),
      name = Some("createdAt"),
      options = BSONDocument("expireAfterSeconds" -> config.expireAfterSeconds))))
    collection.flatMap(_.insert.one(session)).map(_.ok)
  }

  def findByUsername(value: String): Future[Option[UserSession]] = {
    collection.flatMap(_.find(Json.obj("username" -> value), Some(Json.obj())).one[UserSession])
  }

  def findByUuid(value: String): Future[Option[UserSession]] = {
    collection.flatMap(_.find(Json.obj("uuid" -> value), Some(Json.obj())).one[UserSession])
  }

  def flush: Future[Boolean] = {
    collection.flatMap(_.drop(false))
  }
}