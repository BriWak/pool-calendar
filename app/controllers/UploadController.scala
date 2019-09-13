package controllers

import javax.inject._
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import services.FixtureService
import java.io.File


@Singleton
class UploadController @Inject()(cc: ControllerComponents, fixtureService: FixtureService) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def index = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.upload("File Upload In Play"))
  }

  def uploadFile = Action(parse.multipartFormData) { request =>
    request.body.file("fileUpload").map { video =>
      val videoFilename = video.filename
      val contentType = video.contentType.get
      video.ref.moveFileTo(new File(System.getProperty("user.dir")+"/app/resources/" + video.filename), replace = true)
    }.getOrElse {
      Redirect(routes.UploadController.index)
    }
    Ok("File has been uploaded")
  }
}
