package controllers

import java.io.File

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
    request.body.file("fileUpload").map { file =>
      val filename = file.filename
      if (filename.takeRight(4) == ".csv") {
        file.ref.moveFileTo(new File(appConfig.fixturesFilePath + filename), replace = true)
        val allTeams: List[Team] = fileService.getTeams()
        val allFixtures: List[FixtureList] = allTeams.map(fileService.getFixturesForTeam)
        mongoService.uploadAllTeams(allTeams)
        mongoService.uploadAllFixturesForAllTeams(allFixtures)
          .map { y =>
            if (y) {
              Ok(upload("The file has been successfully uploaded.", true))
            } else
            Ok(upload("There has been a problem saving the fixture information to the database.", true))
          }
      } else {
        Future.successful(Ok(upload("The file type is incorrect, only CSV files are supported.", true)))
      }
    }.getOrElse {
      Future.successful(Redirect(routes.UploadController.uploadPage))
    }
  }

}
