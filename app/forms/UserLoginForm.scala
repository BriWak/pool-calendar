package forms

import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, text}

case class UserLoginForm(username: String, password: String)

object UserLoginForm {

  def form(errorMsg: String = ""): Form[UserLoginForm] = Form[UserLoginForm](
    mapping(
      "username" -> text.verifying("Please enter a Username", _.trim.nonEmpty),
      "password" -> text.verifying("Please enter a Password", _.trim.nonEmpty)
    )(UserLoginForm.apply)(UserLoginForm.unapply)
  )

}
