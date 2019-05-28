package ci.ws.Models.entities;

/**
 * Created by Ryan on 16/5/27.
 */
@Deprecated
public class CICheckInPaxEntity {

    /**名( Len: Max. 30)*/
    public String First_Name;

    /**姓( Len: Max. 30)*/
    public String Last_Name;

    /**旅客ID( Len: 16)*/
    public String Uci;

    /**訂位代號 (Len: 6)*/
    public String Pnr_Id;

    /**黑名單(Len: 1)*/
    public Boolean Is_Black;
}
