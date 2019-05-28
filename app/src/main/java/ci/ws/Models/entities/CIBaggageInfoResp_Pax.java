package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

public class CIBaggageInfoResp_Pax implements Serializable {

    @Expose
    public String First_Name;

    @Expose
    public String Last_Name;

    @Expose
    public ArrayList<CIBaggageInfoNumEntity> Baggage_Number;

}


