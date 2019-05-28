package ci.ws.Models.entities;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Ryan on 16/4/26.
 * MyTrip查詢回來的資料,指的就是一筆PNR
 */
@SuppressWarnings("serial")
public class CITripListResp_Itinerary implements Serializable {

    //代表未知的狀態, 需要透過WS去取狀態
    public static final int UNKNOW_STATUS_CODE = -1;


    /**訂位代號(Reservation no) (Len: 6)*/
    @Expose
    public String Pnr_Id;

    /**航段序號 (Len: 4)*/
    @Expose
    public String Segment_Number;

    /**訂位代號編號順序 (Len: 4)*/
    @Expose
    public String Pnr_Seq;

    /**出發日期 (完整) (Len: 10)
     * 調整為表定時間*/
    @Expose
    public String Departure_Date;

    /**出發時間( Len: Max. 5)
     * 調整為表定時間*/
    @Expose
    public String Departure_Time;

    /**出發日期GMT (完整) (Len: 10)
     * 調整為表定時間*/
    @Expose
    public String Departure_Date_Gmt;

    /**出發時間GMT( Len: Max. 5)
     * 調整為表定時間*/
    @Expose
    public String Departure_Time_Gmt;

    /**出發地 (城市代碼) (Len: 3)*/
    @Expose
    public String Departure_Station;

    /**出發地航廈*/
    @Expose
    public String Departure_Terminal;

    /**出發地 (全名) (Len: 100)多國語言*/
    @Expose
    public String Departure_Station_Name;

    /**目的地日期 (完整) (Len: 10)
     * 調整為表定時間*/
    @Expose
    public String Arrival_Date;

    /**目的地時間( Len: Max. 5)
     * 調整為表定時間*/
    @Expose
    public String Arrival_Time;

    /**目的地日期GMT (完整) (Len: 10)
     * 調整為表定時間*/
    @Expose
    public String Arrival_Date_Gmt;

    /**目的地時間GMT( Len: Max. 5)
     * 調整為表定時間*/
    @Expose
    public String Arrival_Time_Gmt;

    /**目的地 (城市代碼) (Len: 3)*/
    @Expose
    public String Arrival_Station;

    /**目的地 (全名) (Len: 100)多國語言*/
    @Expose
    public String Arrival_Station_Name;

    /**目的地航廈*/
    @Expose
    public String Arrival_Terminal;

    /**航空公司 (Len: 2)*/
    @Expose
    public String Airlines;

    /**航空公司 (全名)  (Len: 100)多國語言*/
    @Expose
    public String Airlines_Name;

    /**班機編號 (Len: 4)*/
    @Expose
    public String Flight_Number;

    /**旅客編號 (Len: 4)*/
    @Expose
    public String Pax_Number;

    /**乘客類型(成人/小孩/嬰兒)*/
    @Expose
    public String Pax_Type;

    /**訂位艙等 (Len: 1)
     * 此為訂位時的原始艙等, 未經轉換,
     * 可用於可否選位以及選餐判斷,
     * 選位：除L/X/G/S外，皆可選位
     * 選餐：J/C/D/I/O可選餐*/
    @Expose
    public String Booking_Class;

    /**班機艙等
     * 使用Booking_Class 轉換為下列三種艙等
     * #CIWSBookingClass.BOOKING_CLASS_BUSINESS
     * #CIWSBookingClass.BOOKING_CLASS_PREMIUM
     * #CIWSBookingClass.BOOKING_CLASS_ECONOMY
     * 用於抓取艙等中文名稱 */
    @Expose
    public String Booking_Class_Name_Tag;

    /**機型*/
    @Expose
    public  String Equipment;

    /**班機狀態描述*/
    @Expose
    public String Flight_Status;

    /**狀態碼*/
    public int Status_Code;

    /**標註顏色*/
    @Expose
    public String Color_Code;

    /**飛行時間*/
    @Expose
    public String Time_In_Flight;

    /**
     * 1:航段一
     * 2:航段二
     */
    @Expose
    public String Itinerary_Num;

