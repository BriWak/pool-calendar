package models

import play.api.libs.json.{Json, OFormat}

case class FixtureList(team: Team, fixtures: List[Fixture]) {

  def sort(): FixtureList = {
    FixtureList(team, fixtures.sortBy(_.date))
  }
}

object FixtureList {
  implicit val fmts: OFormat[FixtureList]= Json.format[FixtureList]

}
