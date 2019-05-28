package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIExpiringMileageResp;
import ci.ws.Models.entities.CIInquiryAwardRecordRespList;
import ci.ws.Models.entities.CIMileageRecordResp;
import ci.ws.Models.entities.CIMileageResp;
import ci.ws.Models.entities.CIRedeemRecordResp;

/**
 * Created by jlchen on 16/5/11.
 */
public interface CIInquiryMileageListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param MileageResp Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryMileageSuccess(String rt_code, String rt_msg, CIMileageResp MileageResp);

    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryMileageError(String rt_code, String rt_msg);

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param expiringMileageResp Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryExpiringMileageSuccess(String rt_code, String rt_msg, CIExpiringMileageResp expiringMileageResp);

    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryExpiringMileageError(String rt_code, String rt_msg);

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
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param redeemRecordResp Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryRedeemRecordSuccess(String rt_code, String rt_msg, CIRedeemRecordResp redeemRecordResp);

    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryRedeemRecordError(String rt_code, String rt_msg);

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param awardRecordResps Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryAwardRecordSuccess(String rt_code, String rt_msg, CIInquiryAwardRecordRespList awardRecordResps);

    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryAwardRecordError(String rt_code, String rt_msg);
    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();

    /**
     * 收到rt_code為999或995, 表示授權失敗
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onAuthorizationFailedError(String rt_code, String rt_msg);
}
