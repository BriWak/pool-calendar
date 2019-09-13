package controllers

import forms.TeamForm
import javax.inject._
import models.Team
import play.api.mvc._
import services.FixtureService

@Singleton
class HomeController @Inject()(cc: ControllerComponents, fixtureService: FixtureService) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(TeamForm.form, fixtureService.teams))
  }

  def downloadCalendar(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    TeamForm.form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(formWithErrors.errors.toString)
      },
      formData => {
        val team: Option[Team] = fixtureService.getTeamFromName(formData.teamName)

        team.fold(BadRequest("Team does not exist")) { data =>
          val calendar = fixtureService.createCalendar(data)

          Ok(calendar).as("text/calendar").withHeaders(
            "Content-Disposition" -> s"attachment; filename=${data.name} Fixtures.ics"
          )
        }
      }
    )
  }

}
