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

  lazy val fixturesFilePath: String = loadConfig("fixtures.file.path")
  lazy val fixtureStartTime: String = loadConfig("fixture.start.time")
  lazy val fixtureEndTime: String = loadConfig("fixture.end.time")
  lazy val expireAfterSeconds: Int = loadConfig("mongodb.expireAfterSeconds").toInt
}
