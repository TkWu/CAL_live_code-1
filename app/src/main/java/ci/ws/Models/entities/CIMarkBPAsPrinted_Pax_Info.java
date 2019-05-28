package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan on 16/8/11.
 */
public class CIMarkBPAsPrinted_Pax_Info {

    /**名( Len: Max. 30)*/
    @Expose
    public String First_Name;
    /**姓( Len: Max. 30)*/
    @Expose
    public String Last_Name;
    /**旅客ID( Len: 16)*/
    @Expose
    public String Uci;
    /**訂位代號( Len: 6)*/
    @Expose
    public String Pnr_id;
    /**航段資訊*/
    @Expose
    public List<CIMarkBP_Pax_ItineraryInfo> Itinerary_Info = new ArrayList<>();

}
