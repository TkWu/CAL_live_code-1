package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Set;

import ci.ws.Models.CIInquiryBoardingPassModel;
import ci.ws.Models.entities.CIBoardPassResp_Itinerary;
import ci.ws.Models.entities.CIInquiryBoardPassDBEntity;
import ci.ws.Models.entities.CIBoardPassResp;
import ci.ws.Presenter.Listener.CIInquiryBoardPassListener;

/**
 * Created by jlchen on 16/5/31.
 * 功能說明:使用Card No、PNR(List)取得Ewallet BoardingPass資訊
 */
public class CIInquiryBoardPassPresenter {

    private CIInquiryBoardPassListener          m_Listener          = null;
    private CIInquiryBoardingPassModel          m_Model             = null;
    private static CIInquiryBoardPassPresenter  s_Instance          = null;
    private static Handler                      s_hdUIThreadhandler = null;

    public static CIInquiryBoardPassPresenter getInstance(
            CIInquiryBoardPassListener listener ){

        if ( null == s_Instance){
            s_Instance = new CIInquiryBoardPassPresenter(listener);
        }

        if ( null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        s_Instance.setCallbackListener(listener);

        return s_Instance;
    }

    public CIInquiryBoardPassPresenter(CIInquiryBoardPassListener listener ){
        this.m_Listener = listener;
    }

    private void setCallbackListener( CIInquiryBoardPassListener listener ){
        this.m_Listener = listener;
    }

    /**依靠多個PNR or 卡號 查詢Boarding Pass資料*/
    public void InquiryBoardPassFromWSByPNRListAndCardNo(String strCard,
                                                         String strFirstName,
                                                         String strLastName,
                                                         Set<String> pnrData){
        if ( null != m_Listener){
            m_Listener.showProgress();
        }

        if ( null == m_Model){
            m_Model = new CIInquiryBoardingPassModel(ModelCallback);
        }
        //2018-06-29 第二階段CR 新增FirstName LastName 避免PNRId重複使用導致看到別人的資料
        m_Model.sendReqDataToWSByPNRListAndCardNo(strCard, strFirstName, strLastName, pnrData);
    }

    /**依靠機票號碼Ticket 查詢Boarding Pass資料*/
    public void InquiryBoardPassFromWSByTicket(String ticket,
                                               String strFirstName,
                                               String strLastName){
        if ( null != m_Listener){
            m_Listener.showProgress();
        }

        if ( null == m_Model){
            m_Model = new CIInquiryBoardingPassModel(ModelCallback);
        }
        m_Model.sendReqDataToWSByTicket(ticket, strFirstName, strLastName);
    }

    /**依靠機票號碼PNR No 查詢Boarding Pass資料*/
    public void InquiryBoardPassFromWSByPNRNo(String PNRNo,
                                              String strFirstName,
                                              String strLastName){
        if ( null != m_Listener){
            m_Listener.showProgress();
        }

        if ( null == m_Model){
            m_Model = new CIInquiryBoardingPassModel(ModelCallback);
        }
        m_Model.sendReqDataToWSByPNRNo(PNRNo, strFirstName, strLastName);
    }

    /**從DB取BoardingPass資料*/
    public ArrayList<CIBoardPassResp_Itinerary> InquiryBoardPassFromDB(){
        if ( null == m_Model){
            m_Model = new CIInquiryBoardingPassModel(ModelCallback);
        }
        return m_Model.getDataFromDB();
    }

    /**BoardingPass資料暫存至DB*/
    public void saveBoardPassDataToDB(CIInquiryBoardPassDBEntity entity){
        if ( null == m_Model){
            m_Model = new CIInquiryBoardingPassModel(ModelCallback);
        }
        m_Model.saveDataToDB(entity);
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

    private CIInquiryBoardingPassModel.CallBack ModelCallback
            = new CIInquiryBoardingPassModel.CallBack() {
        @Override
        public void onSuccess(final String rt_code,
                              final String rt_msg,
                              final CIBoardPassResp datas) {
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
