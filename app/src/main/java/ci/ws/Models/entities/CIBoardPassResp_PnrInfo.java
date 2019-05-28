package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlchen on 2016/5/31.
 */
@SuppressWarnings("serial")
public class CIBoardPassResp_PnrInfo implements Serializable, Cloneable {

    /**訂位代號 (Len: 6)*/
    @Expose
    public String Pnr_Id;

    /**處理結果代碼，接收會員專區api回覆內容*/
    @Expose
    public String rt_code;

    /**處理結果說明，接收會員專區api回覆內容*/
    @Expose
    public String rt_msg;

    /**各航段資料*/
    @Expose
    public List<CIBoardPassResp_Itinerary> Itinerary;

    public CIBoardPassResp_PnrInfo(){
        Pnr_Id  = "";
        rt_code = "";
        rt_msg  = "";
        Itinerary = new ArrayList<CIBoardPassResp_Itinerary>();
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
