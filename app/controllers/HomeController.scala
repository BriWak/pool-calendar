package controllers

import forms.TeamForm
import javax.inject._
import models.Team
import play.api._
import play.api.mvc._
import services.FixtureService

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, fixtureService: FixtureService) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(TeamForm.form))
  }

  def teamSelection = Action { implicit request: Request[AnyContent] =>
    TeamForm.form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(formWithErrors.errors.toString)
      },
      formData => {
        val team: Option[Team] = fixtureService.getTeamFromName(formData.teamName)

        team.fold(BadRequest("Team does not exist")) { data =>
          val fixtureList = fixtureService.createAllFixturesForTeam(data)
          Ok(fixtureList.fixtures.mkString("\n"))
        }
      }
    )

  }
}
