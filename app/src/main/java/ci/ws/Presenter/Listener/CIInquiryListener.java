package ci.ws.Presenter.Listener;

/**
 * Created by Ryan on 16/4/22.
 */
public interface CIInquiryListener {

    /**
     * 取得會員編號, 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param email     會員信箱
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryCardNoSuccess( String rt_code, String rt_msg, String email );
    /**
     * 取得會員編號, 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryCardNoError( String rt_code, String rt_msg );

    /**
     * 取得會員密碼, 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param email     會員信箱
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryPasswordSuccess(String rt_code, String rt_msg, String email);
    /**
     * 取得會員密碼, 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryPasswordError(String rt_code, String rt_msg);

    /**
     * 申請會員密碼, 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param email     會員信箱
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onApplyPasswordSuccess(String rt_code, String rt_msg, String email);
    /**
     * 申請會員密碼, 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onApplyPasswordError(String rt_code, String rt_msg);


    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
