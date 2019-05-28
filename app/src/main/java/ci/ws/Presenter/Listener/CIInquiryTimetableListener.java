package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CITimeTableListResp;

/**
 * Created by Ryan on 16/4/29.
 */
public interface CIInquiryTimetableListener {

    /**
     * 成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param timetableList Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onTimeTableSuccess( String rt_code, String rt_msg, CITimeTableListResp timetableList );

    /**
     * 失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onTimeTableError( String rt_code, String rt_msg );


    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
