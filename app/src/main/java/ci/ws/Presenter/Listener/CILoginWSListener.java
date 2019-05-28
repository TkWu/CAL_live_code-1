package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CILoginResp;

/**
 * Created by Ryan on 16/4/14.
 */
public interface CILoginWSListener {

    /**
     * 發送登入的response, 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param loginResp Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onLoginSuccess( String rt_code, String rt_msg, CILoginResp loginResp );
    /**
     * 發送登入的response, 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code, 989/990請走wireframeP.21 LoginM2流程
     * @param rt_msg    result msg
     */
    void onLoginError( String rt_code, String rt_msg );

    /**
     * 使用Identity/Passport Id取得卡號。, 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param strCard_no Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquirySuccess(String rt_code, String rt_msg, String strCard_no, String strPassword);

    /**
     * 使用Identity/Passport Id取得卡號, 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryError(String rt_code, String rt_msg);

    /**
     * 功能說明: 使用Open Id如Facebook、Google+ Id登入系統。成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param socialLoginResp 登入成功回復的資料, 同Login
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onSocialLoginSuccess(String rt_code, String rt_msg, CILoginResp socialLoginResp);

    /**
     * 功能說明: 使用Open Id如Facebook、Google+ Id登入系統。,失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onSocialLoginError(String rt_code, String rt_msg);

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
