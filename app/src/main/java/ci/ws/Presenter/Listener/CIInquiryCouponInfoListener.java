package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIInquiryCouponResp;

/**
 * Created by jlchen on 16/6/16.
 * 功能說明:取得Ewallet大於系統日的Coupon資訊
 * 對應 : CIInquiryCouponInfoPresenter
 */
public interface CIInquiryCouponInfoListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onSuccess(String rt_code, String rt_msg, CIInquiryCouponResp datas);
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