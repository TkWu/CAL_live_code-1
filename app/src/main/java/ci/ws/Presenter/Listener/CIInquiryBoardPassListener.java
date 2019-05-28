package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIBoardPassResp;

/**
 * Created by jlchen on 16/5/31.
 * 功能說明:使用Card No、PNR(List)取得Ewallet BoardingPass資訊
 * 對應 : CIInquiryBoardPassPresenter
 */
public interface CIInquiryBoardPassListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas);
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
