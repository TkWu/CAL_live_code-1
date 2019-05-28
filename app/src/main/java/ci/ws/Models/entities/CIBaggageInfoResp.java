package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**行李資訊List*/
public class CIBaggageInfoResp implements Serializable {


    @Expose
    public String Departure_Station;

    @Expose
    public String Arrival_Station;

    @Expose
    public String Departure_Date;

    @Expose
    public String Arrival_Date;

    @Expose
    public ArrayList<CIBaggageInfoResp_Pax> Pax;

}


