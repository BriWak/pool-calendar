package controllers

import conf.ApplicationConfig
import controllers.auth.{AuthAction, TeamAction}
import javax.inject._
import models.{FixtureList, Team}
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
            successMessage => {
              val allTeams: List[Team] = fileService.getTeams()
              val allFixtures: List[FixtureList] = allTeams.map(fileService.getFixturesForTeam)
              val teams = mongoService.uploadAllTeams(allTeams)
              val fixtures = mongoService.uploadAllFixturesForAllTeams(allFixtures)
              for {_ <- teams
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
