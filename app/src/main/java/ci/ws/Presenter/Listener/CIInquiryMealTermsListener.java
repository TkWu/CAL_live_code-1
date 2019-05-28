package ci.ws.Presenter.Listener;

/**
 * Created by Ryan on 16/5/16.
 */
public interface CIInquiryMealTermsListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param strMealteams Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryMealTermsSuccess( String rt_code, String rt_msg, String strMealteams );
    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryMealTermsError( String rt_code, String rt_msg );

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
