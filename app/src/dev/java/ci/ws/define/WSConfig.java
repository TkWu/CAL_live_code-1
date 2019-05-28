package ci.ws.define;

/**
 * Created by ryan on 16/3/25.
 * 開發環境的 WS 連線位置
 */
public class WSConfig extends BaseWSConfig{

    public static final String DEF_WS_SITE              = "http://10.1.71.55";
    public static final String DEF_WS_SITE_QUES         = "http://10.1.71.55";
    //public static final String CHECK_IN_WS_SITE         = "http://10.1.71.55/CIAPP_CKIN/api/";

    public static final String DEF_CONTENT_TYPE_KEY     = "Content-Type";
    public static final String DEF_CONTENT_TYPE         = "application/json;charset=UTF-8";
    public static final String DEF_CONTENT_LENGTH_KEY   = "Content-Length";

    public static final String HEADER_KEY_AUTH          = "Authorization";

    public static final int    DEF_CONNECTION_TIME_OUT = 1 * 1000;
    public static final int    DEF_READ_TIME_OUT       = 1 * 1000;

    public static final String DEF_API_VERSION          = "1.0.0.0";

    /**WS, 使用測試的假資料*/
    public static final Boolean WS_TESTMODE                     = true;
    /**定義假資料的檔名*/
    public static final String FlightStatus                     = "Flight_status.json";
    public static final String FlightStatus_station             = "Flight_status_station.json";
    public static final String SignUpMealList                   = "MealList.json";
    public static final String TimeTable_round                  = "timetable_round.json";
    public static final String MyTripByCardNo                   = "MyTripByCardNo.json";
    public static final String MyTriByTicket                    = "MyTriByTicket.json";
    public static final String MyTripbyPNR                      = "MyTripbyPNR.json";
    public static final String NationalList                     = "InquiryNationalList.json";
    public static final String InquiryMilesProgress             = "InquiryMilesProgress.json";
    public static final String InquiryProfile                   = "InquiryProfile.json";
    public static final String InquiryPassenagerByPNR           = "InquiryPassenagerByPNR.json";
    public static final String InquiryStationList               = "InquiryStationList.json";
    public static final String InquiryStatusOD                  = "InquiryStatusOD.json";
    public static final String InquiryTimeTableOD               = "InquiryTimeTableOD.json";
    public static final String InquiryBookTicketOD              = "InquiryBookTicketOD.json";
    public static final String InquiryMileage                   = "InquiryMileage.json";
    public static final String InquiryExpiringMileage           = "InquiryExpiringMileage.json";
    public static final String InquiryMileageRecord             = "InquiryMileageRecord.json";
    public static final String InquiryRedeemRecord              = "InquiryRedeemRecord.json";
    public static final String SelectSeat                       = "SelectSeat.json";
    public static final String InquiryAwardRecord               = "InquiryAwardRecord.json";
    public static final String InquiryMealTerms                 = "InquiryMealTerms.json";
    public static final String InquiryMealByPassanger           = "InquiryMealByPassenager.json";
    public static final String InquiryMealInfo                  = "InquiryMealInfo.json";
    public static final String CInquiryExtraServiceByPNRNoSIT   = "CInquiryExtraServiceByPNRNoSIT.json";
    public static final String InquiryBoardPass                 = "InquiryBoardPass.json";
    public static final String InquiryCheckInByCardNo           = "InquiryCheckInByCard.json";
    public static final String AllocateSeat                     = "AllocateSeat.json";
    public static final String InquiryPNRItineraryStatus        = "InquiryPNRItineraryStatus.json";
    public static final String InquiryCouponInfo                = "InquiryCouponInfo.json";
    public static final String CancelCheckIn                    = "CancelCheckIn.json";
    public static final String InquiryBranch                    = "InquiryBranch.json";
    public static final String PullQuestionnaire                = "PullQuestionnaire.json";
    public static final String PushQuestionnaire                = "PushQuestionnaire.json";
    public static final String AddMemberCard                    = "";
    public static final String CheckVersionAndAnnouncement      = "CheckVersionAndAnnouncement.json";
    public static final String InquiryEBBasicInfo               = "";
    public static final String InquiryExcessBaggageInfo         = "";
    public static final String CCPage                           = "";
    public static final String EBPayment                        = "";
    public static final String InquiryPromoteCodeToken          = "";
}
