package controllers

import com.google.inject.Inject
import play.api.http.HttpErrorHandler
import play.api.http.Status.NOT_FOUND
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import views.html.ErrorPage

import scala.concurrent.Future

class HttpPageErrorHandler @Inject()(errorPage: ErrorPage) extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String) : Future[Result]= {
    statusCode match {
      case NOT_FOUND =>
        Future.successful(NotFound(errorPage("The page you are looking for does not exist. Please check the URL")))
      case clientError if statusCode >= 400 && statusCode < 500 =>
        Future.successful(Forbidden(errorPage(s"status : $clientError")))
      case _ =>
        Future.successful(Status(statusCode)(errorPage(s"Client error $statusCode")))
    }
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    val message: String = Option(exception.getMessage).getOrElse("An unexpected error occurred")
    Future.successful(InternalServerError(errorPage(message)))
  }
}
