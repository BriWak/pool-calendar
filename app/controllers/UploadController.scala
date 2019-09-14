package controllers

import java.io.File

import javax.inject._
import play.api.{Environment, Logger}
import play.api.libs.Files
import play.api.mvc._
import services.FixtureService

import scala.concurrent.ExecutionContext

@Singleton
class UploadController @Inject()(cc: ControllerComponents, fixtureService: FixtureService, environment: Environment)(implicit ec: ExecutionContext) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def uploadPage: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.upload("File Upload In Play"))
  }

  def uploadFile: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { request =>
    request.body.file("fileUpload").map { file =>
      val filename = file.filename
      if (filename.takeRight(4) == ".csv") {
        Logger.warn("Environment root is " + environment.rootPath)
        Logger.warn("Files in root: " + getListOfFiles(s"${environment.rootPath}"))
        Logger.warn("Files in app: " + getListOfFiles("app/"))
        Logger.warn("Hopefully also files in app 1: " + getListOfFiles("../../"))
        Logger.warn("Hopefully also files in app 2: " + getListOfFiles("../../../"))
        Logger.warn("Hopefully also files in app 3: " + getListOfFiles("../../../../"))
        Logger.warn("Hopefully files in resources: " + getListOfFiles("../../../resources/"))

        file.ref.moveFileTo(new File("../../../../app/resources/" + filename), replace = true)
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
      d.listFiles.filter(x => x.isFile || x.isDirectory).toList
    } else {
      List[File]()
    }
  }
}
