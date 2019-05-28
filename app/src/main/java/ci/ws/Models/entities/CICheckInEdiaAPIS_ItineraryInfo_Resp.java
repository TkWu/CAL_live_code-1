package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

public class CICheckInEdiaAPIS_ItineraryInfo_Resp implements Serializable {

    /**出發地 (城市代碼) (Len: 3)*/
    @Expose
    public String Departure_Station;

    /**目的地 (城市代碼) (Len: 3)*/
    @Expose
    public String Arrival_Station;

    /**Check-in時是否需要做檢查文件(Len: 1)
     * 參數由InquiryCheckInAllPaxByPNR,ticket 帶過來*/
    @Expose
    public String Is_Do_Check_Document;

    /**航段ID( Len: 16)*/
    @Expose
    public String Did;

    /**座位號碼*/
    @Expose
    public String Seat_Number;

    /**航段編號*/
    @Expose
    public String Segment_Number;

    /**旅客國籍( Len: 3)*/
    @Expose
    public String Nationality;

    /**是否需要VISA簽證資料(Y/N)，Y時要出欄位供旅客輸入VISA資料*/
    @Expose
    public String Is_Need_Visa;

    /**CPR資料，航空公司(無須變動，在呼叫下隻CheckIn程式時一起帶入)*/
    @Expose
    public String Carrier;

    /**CPR資料，航班編號(無須變動，在呼叫下隻CheckIn程式時一起帶入)*/
    @Expose
    public String Flight_number;

    /**CPR資料，行程出發日期(無須變動，在呼叫下隻CheckIn程式時一起帶入)*/
    @Expose
    public String Departure_Date;

    /**CPR資料，起站(無須變動，在呼叫下隻CheckIn程式時一起帶入)*/
    @Expose
    public String CPR_BoardPoint;

    /**CPR資料，終點站(無須變動，在呼叫下隻CheckIn程式時一起帶入)*/
    @Expose
    public String CPR_OffPoint;

    /**簽證種類名稱*/
    @Expose
    public String VISA_Wording;

    @Expose
    /**
     * 旅客Apis護照資料
     * CheckIn
     * 由 WS 回傳Apis
     */
    public ArrayList<CICheckInApisEntity> Apis;

    @Expose
    /**
     * 旅客Apis護照地址
     * CheckIn
     * 由 WS 回傳Doca
     */
    //public ArrayList<CICheckInDocaEntity> Doca;
    public CICheckInDocaEntity Doca;
}
