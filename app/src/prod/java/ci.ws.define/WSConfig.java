package ci.ws.define;

/**
 * Created by ryan on 16/3/25.
 * * 正式環境的 WS 連線位置
 */
public class WSConfig extends BaseWSConfig{
    public static final String DEF_WS_SITE              = "https://" + DEF_MOBILE30_BASE_WS_SITE;
    public static final String DEF_WS_SITE_QUES         = "https://" + DEF_MOBILE30_BASE_WS_SITE;
    public static final String DEF_CONTENT_TYPE_KEY     = "Content-Type";
    public static final String DEF_CONTENT_TYPE         = "application/json;charset=UTF-8";
    public static final String DEF_CONTENT_LENGTH_KEY   = "Content-Length";

    public static final String HEADER_KEY_AUTH          = "Authorization";

    //20190701 旅客 ＰＮＲ行程過多造成time out 而無法取得資料
    //延長到１分鐘
    public static final int    DEF_CONNECTION_TIME_OUT = 60 * 1000;
    public static final int    DEF_READ_TIME_OUT       = 30 * 1000;

    public static final String DEF_API_VERSION      = "1.0.0.0";

    /**WS, 使用測試的假資料的Flag, 正式機環境不使用假資料*/
    public static final Boolean WS_TESTMODE                     = false;
    //public static final Boolean Todd_WS_TESTMODE               = true;

    /**定義假資料的檔名*/
    public static final String FlightStatus                     = "";
    public static final String FlightStatus_station             = "";
    public static final String SignUpMealList                   = "";
    public static final String TimeTable_round                  = "";
    public static final String MyTripByCardNo                   = "";
    public static final String MyTriByTicket                    = "";
    public static final String MyTripbyPNR                      = "";
    public static final String NationalList                     = "";
    public static final String InquiryMilesProgress             = "";
    public static final String InquiryProfile                   = "";
    public static final String InquiryPassenagerByPNR           = "";
    public static final String InquiryStationList               = "";
    public static final String InquiryStatusOD                  = "";
    public static final String InquiryTimeTableOD               = "";
    public static final String InquiryBookTicketOD              = "";
    public static final String InquiryMileage                   = "";
    public static final String InquiryExpiringMileage           = "";
    public static final String InquiryMileageRecord             = "";
    public static final String InquiryRedeemRecord              = "";
    public static final String SelectSeat                       = "";
    public static final String InquiryAwardRecord               = "";
    public static final String InquiryMealTerms                 = "";
    public static final String InquiryMealByPassanger           = "";
    public static final String InquiryMealInfo                  = "";
    public static final String CInquiryExtraServiceByPNRNoSIT   = "";
    public static final String InquiryBoardPass                 = "";
    public static final String InquiryCheckInByCardNo           = "";
    public static final String AllocateSeat                     = "";
    public static final String InquiryPNRItineraryStatus        = "";
    public static final String InquiryCouponInfo                = "";
    public static final String CancelCheckIn                    = "";
    public static final String InquiryBranch                    = "";
    public static final String PullQuestionnaire                = "";
    public static final String PushQuestionnaire                = "";
    public static final String AddMemberCard                    = "";
    public static final String CheckVersionAndAnnouncement      = "";
    public static final String InquiryEBBasicInfo               = "";
    public static final String InquiryExcessBaggageInfo         = "";
    public static final String CCPage                           = "";
    public static final String EBPayment                        = "";
    public static final String InquiryPromoteCodeToken          = "";

    public static final String extra_service_vip_act          = "extra_service_vip_act.json";
}
