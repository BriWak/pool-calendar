package controllers.auth

import com.google.inject.Inject
import play.api.mvc.Results.Redirect
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class AuthAction @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[Request, AnyContent] with ActionRefiner[Request, Request] {

  def addtoSession[A](values: Session, request: Request[A], block: (Request[A]) => Future[Result]) = {
    block(request).map(_.withSession(values))
  }
  override protected def refine[A](request: Request[A]): Future[Either[Result, Request[A]]] = {

    val usernameValue = request.session.get("username")
    val passwordValue = request.session.get("password")

    if (usernameValue.isEmpty || passwordValue.isEmpty) {
      Future.successful(Left(Redirect(controllers.routes.LoginController.onPageLoad())))
    } else {
      val session = Session(Map("username" -> usernameValue.get, "password" -> passwordValue.get))

      Future.successful(Right(request))
    }

  }
}
