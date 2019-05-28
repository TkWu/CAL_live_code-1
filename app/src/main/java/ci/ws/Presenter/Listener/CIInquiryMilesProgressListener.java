package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIMilesProgressEntity;

/**
 * Created by Ryan on 16/5/6.
 */
public interface CIInquiryMilesProgressListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param miles Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onStationSuccess( String rt_code, String rt_msg,  CIMilesProgressEntity miles  );

    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onStationError( String rt_code, String rt_msg );

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
