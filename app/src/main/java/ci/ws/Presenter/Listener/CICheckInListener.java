package ci.ws.Presenter.Listener;

import java.util.ArrayList;

import ci.ws.Models.entities.CICheckInEditAPIS_Resp;
import ci.ws.Models.entities.CICheckIn_Resp;

/**
 * Created by joannyang on 16/6/20.
 */
public interface CICheckInListener {

    /**
     * CheckIn成功由此訊息通知,
     * rt_code 規則同api文件
     * @param rt_code
     * @param rt_msg
     * @param arPaxInfo
     */
    void   onCheckInSuccess( String rt_code, String rt_msg, ArrayList<CICheckIn_Resp> arPaxInfo);
    /**
     * CheckIn失敗由此訊息通知,
     * rt_code 規則同api文件
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onCheckInError( String rt_code, String rt_msg );

    /**EditAPIS*/
    void onEditAPISSuccess(String rt_code, String rt_msg, String strNeedVISA, ArrayList<CICheckInEditAPIS_Resp> arPaxInfo);

    /**EditAPIS*/
    void onEditAPISError( String rt_code, String rt_msg );

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();
}
