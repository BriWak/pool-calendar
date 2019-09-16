package forms

import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}

case class UserLoginForm(username: String, password: String)

object UserLoginForm {

  def form(errorMsg: String = ""): Form[UserLoginForm] = Form[UserLoginForm](
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserLoginForm.apply)(UserLoginForm.unapply)
  )

}
