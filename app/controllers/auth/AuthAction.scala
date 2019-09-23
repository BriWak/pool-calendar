package controllers.auth

import com.google.inject.Inject
import conf.ApplicationConfig
import play.api.mvc.Results.Redirect
import play.api.mvc._
import repositories.SessionRepository

import scala.concurrent.{ExecutionContext, Future}

class AuthAction @Inject()(val parser: BodyParsers.Default,
                           appConfig: ApplicationConfig,
                           sessionRepository: SessionRepository)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[Request, AnyContent] with ActionRefiner[Request, Request] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, Request[A]]] = {

    val sessionUUID = request.session.get("UUID")

    if (sessionUUID.isDefined) {
      sessionRepository.findByUuid(sessionUUID.get).map {
        case Some(_) => Right(request)
        case _ => Left(Redirect(controllers.routes.LoginController.onPageLoad()))
      }
    } else {
      Future.successful(Left(Redirect(controllers.routes.LoginController.onPageLoad())))
    }

  }
}
