package connectors

import base.SpecBase

class FileConnectorSpec extends SpecBase {

  private val fixtureFileConnector = new FileConnector(app.environment) {
    override protected lazy val path = "./test/resources/Test fixtures.csv"
  }

  val processedCsv = List("Super League,,,,,,,,,",
    ",,,,,,,,,",
    "1,1-2,3-4,5-6,7-8,9-10,11-12,13-14,23/09/21,20/01/22",
    "2,8-11,13-10,12-1,14-3,2-5,4-7,6-9,30/09/21,27/01/22",
    "3,10-5,12-7,14-9,2-11,4-13,1-6,3-8,07/10/21,03/02/22",
    "4,9-4,6-11,13-8,1-10,3-12,5-14,7-2,14/10/21,10/02/22",
    "5,14-7,2-9,11-4,6-13,1-8,10-3,12-5,21/10/21,17/02/22",
    "6,3-6,8-5,10-7,9-12,11-14,13-2,4-1,28/10/21,24/02/22",
    "7,12-2,6-14,11-3,7-1,4-10,9-8,5-13,04/11/21,03/03/22",
    "8,10-8,1-3,7-9,5-4,13-11,2-6,14-12,11/11/21,10/03/22",
    "9,4-14,10-2,11-5,3-13,6-7,8-12,9-1,18/11/21,17/03/22",
    "10,7-11,1-14,4-6,12-10,5-3,9-13,8-2,25/11/21,24/03/22",
    "11,13-12,5-9,14-8,2-4,10-6,1-11,3-7,02/12/21,31/03/22",
    "12,3-9,12-4,1-13,6-8,2-14,7-5,11-10,09/12/21,07/04/22",
    "13,5-1,13-7,3-2,9-11,6-12,10-14,8-4,16/12/21,14/04/22",
    ",,,,,,,,,",
    ",,,,,,,,,",
    ",,,,,,,,,",
    "1 Annitsford Irish A,,,,,,8 Annitsford Irish B,,,",
    "2 Isabella A,,,,,,9 Breakers B,,,",
    "3 South Beach Community,,,,,,10 General Havelock,,,",
    "4 Breakers A,,,,,,11 Bedlington Station,,,",
    "5 Newsham Side Club,,,,,,12 Market Tavern ,,,",
    "6 Comrades,,,,,,13 Charltons,,,",
    "7 Sports Club,,,,,,14 Bebside,,,"
  )

  "processCsvFile" should {

    "create a list of fixtureWeeks when a valid csv file has been processed" in {
      val result = fixtureFileConnector.processCsvFile

      result mustEqual processedCsv
    }
  }
}
