package controllers

import conf.ApplicationConfig
import controllers.auth.{AuthAction, TeamAction}
import javax.inject._
import models.{FixtureList, League, Team}
import play.api.Environment
import play.api.libs.Files
import play.api.mvc._
import services.{FileService, FixtureService, MongoService}
import views.html.UploadPage

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadController @Inject()(cc: ControllerComponents,
                                 teamAction: TeamAction,
                                 fixtureService: FixtureService,
                                 environment: Environment,
                                 appConfig: ApplicationConfig,
                                 authAction: AuthAction,
                                 fileService: FileService,
                                 mongoService: MongoService,
                                 uploadPage: UploadPage)
                                (implicit ec: ExecutionContext
                                ) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def onPageLoad: Action[AnyContent] = (authAction andThen teamAction) { implicit request =>
    Ok(uploadPage("File Upload"))
  }

  def uploadFile: Action[MultipartFormData[Files.TemporaryFile]] = (authAction andThen teamAction)(parse.multipartFormData).async {
    implicit request =>
      request.body.file("fileUpload").map(fileService.saveFile)
        .fold(Future.successful(Redirect(routes.UploadController.onPageLoad()))) { csv =>
          csv.fold(errorMessage => Future.successful(Ok(uploadPage(errorMessage, true))),
            successData => {
              val (successMessage, league) = successData
              val allTeams: List[Team] = fileService.getTeams()
              val allFixtures: List[FixtureList] = allTeams.map(fileService.getFixturesForTeam)
              val leagueUpload = mongoService.uploadLeague(League(league, allTeams))
//              val teams = mongoService.uploadAllTeams(league, allTeams)
              val fixtures = mongoService.uploadAllFixturesForAllTeams(allFixtures)
              for {_ <- leagueUpload
                   _ <- fixtures
                   } yield {
                Ok(uploadPage(successMessage, true))
              }
            }
          )
        }.recoverWith {
        case _ =>
          Future.successful(Ok(uploadPage("There has been a problem saving the fixture information to the database.", true)))
      }
  }
}
