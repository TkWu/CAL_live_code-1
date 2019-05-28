package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIDeleteOrderMealModel;
import ci.ws.Models.CIInsertOrderMealModel;
import ci.ws.Models.entities.CIDeleteOrderMealReq;
import ci.ws.Models.entities.CIInsertOrderMealReq;
import ci.ws.Presenter.Listener.CIHandleOrderMealListener;

/**
 * Created by kevin cheng on 16/5/19.
 * 功能說明:新增餐點訂單、刪除餐點訂單。
 */
public class CIHandleOrderMealPresenter {

    private CIHandleOrderMealListener m_Listener = null;
    private CIInsertOrderMealModel      m_InsertOrderMealModel  = null;
    private CIDeleteOrderMealModel      m_DeleteOrderMealModel  = null;
    private static CIHandleOrderMealPresenter s_Instance        = null;
    private static Handler              s_hdUIThreadhandler     = null;

    public static CIHandleOrderMealPresenter getInstance(CIHandleOrderMealListener listener ){

        if ( null == s_Instance){
            s_Instance = new CIHandleOrderMealPresenter(listener);
        }

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        s_Instance.setCallbackListener(listener);

        return s_Instance;
    }

    private void setCallbackListener( CIHandleOrderMealListener listener ){
        this.m_Listener = listener;
    }

    public CIHandleOrderMealPresenter(CIHandleOrderMealListener listener ){
        this.m_Listener = listener;
    }

    /**新增餐點訂單*/
    public void InsertOrderMealFromWS(CIInsertOrderMealReq reqData){
        if(null == m_InsertOrderMealModel){
            m_InsertOrderMealModel = new CIInsertOrderMealModel(insertOrderMealModelCallback);
        }
        m_InsertOrderMealModel.sendReqDataToWS(reqData);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**刪除餐點訂單*/
    public void DeleteOrderMealFromWS(CIDeleteOrderMealReq reqData){
        if(null == m_DeleteOrderMealModel){
            m_DeleteOrderMealModel = new CIDeleteOrderMealModel(deleteOrderMealModelCallback);
        }
        m_DeleteOrderMealModel.sendReqDataToWS(reqData);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    private CIDeleteOrderMealModel.CallBack deleteOrderMealModelCallback = new CIDeleteOrderMealModel.CallBack() {
        @Override
        public void onSuccess(final String rt_code, final String rt_msg) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_Listener){
                        m_Listener.hideProgress();
                        m_Listener.onDeleteOrderSuccess(rt_code, rt_msg);
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
                        m_Listener.onDeleteOrderError(rt_code, rt_msg);
                    }
                }
            });
        }
    };

    private CIInsertOrderMealModel.CallBack insertOrderMealModelCallback = new CIInsertOrderMealModel.CallBack() {
        @Override
        public void onSuccess(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_Listener) {
                        m_Listener.hideProgress();
                        m_Listener.onOrderSuccess(rt_code, rt_msg);
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
                        m_Listener.onOrderError(rt_code, rt_msg);
                    }
                }
            });
        }
    };
}
