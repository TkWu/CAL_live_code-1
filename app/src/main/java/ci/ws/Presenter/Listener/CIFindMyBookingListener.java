package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CITripListResp;

/**
 * Created by ryan on 16/4/30.
 */
public interface CIFindMyBookingListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param Tripslist Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryTripsSuccess(String rt_code, String rt_msg, CITripListResp Tripslist);

    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryTripsError(String rt_code, String rt_msg);

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
