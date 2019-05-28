package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIApisResp;

/**
 * Created by joannyang on 16/5/23.
 */
public interface CIInquiryApisListListener {
    void InquiryApisSuccess(String rt_code, String rt_msg, final CIApisResp apis);

    void InquiryApisError(String rt_code, String rt_msg);

    void InsertApidSuccess(String rt_code, String rt_msg);

    void InsertApisError(String rt_code, String rt_msg);

    void UpdateApisSuccess(String rt_code, String rt_msg);

    void UpdateApisError(String rt_code, String rt_msg);

    void DeleteApisSuccess(String rt_code, String rt_msg);

    void DeleteApisError(String rt_code, String rt_msg);

    void InsertUpdateApisSuccess(String rt_code, String rt_msg);

    void InsertUpdateApisError(String rt_code, String rt_msg);

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
