package ci.ws.Models.entities;

import java.io.Serializable;

/**
 * Created by jlchen on 2016/6/23.
 */
@SuppressWarnings("serial")
public class CICancelCheckInReq_ItineraryInfo implements Serializable {
    public String Did;
    public String Departure_Station;
    public String Arrival_Station;

    public CICancelCheckInReq_ItineraryInfo(){
        Did                 = "";
        Departure_Station   = "";
        Arrival_Station     = "";
    }
}
