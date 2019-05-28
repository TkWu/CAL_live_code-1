package ci.ws.Models.entities;

/**
 * Created by Ryan on 16/4/27.
 * 航班動態Request的資料結構
 */
public class CIFlightStatusReq {

    public static final String BY_ROUTE = "BY_ROUTE";
    public static final String BY_FLIGHT= "BY_FLIGHT";
    public static final String BY_PIN_FLIGHT= "BY_PIN_FLIGHT";

    public static final String DEPARTURE_DATE  = "1";
    public static final String ARRIVAL_DATE    = "2";


    public static final String FLIGHT_ARRIER_CI  = "CI";
    public static final String FLIGHT_ARRIER_AE  = "AE";

    /**查詢方式*/
    public String search_type;
    /**出發機場代號，Length 3，
     * Ex. TPE，search_type= BY_ROUTE必要輸入*/
    public String departure_station;
    /**抵達機場代號，Length 3，
     * Ex. SIN，search_type= BY_ROUTE必要輸入*/
    public String arrival_station;
    /**航班日期，格式YYYY-MM-DD ，Ex. 2016-02-28*/
    public String flight_date;
    /**航班編號， Length 4，search_type= BY_FLIGHT必要輸入*/
    public String flight_number;
    /**航空公司，CI或AE，search_type= BY_FLIGHT必要輸入*/
    public String flight_carrier;
    /**Search by
     * 1 = Departure Date
     * 2 = Arrival Date
     * flight_date 有值時必要輸入*/
    public String by_depature_arrival_date;


    public CIFlightStatusReq(){
        search_type = "";
        departure_station   = "";
        arrival_station     = "";
        flight_date         = "";
        flight_number       = "";
        flight_carrier      = "";
        by_depature_arrival_date = "";
    }
}
