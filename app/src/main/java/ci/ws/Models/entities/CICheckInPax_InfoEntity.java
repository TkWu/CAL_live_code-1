package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ryan on 16/5/26.
 */
@SuppressWarnings("serial")
public class CICheckInPax_InfoEntity implements Serializable {

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
    /**旅客類型 ADT:成人,CHD:兒童,INF:嬰兒*/
    public String Pax_Type;

    @Expose
    public ArrayList<CICheckInPax_ItineraryInfoEntity> m_Itinerary_InfoList = new ArrayList<>();

}
