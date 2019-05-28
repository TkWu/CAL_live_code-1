package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIPassengerListResp;

/**
 * Created by jlchen on 16/5/11.
 */
public interface CIInquiryPassengerByPNRListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param PassengerList Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryPassengerByPNRSuccess(String rt_code, String rt_msg, CIPassengerListResp PassengerList);

    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryPassengerByPNRError(String rt_code, String rt_msg);

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
