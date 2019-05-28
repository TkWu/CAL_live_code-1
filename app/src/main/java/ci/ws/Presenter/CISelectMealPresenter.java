package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIInquiryMealByPassenagerModel;
import ci.ws.Models.CIInquiryMealInfoModel;
import ci.ws.Models.entities.CIInquirtMealInfoResp;
import ci.ws.Models.entities.CIInquiryMealByPassangerResp;
import ci.ws.Presenter.Listener.CISelectMealListener;

/**
 * Created by Ryan on 16/5/18.
 * 功能說明:選餐ＷＳ。
 */
public class CISelectMealPresenter {

    private CISelectMealListener            m_Listener  = null;
    private CIInquiryMealByPassenagerModel m_InuqiryMealbyPassenager = null;
    private CIInquiryMealInfoModel          m_InquiryMealInfo = null;

    private static CISelectMealPresenter    s_Instance  = null;

    private static Handler s_hdUIThreadhandler          = null;

    public static CISelectMealPresenter getInstance( CISelectMealListener listener ){

        if ( null == s_Instance){
            s_Instance = new CISelectMealPresenter();
        }
        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        s_Instance.setCallbackListener(listener);

        return s_Instance;
    }

    public void setCallbackListener( CISelectMealListener listener ){
        this.m_Listener = listener;
    }

    public CISelectMealPresenter(){}


    /***
     * 功能說明: 依訂位代碼、乘客姓、乘客名取得乘客、航班、預選餐點資料。
     * @param strPNR 訂位代號
     * @param first_name 名
     * @param last_name  姓
     * @param flight_sector 航班起迄地點. ex TPE->FUK, 請帶TPEFUK
     * */
    public void InquiryMealByPassangerFromWS( String strPNR, String first_name, String last_name, String flight_sector, String departure_date ){

        if ( null == m_InuqiryMealbyPassenager ){
            m_InuqiryMealbyPassenager = new CIInquiryMealByPassenagerModel(m_passenagerCallback);
        }
        m_InuqiryMealbyPassenager.InquiryMealByPassangerFromWS(strPNR, first_name, last_name, flight_sector, departure_date);

        if ( null != m_Listener ){
            m_Listener.showProgress();
        }
    }

    /**取消取得選餐乘客的WS*/
    public void InterruptInquiryMealByPassangerFromWS(){

        if ( null != m_InuqiryMealbyPassenager ){
            m_InuqiryMealbyPassenager.CancelRequest();
        }
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    /**依航班日期、航班編號、航班航段、艙等、餐點類型編號等取得該班機提供的餐點資訊。
     * @param flight_date 班機日期
     * @param flight_num 航班編號
     * @param flight_sector 航班起迄地點  ex TPE->FUK, 請帶TPEFUK
     * @param seat_class 訂票艙等*/
    public void InquiryMealInfoFromWS( String flight_date, String flight_num, String flight_sector, String seat_class ){

        if ( null == m_InquiryMealInfo ){
            m_InquiryMealInfo = new CIInquiryMealInfoModel(m_MealInfoCallBack);
        }

        m_InquiryMealInfo.InquiryMealInfoFromWS(flight_date, flight_num, flight_sector, seat_class);

        if ( null != m_Listener){
            m_Listener.showProgress();
        }
    }


    /**取消取餐點的WS*/
    public void InterruptInquiryMealInfoFromWS(){

        if ( null != m_InquiryMealInfo ){
            m_InquiryMealInfo.CancelRequest();
        }
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    CIInquiryMealByPassenagerModel.PassangerCallBack m_passenagerCallback = new CIInquiryMealByPassenagerModel.PassangerCallBack() {
        @Override
        public void onInquiryPassenagerSuccess(final String rt_code, final String rt_msg, final CIInquiryMealByPassangerResp mealByPassangerResp) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryMealPassenagerSuccess(rt_code, rt_msg, mealByPassangerResp);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryPassenagerError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryMealPassenagerError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIInquiryMealInfoModel.InquiryMealInfoCallBack m_MealInfoCallBack = new CIInquiryMealInfoModel.InquiryMealInfoCallBack() {
        @Override
        public void onInquiryMealInfoSuccess(final String rt_code, final String rt_msg, final CIInquirtMealInfoResp mealInfoResp) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryMealInfoSuccess(rt_code, rt_msg, mealInfoResp);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryMealInfoError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryMealInfoError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };
}
