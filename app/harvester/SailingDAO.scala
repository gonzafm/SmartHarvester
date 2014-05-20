package harvester

import play.api.Play.current
import models.SailingsToHarvest
import play.api.db.DB
import anorm._
import play.Logger

/**
 * Created by Gonza on 2/5/14.
 */
trait SailingDAO {
  def getSailing(criteria: Criteria): List[SailingsToHarvest]
}

class SailingDAOImpl extends SailingDAO {
  //language=SQL
  val retrieveSailingQuery2: String = """SELECT
                                            v.VENDOR_CODE,
                                            s.EXT_SAILING_ID,
                                            s.SAILING_ID,
                                            s.CRUISE_ID,
                                            to_char(s.SAILING_DATE, 'YYYY-MM-DD') AS SAILING_DATE,
                                            v.VENDOR_ID
                                          FROM crzcms_o_1.crz_sailing s,
                                                crzcms_o_1.crz_cruise c,
                                                crzcms_o_1.crz_vendor v
                                          WHERE s.cruise_id = c.cruise_id
                                                AND c.vendor_id = v.vendor_id
                                                AND s.sailing_date > sysdate + {minDays}
                                                AND s.sailing_date <= sysdate + {maxDays}
                                                AND vendor_code in ('AM','AZ','CV','CB','CO','CS','CU','DS','HA','MS','NC','OE','PC','RE','RC')"""

  override def getSailing(criteria: Criteria): List[SailingsToHarvest] = {
    Logger.info("Vendor Codes to query:" + criteria.vendorCode.mkString("'","','","'"))
    DB.withConnection {
      implicit conn =>
        SQL(retrieveSailingQuery2)
          .on("minDays" -> criteria.minDays,
              "maxDays" -> criteria.maxDays)()
          .map( sailing =>
                          new SailingsToHarvest(vendorCode = sailing[String]("VENDOR_CODE"),
                              externalSailingId = sailing[String]("EXT_SAILING_ID"),
                              sailingId = sailing[java.math.BigDecimal]("SAILING_ID").toString,
                              cruiseId = sailing[java.math.BigDecimal]("CRUISE_ID").toString,
                              sailingDate = sailing[String]("SAILING_DATE"),
                              vendorId = sailing[java.math.BigDecimal]("VENDOR_ID").toString)
        ).toList
    }
  }
}

