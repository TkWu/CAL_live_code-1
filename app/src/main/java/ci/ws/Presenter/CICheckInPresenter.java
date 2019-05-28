package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import ci.ws.Models.CICheckInEditAPISModel;
import ci.ws.Models.CICheckInModel;
import ci.ws.Models.entities.CICheckIn_Req;
import ci.ws.Models.entities.CICheckInEditAPIS_Req;
import ci.ws.Models.entities.CICheckInEditAPIS_Resp;
import ci.ws.Models.entities.CICheckIn_Resp;
import ci.ws.Presenter.Listener.CICheckInListener;

/**
 * Created by joannyang on 16/6/20.
 */
public class CICheckInPresenter {

    private static CICheckInPresenter m_Instance = null;
    private CICheckInListener m_Listener = null;

    private CICheckInModel         m_CheckIn  = null;
    private CICheckInEditAPISModel m_EditAPIS = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CICheckInPresenter getInstance( CICheckInListener listener ){

        if ( null == m_Instance ){
            m_Instance = new CICheckInPresenter();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallBack(listener);
        return m_Instance;
    }

    private void setCallBack( CICheckInListener listener ){
        m_Listener = listener;
    }

    /**
     * CheckInFromWS
     * 功能說明: InputAPIS
     * @param arCheckInPaxInfo
     */
    public void CheckInFromWS(ArrayList<CICheckIn_Req> arCheckInPaxInfo ) {
        if( null == m_CheckIn ) {
            m_CheckIn = new CICheckInModel();
        }

        m_CheckIn.setCallback(m_CheckInCallback);
        m_CheckIn.CheckIn(arCheckInPaxInfo);

        if ( null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**
     * 功能說明: EditAPIS
     * @param arEditAPISPaxInfo
     */
    public void EditAPISFromWS(ArrayList<CICheckInEditAPIS_Req> arEditAPISPaxInfo ) {

        if( null == m_EditAPIS ) {
            m_EditAPIS = new CICheckInEditAPISModel();
        }

        m_EditAPIS.setCallback(m_EDitAPISCallback);
        m_EditAPIS.EditAPIS(arEditAPISPaxInfo);

        if ( null != m_Listener){
            m_Listener.showProgress();
        }
    }


    CICheckInModel.CheckInCallBack m_CheckInCallback = new CICheckInModel.CheckInCallBack() {
        @Override
        public void onCheckInSuccess(final String rt_code, final String rt_msg,final ArrayList<CICheckIn_Resp> arPaxInfo) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onCheckInSuccess(rt_code, rt_msg, arPaxInfo);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onCheckInError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onCheckInError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CICheckInEditAPISModel.CIEditAPISCallBack m_EDitAPISCallback = new CICheckInEditAPISModel.CIEditAPISCallBack() {
        @Override
        public void onEditAPISSuccess(final String rt_code, final String rt_msg, final String strNeedVISA, final ArrayList<CICheckInEditAPIS_Resp> arPaxInfo) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onEditAPISSuccess(rt_code, rt_msg, strNeedVISA, arPaxInfo);
                        m_Listener.hideProgress();
                    }
                }
            });
        }

        @Override
        public void onEditAPISError(final String rt_code, final String rt_msg) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onEditAPISError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };


}
