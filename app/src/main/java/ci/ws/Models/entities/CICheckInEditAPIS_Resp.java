package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ryan on 2018/09/02
 */
public class CICheckInEditAPIS_Resp implements Serializable {
    @Expose
    /**名( Len: Max. 30)*/
    public String First_Name;

    @Expose
    /**姓( Len: Max. 30)*/
    public String Last_Name;

    @Expose
    /**旅客ID( Len: 16)*/
    public String Uci;

    @Expose
    /**訂位代號 (Len: 6)*/
    public String Pnr_Id;

    @Expose
    /**CPR資料，旅客型態(無須變動，在呼叫下隻CheckIn程式時一起帶入)*/
    public String Pax_Type;

    @Expose
    public ArrayList<CICheckInEdiaAPIS_ItineraryInfo_Resp> Itinerary_Info = new ArrayList<>();
}
