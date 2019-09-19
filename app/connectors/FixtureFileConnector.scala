package connectors

import java.nio.file.{Files, Paths}

import com.google.inject.Inject
import conf.ApplicationConfig
import models.{FixtureWeek, Team}
import play.api.{Environment, Logger}
import repositories.{MongoFixtureRepository, MongoTeamRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

class FixtureFileConnector @Inject()(environment: Environment,
                                     appConfig: ApplicationConfig,
                                     teamMongoService: MongoTeamRepository,
                                     fixtureMongoService: MongoFixtureRepository) {

  val logger: Logger = Logger("play")

  private def processCsvFile: List[String] = {
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

  def getFixtureWeeks(processedCsv: List[String] = processCsvFile): List[FixtureWeek] = {

    case class TempFixtureWeek(fixtures: List[(Int, Int)], date1: Option[String], date2: Option[String])

    val numOnlyPattern = """^\d+$""".r
    val fixturePattern = """^(\d+)-(\d+)$""".r

    val rows: List[List[String]] = processedCsv.map { _.split(",").map(_.trim).toList}

    val fixtureRows: List[List[String]] = rows.collect{case a if numOnlyPattern.matches(a.headOption.getOrElse("")) => a }

    fixtureRows.map {
      row =>
        row.tail.foldLeft[TempFixtureWeek](TempFixtureWeek(Nil, None, None)) { (acc, current) =>
          if (fixturePattern.matches(current)) {
            val (home, away) = fixturePattern.findFirstMatchIn(current).map{ matches =>
              (matches.group(1), matches.group(2))
            }.getOrElse(throw new Exception("Error retrieving fixtures"))
            acc.copy(acc.fixtures.appended((home.toInt, away.toInt)))
          } else if (acc.date1.isEmpty) {
            acc.copy(date1 = Some(current))
          }
          else {
            acc.copy(date2 = Some(current))
          }
        }
    }.map{
      fixtureTemp =>
        FixtureWeek(fixtureTemp.fixtures, fixtureTemp.date1.get, fixtureTemp.date2.get)
    }
  }

  def getTeams(processedCsv: List[String] = processCsvFile) = {

    val teamPattern = """(\d+)\ ([a-zA-z -]+)""".r

    teamPattern.findAllMatchIn(processedCsv.mkString("\n")).map(m => Team(m.group(2).trim,m.group(1).toInt)).toList.sortBy(_.number)

  }
}

