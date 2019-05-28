package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIPullQuestionnaireResp;

/**
 * Created by Kevin Cheng on 17/5/11.
 */
public interface CIPullQuestionnaireListner extends CIQuestionnaireListner{

    /**
     * 在取得資料的時候
     * @param data   result data
     */
    void onFetchData(CIPullQuestionnaireResp data);
}
