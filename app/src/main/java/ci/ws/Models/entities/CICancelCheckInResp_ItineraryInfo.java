package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by jlchen on 2016/6/17.
 */
@SuppressWarnings("serial")
public class CICancelCheckInResp_ItineraryInfo implements Serializable, Cloneable {

    /**航段ID( Len: 16)*/
    @Expose
    public String Did;

    /**出發地 (城市代碼) (Len: 3)*/
    @Expose
    public String Departure_Station;

    /**目的地 (城市代碼) (Len: 3)*/
    @Expose
    public String Arrival_Station;

    /**處理結果代碼，接收會員專區api回覆內容 */
    @Expose
    public String rt_code;

    /**處理結果說明，接收會員專區api回覆內容 */
    @Expose
    public String rt_msg;

    /**步驟 */
    @Expose
    public String Step;

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
