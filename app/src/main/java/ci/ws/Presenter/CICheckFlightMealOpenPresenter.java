package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CICheckFlightMealOpenModel;
import ci.ws.Models.entities.CICheckFlightMealOpenReq;
import ci.ws.Presenter.Listener.CICheckFlightMealOpenListener;

/**
 * Created by JL Chen on 2016/05/19.
 * 功能說明:依航班檢查是否開放預訂餐點。
 */
public class CICheckFlightMealOpenPresenter {

    private CICheckFlightMealOpenListener           m_Listener          = null;
    private CICheckFlightMealOpenModel              m_Model             = null;
    private static CICheckFlightMealOpenPresenter   s_Instance          = null;
    private static Handler                          s_hdUIThreadhandler = null;

    public static CICheckFlightMealOpenPresenter getInstance(
            CICheckFlightMealOpenListener listener ){

        if ( null == s_Instance ){
            s_Instance          = new CICheckFlightMealOpenPresenter(listener);
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        s_Instance.setCallbackListener(listener);

        return s_Instance;
    }

    private void setCallbackListener( CICheckFlightMealOpenListener listener ){
        this.m_Listener = listener;
    }

    public CICheckFlightMealOpenPresenter( CICheckFlightMealOpenListener listener ){
        this.m_Listener = listener;
    }

    public void CheckFlightMealOpenFromWS( CICheckFlightMealOpenReq reqData ){
        if(null == m_Model){
            m_Model = new CICheckFlightMealOpenModel(m_Callback);
        }

        if(null != m_Listener){
            m_Listener.showProgress();
        }

        m_Model.sendReqDataToWS(reqData);
    }

    /**取消equest*/
    public void CheckFlightMealOpenCancel(){
        if ( null != m_Model){
            m_Model.CancelRequest();
        }
        if ( null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    private CICheckFlightMealOpenModel.CallBack m_Callback = new CICheckFlightMealOpenModel.CallBack() {
        @Override
        public void onSuccess(final String rt_code, final String rt_msg) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_Listener){
                        m_Listener.hideProgress();
                        m_Listener.onSuccess(rt_code, rt_msg);
                    }
                }
            });
        }

        @Override
        public void onError(final String rt_code, final String rt_msg) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_Listener) {
                        m_Listener.hideProgress();
                        m_Listener.onError(rt_code, rt_msg);
                    }
                }
            });
        }
    };
}
