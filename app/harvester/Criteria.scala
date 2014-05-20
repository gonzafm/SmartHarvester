package harvester

/**
 * Represents a configuration criteria
 */
class Criteria(val vendorCode: List[String] = List("RC", "CB","CU","RE","OE"),
               val minDays: Int = 3,
               val maxDays: Int = 180) {
}
