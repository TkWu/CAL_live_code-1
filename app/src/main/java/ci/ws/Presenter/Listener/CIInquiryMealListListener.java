package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIMealListResp;

/**
 * Created by Ryan on 16/4/26.
 * * 功能說明:取的餐點偏好餐點列表。
 * 對應 : CIInquiryMealListPresenter
 */
public interface CIInquiryMealListListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param mealListResp Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryMealListSuccess( String rt_code, String rt_msg, CIMealListResp mealListResp );
    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryMealListError( String rt_code, String rt_msg );


    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
