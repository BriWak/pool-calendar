package models

case class FixtureList(fixtures: List[Fixture]) {

  def sort(): FixtureList = {
    FixtureList(fixtures.sortBy(_.date))
  }
}

object FixtureList {
  def apply(fixtures: Fixture*)(implicit d: DummyImplicit) = new FixtureList(List(fixtures: _*))

}
