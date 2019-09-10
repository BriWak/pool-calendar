package models

import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec
import utils.DateHelper._

class FixtureListSpec extends PlaySpec with MustMatchers {

  "sort" must {
    "sort the fixtures by ascending date" in {
      val fixtures: FixtureList = FixtureList(
        Fixture(convertStringToDate("10/10/18"), Team("Team1","Home",1), Team("Team2","Away",2), "Team1Venue"),
        Fixture(convertStringToDate("10/11/18"), Team("Team5","Home",1), Team("Team6","Away",2), "Team5Venue"),
        Fixture(convertStringToDate("04/06/18"), Team("Team3","Home",3), Team("Team4","Away",4), "Team3Venue2")
      )

      val SortedFixtures: FixtureList = FixtureList(
        Fixture(convertStringToDate("04/06/18"), Team("Team3","Home",3), Team("Team4","Away",4), "Team3Venue2"),
        Fixture(convertStringToDate("10/10/18"), Team("Team1","Home",1), Team("Team2","Away",2), "Team1Venue"),
        Fixture(convertStringToDate("10/11/18"), Team("Team5","Home",1), Team("Team6","Away",2), "Team5Venue")
      )

      fixtures.sort mustBe SortedFixtures
    }
  }
}
