package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by joannyang on 16/6/20.
 */
public class CICheckIn_Resp implements Serializable {
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
    public String Pax_Type;

    @Expose
    public ArrayList<CICheckIn_ItineraryInfo_Resp> Itinerary_Info = new ArrayList<>();
}
