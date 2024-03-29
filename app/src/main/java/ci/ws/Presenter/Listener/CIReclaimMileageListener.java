package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIReclaimErrorResp;

/**
 * Created by jlchen on 16/5/11.
 */
public interface CIReclaimMileageListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onReclaimMileageSuccess(String rt_code, String rt_msg);

    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onReclaimMileageError(String rt_code, String rt_msg);

    /**
     * 000但須再輸入票號 由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onReclaimMileageSuccess007(String rt_code, String rt_msg, CIReclaimErrorResp rt_data);

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
