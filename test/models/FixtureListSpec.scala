package models

import base.SpecBase
import utils.DateHelper._

class FixtureListSpec extends SpecBase {

  "sort" should {

    "sort the fixtures by ascending date" in {
      val fixtures: FixtureList = FixtureList( Team(1, "Team1", None), List(
        Fixture(convertStringToDate("10/10/18"), Team(1, "Team1", "Home", None), Team(2, "Team2", "Away", None), "Team1Venue", None),
        Fixture(convertStringToDate("10/11/18"), Team(1, "Team5", "Home", None), Team(1, "Team1", "Away", None), "Team5Venue", None),
        Fixture(convertStringToDate("04/06/18"), Team(1, "Team1", "Home", None), Team(4, "Team4", "Away", None), "Team3Venue2", None)
      )
      )

      val SortedFixtures: FixtureList = FixtureList( Team(1, "Team1", None), List(
        Fixture(convertStringToDate("04/06/18"), Team(1, "Team1", "Home", None), Team(4, "Team4", "Away", None), "Team3Venue2", None),
        Fixture(convertStringToDate("10/10/18"), Team(1, "Team1", "Home", None), Team(2, "Team2", "Away", None), "Team1Venue", None),
        Fixture(convertStringToDate("10/11/18"), Team(1, "Team5", "Home", None), Team(1, "Team1", "Away", None), "Team5Venue", None)
      )
      )

      fixtures.sort mustBe SortedFixtures
    }
  }
}
