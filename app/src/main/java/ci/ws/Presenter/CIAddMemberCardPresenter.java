package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIAddMemberCardModel;
import ci.ws.Models.entities.CIAddMemberCardReq;
import ci.ws.Presenter.Listener.CIAddMemberListener;

/**
 * Created by Kevin Cheng on 17/5/11.
 * 問卷調查用
 */
public class CIAddMemberCardPresenter {

    private static CIAddMemberCardPresenter m_Instance = null;

    private CIAddMemberCardModel m_addMemberCardModel = null;

    private CIAddMemberListener m_Listener = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIAddMemberCardPresenter getInstance(CIAddMemberListener listener){

        if ( null == m_Instance ){
            m_Instance = new CIAddMemberCardPresenter(listener);
        }

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallbackListener(listener);

        return m_Instance;
    }

    public static CIAddMemberCardPresenter getInstance(){
        if(null == m_Instance){
            return getInstance(null);
        } else {
            return m_Instance;
        }
    }

    private void setCallbackListener( CIAddMemberListener listener ){
        this.m_Listener = listener;
    }

    CIAddMemberCardPresenter(CIAddMemberListener listener){
        m_Listener = listener;
    }

    /**像Server取得問卷調查*/
    public void addMemberCard(CIAddMemberCardReq req){

        if ( null == m_addMemberCardModel){
            m_addMemberCardModel = new CIAddMemberCardModel(m_pullQuesCallback);
        }
        if(null != m_Listener){
            m_Listener.showProgress();
        }
        m_addMemberCardModel.setRequestAndDoConnection(req);
    }

    public void interrupt(){
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_addMemberCardModel){
            m_addMemberCardModel.CancelRequest();
        }
    }


    CIAddMemberCardModel.InquiryCallback m_pullQuesCallback = new CIAddMemberCardModel.InquiryCallback() {

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
