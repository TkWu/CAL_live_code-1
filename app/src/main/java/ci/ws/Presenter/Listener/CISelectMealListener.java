package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIInquirtMealInfoResp;
import ci.ws.Models.entities.CIInquiryMealByPassangerResp;

/**
 * Created by Ryan on 16/5/18.
 * 對應 : CISelectMealPresenter
 */
public interface CISelectMealListener {

    /**
     * 取得選餐乘客成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param mealByPassangerResp Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryMealPassenagerSuccess( String rt_code, String rt_msg, CIInquiryMealByPassangerResp mealByPassangerResp );
    /**
     * 取得選餐乘客失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryMealPassenagerError( String rt_code, String rt_msg );

    /**
     * 取得該班機提供的餐點資訊成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param mealInfoResp Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryMealInfoSuccess(String rt_code, String rt_msg, CIInquirtMealInfoResp mealInfoResp);
    /**
     * 取得該班機提供的餐點資訊失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryMealInfoError(String rt_code, String rt_msg);

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
