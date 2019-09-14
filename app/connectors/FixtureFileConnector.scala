package connectors

import java.nio.file.{Files, Paths}

import com.google.inject.Inject
import conf.ApplicationConfig
import models.{FixtureWeek, Team}
import org.slf4j.LoggerFactory
import play.api.{Environment, Logger}

import scala.io.Source

class FixtureFileConnector @Inject()(environment: Environment,
                                     appConfig: ApplicationConfig) {


  private def processCsvFile: List[String] = {
    val bufferedSource = if (Files.exists(Paths.get(environment.rootPath + "/Pool fixtures.csv"))) {
      Logger.warn("Loading uploaded file from server")
      Source.fromFile(environment.rootPath + "/Pool fixtures.csv")
    } else {
      Logger.warn("Loading uploaded file locally")
      Source.fromFile("./app/resources/Pool fixtures.csv")
    }
    val csv: List[String] = bufferedSource.getLines.toSeq.filterNot(_ == "").toList
    bufferedSource.close
    csv
  }

  def getFixtureWeeks: List[FixtureWeek] = {

    case class TempFixtureWeek(fixtures: Seq[(Int, Int)], date1: Option[String], date2: Option[String])

    val csv = processCsvFile

    val numOnlyPattern = """^\d+$""".r
    val fixturePattern = """^(\d+)-(\d+)$""".r

    val rows: List[List[String]] = csv.map { _.split(",").map(_.trim).toList}

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

  def getTeams = {

    val csv = processCsvFile

    val teamPattern = """(\d+)\ ([a-zA-z -]+)""".r

    teamPattern.findAllMatchIn(csv.mkString).map(m => Team(m.group(2),m.group(1).toInt)).toSeq.sortBy(_.number)

  }
}

