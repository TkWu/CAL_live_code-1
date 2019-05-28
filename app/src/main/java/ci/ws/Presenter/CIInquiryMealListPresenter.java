package ci.ws.Presenter;

import java.util.HashMap;

import ci.ws.Models.CIInquiryMealListModel;
import ci.ws.Models.entities.CIMealEntity;
import ci.ws.Models.entities.CIMealList;
import ci.ws.Presenter.Listener.CIInquiryMealListListener;

/**
 * Created by Ryan on 16/4/25.
 * 功能說明:取的餐點偏好餐點列表。
 * 對應CI API : QueryNatMealList
 */
public class CIInquiryMealListPresenter {

    private CIInquiryMealListListener   m_Listener = null;
    private CIInquiryMealListModel      m_MealModel = null;
    private static CIInquiryMealListPresenter s_Instance = null;

    public static CIInquiryMealListPresenter getInstance( CIInquiryMealListListener listener ){

        if ( null == s_Instance){
            s_Instance = new CIInquiryMealListPresenter(listener);
        }

        s_Instance.setCallbackListener(listener);

        return s_Instance;
    }

    private void setCallbackListener( CIInquiryMealListListener listener ){
        this.m_Listener = listener;
    }

    public CIInquiryMealListPresenter( CIInquiryMealListListener listener ){
        this.m_Listener = listener;
    }

    /**2016-05-06停用*/
    /**取的餐點偏好餐點列表*/
    public void InquiryMealListFromWS(){

//        m_MealModel = new CIInquiryMealListModel(
//                CIApplication.getLanguageInfo().getWSLanguage(),
//                CIApplication.getDeviceInfo().getAndroidId(),
//                WSConfig.DEF_API_VERSION,
//                m_callback);
//
//        m_MealModel.DoConnection();
    }

    /**2016-05-06停用*/
    /**取消取得餐點的WS*/
    public void InquiryMealListCancel(){
//        if ( null != m_MealModel ){
//            m_MealModel.CancelRequest();
//        }
    }

    /**取得本地檔案餐點資料*/
    public CIMealList fetchMealList(){
        m_MealModel = new CIInquiryMealListModel();
        return m_MealModel.findData();
    }

    /**取得本地檔案餐點資料*/
    public HashMap<String, CIMealEntity> fetchMealMap(){
        m_MealModel = new CIInquiryMealListModel();
        return m_MealModel.getMealMap();
    }


//    private  CIInquiryMealListModel.MealLisCallBack m_callback = new CIInquiryMealListModel.MealLisCallBack() {
//        @Override
//        public void onInquiryMealListSuccess(String rt_code, String rt_msg, CIMealListResp mealListResp) {
//            if ( null != m_Listener ){
//                m_Listener.onInquiryMealListSuccess(rt_code, rt_msg, mealListResp);
//            }
//        }
//
//        @Override
//        public void onInquiryMealListError(String rt_code, String rt_msg) {
//            if ( null != m_Listener ){
//                m_Listener.onInquiryMealListError(rt_code, rt_msg);
//            }
//        }
//    };
}
