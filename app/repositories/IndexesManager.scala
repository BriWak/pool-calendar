/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
                                         mongo: ReactiveMongoApi,
                                       )(implicit ec: ExecutionContext) extends Logging {

  implicit final val jsObjectWrites: OWrites[JsObject] = OWrites[JsObject](identity)

  val collectionName: String

  val cacheTtl: Option[Int]

  val lastUpdatedIndexName: String

  def collection: Future[BSONCollection] =
    for {
      _ <- ensureIndexes
      res <- mongo.database.map(_.collection[BSONCollection](collectionName))
    } yield res

  def ensureIndexes: Future[Boolean] = {

    lazy val lastUpdatedIndex = MongoIndex(
      key = Seq("updatedAt" -> IndexType.Ascending),
      name = lastUpdatedIndexName,
      expireAfterSeconds = cacheTtl
    )

    for {
      collection <- mongo.database.map(_.collection[BSONCollection](collectionName))
      createdLastUpdatedIndex <- collection.indexesManager.ensure(lastUpdatedIndex)
    } yield createdLastUpdatedIndex
  }

  def findAndUpdateSession(selector: JsObject)(implicit rds: Reads[UserSession]): Future[Option[UserSession]] = {

    val modifier = Json.obj(
      "$set" -> Json.obj(
        "updatedAt" -> UserSession.dateTimeWrite.writes(DateTime.now)
      )
    )
    collection.flatMap(_.findAndUpdate(
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
      arrayFilters = Nil)).map(_.result[UserSession])

  }
}