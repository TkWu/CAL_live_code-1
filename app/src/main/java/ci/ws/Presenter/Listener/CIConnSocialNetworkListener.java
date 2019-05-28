package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIConnSocialResp;

/**
 * Created by Ryan on 16/4/22.
 */
public interface CIConnSocialNetworkListener {

    /**
     * 綁定社群帳號, 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param connSocialResp Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onSocialConnSuccess( String rt_code, String rt_msg, CIConnSocialResp connSocialResp );
    /**
     * 綁定社群帳號, 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onSocialConnError( String rt_code, String rt_msg );

    /**
     * 綁定社群帳號, 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onDisConnSocialConnSuccess(String rt_code, String rt_msg);
    /**
     * 綁定社群帳號, 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onDisConnSocialConnError(String rt_code, String rt_msg);

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
