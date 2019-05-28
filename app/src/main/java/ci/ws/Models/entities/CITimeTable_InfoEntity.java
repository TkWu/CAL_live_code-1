package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Ryan on 16/4/28.
 * 時刻表的每張航段牌卡的資料結構
 */
public class CITimeTable_InfoEntity {

    /**直飛*/
    public static final String JOURNEY_STATUS_D = "D";

    /**非直飛-開始*/
    public static final String JOURNEY_STATUS_S = "S";
    /**非直飛-轉*/
    public static final String JOURNEY_STATUS_C = "C";
    /**非直飛-抵達*/
    public static final String JOURNEY_STATUS_E = "E";

    /**出發日期 (當地) (yyyy-mm-dd)*/
    @Expose
    public String departure_date;

    /**出發時間( hh:mm)*/
    @Expose
    public String departure_time;

    /**出發日期(GMT)(yyyy-mm-dd)*/
    @Expose
    public String departure_date_gmt;

    /**出發時間(GMT)( hh:mm)*/
    @Expose
    public String departure_time_gmt;

    /**目的地日期 (當地) (yyyy-mm-dd)*/
    @Expose
    public String arrival_date;

    /**目的地時間( hh:mm)*/
    @Expose
    public String arrival_time;

    /**目的地日期(GMT)(yyyy-mm-dd)*/
    @Expose
    public String arrival_date_gmt;

    /**目的地時間(GMT)( hh:mm)*/
    @Expose
    public String arrival_time_gmt;

    /**出發地 (城市代碼) (Len: 3)*/
    @Expose
    public String departure_air_port;

    /**出發地 (城市名稱)*/
    @Expose
    public String departure_name;

    /**出發航廈*/
    @Expose
    public String departure_terimal;

    /**目的地 (城市代碼) (Len: 3)*/
    @Expose
    public String arrival_air_port;

    /**目的地 (城市名稱)*/
    @Expose
    public String arrival_name;

    /**目的航廈*/
    @Expose
    public String arrival_terimal;

    /**航空公司 (Len: 2)*/
    @Expose
    public String company;

    /**直飛/非直飛, 非直飛開頭一定是S,E結尾
     * journey_status*/
    public String Status;

    /**班機編號 (Len: 4)*/
    @Expose
    public String flight_number;

    /**飛機型號*/
    @Expose
    public String type_of_aircraft;

    @Expose
    public String number_of_stops;

    /**飛行時間(hh:mm)*/
    @Expose
    public String leg_duration;

    @Expose
    public String day_of_operation;

    @Expose
    public String effective_period;

    //2017-02-15 CR 增加航班的飛行日期(七天)
    public ArrayList<CITimeTableDayOfFlightResp> day_of_flight;

    //2017-03-02 CR 承運廠商
    @Expose
    public String operating_Company;
}
