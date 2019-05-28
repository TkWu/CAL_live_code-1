package ci.ws.Models.entities;

import java.util.ArrayList;

/**
 * Created by Ryan on 16/5/13.
 */
public class CIGetSeatReq {

    /**出發日期 (當地) (完整) (Len: 10)*/
    public String Departure_Date;

    /**出發地 (城市代碼) (Len: 3)*/
    public String Departure_Station;

    /**目的地 (城市代碼) (Len: 3)*/
    public String Arrival_Station;

    /**航空公司 (Len: 2)*/
    public String Airlines;

    /**班機編號 (Len: 4)*/
    public String Flight_Number;

    /**訂位艙等 (Len: 1)*/
    public String Booking_Class;

    /**是否為Check-in時段進行選位
     * Y = CPR
     * N = PNR*/
    public String Is_CPR;

    /**航段ID*/
    //public String Did;

    public ArrayList<CISeatMapPaxInfo> pax_info;

    public String PNR_Id;

    public CIGetSeatReq(){
        Departure_Date      = "";
        Departure_Station   = "";
        Arrival_Station     = "";
        Airlines            = "";
        Flight_Number       = "";
        Booking_Class       = "";
        Is_CPR              = "N";
        //Did                 ="";
        pax_info            = new ArrayList<CISeatMapPaxInfo>();
        PNR_Id              = "";
    }

}
