package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIInquiryAwardRecordModel;
import ci.ws.Models.CIInquiryExpiringMileageModel;
import ci.ws.Models.CIInquiryMileageModel;
import ci.ws.Models.CIInquiryMileageRecordModel;
import ci.ws.Models.CIInquiryRedeemRecordModel;
import ci.ws.Models.entities.CIExpiringMileageResp;
import ci.ws.Models.entities.CIInquiryAwardRecordReq;
import ci.ws.Models.entities.CIInquiryAwardRecordRespList;
import ci.ws.Models.entities.CIMileageRecordResp;
import ci.ws.Models.entities.CIMileageReq;
import ci.ws.Models.entities.CIMileageResp;
import ci.ws.Models.entities.CIRedeemRecordResp;
import ci.ws.Presenter.Listener.CIInquiryMileageListener;
import ci.ws.define.CIWSResultCode;

/**
 * Manage Miles 里程管理 的 Presenter
 * Created by jlchen on 16/5/13.
 */
public class CIInquiryMileagePresenter {

    private static CIInquiryMileagePresenter m_Instance = null;
    private CIInquiryMileageListener        m_Listener = null;

    private CIInquiryMileageModel           m_InquiryMileage = null;
    private CIInquiryExpiringMileageModel   m_InquiryExpiringMileageModel   = null;
    private CIInquiryMileageRecordModel     m_InquiryMileageRecordModel     = null;
    private CIInquiryRedeemRecordModel      m_InquiryRedeemRecordModel      = null;
    private CIInquiryAwardRecordModel       m_InquiryAwardRecordModel       = null;

    private static Handler                  s_hdUIThreadhandler = null;

