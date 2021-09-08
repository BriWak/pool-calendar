package connectors

import com.google.inject.Inject
import play.api.{Environment, Logging}

import java.nio.file.{Files, Paths}
import scala.io.Source

class FileConnector @Inject()(environment: Environment) extends Logging {

  protected lazy val path: String = if (Files.exists(Paths.get(s"${environment.rootPath}/Pool fixtures.csv"))) {
    logger.info("Reading file from server root")
    s"${environment.rootPath}/Pool fixtures.csv"
  } else {
    logger.info("Reading file from resources")
    "./app/resources/Pool fixtures.csv"
  }

  def processCsvFile: List[String] = {
    val bufferedSource = Source.fromFile(path)

    val csv: List[String] = bufferedSource.getLines.toList.filterNot(_ == "").toList
    bufferedSource.close
    csv
  }

}
