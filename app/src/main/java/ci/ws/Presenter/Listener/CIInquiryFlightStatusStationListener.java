package ci.ws.Presenter.Listener;

import ci.ws.Presenter.CIInquiryFlightStationPresenter;

/**
 * Created by Ryan on 16/5/5.
 */
public interface CIInquiryFlightStatusStationListener {

    /**
     * 成功取得總站由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     * @param presenter
     */
    void onAllStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter);
    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onStationError( String rt_code, String rt_msg );

    /**
     * 成功取得對應的目的站由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     * @param presenter
     */
    void onODStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter);

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
