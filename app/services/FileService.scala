package services

import java.io.File
import java.time.LocalDate

import com.google.inject.Inject
import conf.ApplicationConfig
import connectors.FileConnector
import models.{Fixture, FixtureList, FixtureWeek, Team}
import play.api.libs.Files
import play.api.mvc.MultipartFormData
import utils.DateHelper._
import utils.LeagueHelper._

class FileService @Inject()(fileConnector: FileConnector,
                            appConfig: ApplicationConfig) {

  def saveFile(file: MultipartFormData.FilePart[Files.TemporaryFile]): Either[String, String] = {
    val filename = file.filename.trim
    if (filename.takeRight(4) != ".csv") {
      Left("The file type is incorrect, only CSV files are supported.")
    } else if (leagues.contains(filename.dropRight(4))) {
      file.ref.moveTo(new File(s"${appConfig.fixturesFilePath}Pool fixtures.csv"), replace = true)
      Right(filename.dropRight(4))
    } else {
      Left("The file name is incorrect, it must be the name of the league.")
    }
  }

  def getTeams(processedCsv: List[String] = fileConnector.processCsvFile): List[Team] = {
    val teamPattern = """(\d+)\ ([a-zA-z -]+)""".r
    teamPattern.findAllMatchIn(processedCsv.mkString("\n"))
      .map(m => Team(m.group(2).trim, m.group(1).toInt)).toList.sortBy(_.number)
  }

  def getFixturesForTeam(team: Team): FixtureList = {
    val allFixtures = getFixtureWeeks().flatMap {
      fixtureWeek =>
        val fixture: Option[(Int, Int)] = fixtureWeek.fixtures
          .find(fixture => fixture._1 == team.number || fixture._2 == team.number)

        fixture match {
          case Some((home, away)) =>
            Seq(
              createFixture(convertStringToDate(fixtureWeek.date1), home, away),
              createFixture(convertStringToDate(fixtureWeek.date2), away, home)
            )
          case _ =>
            throw new Exception("Team does not exist")
        }
    }
    FixtureList(team, allFixtures.sortBy(_.date))
  }

  private def getFixtureWeeks(processedCsv: List[String] = fileConnector.processCsvFile): List[FixtureWeek] = {

    case class TempFixtureWeek(fixtures: List[(Int, Int)], date1: Option[String], date2: Option[String])

    val numOnlyPattern = """^\d+$""".r
    val fixturePattern = """^(\d+)-(\d+)$""".r

    val rows: List[List[String]] = processedCsv.map {
      _.split(",").map(_.trim).toList
    }

    val fixtureRows: List[List[String]] = rows.collect {
      case row if numOnlyPattern.matches(row.headOption.getOrElse("")) => row
    }

    fixtureRows.map {
      row =>
        row.tail.foldLeft[TempFixtureWeek](TempFixtureWeek(Nil, None, None)) { (acc, current) =>
          if (fixturePattern.matches(current)) {
            val (home, away) = fixturePattern.findFirstMatchIn(current).map { matches =>
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
    }.map {
      fixtureTemp =>
        FixtureWeek(fixtureTemp.fixtures, fixtureTemp.date1.get, fixtureTemp.date2.get)
    }
  }

  private def createFixture(date: LocalDate, homeTeam: Int, awayTeam: Int): Fixture = {
    val teams = getTeams()
    val home = teams.find(_.number == homeTeam)
    val away = teams.find(_.number == awayTeam)

    if (home.isDefined && away.isDefined) {
      Fixture(date, home.get, away.get)
    } else {
      throw new Exception("Team number does not exist")
    }
  }

}
