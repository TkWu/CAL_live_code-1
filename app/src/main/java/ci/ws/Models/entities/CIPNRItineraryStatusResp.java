package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/** InquiryPNRItineraryStatus WS 的 response 結構
 * Created by jlchen on 2016/6/13.
 */
public class CIPNRItineraryStatusResp implements Cloneable {

    @Expose
    public CIPNRStatus PNR_Status;

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