    public static CIInquiryMileagePresenter getInstance(CIInquiryMileageListener listener ){

        if ( null == m_Instance ){
            m_Instance = new CIInquiryMileagePresenter();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallBack(listener);
        return m_Instance;
    }

    private void setCallBack( CIInquiryMileageListener listener ){
        m_Listener = listener;
    }

    /**
     * InquiryMileage(查詢總累積哩程數)
     * 功能說明: 使用卡號查詢
     * 對應1A API : QueryMileage
     */
    public void InquiryMileageFromWS(){

        if ( null == m_InquiryMileage){
            m_InquiryMileage = new CIInquiryMileageModel(m_MileageCallBack);
        }

        m_InquiryMileage.setMileageReq(new CIMileageReq());

        m_InquiryMileage.DoConnection();

        //取得總里程數不用鎖定畫面
//        if ( null != m_Listener){
//            m_Listener.showProgress();
//        }
    }

    /**取消request*/
    public void InquiryMileageCancel(){
        if ( null != m_InquiryMileage){
            m_InquiryMileage.CancelRequest();
        }

        //取得總里程數不用鎖定畫面
//        if ( null != m_Listener){
//            m_Listener.hideProgress();
//        }
    }

    /**
     * InquiryExpiringMileage(查詢到期哩程數)
     * 功能說明: 使用卡號查詢
     * 對應1A API : QueryExpiringMileage
     */
    public void InquiryExpiringMileageFromWS(){

        if ( null == m_InquiryExpiringMileageModel){
            m_InquiryExpiringMileageModel
                    = new CIInquiryExpiringMileageModel(m_ExpiringMileageCallBack);
        }

        m_InquiryExpiringMileageModel.setMileageReq(new CIMileageReq());

        m_InquiryExpiringMileageModel.DoConnection();
        if ( null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**取消request*/
    public void InquiryExpiringMileageCancel(){
        if ( null != m_InquiryExpiringMileageModel){
            m_InquiryExpiringMileageModel.CancelRequest();
        }
        if ( null != m_Listener){
            m_Listener.hideProgress();
        }
    }


    /**
     * InquiryMileageRecord(查詢里程明細)
     * 功能說明: 使用卡號查詢
     * 對應1A API : QueryMileageRecord
     */
    public void InquiryMileageRecordFromWS(){

        if ( null == m_InquiryMileageRecordModel){
            m_InquiryMileageRecordModel
                    = new CIInquiryMileageRecordModel(m_MileageRecordCallBack);
        }

        m_InquiryMileageRecordModel.setMileageReq(new CIMileageReq());

        m_InquiryMileageRecordModel.DoConnection();
        if ( null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**取消request*/
    public void InquiryMileageRecordCancel(){
        if ( null != m_InquiryMileageRecordModel){
            m_InquiryMileageRecordModel.CancelRequest();
        }
        if ( null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    /**
     * InquiryRedeemRecord(查詢已兌換里程明細)
     * 功能說明: 使用卡號查詢
     * 對應1A API : QueryRedeemRecord
     */
    public void InquiryRedeemRecordFromWS(){

        if ( null == m_InquiryRedeemRecordModel){
            m_InquiryRedeemRecordModel
                    = new CIInquiryRedeemRecordModel(m_RedeemRecordCallBack);
        }

        m_InquiryRedeemRecordModel.setMileageReq(new CIMileageReq());

        m_InquiryRedeemRecordModel.DoConnection();
        if ( null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**取消request*/
    public void InquiryRedeemRecordCancel(){
        if ( null != m_InquiryRedeemRecordModel){
            m_InquiryRedeemRecordModel.CancelRequest();
        }
        if ( null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    /**
     * InquiryAwardRecord(查詢兌換里程紀錄)
     * 功能說明: 使用卡號查詢
     * 對應1A API : QueryAwardRecord
     */
    public void InquiryAwardRecordFromWS(){

        if ( null == m_InquiryAwardRecordModel){
            m_InquiryAwardRecordModel
                    = new CIInquiryAwardRecordModel(m_AwardRecordCallBack);
        }

        m_InquiryAwardRecordModel.setupRquest(new CIInquiryAwardRecordReq());

        m_InquiryAwardRecordModel.DoConnection();
        if ( null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**取消request*/
    public void InquiryAwardRecordCancel(){
        if ( null != m_InquiryAwardRecordModel){
            m_InquiryAwardRecordModel.CancelRequest();
        }
        if ( null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    CIInquiryMileageModel.InquiryMileageCallBack m_MileageCallBack
            = new CIInquiryMileageModel.InquiryMileageCallBack() {
        @Override
        public void onInquiryMileageSuccess(final String rt_code,
                                            final String rt_msg,
                                            final CIMileageResp mileageResp) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryMileageSuccess(rt_code, rt_msg, mileageResp);

                        //取得總里程數不用鎖定畫面
//                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryMileageError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        switch (rt_code) {
                            case CIWSResultCode.ERROR_LOGOUT_AUTOMATICALLY:
                            case CIWSResultCode.ERROR_AUTHORIZATION_FAILED:
                                m_Listener.onAuthorizationFailedError(rt_code, rt_msg);
                                break;
                            default:
                                m_Listener.onInquiryMileageError(rt_code, rt_msg);
                                break;
                        }

                        //取得總里程數不用鎖定畫面
//                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIInquiryExpiringMileageModel.InquiryExpiringMileageCallBack m_ExpiringMileageCallBack
            = new CIInquiryExpiringMileageModel.InquiryExpiringMileageCallBack() {
        @Override
        public void onInquiryExpiringMileageSuccess(final String rt_code,
                                                    final String rt_msg,
                                                    final CIExpiringMileageResp expiringMileageResp) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryExpiringMileageSuccess(
                                rt_code,
                                rt_msg,
                                expiringMileageResp);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryExpiringMileageError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        switch (rt_code) {
                            case CIWSResultCode.ERROR_LOGOUT_AUTOMATICALLY:
                            case CIWSResultCode.ERROR_AUTHORIZATION_FAILED:
                                m_Listener.onAuthorizationFailedError(rt_code, rt_msg);
                                break;
                            default:
                                m_Listener.onInquiryExpiringMileageError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIInquiryMileageRecordModel.InquiryMileageRecordCallBack m_MileageRecordCallBack = new CIInquiryMileageRecordModel.InquiryMileageRecordCallBack() {
        @Override
        public void onInquiryMileageRecordSuccess(final String rt_code, final String rt_msg, final CIMileageRecordResp mileageRecordResp) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryMileageRecordSuccess(
                                rt_code,
                                rt_msg,
                                mileageRecordResp);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryMileageRecordError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        switch (rt_code) {
                            case CIWSResultCode.ERROR_LOGOUT_AUTOMATICALLY:
                            case CIWSResultCode.ERROR_AUTHORIZATION_FAILED:
                                m_Listener.onAuthorizationFailedError(rt_code, rt_msg);
                                break;
                            default:
                                m_Listener.onInquiryMileageRecordError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIInquiryRedeemRecordModel.InquiryRedeemRecordCallBack m_RedeemRecordCallBack = new CIInquiryRedeemRecordModel.InquiryRedeemRecordCallBack() {
        @Override
        public void onInquiryRedeemRecordSuccess(final String rt_code, final String rt_msg, final CIRedeemRecordResp redeemRecordResp) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryRedeemRecordSuccess(
                                rt_code,
                                rt_msg,
                                redeemRecordResp);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryRedeemRecordError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        switch (rt_code) {
                            case CIWSResultCode.ERROR_LOGOUT_AUTOMATICALLY:
                            case CIWSResultCode.ERROR_AUTHORIZATION_FAILED:
                                m_Listener.onAuthorizationFailedError(rt_code, rt_msg);
                                break;
                            default:
                                m_Listener.onInquiryRedeemRecordError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIInquiryAwardRecordModel.CallBack m_AwardRecordCallBack = new CIInquiryAwardRecordModel.CallBack() {
        @Override
        public void onSuccess(final String rt_code, final String rt_msg, final CIInquiryAwardRecordRespList datas) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryAwardRecordSuccess(
                                rt_code,
                                rt_msg,
                                datas);
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
                        switch (rt_code) {
                            case CIWSResultCode.ERROR_LOGOUT_AUTOMATICALLY:
                            case CIWSResultCode.ERROR_AUTHORIZATION_FAILED:
                                m_Listener.onAuthorizationFailedError(rt_code, rt_msg);
                                break;
                            default:
                                m_Listener.onInquiryAwardRecordError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };
}
