package connectors

import java.nio.file.{Files, Paths}

import com.google.inject.Inject
import conf.ApplicationConfig
import play.api.{Environment, Logger}

import scala.io.Source

class FileConnector @Inject()(environment: Environment,
                              appConfig: ApplicationConfig) {

  val logger: Logger = Logger("play")

  def processCsvFile: List[String] = {
    val bufferedSource = if (Files.exists(Paths.get(s"${environment.rootPath}/Pool fixtures.csv"))) {
      logger.info("Reading file from server root")
      Source.fromFile(s"${environment.rootPath}/Pool fixtures.csv")
    } else {
      logger.info("Reading file from resources")
      Source.fromFile("./app/resources/Pool fixtures.csv")
    }
    val csv: List[String] = bufferedSource.getLines.toList.filterNot(_ == "").toList
    bufferedSource.close
    csv
  }



}

