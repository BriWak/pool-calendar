package conf

import javax.inject._
import play.api.{Configuration, Environment}

@Singleton
class ApplicationConfig @Inject()(
                                   configuration: Configuration,
                                   environment: Environment
                                 ) {

  lazy val fixturesFilePath: String = configuration.get[String]("fixtures.file.path")
  lazy val fixtureStartTime: String = configuration.get[String]("fixture.start.time")
  lazy val fixtureEndTime: String = configuration.get[String]("fixture.end.time")
  lazy val expireAfterSeconds: Int = configuration.get[Int]("mongodb.expireAfterSeconds")
}
