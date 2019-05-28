package ci.ws.Presenter.Listener;

import java.util.ArrayList;

import ci.ws.Models.entities.CICheckInPaxEntity;

/**
 * Created by Ryan on 16/5/27.
 */
@Deprecated
public interface CIInquiryCheckInPaxListener {

    /**
     * InquiryCheckInPax成功由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param arPaxList Response object
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryCheckInPaxSuccess( String rt_code, String rt_msg, ArrayList<CICheckInPaxEntity> arPaxList );
    /**
     * InquiryCheckInPax失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onInquiryCheckInPaxError( String rt_code, String rt_msg );

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
