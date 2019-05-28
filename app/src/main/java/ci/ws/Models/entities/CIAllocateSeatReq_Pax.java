package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by jlchen on 2016/7/7.
 */
@SuppressWarnings("serial")
public class CIAllocateSeatReq_Pax implements Serializable {

    /**旅客編號(Len: 4)*/
    @Expose
    public String Pax_Number;
    /**座位編號(Len: 3)*/
    @Expose
    public String Seat_Number;

    public CIAllocateSeatReq_Pax(){
        Pax_Number      = "";
        Seat_Number     = "";
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