    /**
     * 是為中華航空(CI)或者是華信航空(AE)
     * Y: 中華航空(CI)
     * N: 華信航空(AE)
     * 否代表該行段不屬於華航的班機, 不可選餐,也不可以選位
     */
    @Expose
    public String Is_Do;

    /**
     * 解析後的資料
     * true  : 中華航空(CI)
     * false : 華信航空(AE)
     * false 代表該行段不屬於華航的班機, 不可選餐,也不可以選位
     */
    @Expose
    public boolean bIs_Do_Tag;


    /**
     * 2016-10-12 新增
     * ui顯示用出發日期*/
    @Expose
    public String Display_Departure_Date = "";

    /**
     * 2016-10-12 新增
     * ui顯示用出發時間*/
    @Expose
    public String Display_Departure_Time = "";

    /**
     * 2016-10-12 新增
     * ui顯示用出發日期GMT*/
    @Expose
    public String Display_Departure_Date_Gmt = "";

    /**
     * 2016-10-12 新增
     * ui顯示用出發時間GMT*/
    @Expose
    public String Display_Departure_Time_Gmt = "";

    /**
     * 2016-10-12 新增
     * ui顯示用抵達日期*/
    @Expose
    public String Display_Arrival_Date = "";

    /**
     * 2016-10-12 新增
     * ui顯示用抵達時間*/
    public String Display_Arrival_Time = "";

    /**
     * 2016-10-12 新增
     * ui顯示用抵達日期GMT*/
    public String Display_Arrival_Date_Gmt = "";

    /**
     * 2016-10-12 新增
     * ui顯示用抵達時間GMT*/
    public String Display_Arrival_Time_Gmt = "";


    /**檢查Display date, time 是否有值, 有就顯示, 沒有就抓取表定時間
     * @param strDisplayTime 實際時間
     * @param strScheduleTime 表定時間*/
    private String CheckActualDateTimeToDisplay( String strDisplayTime , String strScheduleTime ){

        if ( TextUtils.isEmpty(strDisplayTime) ){
            return strScheduleTime;
        } else {
            return strDisplayTime;
        }
    }

    /**取得出發日期
     * 會檢查先實際時間是否有值, 無值改抓表定時間*/
    public String getDisplayDepartureDate(){
        return CheckActualDateTimeToDisplay(Display_Departure_Date, Departure_Date);
    }

    /**取得出發時間
     * 會檢查先實際時間是否有值, 無值改抓表定時間 */
    public String getDisplayDepartureTime(){
        return CheckActualDateTimeToDisplay(Display_Departure_Time, Departure_Time);
    }

    /**取得抵達日期
     * 會檢查先實際時間是否有值, 無值改抓表定時間 */
    public String getDisplayArrivalDate(){
        return CheckActualDateTimeToDisplay(Display_Arrival_Date, Arrival_Date);
    }

    /**取得抵達時間
     * 會檢查先實際時間是否有值, 無值改抓表定時間 */
    public String getDisplayArrivalTime(){
        return CheckActualDateTimeToDisplay(Display_Arrival_Time, Arrival_Time);
    }

    /**取得出發日期ＧＭＴ
     * 會檢查先實際時間是否有值, 無值改抓表定時間*/
    public String getDisplayDepartureDate_GMT(){
        return CheckActualDateTimeToDisplay(Display_Departure_Date_Gmt, Departure_Date_Gmt);
    }

    /**取得出發時間ＧＭＴ
     * 會檢查先實際時間是否有值, 無值改抓表定時間 */
    public String getDisplayDepartureTime_GMT(){
        return CheckActualDateTimeToDisplay(Display_Departure_Time_Gmt, Departure_Time_Gmt);
    }

    /**取得抵達日期ＧＭＴ
     * 會檢查先實際時間是否有值, 無值改抓表定時間 */
    public String getDisplayArrivalDate_GMT(){
        return CheckActualDateTimeToDisplay(Display_Arrival_Date_Gmt, Arrival_Date_Gmt);
    }

    /**取得抵達時間ＧＭＴ
     * 會檢查先實際時間是否有值, 無值改抓表定時間 */
    public String getDisplayArrivalTime_GMT(){
        return CheckActualDateTimeToDisplay(Display_Arrival_Time_Gmt, Arrival_Time_Gmt);
    }
}
