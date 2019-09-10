package models

case class FixtureList(fixtures: Seq[Fixture]) {

  def sort(): FixtureList = {
    FixtureList(fixtures.sortBy(_.date))
  }
}

object FixtureList {
  def apply(fixtures: Fixture*)(implicit d: DummyImplicit) = new FixtureList(Seq(fixtures: _*))

}
