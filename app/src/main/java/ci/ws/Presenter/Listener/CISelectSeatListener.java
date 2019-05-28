package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CISeatInfoList;

/**
 * Created by Ryan on 16/5/14.
 */
public interface CISelectSeatListener {

    /**
     * 取得選位座位圖成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param SeatInfoList Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onGetSeatMapSuccess( String rt_code, String rt_msg, CISeatInfoList SeatInfoList );
    /**
     * 取得選位座位圖失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onGetSeatMapError( String rt_code, String rt_msg );

    /**
     * 變更座位成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param strSeat Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onAllocateSeatSuccess( String rt_code, String rt_msg, String strSeat );
    /**
     * 變更座位失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onAllocateSeatError( String rt_code, String rt_msg );

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
