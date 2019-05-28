package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ryan on 16/5/26.
 * CheckIn 牌卡的資料結構
 */
@SuppressWarnings("serial")
public class CICheckInPax_ItineraryInfoEntity implements Serializable{

    /**航段序號 (Len: 4)*/
    @Expose
    public String Segment_Number;

    /**出發日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Departure_Date;

    /**出發時間( Len: Max. 5)*/
    @Expose
    public String Departure_Time;

    //
    /**實際出發日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Actual_Departure_Date;
    /**實際出發時間 (Len: 5)*/
    @Expose
    public String Actual_Departure_Time;
    /**預計出發日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Estimate_Departure_Date;
    /**預計出發時間 (Len: 5)*/
    @Expose
    public String Estimate_Departure_Time;
    /**表定出發日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Departure_Date_Gmt;
    /**表訂出發日期 (Len: 5)*/
    @Expose
    public String Departure_Time_Gmt;
    /**實際出發日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Actual_Departure_Date_Gmt;
    /**實際出發時間 (Len: 5)*/
    @Expose
    public String Actual_Departure_Time_Gmt;
    /**預計出發日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Estimate_Departure_Date_Gmt;
    /**預計出發時間 (Len: 5)*/
    @Expose
    public String Estimate_Departure_Time_Gmt;
    /**經過計算(表定,預計,實際)要呈現在畫面的日期*/
    @Expose
    public String Display_Departure_Date;
    /**經過計算(表定,預計,實際)要呈現在畫面的時間*/
    @Expose
    public String Display_Departure_Time;
    /**經過計算(表定,預計,實際)要呈現在畫面的日期*/
    @Expose
    public String Display_Departure_Date_Gmt;
    /**經過計算(表定,預計,實際)要呈現在畫面的時間*/
    @Expose
    public String Display_Departure_Time_Gmt;
    //
    //

    /**出發地 (城市代碼) (Len: 3)*/
    @Expose
    public String Departure_Station;

    /**出發地 (全名) (Len: 100)*/
    @Expose
    public String Departure_Station_Name;

    /**表定目的地日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Arrival_Date;

    /**表定目的地時間( Len: Max. 5)*/
    @Expose
    public String Arrival_Time;

    //
    /**實際目的地日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Actual_Arrival_Date;
    /**實際目的地時間 (Len: 5)*/
    @Expose
    public String Actual_Arrival_Time;
    /**預計目的地日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Estimate_Arrival_Date;
    /**預計目的地時間 (Len: 5)*/
    @Expose
    public String Estimate_Arrival_Time;
    /**表定目的地日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Arrival_Date_Gmt;
    /**表訂目的地日期 (Len: 5)*/
    @Expose
    public String Arrival_Time_Gmt;
    /**實際目的地日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Actual_Arrival_Date_Gmt;
    /**實際目的地時間 (Len: 5)*/
    @Expose
    public String Actual_Arrival_Time_Gmt;
    /**預計目的地日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Estimate_Arrival_Date_Gmt;
    /**預計目的地時間 (Len: 5)*/
    @Expose
    public String Estimate_Arrival_Time_Gmt;
    /**經過計算(表定,預計,實際)要呈現在畫面的日期*/
    @Expose
    public String Display_Arrival_Date;
    /**經過計算(表定,預計,實際)要呈現在畫面的時間*/
    @Expose
    public String Display_Arrival_Time;
    /**經過計算(表定,預計,實際)要呈現在畫面的日期*/
    @Expose
    public String Display_Arrival_Date_Gmt;
    /**經過計算(表定,預計,實際)要呈現在畫面的時間*/
    @Expose
    public String Display_Arrival_Time_Gmt;
    //
    //

    /**目的地 (城市代碼) (Len: 3)*/
    @Expose
    public String Arrival_Station;

    /**目的地 (全名) (Len: 100)
     */
    @Expose
    public String Arrival_Station_Name;

    /**航空公司 (Len: 2)*/
    @Expose
    public String Airlines;

    /**航空公司 (全名)  (Len: 100)
     */
    @Expose
    public String Airlines_Name;

    /**班機編號 (Len: 4)*/
    @Expose
    public String Flight_Number;

    /**航段ID( Len: 16)*/
    @Expose
    public String Did;

    /**黑名單(Len: 1)*/
    @Expose
    public Boolean Is_Black;

    /**是否已經報到( Len: Max. 5)*/
    @Expose
    public Boolean Is_Check_In;

    /**是否可以報到(Len: 1)*/
    @Expose
    public Boolean Is_Do_Check_In;

    /**Check-in時是否需要做檢查文件(Len: 1)*/
    @Expose
    public String Is_Do_Check_Document;

    /**是否可以取消報到(Len: 1)*/
    @Expose
    public Boolean Is_Do_Cancel_Check_In;

    /**是否可以換位(Len: 1)*/
    @Expose
    public Boolean Is_Change_Seat;

    /**登機證號碼*/
    @Expose
    public String Boarding_Pass;

    /**座位號碼*/
    @Expose
    public String Seat_Number;

    /**是否可以顯示登機證*/
    @Expose
    public String Is_Display_Boarding_pass;

    /**登機門號*/
    @Expose
    public String Boarding_Gate;

    /**登機日期*/
    @Expose
    public String Boarding_Date;

    /**登機時間*/
    @Expose
    public String Boarding_Time;

    /**臨櫃check in號碼*/
    @Expose
    public String Check_In_Counter;

    @Expose
    public String BookingClass;

    /**行李轉盤*/
    @Expose
    public String Carousel;

    @Expose
    /**其他須做Check In航段ID( Len: 16)*/
    public ArrayList<Oids> Oids = new ArrayList<>();

    @SuppressWarnings("serial")
    public class Oids implements Serializable{

        @Expose
        public String Oid;
    }

    @Expose
    /**
     * 旅客Apis護照資料
     * InquiryCheckInAllPaxByPNR / InquiryCheckInAllPaxByTicket
     * 由 WS 回傳Apis
     */
    public ArrayList<Apis> Apis;

    @SuppressWarnings("serial")
    public class Apis implements Serializable{

        @Expose
        public String Pax_Sex;

        @Expose
        public String Pax_Birth;

        @Expose
        public ArrayList<String> Resident_Country;

        @Expose
        public String Nationality;

        @Expose
        public String Document_Type;

        @Expose
        public String Document_No;

        @Expose
        public String Docuemnt_Eff_Date;

        @Expose
        public String Issue_Country;
    }

    @Expose
    /**
     * 旅客Apis護照資料
     * InquiryCheckInAllPaxByPNR / InquiryCheckInAllPaxByTicket
     * 由 WS 回傳Doca
     */
    public ArrayList<Doca> Doca;

    @SuppressWarnings("serial")
    public class Doca implements Serializable{
        @Expose
        public String Traveler_Address;

        @Expose
        public String Traveler_City;

        @Expose
        public String Traveler_Postcode;

        @Expose
        public String Country_Sub_Entity;
    }

//    /**行李追蹤*/
//    @Expose
//    public ArrayList<CIBaggageInfoNumEntity> Baggage_Number;

}
