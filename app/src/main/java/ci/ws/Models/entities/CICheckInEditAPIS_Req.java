package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by ryan on 2018/09/02
 */
public class CICheckInEditAPIS_Req {
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
    public ArrayList<CICheckInEditAPIS_ItineraryInfo_Req> Itinerary_Info = new ArrayList<>();
}
