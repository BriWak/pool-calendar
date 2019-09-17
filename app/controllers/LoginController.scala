package controllers

import java.util.UUID

import conf.ApplicationConfig
import forms.UserLoginForm
import javax.inject._
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.PasswordService
import views.html.login

@Singleton
class LoginController @Inject()(cc: ControllerComponents,
                                passwordService: PasswordService,
                                appConfig: ApplicationConfig) extends AbstractController(cc) with I18nSupport {

  def onPageLoad(): Action[AnyContent] = Action {
    implicit request =>
      Unauthorized(login(UserLoginForm.form()))
  }

  def onSubmit(): Action[AnyContent] = Action {
    implicit request =>
      UserLoginForm.form().bindFromRequest.fold(
        formWithErrors => {
          BadRequest(login(formWithErrors))
        },
        userData => {
          val username = userData.username
          val password = userData.password
          //Todo: Change password to UUID once working
          if (passwordService.checkHash(s"$username/$password", "$2a$10$y5xXcLpJCO3UNDyOwQzoteTjvFFgjELglh8gb2Rqt7yx0ZnMmIhQi")) {
          Redirect(controllers.routes.UploadController.uploadPage())
            .addingToSession(
          "UUID" -> appConfig.uuid
          )
        } else {
            Unauthorized(login(UserLoginForm.form()))
          }
        }
      )
  }

}
