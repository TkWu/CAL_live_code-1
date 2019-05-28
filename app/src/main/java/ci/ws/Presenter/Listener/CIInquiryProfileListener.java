package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIProfileEntity;

/**
 * Created by Ryan on 16/5/10.
 */
public interface CIInquiryProfileListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryProfileSuccess( String rt_code, String rt_msg, CIProfileEntity profile );
    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryProfileError( String rt_code, String rt_msg );

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onUpdateProfileSuccess(String rt_code, String rt_msg );
    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onUpdateProfileError(String rt_code, String rt_msg);

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();

    /**
     * 收到rt_code為999或995, 表示授權失敗
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onAuthorizationFailedError(String rt_code, String rt_msg);
}
