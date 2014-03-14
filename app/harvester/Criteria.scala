package harvester

/**
 * Represents a configuration criteria
 */
class Criteria(val vendorCode: List[String] = List("RC", "CB"),
               val minDays: Int = 3,
               val maxDays: Int = 180) {

}

object Criteria{
  def loadCriteria():Criteria = new Criteria()
}
