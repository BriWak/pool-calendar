package controllers

import java.io.File
import java.nio.file.attribute.PosixFilePermission._
import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{Paths, Files => JFiles}

import akka.stream.IOResult
import akka.stream.scaladsl._
import akka.util.ByteString
import javax.inject._
import play.api.libs.streams._
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._
import play.core.parsers.Multipart.FileInfo
import services.FixtureService

import scala.concurrent.ExecutionContext

@Singleton
class UploadController @Inject()(cc: ControllerComponents, fixtureService: FixtureService)(implicit ec: ExecutionContext) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def uploadPage: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.upload("File Upload In Play"))
  }


  def uploadFile = Action(parse.temporaryFile) { request =>

    request.body.moveFileTo(Paths.get("./app/resources/Pool fixtures.csv"), replace = true)
    Ok("File has been uploaded")
  }



//  def uploadFile: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { request =>
//    request.body.file("fileUpload").map { video =>
//      val videoFilename = video.filename
//      val contentType = video.contentType.get
//      video.ref.moveFileTo(new File("./app/resources/" + video.filename), replace = true)
//    }.getOrElse {
//      Redirect(routes.UploadController.uploadPage)
//    }
//    Ok("File has been uploaded")
//  }
}
