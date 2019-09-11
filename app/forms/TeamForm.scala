package forms

import models.Team
import play.api.data._
import play.api.data.Forms._
import services.FixtureService

case class TeamForm(teamName: String)

object TeamForm {

  val fixtureService = new FixtureService

  val teams = fixtureService.teams

  val form: Form[TeamForm] = Form(
    mapping(
        "name" -> nonEmptyText
    )(TeamForm.apply)(TeamForm.unapply)
  )

}
