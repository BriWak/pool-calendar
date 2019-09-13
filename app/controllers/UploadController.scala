package controllers

import java.io.File

import javax.inject._
import play.api.libs.Files
import play.api.mvc._
import services.FixtureService

import scala.concurrent.ExecutionContext

@Singleton
class UploadController @Inject()(cc: ControllerComponents, fixtureService: FixtureService)(implicit ec: ExecutionContext) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def uploadPage: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.upload("File Upload In Play"))
  }


  def uploadFile: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { request =>
    request.body.file("fileUpload").map { file =>
      val videoFilename = file.filename
      val contentType = file.contentType.get
      file.ref.moveFileTo(new File("./app/resources/" + file.filename), replace = true)
    }.getOrElse {
      Redirect(routes.UploadController.uploadPage)
    }
    Ok("File has been uploaded")
  }
}
