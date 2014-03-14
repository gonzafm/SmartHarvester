package models

import scala.xml.NodeSeq

/**
 * Created by Gonza on 1/17/14.
 */
class CategoryInfoRequest(val pcc:String = "NR77",
                          val serviceTag:String = "TCYPHONE",
                          val jsessionId:String = "smartHavester_mockId",
                          val language:String = "en",
                          val cruiseId:String,
                          val sailingDate:String,
                          val zipCode:String = "00000",
                          val affiliateId:String,
                          val subAffiliateId:String = "",
                          val cruiseCode:String) {

  def toTupple() = (("pcc" -> pcc),
                   ("serviceTag" -> serviceTag),
                   ("jsessionId" -> jsessionId),
                   ("language" -> language),
                   ("cruiseId" -> cruiseId),
                   ("sailingDate" -> sailingDate),
                   ("zipCode" -> zipCode),
                   ("affiliateId" -> affiliateId),
                   ("subAffiliateId" -> subAffiliateId),
                   ("cruiseCode" -> cruiseCode))
  def toMap():Map[String, String] = Map("pcc" -> pcc,
    "serviceTag" -> serviceTag,
    "jsessionId" -> jsessionId,
    "language" -> language,
    "cruiseId" -> cruiseId,
    "sailingDate" -> sailingDate,
    "zipCode" -> zipCode,
    "affiliateId" -> affiliateId,
    "subAffiliateId" -> subAffiliateId,
    "cruiseCode" -> cruiseCode)

}

object CategoryInfoRequest{

  /**
   * Creates a categoryInfoRequest based on sailing
   */
  def build(sailingResult: SailingsToHarvest):CategoryInfoRequest = {
    new CategoryInfoRequest(cruiseId = sailingResult.cruiseId,
                            sailingDate = sailingResult.sailingDate,
                            affiliateId = sailingResult.sailingId,
                            cruiseCode = sailingResult.vendorCode)
  }

  def toXml(request:CategoryInfoRequest):NodeSeq = {
    <CategoryInfoRequest>
      <JSessionid>{request.jsessionId}</JSessionid>
      <LanguageCode>{request.language}</LanguageCode>
      <ServiceTag>{request.serviceTag}</ServiceTag>
      <CruiseId>{request.cruiseId}</CruiseId>
      <SailingDate>{request.sailingDate}</SailingDate>
      <ZipCode>{request.zipCode}</ZipCode>
      <IP>127.0.0.1</IP>
      <AffiliateId>{request.affiliateId}</AffiliateId>
      <SubAffiliateId>{request.subAffiliateId}</SubAffiliateId>
      <DefaultMetCategory>SUITE</DefaultMetCategory>
      <PccCode>{request.pcc}</PccCode>
      <PassengerInfo>
        <NumOfAdults>2</NumOfAdults>
      </PassengerInfo>
      <CruiseCode>{request.cruiseCode}</CruiseCode>
    </CategoryInfoRequest>
  }
}
