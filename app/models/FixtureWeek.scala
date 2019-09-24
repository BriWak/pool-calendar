package models

import play.api.libs.json.{Json, OFormat}

case class FixtureWeek(fixtures: List[(Int, Int)], date1: String, date2: String)

object FixtureWeek {

    implicit val fmts: OFormat[FixtureWeek]= Json.format[FixtureWeek]
}
