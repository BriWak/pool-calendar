package models

import play.api.mvc.{Request, WrappedRequest}

case class TeamRequest[A](teamList: List[String], request: Request[A]) extends WrappedRequest[A](request)
