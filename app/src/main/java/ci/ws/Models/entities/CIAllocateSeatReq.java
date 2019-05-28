package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ryan on 16/5/17.
 */
@SuppressWarnings("serial")
public class CIAllocateSeatReq implements Serializable {

    /**訂位代號 (Len: 6)*/
    @Expose
    public String Pnr_id;

    /**航段編號(Len: 4)*/
    @Expose
    public String Pnr_Seq;

    /**乘客選位資料*/
    public ArrayList<CIAllocateSeatReq_Pax> Pax_Info;

    public CIAllocateSeatReq(){
        Pnr_id          = "";
        Pnr_Seq         = "";
        Pax_Info        = new ArrayList<>();
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
