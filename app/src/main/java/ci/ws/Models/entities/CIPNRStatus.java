package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/** InquiryPNRItineraryStatus WS 的 response 結構
 * Created by jlchen on 2016/6/13.
 */
public class CIPNRStatus implements Cloneable {

    /**訂位代號 (Len: 6)*/
    @Expose
    public String PNR_Id;

    /**狀態碼
     *  0:  顯示抵達
     *  11: 顯示飛行中
     *  12: 顯示轉機
     *  2:  登機門
     *  3:  預辦登機或臨櫃
     *  4:  顯示選餐及選位
     *  5:  顯示選餐及選位
     *  6:  顯示還很久
     * */
    @Expose
    public String Status_Code;

    public int iStatus_Code = -1;

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
