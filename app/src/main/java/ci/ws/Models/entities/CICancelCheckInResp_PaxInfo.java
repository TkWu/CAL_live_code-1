package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jlchen on 2016/6/17.
 */
@SuppressWarnings("serial")
public class CICancelCheckInResp_PaxInfo implements Serializable, Cloneable {

    /**名( Len: Max. 30)*/
    @Expose
    public String First_Name;

    /**姓( Len: Max. 30)*/
    @Expose
    public String Last_Name;

    /**旅客ID( Len: 16)*/
    @Expose
    public String Uci;

    /**訂位代號 (Len: 6)*/
    @Expose
    public String Pnr_Id;

    /**各航段資料*/
    @Expose
    public List<CICancelCheckInResp_ItineraryInfo> Itinerary_Info;

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}