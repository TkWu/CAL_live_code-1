package ci.ws.Presenter.Listener;

/**
 * Created by ryan on 2016-08-11
 */
public interface CIMarkBPAsPrintedListener {

    /**
     * Api: MakeBPAsPrint 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onMarkBPAsPrintSuccess(String rt_code, String rt_msg);
    /**
     * Api: MakeBPAsPrint 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onMarkBPAsPrintError(String rt_code, String rt_msg);

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
