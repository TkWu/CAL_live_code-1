package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import ci.ws.Models.CIInquiryCouponInfoModel;
import ci.ws.Models.entities.CIInquiryCouponResp;
import ci.ws.Models.entities.CIInquiryCoupon_Info;
import ci.ws.Presenter.Listener.CIInquiryCouponInfoListener;

/**
 * Created by jlchen on 16/6/16.
 * 文件：CI_APP_API_Coupon.docx
 * 4.1.1 InquiryCouponInfo
 * 功能說明:取得大於系統日的Coupon資訊
 * 對應OracleDB : PPDB.PPTCUPN
 */
public class CIInquiryCouponInfoPresenter {

    private CIInquiryCouponInfoListener         m_Listener          = null;
    private CIInquiryCouponInfoModel            m_Model             = null;
    private static CIInquiryCouponInfoPresenter s_Instance          = null;
    private static Handler                      s_hdUIThreadhandler = null;

    public static CIInquiryCouponInfoPresenter getInstance(
            CIInquiryCouponInfoListener listener ){

        if ( null == s_Instance){
            s_Instance = new CIInquiryCouponInfoPresenter(listener);
        }

        if ( null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        s_Instance.setCallbackListener(listener);

        return s_Instance;
    }

    public CIInquiryCouponInfoPresenter( CIInquiryCouponInfoListener listener ){
        this.m_Listener = listener;
    }

    private void setCallbackListener( CIInquiryCouponInfoListener listener ){
        this.m_Listener = listener;
    }

    /**向WS查詢Coupon資料*/
    public void InquiryCouponInfoFromWS(){
        if ( null != m_Listener){
            m_Listener.showProgress();
        }

        if ( null == m_Model){
            m_Model = new CIInquiryCouponInfoModel(ModelCallback);
        }
        m_Model.sendReqDataToWS();
    }

    /**
     * 中斷要求
     */
    public void interrupt(){
        if ( null != m_Model) {
            m_Model.CancelRequest();
        }
        if ( null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    public ArrayList<CIInquiryCoupon_Info> InquiryCouponInfoFromDB(){

        if ( null == m_Model){
            m_Model = new CIInquiryCouponInfoModel(ModelCallback);
        }
        return m_Model.getDataFromDB();
    }

    private CIInquiryCouponInfoModel.CallBack ModelCallback
            = new CIInquiryCouponInfoModel.CallBack() {

        @Override
        public void onSuccess(final String rt_code,
                              final String rt_msg,
                              final CIInquiryCouponResp datas) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ){
                        m_Listener.hideProgress();
                        m_Listener.onSuccess(rt_code, rt_msg, datas);
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
                        m_Listener.hideProgress();
                        m_Listener.onError(rt_code, rt_msg);
                    }
                }
            });
        }
    };
}
