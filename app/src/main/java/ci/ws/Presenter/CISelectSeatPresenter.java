package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIAllocateSeatCheckInModel;
import ci.ws.Models.CIAllocateSeatModel;
import ci.ws.Models.CIGetSeatMapModel;
import ci.ws.Models.entities.CIAllocateSeatCheckInReq;
import ci.ws.Models.entities.CIAllocateSeatReq;
import ci.ws.Models.entities.CIGetSeatReq;
import ci.ws.Models.entities.CISeatInfoList;
import ci.ws.Presenter.Listener.CISelectSeatListener;

/**
 * Created by Ryan on 16/5/14.
 */
public class CISelectSeatPresenter {

    private static CISelectSeatPresenter m_Instance = null;

    private CIGetSeatMapModel           m_SeatModel             = null;
    private CIAllocateSeatModel         m_AllocateSeat          = null;
    private CIAllocateSeatCheckInModel  m_AllocateSeatCheckIn   = null;
    private CISelectSeatListener        m_Listener              = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CISelectSeatPresenter getInstance( CISelectSeatListener listener ){
        if ( null == m_Instance ){
            m_Instance = new CISelectSeatPresenter();
        }
        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }
        m_Instance.setCallbackListener(listener);

        return m_Instance;
    }

    private void setCallbackListener( CISelectSeatListener listener ){
        this.m_Listener = listener;
    }

    /**取得班機座位圖
     * @param Seatreq 參數*/
    public void GetSeatMap( CIGetSeatReq Seatreq ){
        if ( null == m_SeatModel ){
            m_SeatModel = new CIGetSeatMapModel(m_ModelCallback);
        }

        m_SeatModel.GetSeatMap(Seatreq);
        if ( null != m_Listener ){
            m_Listener.showProgress();
        }
    }

    /**取消取得座位圖的WS*/
    public void CancelGetSeatMap(){

        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_SeatModel ){
            m_SeatModel.CancelRequest();
        }
    }

    /**選位
     * @param seat 參數*/
    public void AllocateSeat(  CIAllocateSeatReq seat  ){
        if ( null == m_AllocateSeat ){
            m_AllocateSeat = new CIAllocateSeatModel(m_AllocateSeatcallback);
        }

        m_AllocateSeat.AllocateSeat(seat);
        if ( null != m_Listener ){
            m_Listener.showProgress();
        }
    }

    /**取消選擇座位的WS*/
    public void CancelAllocateSeat(){

        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_AllocateSeat ){
            m_AllocateSeat.CancelRequest();
        }
    }

    /**選位-CheckIn
     * @param seat 參數*/
    public void AllocateSeatCheckIn(  CIAllocateSeatCheckInReq seat  ){
        if ( null == m_AllocateSeatCheckIn ){
            m_AllocateSeatCheckIn = new CIAllocateSeatCheckInModel(m_AllocateSeatCheckInCallBack);
        }

        m_AllocateSeatCheckIn.AllocateSeatCheckIn(seat);
        if ( null != m_Listener ){
            m_Listener.showProgress();
        }
    }

    /**取消選擇座位-CheckIn 的WS*/
    public void CancelAllocateSeatCheckIn(){

        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_AllocateSeatCheckIn ){
            m_AllocateSeatCheckIn.CancelRequest();
        }
    }

    CIGetSeatMapModel.GetSeatMapCallBack m_ModelCallback = new CIGetSeatMapModel.GetSeatMapCallBack() {
        @Override
        public void onGetSeatMapSuccess(final String rt_code, final String rt_msg, final CISeatInfoList SeatInfoList) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onGetSeatMapSuccess(rt_code, rt_msg, SeatInfoList);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onGetSeatMapError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onGetSeatMapError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };

    CIAllocateSeatModel.AllocateSeatCallBack m_AllocateSeatcallback = new CIAllocateSeatModel.AllocateSeatCallBack() {
        @Override
        public void onAllocateSeatSuccess(final String rt_code, final String rt_msg, final String strSeat) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onAllocateSeatSuccess(rt_code, rt_msg, strSeat);
                        m_Listener.hideProgress();
                    }
                }
            });
        }

        @Override
        public void onAllocateSeatError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onAllocateSeatError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };

    CIAllocateSeatCheckInModel.CallBack m_AllocateSeatCheckInCallBack = new CIAllocateSeatCheckInModel.CallBack() {
        @Override
        public void onSuccess(final String rt_code, final String rt_msg, final String strSeat) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onAllocateSeatSuccess(rt_code, rt_msg, strSeat);
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
                        m_Listener.onAllocateSeatError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };
}
