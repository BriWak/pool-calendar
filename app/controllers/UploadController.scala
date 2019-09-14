package controllers

import java.io.File

import conf.ApplicationConfig
import javax.inject._
import play.api.{Configuration, Environment, Logger}
import play.api.libs.Files
import play.api.mvc._
import services.FixtureService

import scala.concurrent.ExecutionContext

@Singleton
class UploadController @Inject()(cc: ControllerComponents,
                                 fixtureService: FixtureService,
                                 environment: Environment,
                                 appConfig: ApplicationConfig)
                                (implicit ec: ExecutionContext
                                ) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def uploadPage: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.upload("File Upload In Play"))
  }

  def uploadFile: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { request =>
    request.body.file("fileUpload").map { file =>
      val filename = file.filename
      if (filename.takeRight(4) == ".csv") {
        Logger.warn("Environment root is " + environment.rootPath)
        Logger.warn("Files in root: " + getListOfFiles(s"${environment.rootPath}"))
        file.ref.moveFileTo(new File(appConfig.fixturesFilePath + filename), replace = true)
        Logger.warn("File saved: " + getListOfFiles(s"${environment.rootPath}"))
        Ok("File has been uploaded")
      } else {
        Ok("File type is incorrect")
      }
    }.getOrElse {
      Redirect(routes.UploadController.uploadPage)
    }
  }

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(x => x.isFile).toList
    } else {
      List[File]()
    }
  }
}
