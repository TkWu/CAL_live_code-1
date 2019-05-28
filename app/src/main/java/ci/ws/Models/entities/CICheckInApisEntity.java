package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by joannyang on 16/6/16.
 */
public class CICheckInApisEntity implements Serializable{

    @Expose
    public String Pax_Sex;

    @Expose
    public String Pax_Birth;

    @Expose
    public String Resident_Country;

//    @Expose
    public String Nationality;

    @Expose
    public String Document_Type;

    @Expose
    public String Document_No;

    @Expose
    public String Docuemnt_Eff_Date;

    @Expose
    public String Issue_Country;

}
