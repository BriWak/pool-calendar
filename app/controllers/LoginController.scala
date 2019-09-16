package controllers

import forms.UserLoginForm
import javax.inject._
import play.api.i18n.I18nSupport
import play.api.mvc._
import views.html.login

@Singleton
class LoginController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

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
          Redirect(controllers.routes.UploadController.uploadPage())
              .addingToSession(
                "username" -> s"$username",
                "password" -> password
              )
        }
      )
  }

}
