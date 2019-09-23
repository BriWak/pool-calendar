package controllers

import java.util.UUID

import conf.ApplicationConfig
import forms.UserLoginForm
import javax.inject._
import models.UserSession
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import services.PasswordService
import views.html.login
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LoginController @Inject()(cc: ControllerComponents,
                                passwordService: PasswordService,
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
          Future.successful(BadRequest(login(formWithErrors)))
        },
        userData => {
          val username = userData.username
          val password = userData.password
          if (passwordService.checkHash(s"$username/$password", "$2a$10$y5xXcLpJCO3UNDyOwQzoteTjvFFgjELglh8gb2Rqt7yx0ZnMmIhQi")) {
            val session = UserSession(username, UUID.randomUUID().toString)
            sessionRepository.create(session).map { _ =>
              Redirect(controllers.routes.UploadController.uploadPage())
                .addingToSession("UUID" -> session.uuid)
            }
          } else {
            Future.successful(Unauthorized(login(UserLoginForm.form())))
          }
        }
      )
  }

}
