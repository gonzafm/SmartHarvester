package harvester

/**
 * Status
 */
class HarvestingStatus(val total:Int = 0,
             var success:Int = 0,
             var failure:Int = 0,
             var isComplete:Boolean = false ) {

  def increaseSuccess():Unit = {
    success = success + 1
  }

  def increaseFailure():Unit = {
    failure = failure + 1
  }
}
