package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ci.ws.Models.CIDeleteAPISModel;
import ci.ws.Models.CIInquiryApisListModel;
import ci.ws.Models.CIInsertAPISModel;
import ci.ws.Models.CIInsertUpdateAPISModel;
import ci.ws.Models.CIUpdateAPISModel;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Models.entities.CIApisDocmuntTypeList;
import ci.ws.Models.entities.CIApisEntity;
import ci.ws.Models.entities.CIApisNationalEntity;
import ci.ws.Models.entities.CIApisNationalList;
import ci.ws.Models.entities.CIApisQryRespEntity;
import ci.ws.Models.entities.CIApisResp;
import ci.ws.Models.entities.CIApisStateEntity;
import ci.ws.Models.entities.CICheckInEditAPIS_Resp;
import ci.ws.Models.entities.CICompanionApisEntity;
import ci.ws.Models.entities.CICompanionApisNameEntity;
import ci.ws.Models.entities.CIApisAddEntity;
import ci.ws.Presenter.Listener.CIInquiryApisListListener;
import ci.ws.define.CIWSResultCode;

/**
 * Created by joannyang on 16/5/23.
 */
public class CIAPISPresenter {

    private CIInquiryApisListListener   m_Listener = null;
    private CIInquiryApisListModel      m_ApisModel = null;
    private CIInsertAPISModel           m_insertApisModel = null;
    private CIUpdateAPISModel           m_UpdateApisModel = null;
    private CIDeleteAPISModel           m_DeleteApisModel = null;
    private CIInsertUpdateAPISModel     m_InsertUpdateApisModel = null;
    private static CIAPISPresenter s_Instance = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIAPISPresenter getInstance(  ){

        if ( null == s_Instance){
            s_Instance = new CIAPISPresenter();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }



        return s_Instance;
    }


    private void setCallbackListener( CIInquiryApisListListener listener ){
        this.m_Listener = listener;
    }

    public CIAPISPresenter() { }

//    /**取得Apis列表*/
//    public void InquiryMyApisListFromWS( String strCardNo,CIInquiryApisListListener listener ){
//
//        if ( null == m_ApisModel ){
//            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
//        }
//
//        s_Instance.setCallbackListener(listener);
//
//        m_ApisModel.InquiryApisFromWS(strCardNo);
//        if(null != m_Listener){
//            m_Listener.showProgress();
//        }
//    }

    /**取得Apis列表 201907 */
    public void InquiryMyApisListNewFromWS( String strCardNo,CIInquiryApisListListener listener ){

        if ( null == m_ApisModel ){
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }

        s_Instance.setCallbackListener(listener);

        m_ApisModel.InquiryApisNewFromWS(strCardNo);
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    public void InsertApisFromWS( String strCardNo,CIInquiryApisListListener listener , CIApisAddEntity ciApisEntity ) {

        if( null == m_insertApisModel ) {
            m_insertApisModel = new CIInsertAPISModel(m_insertCallback);
        }
        s_Instance.setCallbackListener(listener);

        m_insertApisModel.InsertApisFromWS(strCardNo, ciApisEntity);
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    public void UpdateApisFromWS( String strCardNo,CIInquiryApisListListener listener , CIApisAddEntity ciApisEntity ) {
        if( null == m_UpdateApisModel ) {
            m_UpdateApisModel = new CIUpdateAPISModel(m_updateCallback);
        }
        s_Instance.setCallbackListener(listener);

        m_UpdateApisModel.UpdateApisFromWS(strCardNo, ciApisEntity);
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    public void InsertUpdateApisFromWS( String strCardNo, CIInquiryApisListListener listener , CIApisAddEntity ciApisEntity ) {
        if( null == m_InsertUpdateApisModel ) {
            m_InsertUpdateApisModel = new CIInsertUpdateAPISModel(m_InsertUpdateCallback);
        }

        s_Instance.setCallbackListener(listener);

        m_InsertUpdateApisModel.InsertUpdateApisFromWS(strCardNo, ciApisEntity);
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    public void DeleteApisFromWS( String strCardNo,CIInquiryApisListListener listener , CIApisAddEntity ciApisEntity ) {
        if( null == m_DeleteApisModel ) {
            m_DeleteApisModel = new CIDeleteAPISModel(m_deleteCallback);
        }
        s_Instance.setCallbackListener(listener);
        m_DeleteApisModel.DeleteApisFromWS(strCardNo, ciApisEntity);
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }
//    public void DeleteApisFromWS(String strCardNo, String strDocType, CIInquiryApisListListener listener) {
//        if( null == m_DeleteApisModel ) {
//            m_DeleteApisModel = new CIDeleteAPISModel(m_deleteCallback);
//        }
//
//        s_Instance.setCallbackListener(listener);
//
//        m_DeleteApisModel.DeleteApisFromWS(strCardNo, strDocType);
//        if(null != m_Listener){
//            m_Listener.showProgress();
//        }
//    }

    /**取消WS*/
    public void InquiryApisListCancel(){
        if ( null != m_ApisModel ){
            m_ApisModel.CancelRequest();
        }
    }

    /**取得本地檔案護照名稱*/
    public CIApisDocmuntTypeList fetchApisList(){
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }
        return m_ApisModel.findDocmuntTypeData();
    }

    /**取得本地檔案所有的APIS證件種類*/
    public CIApisDocmuntTypeList fetchAllApisList(){
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }
        return m_ApisModel.findAllDocmuntTypeData();
    }

    /**取得本地檔案所有的APIS證件種類
     * Key = 證件type, ex 護照 = Ｐ*/
    public HashMap<String, CIApisDocmuntTypeEntity> fetchApisDocmuntMap(){

        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }
        return m_ApisModel.getApisDocmuntMap();
    }

    /**取得本地檔案 "APISNationalList.json" 國家名稱*/
    public CIApisNationalList fetchApisNationalList(){
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }
        return m_ApisModel.findNationListData();
    }

