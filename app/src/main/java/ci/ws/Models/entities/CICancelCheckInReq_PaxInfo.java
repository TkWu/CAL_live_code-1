package ci.ws.Models.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlchen on 2016/6/23.
 */
@SuppressWarnings("serial")
public class CICancelCheckInReq_PaxInfo implements Serializable{

    public String First_Name;
    public String Last_Name;
    public String Uci;
    public String Pnr_Id;

    public List<CICancelCheckInReq_ItineraryInfo> Itinerary_Info;

    public CICancelCheckInReq_PaxInfo(){
        First_Name      = "";
        Last_Name       = "";
        Uci             = "";
        Pnr_Id          = "";
        Itinerary_Info  = new ArrayList<>();
    }
}