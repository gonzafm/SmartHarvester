package util

import anorm._
import java.sql.Timestamp


/**
 * Created by Gonza on 2/5/14.
 */
object AnormExtension {

//  implicit def rowToTimestamp: Column[Timestamp] = Column.nonNull { (value, meta) =>
//    val MetaDataItem(qualified, nullable, clazz) = meta
//    value match {
//      case ts: java.sql.Timestamp => Right(new DateTime(ts.getTime))
//      case d: java.sql.Date => Right(new DateTime(d.getTime))
//      case str: java.lang.String => Right(dateFormatGeneration.parseDateTime(str))
//      case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass) )
//    }
//  }
//
//  implicit val dateTimeToStatement = new ToStatement[DateTime] {
//    def set(s: java.sql.PreparedStatement, index: Int, aValue: DateTime): Unit = {
//      s.setTimestamp(index, new java.sql.Timestamp(aValue.withMillisOfSecond(0).getMillis()) )
//    }
//  }
}
