package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIMarkBPAsPrintedModel;
import ci.ws.Models.entities.CIMarkBPAsPrintedEntity;
import ci.ws.Presenter.Listener.CIMarkBPAsPrintedListener;

/**
 * Created by ryan on 2016-08-11
 */
public class CIMarkBPAsPrintedPresenter {

    private static CIMarkBPAsPrintedPresenter   m_Instance  = null;
    private CIMarkBPAsPrintedListener           m_Listener  = null;

    private CIMarkBPAsPrintedModel              m_Model     = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIMarkBPAsPrintedPresenter getInstance( CIMarkBPAsPrintedListener listener ){

        if ( null == m_Instance ){
            m_Instance = new CIMarkBPAsPrintedPresenter();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallBack(listener);
        return m_Instance;
    }

    private void setCallBack( CIMarkBPAsPrintedListener listener ){
        m_Listener = listener;
    }

    /**
     * 4.1.11.	MarkBPAsPrinted()
     * 功能說明:
     * 對應1A API : DCSPRT_MarkBPAsPrinted
     */
    public void MarkBPAsPrinted( CIMarkBPAsPrintedEntity markBPAsPrintedReq ){

        if( null == m_Model ) {
            m_Model = new CIMarkBPAsPrintedModel();
        }

        m_Model.setCallback(m_MarkBPAsPrinted);
        m_Model.MarkBPAsPrinted(markBPAsPrintedReq);

        if ( null != m_Listener){
            m_Listener.showProgress();
        }
    }

    CIMarkBPAsPrintedModel.MarkBPAsPrintCallBack m_MarkBPAsPrinted = new CIMarkBPAsPrintedModel.MarkBPAsPrintCallBack() {
        @Override
        public void onMarkBPAsPrintSuccess(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onMarkBPAsPrintSuccess(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }

        @Override
        public void onMarkBPAsPrintError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onMarkBPAsPrintError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };

}
