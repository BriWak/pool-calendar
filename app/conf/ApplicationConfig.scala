package conf

import javax.inject._
import play.api.{Configuration, Environment}

@Singleton
class ApplicationConfig @Inject()(
                                   configuration: Configuration,
                                   environment: Environment
                                 ) {

  private def loadConfig(key: String): String = {
    configuration.get[String](key)
  }

  val fixturesFilePath: String = loadConfig("fixtures.file.path")
  val fixtureStartTime: String = loadConfig("fixture.start.time")
  val fixtureEndTime: String = loadConfig("fixture.end.time")
  val expireAfterSeconds: Int = loadConfig("mongodb.expireAfterSeconds").toInt
  val mongoUrI: String = loadConfig("mongodb.uri")
}
