package forms

import play.api.data.Forms._
import play.api.data._

case class TeamForm(teamName: String)

object TeamForm {

  val form: Form[TeamForm] = Form(
    mapping(
        "name" -> nonEmptyText
    )(TeamForm.apply)(TeamForm.unapply)
  )

}
