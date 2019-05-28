package ci.ws.Presenter.Listener;

/**
 * Created by JL Chen on 2016/05/19.
 * * 功能說明:依航班檢查是否開放預訂餐點。
 * 對應 : CICheckFlightMealOpenPresenter
 */
public interface CICheckFlightMealOpenListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
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
