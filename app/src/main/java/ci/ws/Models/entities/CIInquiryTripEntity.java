package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kevincheng on 2016/5/25.
 * 查詢行程後用於儲存的資料表，僅給ByTicket and ByPNR使用
 */

@DatabaseTable
public class CIInquiryTripEntity  {


    @DatabaseField(id = true, canBeNull = false)
    @Expose
    /**訂位代號*/
    public String PNR;

    @DatabaseField
    @Expose
    public String firstname;


    @DatabaseField
    @Expose
    public String lastname;


    /**該訂位編號的航段編號*/
    @DatabaseField
    @Expose
    public String Itinerary_Num;

    /**該航段編號的狀態*/
    @DatabaseField
    @Expose
    public int Status_Code;

    /**是否可以選餐(Y/N)*/
    @DatabaseField
    @Expose
    public String Is_Select_Meal;

    /**航段序號*/
    @DatabaseField
    @Expose
    public String Segment_Num;

    /**是否在首頁顯示的flag*/
    @DatabaseField
    @Expose
    public boolean isVisibleAtHome;


    /**responce的Json格式字串，存於DB等待需要使用的時候使用Gson轉成物件
     * 補充: 僅接受 CITripListResp class 轉換的資料*/
    @DatabaseField(dataType = DataType.STRING_BYTES)
    @Expose
    public String respResult;
}
