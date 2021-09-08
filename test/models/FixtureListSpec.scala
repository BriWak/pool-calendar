package models

import base.SpecBase
import utils.DateHelper._

class FixtureListSpec extends SpecBase {

  "sort" should {

    "sort the fixtures by ascending date" in {
      val fixtures: FixtureList = FixtureList( Team("Team1",1), List(
        Fixture(convertStringToDate("10/10/18"), Team("Team1","Home",1), Team("Team2","Away",2), "Team1Venue"),
        Fixture(convertStringToDate("10/11/18"), Team("Team5","Home",1), Team("Team1","Away",1), "Team5Venue"),
        Fixture(convertStringToDate("04/06/18"), Team("Team1","Home",1), Team("Team4","Away",4), "Team3Venue2")
      )
      )

      val SortedFixtures: FixtureList = FixtureList( Team("Team1",1), List(
        Fixture(convertStringToDate("04/06/18"), Team("Team1","Home",1), Team("Team4","Away",4), "Team3Venue2"),
        Fixture(convertStringToDate("10/10/18"), Team("Team1","Home",1), Team("Team2","Away",2), "Team1Venue"),
        Fixture(convertStringToDate("10/11/18"), Team("Team5","Home",1), Team("Team1","Away",1), "Team5Venue")
      )
      )

      fixtures.sort mustBe SortedFixtures
    }
  }
}
