package controllers

import play.api.mvc._

import javax.inject._

@Singleton
class KeepAliveController @Inject()(cc: ControllerComponents
                              ) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def keepAlive: Action[AnyContent] = Action {
    Ok
  }

}
