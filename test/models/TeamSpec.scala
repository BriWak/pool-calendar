package models

import base.SpecBase

class TeamSpec extends SpecBase {

  "The custom apply method" should {

    "correctly add the venue if the team is a B team" in {
      Team(7, "Annitsford Irish A", None) mustEqual Team(7, "Annitsford Irish A", "Annitsford Irish", None)
    }

    "correctly add the venue if there is only one team at the venue" in {
      Team(7, "Annitsford Irish", None) mustEqual Team(7, "Annitsford Irish", "Annitsford Irish", None)
    }
  }
}
