package repositories

import com.google.inject.Inject
import conf.ApplicationConfig
import models.UserSession
import org.joda.time.DateTime
import play.api.libs.json._
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.{ExecutionContext, Future}

class SessionRepository @Inject()(val reactiveMongoApi: ReactiveMongoApi,
                                  config: ApplicationConfig
                                 )(implicit ec: ExecutionContext) extends IndexesManager(reactiveMongoApi) with ReactiveMongoComponents {

  override val collectionName: String = "Session"

  override val cacheTtl: Option[Int] = Some(config.expireAfterSeconds)

  override val lastUpdatedIndexName: String = "updatedAt_index"

  def collection: Future[BSONCollection] = collectionF

  def set(session: UserSession): Future[Boolean] = {
    val modifier = Json.obj("$set" -> session.copy(updatedAt = DateTime.now))
    val selector = Json.obj("uuid" -> session.uuid)

    for {
      col <- collection
      r <- col.update(ordered = false).one(selector, modifier, upsert = true, multi = false)
    } yield r.writeConcernError.isEmpty
  }

  def findByUsername(value: String): Future[Option[UserSession]] = {
    val selector = Json.obj("username" -> value)
    findAndUpdateSession(selector)
  }

  def findByUuid(value: String): Future[Option[UserSession]] = {
    val selector = Json.obj("uuid" -> value)
    findAndUpdateSession(selector)
  }

  def flush: Future[Boolean] = {
    collection.flatMap(_.drop(false))
  }
}