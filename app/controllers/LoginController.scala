package controllers

import conf.ApplicationConfig
import controllers.auth.TeamAction
import forms.UserLoginForm
import javax.inject._
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import services.AuthService
import views.html.LoginPage

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LoginController @Inject()(cc: ControllerComponents,
                                teamAction: TeamAction,
                                authService: AuthService,
                                appConfig: ApplicationConfig,
                                sessionRepository: SessionRepository,
                                loginPage: LoginPage) extends AbstractController(cc) with I18nSupport {

  def onPageLoad(): Action[AnyContent] = teamAction {
    implicit request =>
      Unauthorized(loginPage(UserLoginForm.form()))
  }

  def onSubmit(): Action[AnyContent] = teamAction.async {
    implicit request =>
      UserLoginForm.form().bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(loginPage(formWithErrors, Some("Invalid Username or Password"))))
        },
        userData => {
          authService.checkCredentials(userData.username, userData.password).map { authed =>

          if (authed.isDefined) {
            Redirect(controllers.routes.UploadController.onPageLoad()).addingToSession("UUID" -> authed.get.uuid)
          } else {
            Unauthorized(loginPage(UserLoginForm.form(), Some("Invalid Username or Password")))
          }
          }
        }
      )
  }

}
