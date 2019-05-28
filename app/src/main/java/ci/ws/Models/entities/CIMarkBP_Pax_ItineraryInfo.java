package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by Ryan on 16/8/11.
 */
public class CIMarkBP_Pax_ItineraryInfo {


    /**出發地 (城市代碼) (Len: 3)*/
    @Expose
    public String Departure_Station;
    /**目的地 (城市代碼) (Len: 3)*/
    @Expose
    public String Arrival_Station;
    /**航段ID( Len: 16)*/
    @Expose
    public String Did;

//    //Response用，處理結果代碼
//    @Expose
//    public String rt_code;
//    //Response用，處理結果說明
//    @Expose
//    public String rt_msg;
//    //Response用，步驟
//    @Expose
//    public String Step;
}
