package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.function.Core.CIApplication;
import ci.ws.Models.CIInquiryTimeTableModel;
import ci.ws.Models.entities.CITimeTableListResp;
import ci.ws.Models.entities.CITimeTableReq;
import ci.ws.Presenter.Listener.CIInquiryTimetableListener;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/4/28.
 */
public class CIInquiryTimetablePresenter {

    private CIInquiryTimetableListener m_Listener = null;
    private CIInquiryTimeTableModel m_timetableModel = null;
    private static Handler s_hdUIThreadhandler = null;

    public CIInquiryTimetablePresenter( CIInquiryTimetableListener listner ){
        this.m_Listener = listner;

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }
    }

    /**
     * 畫面：TimeTable List頁面
     * 功能說明: 使用出發地、目的地、出發日期、回程日期取得班機時刻
     * 對應1A API : Air_MultiAvailability
     * @param Timereq Request資料, 說明請參照{@link CITimeTableReq}
     * */
    public void InquiryTimetableFromWS( CITimeTableReq Timereq ){

        if ( null == m_timetableModel ){
            m_timetableModel = new CIInquiryTimeTableModel(m_callback);
        }

        m_timetableModel.InquiryTimeTableFromWS(
                Timereq,
                CIApplication.getLanguageInfo().getWSLanguage(),
                WSConfig.DEF_API_VERSION);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**取消查詢TimeTable的WS*/
    public void CancelInquiryTimetable(){
        if ( null != m_timetableModel ){
            m_timetableModel.CancelRequest();
        }
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    CIInquiryTimeTableModel.TimetableCallBack m_callback = new CIInquiryTimeTableModel.TimetableCallBack() {
        @Override
        public void onTimeTableSuccess(final String rt_code, final String rt_msg, final CITimeTableListResp timetableList) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onTimeTableSuccess(rt_code, rt_msg, timetableList);
                        m_Listener.hideProgress();
                    }
                }
            });
        }

        @Override
        public void onTimeTableError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onTimeTableError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };

}
