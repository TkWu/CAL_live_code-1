package ci.ws.Models.entities;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Ryan on 16/4/29.
 * 查詢行程使用卡號的資料來源
 */
public class CITripbyCardReq {

    /**(近期行程)*/
    public static final String TRIP_TYPE_FLIGHTS = "1";
    /**(歷史紀錄)*/
    public static final String TRIP_TYPE_HISTORY = "2";

    /**是否要取得PNR Status*/
    public static final String PNR_STATUS_Y = "Y";
    public static final String PNR_STATUS_N = "N";

    /**類型, TRIP_TYPE_FLIGHTS or TRIP_TYPE_HISTORY*/
    @SerializedName("Type")
    public String Type;

    /**華夏會員卡號*/
    @SerializedName("Card_Id")
    public String Card_Id;

    /**資料頁數, 預設1頁*/
    @SerializedName("Page_Number")
    public String Page_Number;

    /**資料比數/每頁*/
    @SerializedName("Page_Count")
    public String Page_Count;

    @SerializedName("is_pnr_status")
    /**是否要取得PNR Status*/
    public String is_pnr_status;

    /**訂位代號陣列(Len:6)*/
    @SerializedName("PNR_List")
    public Set<String> Pnr_List;
    /**名( Len: Max. 30)*/
    @SerializedName("First_Name")
    public String First_Name;
    /**姓( Len: Max. 30)*/
    @SerializedName("Last_Name")
    public String Last_Name;



    public CITripbyCardReq(){

        Type = TRIP_TYPE_FLIGHTS;
        Card_Id = "";
        Page_Number = "1";
        Page_Count = "1";
        is_pnr_status = PNR_STATUS_Y;
        Pnr_List = new LinkedHashSet<>();
        First_Name = "";
        Last_Name = "";
    }
}
