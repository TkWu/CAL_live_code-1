package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by jlchen on 2016/7/7.
 */
@SuppressWarnings("serial")
public class CIAllocateSeatCheckInReq_Pax implements Serializable {

    /**座位編號(Len: 3)*/
    @Expose
    public String Seat_Number;

    /**航段ID( Len:16)*/
    @Expose
    public String Did;

    public CIAllocateSeatCheckInReq_Pax(){
        Seat_Number     = "";
        Did             = "";
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
