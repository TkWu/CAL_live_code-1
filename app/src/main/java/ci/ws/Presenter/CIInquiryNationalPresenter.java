package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;

import ci.ws.Models.CINationalListModel;
import ci.ws.Models.entities.CINationalEntity;
import ci.ws.Presenter.Listener.CIInquiryNationalListner;

/**
 * Created by Ryan on 16/5/2.
 */
public class CIInquiryNationalPresenter {

    private static CIInquiryNationalPresenter m_Instance = null;

    private CINationalListModel m_NationalModel = null;

    private CIInquiryNationalListner m_Listener = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIInquiryNationalPresenter getInstance( CIInquiryNationalListner listener ){

        if ( null == m_Instance ){
            m_Instance = new CIInquiryNationalPresenter(listener);
        }

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallbackListener(listener);

        return m_Instance;
    }

    private void setCallbackListener( CIInquiryNationalListner listener ){
        this.m_Listener = listener;
    }

    CIInquiryNationalPresenter( CIInquiryNationalListner listener ){
        m_Listener = listener;
    }

    /**像Server取得國籍列表*/
    public void InquiryNationalListFromWS(){

        if ( null == m_NationalModel ){
            m_NationalModel = new CINationalListModel(m_callback);
        }

        m_NationalModel.getNationalListFrowWS();
    }

    public ArrayList<CINationalEntity> getNationalList(){

        if ( null == m_NationalModel ){
            m_NationalModel = new CINationalListModel(m_callback);
        }
        ArrayList<CINationalEntity> datas = m_NationalModel.getNationalListFromDb();
        if(null == datas && null != m_Listener){
            m_Listener.showProgress();
        }
        return datas;
    }

    public void interrupt(){
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_NationalModel ){
            m_NationalModel.CancelRequest();
        }
    }

    public HashMap<String, CINationalEntity> getNationalMap(){

        if ( null == m_NationalModel ){
            m_NationalModel = new CINationalListModel(m_callback);
        }

        return m_NationalModel.getNationalMapFromDb();
    }

    public void ClearNationListData(){

        if ( null == m_NationalModel ){
            m_NationalModel = new CINationalListModel(m_callback);
        }

        m_NationalModel.ClearDate();
    }

    CINationalListModel.InquiryCallback m_callback = new CINationalListModel.InquiryCallback() {

        @Override
        public void onNationalSuccess(final String rt_code,final String rt_msg) {

                s_hdUIThreadhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if ( null != m_Listener ) {
                            m_Listener.onInquiryNationalSuccess(rt_code, rt_msg);
                            m_Listener.hideProgress();
                        }
                    }
                });

        }

        @Override
        public void onNationalError(final String rt_code,final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryNationalError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };

}
