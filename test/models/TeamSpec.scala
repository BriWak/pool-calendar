package models

import base.SpecBase

class TeamSpec extends SpecBase {

  "The custom apply method" should {

    "correctly add the venue if the team is a B team" in {
      Team("Annitsford Irish A", 7) mustEqual Team("Annitsford Irish A", "Annitsford Irish", 7)
    }

    "correctly add the venue if there is only one team at the venue" in {
      Team("Annitsford Irish", 7) mustEqual Team("Annitsford Irish", "Annitsford Irish", 7)
    }
  }
}
