package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CISignUpResp;

/**
 * Created by ryan on 16/4/16.
 */
public interface CISignUpWSListener {

    /**
     * 發送註冊的response, 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param SignupResp Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onSignUpSuccess( String rt_code, String rt_msg, CISignUpResp SignupResp );
    /**
     * 發送註冊的response, 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onSignUpError( String rt_code, String rt_msg  );

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
