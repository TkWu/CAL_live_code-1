package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by Ryan on 16/4/27.
 * 用來表示FlightStatus, 每張牌卡的資料
 */
public class CIFlightStatus_infoEntity {

    /**航空公司*/
    @Expose
    public String carrier;

    /**航班編號*/
    @Expose
    public String flight_number;

    /**出發場代號*/
    @Expose
    public String depature_station_code;

    /**出發場站名稱*/
    @Expose
    public String depature_station_desc;

    /**出發航廈*/
    @Expose
    public String depature_terminal;

    /**表定啟程日期(YYYY-MM-DD)*/
    @Expose
    public String stdd;
    /**表定啟程時間(hh:mm)*/
    @Expose
    public String stdt;

    /**預定啟程日期(YYYY-MM-DD)*/
    @Expose
    public String etdd;
    /**預定啟程時間(hh:mm)*/
    @Expose
    public String etdt;

    /**實際啟程日期(YYYY-MM-DD)*/
    @Expose
    public String atdd;
    /**實際啟程時間(hh:mm)*/
    @Expose
    public String atdt;

    /**抵達場站代號*/
    @Expose
    public String arrival_station_code;

    /**抵達場站名稱*/
    @Expose
    public String arrival_station_desc;

    /**抵達航廈*/
    @Expose
    public String arrival_terminal;

    /**表定抵達日期(YYYY-MM-DD)*/
    @Expose
    public String stad;
    /**表定抵達時間(hh:mm)*/
    @Expose
    public String stat;

    /**預定抵達日期(YYYY-MM-DD)*/
    @Expose
    public String etad;
    /**預定抵達日期(hh:mm)*/
    @Expose
    public String etat;

    /**實際抵達日期(YYYY-MM-DD)*/
    @Expose
    public String atad;
    /**實際抵達日期(hh:mm)*/
    @Expose
    public String atat;

    /**航班狀態描述*/
    @Expose
    public String flight_status;

    /**是否標注？？*/
    @Expose
    public String is_high_light;

    /**標注顏色*/
    @Expose
    public String color_code;

    @Expose
    public String block_time;

    @Expose
    public String flight_time;

    /**飛行時間*/
    @Expose
    public String time_in_flight;

    /**剩餘時間*/
    @Expose
    public String time_remaining;

    /**顯示用得啟程日期(YYYY-MM-DD)*/
    public String strDisDepartureDate;
    /**顯示用得啟程時間(hh:mm)*/
    public String strDisDepartureTime;
    /**顯示用得啟程時間Tag, ex 表定,預計,實際*/
    public String strDisDepartureName;

    /**顯示用得抵達日期(YYYY-MM-DD)*/
    public String strDisArrivalDate;
    /**顯示用得抵達時間(hh:mm)*/
    public String strDisArrivalTime;
    /**顯示用得抵達時間Tag, ex 表定,預計,實際*/
    public String strDisArrivalName;

    /**機型*/
    @Expose
    public String AC_Type;
}
