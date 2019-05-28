package ci.ws.Models.entities;

/**
 * Created by ryan on 16/4/30.
 * 功能說明: 使用PNR、First Name及Last Name取得行程
 */
public class CITripbyPNRReq {

    public static final String PNR_STATUS_Y = "Y";
    public static final String PNR_STATUS_N = "N";

    /**訂位代號( Len: 6)*/
    public String Pnr_id;
    /**名( Len: Max. 30)*/
    public String First_Name;
    /**姓( Len: Max. 30)*/
    public String Last_Name;

    /**是否要取得PNR Status*/
    public String is_pnr_status;

    public CITripbyPNRReq(){
        Pnr_id = "";
        First_Name = "";
        Last_Name = "";
        is_pnr_status = PNR_STATUS_Y;
    }
}
