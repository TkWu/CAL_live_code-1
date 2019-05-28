package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import ci.ws.Models.CIPostQuestionsModel;
import ci.ws.Models.CIPostAnswersModel;
import ci.ws.Models.entities.CIPendingQuestionnaireEntity;
import ci.ws.Models.entities.CIPullQuestionnaireReq;
import ci.ws.Models.entities.CIPullQuestionnaireResp;
import ci.ws.Models.entities.CIPushQuestionnaireReq;
import ci.ws.Presenter.Listener.CIPullQuestionnaireListner;
import ci.ws.Presenter.Listener.CIQuestionnaireListner;

/**
 * Created by Kevin Cheng on 17/5/11.
 * 問卷調查用
 */
public class CIQuestionnairePresenter {

    private static CIQuestionnairePresenter m_Instance = null;

    private CIPostQuestionsModel m_pullQuesModel = null;

    private CIPostAnswersModel m_pushQuesModel = null;

    private CIQuestionnaireListner m_Listener = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIQuestionnairePresenter getInstance(CIQuestionnaireListner listener){

        if ( null == m_Instance ){
            m_Instance = new CIQuestionnairePresenter(listener);
        }

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallbackListener(listener);

        return m_Instance;
    }

    public static CIQuestionnairePresenter getInstance(){
        if(null == m_Instance){
            return getInstance(null);
        } else {
            return m_Instance;
        }
    }

    private void setCallbackListener( CIQuestionnaireListner listener ){
        this.m_Listener = listener;
    }

    CIQuestionnairePresenter(CIQuestionnaireListner listener){
        m_Listener = listener;
    }

    /**像Server取得問卷調查*/
    public void pullQuestionnaireFromWS(CIPullQuestionnaireReq req){

        if ( null == m_pullQuesModel){
            m_pullQuesModel = new CIPostQuestionsModel(m_pullQuesCallback);
        }
        if(null != m_Listener){
            m_Listener.showProgress();
        }
        m_pullQuesModel.getQuestionnaireFrowWS(req);
    }

    public void pushQuestionnaireToWS(CIPushQuestionnaireReq req){

        if ( null == m_pushQuesModel){
            m_pushQuesModel = new CIPostAnswersModel(m_pushQuesCallback);
        }
        if(null != m_Listener){
            m_Listener.showProgress();
        }
        m_pushQuesModel.postQuestionnaireToWS(req);
    }

    public void interrupt(){
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_pullQuesModel){
            m_pullQuesModel.CancelRequest();
        }
        if ( null != m_pushQuesModel){
            m_pushQuesModel.CancelRequest();
        }
    }

    public void savePendingQuestionnaie(CIPendingQuestionnaireEntity data){
        if ( null == m_pushQuesModel){
            m_pushQuesModel = new CIPostAnswersModel(m_pushQuesCallback);
        }
        m_pushQuesModel.insertOrUpdate(data);
    }

    public ArrayList<CIPendingQuestionnaireEntity> getPendingQuestionnaie(String token, String version){
        if ( null == m_pushQuesModel){
            m_pushQuesModel = new CIPostAnswersModel(m_pushQuesCallback);
        }
        return m_pushQuesModel.findDataByCardIdAndVersion(token, version);
    }

    public void deletePendingQuestionnaieById(int id){
        if ( null == m_pushQuesModel){
            m_pushQuesModel = new CIPostAnswersModel(m_pushQuesCallback);
        }
        m_pushQuesModel.delete(id);
    }


    CIPostQuestionsModel.InquiryCallback m_pullQuesCallback = new CIPostQuestionsModel.InquiryCallback() {

        @Override
        public void onSuccess(final String rt_code, final String rt_msg, final CIPullQuestionnaireResp data) {

                s_hdUIThreadhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if ( null != m_Listener ) {
                            m_Listener.onSuccess(rt_code, rt_msg);
                            if(m_Listener instanceof CIPullQuestionnaireListner){
                                ((CIPullQuestionnaireListner) m_Listener).onFetchData(data);
                            }
                            m_Listener.hideProgress();
                        }
                    }
                });

        }

        @Override
        public void onError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };

    CIPostAnswersModel.InquiryCallback m_pushQuesCallback = new CIPostAnswersModel.InquiryCallback(){
        @Override
        public void onSuccess(final String rt_code, final String rt_msg) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onSuccess(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }

        @Override
        public void onError(final String rt_code, final String rt_msg) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };

}
