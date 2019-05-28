package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIFlightStatusResp;

/**
 * Created by ryan on 16/4/27.
 * 功能說明: 依查詢方式by route或by flight no.查詢出航班狀態
 * 對應CI API : GetFlightInfo
 */
public interface CIInquiryFlightStatusListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param flightStatusResp Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onFlightStatusSuccess( String rt_code, String rt_msg, CIFlightStatusResp flightStatusResp );

    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onFlightStatusError( String rt_code, String rt_msg );

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
