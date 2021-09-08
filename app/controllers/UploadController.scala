package controllers

import controllers.auth.{AuthAction, TeamAction}
import models.{FixtureList, League, Team}
import play.api.libs.Files
import play.api.mvc._
import services.{FileService, MongoService}
import views.html.UploadPage

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadController @Inject()(cc: ControllerComponents,
                                 teamAction: TeamAction,
                                 authAction: AuthAction,
                                 fileService: FileService,
                                 mongoService: MongoService,
                                 uploadPage: UploadPage)
                                (implicit ec: ExecutionContext
                                ) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def onPageLoad: Action[AnyContent] = (authAction andThen teamAction).async { implicit request =>
    mongoService.getLeagues.map { leagues =>
      Ok(uploadPage("File Upload", leagues))
    }
  }

  def uploadFile: Action[MultipartFormData[Files.TemporaryFile]] = (authAction andThen teamAction)(parse.multipartFormData).async {
    implicit request =>
      request.body.file("fileUpload").map(fileService.saveFile)
        .fold(Future.successful(Redirect(routes.UploadController.onPageLoad()))) { csv =>
          csv.fold(errorMessage => Future.successful(Ok(uploadPage(errorMessage, List(), true))),
            filename => {
              val allTeams: List[Team] = fileService.getTeams()
              val allFixtures: List[FixtureList] = allTeams.map(fileService.getFixturesForTeam)
              val leagueUpload = mongoService.uploadLeague(League(filename))
              val teamsUpload = mongoService.uploadAllTeams(allTeams)
              val fixturesUpload = mongoService.uploadAllFixturesForAllTeams(allFixtures)
              for {_ <- leagueUpload
                   _ <- teamsUpload
                   _ <- fixturesUpload
                  leagues <- mongoService.getLeagues
                   } yield {
                Ok(uploadPage(s"The fixtures for $filename have been successfully uploaded.", leagues, hasSubmitted = true))
              }
            }
          )
        }.recoverWith {
        case _ =>
          Future.successful(Ok(uploadPage("There has been a problem saving the fixture information to the database.", List(), hasSubmitted = true)))
      }
  }

  def resetDatabase: Action[AnyContent] = (authAction andThen teamAction).async { implicit request =>
    mongoService.dropDatabase.map { success =>
      if (success) {
        Ok(uploadPage("All information has been deleted from the database.", List(), hasSubmitted = true))
      } else {
        Ok(uploadPage("There has been a problem saving the fixture information to the database.", List(), hasSubmitted = true))
      }
    }
  }
}
