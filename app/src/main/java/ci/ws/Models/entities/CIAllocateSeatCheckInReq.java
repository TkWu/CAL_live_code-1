package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ryan on 16/5/17.
 */
@SuppressWarnings("serial")
public class CIAllocateSeatCheckInReq implements Serializable{

    /**出發日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Departure_Date;

    /**出發地 (城市代碼) (Len: 3)*/
    @Expose
    public String Departure_Station;

    /**目的地 (城市代碼) (Len: 3)*/
    @Expose
    public String Arrival_Station;

    /**航空公司 (Len: 2)*/
    @Expose
    public String Airlines;

    /**班機編號 (Len: 4)*/
    @Expose
    public String Flight_Number;

    /**乘客選位資料*/
    public ArrayList<CIAllocateSeatCheckInReq_Pax> Pax_Info;

    public CIAllocateSeatCheckInReq(){
        Departure_Date      = "";
        Departure_Station   = "";
        Arrival_Station     = "";
        Airlines            = "";
        Flight_Number       = "";
        Pax_Info            = new ArrayList<>();
    }

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
