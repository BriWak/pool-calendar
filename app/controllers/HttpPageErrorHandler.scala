package controllers

import play.api.http.HttpErrorHandler
import play.api.http.Status.NOT_FOUND
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import views.html.errorPage

import scala.concurrent.Future

class HttpPageErrorHandler extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String) : Future[Result]= {
    statusCode match {
      case NOT_FOUND =>
        Future.successful(NotFound(errorPage("The page you are looking for does not exist. Please check the URL")))
      case clientError if statusCode >= 400 && statusCode < 500 =>
        Future.successful(Forbidden(errorPage(s"status : $clientError")))
    }
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful(InternalServerError(errorPage(exception.getMessage)))
  }
}
