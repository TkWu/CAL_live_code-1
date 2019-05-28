package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIMileageRecordResp;
import ci.ws.Models.entities.CITripListResp;

/**
 * Created by ryan on 16/4/30.
 */
public interface CIFindMyBookingForRecordListener extends CIFindMyBookingListener{

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param mileageRecordResp Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryMileageRecordSuccess(String rt_code, String rt_msg, CIMileageRecordResp mileageRecordResp);

    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryMileageRecordError(String rt_code, String rt_msg);

    /**
     * 收到rt_code為999或995, 表示授權失敗
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onAuthorizationFailedError(String rt_code, String rt_msg);
}
