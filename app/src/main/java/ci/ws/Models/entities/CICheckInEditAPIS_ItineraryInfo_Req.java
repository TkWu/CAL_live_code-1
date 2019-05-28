package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class CICheckInEditAPIS_ItineraryInfo_Req {

    /**出發地 (城市代碼) (Len: 3)*/
    @Expose
    public String Departure_Station;

    /**目的地 (城市代碼) (Len: 3)*/
    @Expose
    public String Arrival_Station;

    /**航段ID( Len: 16)*/
    @Expose
    public String Did;

    /**座位號碼*/
    @Expose
    public String Seat_Number;


    /**Check-in時是否需要做檢查文件(Len: 1)
     * 參數由InquiryCheckInAllPaxByPNR,ticket 帶過來*/
    @Expose
    public String Is_Do_Check_Document;

    /**航段序號 (Len: 4)*/
    @Expose
    public String Segment_Number;

    /**旅客國籍*/
    @Expose
    public String Nationality;

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
