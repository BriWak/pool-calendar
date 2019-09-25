package controllers

import conf.ApplicationConfig
import forms.UserLoginForm
import javax.inject._
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import services.AuthService
import views.html.login

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LoginController @Inject()(cc: ControllerComponents,
                                authService: AuthService,
                                appConfig: ApplicationConfig,
                                sessionRepository: SessionRepository) extends AbstractController(cc) with I18nSupport {

  def onPageLoad(): Action[AnyContent] = Action {
    implicit request =>
      Unauthorized(login(UserLoginForm.form()))
  }

  def onSubmit(): Action[AnyContent] = Action.async {
    implicit request =>
      UserLoginForm.form().bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(login(formWithErrors, Some("Invalid Username or Password"))))
        },
        userData => {
          authService.checkCredentials(userData.username, userData.password).map { authed =>

          if (authed.isDefined) {
            Redirect(controllers.routes.UploadController.uploadPage()).addingToSession("UUID" -> authed.get.uuid)
          } else {
            Unauthorized(login(UserLoginForm.form(), Some("Invalid Username or Password")))
          }
          }
        }
      )
  }

}
