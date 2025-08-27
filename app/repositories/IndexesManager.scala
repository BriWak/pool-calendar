package repositories

import models.UserSession
import org.joda.time.DateTime
import play.api.Logging
import play.api.libs.json.{JsObject, Json, OWrites, Reads}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.WriteConcern
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.indexes.IndexType
import reactivemongo.play.json.compat.json2bson._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

abstract class IndexesManager @Inject()(
                                         mongo: ReactiveMongoApi
                                       )(implicit ec: ExecutionContext) extends Logging {

  implicit final val jsObjectWrites: OWrites[JsObject] = OWrites(identity)

  val collectionName: String
  val cacheTtl: Option[Int]
  val lastUpdatedIndexName: String

  lazy val collectionF: Future[BSONCollection] = {
    for {
      _   <- ensureIndexes
      res <- mongo.database.map(_.collection[BSONCollection](collectionName))
    } yield res
  }

  def ensureIndexes: Future[Boolean] = {
    val lastUpdatedIndex = MongoIndex(
      key = Seq("updatedAt" -> IndexType.Ascending),
      name = lastUpdatedIndexName,
      expireAfterSeconds = cacheTtl
    )

    mongo.database
      .flatMap(_.collection[BSONCollection](collectionName).indexesManager.ensure(lastUpdatedIndex))
      .recover {
        case ex =>
          logger.error(s"Failed to ensure indexes for $collectionName", ex)
          false
      }
  }

  def findAndUpdateSession(selector: JsObject)(implicit rds: Reads[UserSession]): Future[Option[UserSession]] = {
    val modifier = Json.obj(
      "$set" -> Json.obj(
        "updatedAt" -> UserSession.dateTimeWrite.writes(DateTime.now)
      )
    )
    collectionF.flatMap(
      _.findAndUpdate(
        selector = selector,
        update = modifier,
        fetchNewObject = true,
        upsert = false,
        sort = None,
        fields = None,
        bypassDocumentValidation = false,
        writeConcern = WriteConcern.Default,
        maxTime = None,
        collation = None,
        arrayFilters = Nil
      ).map(_.result[UserSession])
    )
  }
}
