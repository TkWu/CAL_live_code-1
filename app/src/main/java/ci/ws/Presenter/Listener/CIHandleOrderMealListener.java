package ci.ws.Presenter.Listener;

/**
 * Created by Ryan on 16/4/26.
 * * 功能說明:取的餐點偏好餐點列表。
 * 對應 : CIInquiryMealListPresenter
 */
public interface CIHandleOrderMealListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onOrderSuccess(String rt_code, String rt_msg);
    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onOrderError(String rt_code, String rt_msg);


    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onDeleteOrderSuccess(String rt_code, String rt_msg);
    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onDeleteOrderError(String rt_code, String rt_msg);


    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