    /**取得本地檔案 "APISNationalList.json" 國家名稱*/
    public HashMap<String, CIApisNationalEntity> fetchApisNationalMap(){
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }
        return m_ApisModel.getApisNationalMap();
    }

    /**取得本地檔案 "APISNationalList.json" 國家名稱*/
    public HashMap<String, CIApisNationalEntity> fetchApisNationalResidentMap(){
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }
        return m_ApisModel.getApisNationalResidentMap();
    }

    /**取得本地檔案 "APISNationalList.json" 國家名稱*/
    public HashMap<String, CIApisNationalEntity> fetchApisNationalIssueMap(){
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }
        return m_ApisModel.getApisNationalIssueMap();
    }

    /**取得本地檔案 "APISState.json" 州別名稱*/
    public ArrayList<CIApisStateEntity> fetchApisStateList() {

        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }

        return m_ApisModel.findStateListData();
    }

    /**取得本地檔案 "APISState.json" 州別名稱*/
    public HashMap<String,CIApisStateEntity> fetchApisStateMap() {
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }

        return m_ApisModel.getApisStateMap();
    }

    public ArrayList<CIApisEntity> getMyApisListFromDB(String strCardNo) {
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }
        return m_ApisModel.getMyApisListFromDB(strCardNo);
    }

    public HashSet<String> getMyApisExistDocumentTypeList(String strCardNo) {

        ArrayList<CIApisEntity> arApisList = getMyApisListFromDB(strCardNo);

        if( null != arApisList ) {

            HashSet<String> documentTypeList = new HashSet<>();
            for(CIApisEntity entity : arApisList ) {
                if( !TextUtils.isEmpty(entity.doc_type) ) {
                    documentTypeList.add(entity.doc_type);
                }
            }

            return documentTypeList;
        } else {
            return null;
        }
    }

    public ArrayList<CICompanionApisEntity> getCompanionsApisListWithFullName(String strCardNo, String strFullName) {
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }
        return m_ApisModel.getCompanionApisList(strCardNo,strFullName);
    }

    public void deleteMyApis(CIApisEntity data) {
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }

        m_ApisModel.deleteMyApisData(data);
    }
    public void saveMyApisList(String strCardNo, ArrayList<CIApisEntity> ar_myApisList) {
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }

        m_ApisModel.saveMyAPIS(strCardNo, ar_myApisList);
    }

    public void saveMyApis(CIApisEntity data) {
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }

        m_ApisModel.saveMyAPIS(data);
    }

    public void saveCompanionApis(CICompanionApisEntity data) {
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }

        m_ApisModel.saveCompanionApis(data);
    }

    public void deleteCompanionApis(CICompanionApisEntity data) {
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }

        m_ApisModel.deleteCompanionApisData(data);
    }

    public ArrayList<CICompanionApisNameEntity> getCompanionApisList(String strCardNo) {
        if( null == m_ApisModel ) {
            m_ApisModel = new CIInquiryApisListModel(m_modelCallback);
        }

        return m_ApisModel.getCompanionApisList(strCardNo);
    }



    CIInquiryApisListModel.InquiryApisListCallBack m_modelCallback = new CIInquiryApisListModel.InquiryApisListCallBack() {
        @Override
        public void onInquiryApisListNewSuccess(final String rt_code, final String rt_msg, final CIApisQryRespEntity apis) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ){
                        m_Listener.InquiryApisSuccess(rt_code, rt_msg, apis);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
        @Override
        public void onInquiryApisListSuccess(final String rt_code, final String rt_msg, final CIApisResp apis) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ){
                        //m_Listener.InquiryApisSuccess(rt_code, rt_msg, apis);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryApisListError(final String rt_code, final String rt_msg) {

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
                                m_Listener.InquiryApisError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIInsertAPISModel.InsertApisCallBack m_insertCallback = new CIInsertAPISModel.InsertApisCallBack() {
        @Override
        public void onInsertApisSuccess(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.InsertApidSuccess(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInsertApisError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ){
                        switch (rt_code){
                            case CIWSResultCode.ERROR_LOGOUT_AUTOMATICALLY:
                            case CIWSResultCode.ERROR_AUTHORIZATION_FAILED:
                                m_Listener.onAuthorizationFailedError(rt_code, rt_msg);
                                break;
                            default:
                                m_Listener.InsertApisError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIUpdateAPISModel.UpdateApisCallBack m_updateCallback = new CIUpdateAPISModel.UpdateApisCallBack() {
        @Override
        public void onUpdateApisSuccess( final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ){
                        m_Listener.UpdateApisSuccess(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onUpdateApisError( final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ){
                        m_Listener.UpdateApisError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIInsertUpdateAPISModel.InsertUpdateApisCallBack m_InsertUpdateCallback = new CIInsertUpdateAPISModel.InsertUpdateApisCallBack() {
        @Override
        public void onInsertUpdateApisSuccess( final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ){
                        m_Listener.InsertUpdateApisSuccess(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInsertUpdateApisError( final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ){
                        m_Listener.InsertUpdateApisError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIDeleteAPISModel.DeleteApisCallBack m_deleteCallback = new CIDeleteAPISModel.DeleteApisCallBack() {
        @Override
        public void onDeleteApisSuccess( final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ){
                        m_Listener.DeleteApisSuccess(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onDeleteApisError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ){
                        m_Listener.DeleteApisError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };
}
