package conf

import javax.inject._
import play.api.Configuration

@Singleton
class ApplicationConfig @Inject()(
                                   configuration: Configuration
                                 ) {

  private def loadConfig(key: String): String = {
    configuration.get[String](key)
  }

  val baseUrl: String = loadConfig("base.url")
  val fixtureStartTime: String = loadConfig("fixture.start.time")
  val fixtureEndTime: String = loadConfig("fixture.end.time")
  val expireAfterSeconds: Int = loadConfig("mongodb.expireAfterSeconds").toInt
}
