package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by joannyang on 16/6/20.
 */
public class CICheckIn_ItineraryInfo_Resp implements Serializable {

    /**出發地 (城市代碼) (Len: 3)*/
    @Expose
    public String Departure_Station;

    /**目的地 (城市代碼) (Len: 3)*/
    @Expose
    public String Arrival_Station;

    /**
     * Check時是否需要做檢查文件(Len: 1)
     Y:可以
     N:不能
     */
    @Expose
    public String Is_Do_Check_Document;

    /**航段ID( Len: 16)*/
    @Expose
    public String Did;

    /**座位號碼*/
    @Expose
    public String Seat_Number;

    @Expose
    public String Segment_Number;

    /**旅客國籍( Len: 3)*/
    @Expose
    public String Nationality;

    /**CPR資料，航空公司(無須變動，在呼叫下隻CheckIn程式時一起帶入)*/
    @Expose
    public String Carrier;

    /**CPR資料，航班編號(無須變動，在呼叫下隻CheckIn程式時一起帶入)*/
    @Expose
    public String Flight_Number;

    /**CPR資料，行程出發日期(無須變動，在呼叫下隻CheckIn程式時一起帶入)*/
    @Expose
    public String Departure_Date;

    /**CPR資料，起站(無須變動，在呼叫下隻CheckIn程式時一起帶入)*/
    @Expose
    public String CPR_BoardPoint;

    /**CPR資料，終點站(無須變動，在呼叫下隻CheckIn程式時一起帶入)*/
    @Expose
    public String CPR_OffPoint;

    /////////
    @Expose
    public String BookingClass;

    @Expose
    public String Airlines;

    /**是否可以換位(Len: 1)*/
    @Expose
    public Boolean Is_Change_Seat;


    /** 處理結果代碼，接收會員專區api回覆內容
     * api/CheckIn Resp 回傳rt_code*/
    @Expose
    public String rt_code;

    /** 處理結果說明，接收會員專區api回覆內容
     * api/CheckIn Resp 回傳rt_msg*/
    @Expose
    public String rt_msg;

    /** 步驟
     * api/CheckIn Resp 回傳*/
    @Expose
    public String Step;
}
