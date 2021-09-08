package controllers

import controllers.auth.TeamAction
import forms.UserLoginForm
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.AuthService
import views.html.LoginPage

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LoginController @Inject()(cc: ControllerComponents,
                                teamAction: TeamAction,
                                authService: AuthService,
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
