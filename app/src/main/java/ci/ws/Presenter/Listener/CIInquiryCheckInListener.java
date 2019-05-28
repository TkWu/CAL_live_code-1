package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CICheckInAllPaxResp;

/**
 * Created by Ryan on 16/5/27.
 */
public interface CIInquiryCheckInListener {

    /**
     * InquiryCheckIn成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param CheckInList Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryCheckInSuccess( String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList );
    /**
     * InquiryCheckIn失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryCheckInError( String rt_code, String rt_msg );

    /**
     * InquiryCheckIn成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param CheckInList Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryCheckInAllPaxSuccess( String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList );
    /**
     * InquiryCheckIn失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryCheckInAllPaxError( String rt_code, String rt_msg );


    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
