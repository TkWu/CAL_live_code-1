package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIPNRItineraryStatusResp;

/**
 * Created by jlchen on 2016/6/13.
 */
public interface CIInquiryPNRItineraryStatusListener{

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param resp      Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryPNRItineraryStatusSuccess(String rt_code,
                                            String rt_msg,
                                            CIPNRItineraryStatusResp resp);

    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryPNRItineraryStatusError(String rt_code, String rt_msg);

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
