package ci.ws.Presenter.Listener;

import java.util.ArrayList;

import ci.ws.Models.entities.CIBaggageInfoContentResp;
import ci.ws.Models.entities.CIBaggageInfoResp;


public interface CIBaggageInfoListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     * @param rt_code
     * @param rt_msg
     */
    void   onBaggageInfoByPNRAndBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoResp> arBaggageInfoListResp );
    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onBaggageInfoByPNRAndBGNumError(String rt_code, String rt_msg);

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onBaggageInfoByBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoContentResp> arDataList );
    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onBaggageInfoByBGNumError(String rt_code, String rt_msg);

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
