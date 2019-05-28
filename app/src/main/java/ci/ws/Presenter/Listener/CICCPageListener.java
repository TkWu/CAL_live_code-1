package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CICCPageResp;

/**
 * Created by Kevin Cheng on 17/9/14.
 */
public interface CICCPageListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onSuccess(String rt_code, String rt_msg, CICCPageResp data);
    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onError(String rt_code, String rt_msg);

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
