package controllers

import conf.ApplicationConfig
import controllers.auth.AuthAction
import javax.inject._
import models.{FixtureList, Team}
import play.api.Environment
import play.api.libs.Files
import play.api.mvc._
import services.{FileService, FixtureService, MongoService}
import views.html.upload

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadController @Inject()(cc: ControllerComponents,
                                 fixtureService: FixtureService,
                                 environment: Environment,
                                 appConfig: ApplicationConfig,
                                 authAction: AuthAction,
                                 fileService: FileService,
                                 mongoService: MongoService)
                                (implicit ec: ExecutionContext
                                ) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def uploadPage: Action[AnyContent] = authAction { implicit request: Request[AnyContent] =>
    Ok(upload("File Upload"))
  }

  def uploadFile: Action[MultipartFormData[Files.TemporaryFile]] = authAction(parse.multipartFormData).async { implicit request =>
    request.body.file("fileUpload").map(fileService.saveFile).fold(Future.successful(Redirect(routes.UploadController.uploadPage()))) { csv =>
      csv.fold(errorMessage => Future.successful(Ok(upload(errorMessage, true))),
        successMessage => {
          val allTeams: List[Team] = fileService.getTeams()
          val allFixtures: List[FixtureList] = allTeams.map(fileService.getFixturesForTeam)
          val teams = mongoService.uploadAllTeams(allTeams)
          val fixtures = mongoService.uploadAllFixturesForAllTeams(allFixtures)
          for {_ <- teams
               _ <- fixtures
               } yield {
            Ok(upload(successMessage, true))
          }
        }
      )
    }.recoverWith {
      case _ => Future.successful(Ok(upload("There has been a problem saving the fixture information to the database.", true)))
    }
  }
}
