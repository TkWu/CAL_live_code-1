package ci.ws.Models.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ryan on 16/4/28.
 * 時刻表的Request的資料結構
 */
public class CITimeTableReq {

    /**出發地 - 必填*/
    @SerializedName("departure")
    public String departure;

    /**出發日期 - 必填
     * ex:2016-04-28*/
    @SerializedName("departure_date")
    public String departure_date;

    /**回程日期
     * 非必填欄位, 不填則是查詢單程
     * ex:2016-04-28*/
    @SerializedName("return_date")
    public String return_date;

    /**目的地
     * 必填*/
    @SerializedName("arrival")
    public String arrival;

    public CITimeTableReq(){
        departure ="";
        departure_date = "";
        return_date = "";
        arrival = "";
    }
}
