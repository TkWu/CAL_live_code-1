package ci.ws.Models.entities;

/**
 * Created by ryan on 16/4/30.
 */
public class CITripbyTicketReq {

    public static final String PNR_STATUS_Y = "Y";
    public static final String PNR_STATUS_N = "N";

    /**機票號碼( Len: 13)*/
    public String Ticket;
    /**名( Len: Max. 30)*/
    public String First_Name;
    /**姓( Len: Max. 30)*/
    public String Last_Name;

    /**是否要取得PNR Status*/
    public String is_pnr_status;


    public CITripbyTicketReq(){
        Ticket = "";
        First_Name = "";
        Last_Name = "";
        is_pnr_status = PNR_STATUS_Y;
    }
}
