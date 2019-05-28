package ci.ws.Presenter.Listener;

/**
 * Created by Kevin Cheng on 17/7/14.
 */
public interface CIAddMemberListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onSuccess(String rt_code, String rt_msg);
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
