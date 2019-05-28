package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by joannyang on 16/6/16.
 */
public class CICheckInDocaEntity implements Serializable{
    @Expose
    /**
     * 地址
     */
    public String Traveler_Address;

    @Expose
    /**
     * 城市
     */
    public String Traveler_City;

    @Expose
    /**
     * 郵遞區號(Len : 5)
     * ex: 12345(固定五碼)
     */
    public String Traveler_Postcode;

    @Expose
    /**
     * 州
     */
    public String Country_Sub_Entity;
}
