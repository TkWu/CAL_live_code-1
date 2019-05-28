package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.ArrayList;

import ci.ws.Models.CIInquiryAddressModel;
import ci.ws.Models.entities.CICodeNameEntity;
import ci.ws.Presenter.Listener.CIInquiryAddressListner;

/**
 * Created by Ryan on 16/5/2.
 */
public class CIInquiryAddressPresenter {

    private static CIInquiryAddressPresenter m_Instance = null;

    private CIInquiryAddressModel   m_AddressModel = null;

    private CIInquiryAddressListner m_Listener = null;

    private static Handler s_hdUIThreadhandler = null;

    private String m_strCountryCode   = "";
    private String m_strCityCode      = "";
    private String m_strCountyCode    = "";
    private String m_strStreetCode    = "";

    public static CIInquiryAddressPresenter getInstance(CIInquiryAddressListner listener ){

        if ( null == m_Instance ){
            m_Instance = new CIInquiryAddressPresenter();
        }

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallbackListener(listener);

        return m_Instance;
    }

    public static CIInquiryAddressPresenter getInstance(){

        if ( null == m_Instance ){
            m_Instance = new CIInquiryAddressPresenter();
        }

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        return m_Instance;
    }

    private void setCallbackListener( CIInquiryAddressListner listener ){
        this.m_Listener = listener;
    }

//    public CIInquiryAddressPresenter(CIInquiryAddressListner listener){
//        m_Listener = listener;
//    }

    /**像Server取得國家清單列表*/
    public ArrayList<CICodeNameEntity> getNationalList(){

        if ( null == m_AddressModel ){
            m_AddressModel = new CIInquiryAddressModel(m_callback);
        }

        ArrayList<CICodeNameEntity> arDatalList = m_AddressModel.getNationalList();

        if ( null == arDatalList || arDatalList.size() <= 0 ){
            if( null != m_Listener ){
                m_Listener.showProgress();
            }
            m_AddressModel.clearAllData();
            m_AddressModel.getAddressFrowWS(CIInquiryAddressModel.NATIONAL, "", "", "", "", "");
        }

        return arDatalList;
    }

    /**像Server取得城市清單列表*/
    public ArrayList<CICodeNameEntity> getCityList(){

        if ( null == m_AddressModel ){
            m_AddressModel = new CIInquiryAddressModel(m_callback);
        }

        ArrayList<CICodeNameEntity> arDatalList = m_AddressModel.getCityList();

        if ( null == arDatalList || arDatalList.size() <= 0 ){
            if( null != m_Listener ){
                m_Listener.showProgress();
            }
            m_AddressModel.getAddressFrowWS(CIInquiryAddressModel.CITY, m_strCountryCode, "", "", "", "");
        }

        return arDatalList;
    }

    /**像Server取得區域縣市鄉鎮清單列表*/
    public ArrayList<CICodeNameEntity> getCountyList(){

        if ( null == m_AddressModel ){
            m_AddressModel = new CIInquiryAddressModel(m_callback);
        }

        ArrayList<CICodeNameEntity> arDatalList = m_AddressModel.getCountyList();

        if ( null == arDatalList || arDatalList.size() <= 0 ){
            if( null != m_Listener ){
                m_Listener.showProgress();
            }
            m_AddressModel.getAddressFrowWS(CIInquiryAddressModel.COUNTY, m_strCountryCode, m_strCityCode, "", "", "");
        }

        return arDatalList;
    }

    /**像Server取得路名列表*/
    public ArrayList<CICodeNameEntity> getStreetList(){

        if ( null == m_AddressModel ){
            m_AddressModel = new CIInquiryAddressModel(m_callback);
        }

        ArrayList<CICodeNameEntity> arDatalList = m_AddressModel.getStreetList();

        if ( null == arDatalList || arDatalList.size() <= 0 ){
            if( null != m_Listener ){
                m_Listener.showProgress();
            }
            m_AddressModel.getAddressFrowWS(CIInquiryAddressModel.STREET, m_strCountryCode, m_strCityCode, m_strCountyCode, "", "");
        }

        return arDatalList;
    }

    /**像Server取得郵遞區號清單列表*/
    public ArrayList<CICodeNameEntity> getZipCodeList(){

        if ( null == m_AddressModel ){
            m_AddressModel = new CIInquiryAddressModel(m_callback);
        }

        ArrayList<CICodeNameEntity> arDatalList = m_AddressModel.getZipCodeList();

        if ( null == arDatalList || arDatalList.size() <= 0 ){
            if( null != m_Listener ){
                m_Listener.showProgress();
            }
            m_AddressModel.getAddressFrowWS(CIInquiryAddressModel.ZIPCODE, m_strCountryCode, m_strCityCode, m_strCountyCode, m_strStreetCode, "");
        }

        return arDatalList;
    }

    /**像Server取得附近程式清單*/
    public ArrayList<CICodeNameEntity> getCurrAreaCityList(){

        if ( null == m_AddressModel ){
            m_AddressModel = new CIInquiryAddressModel(m_callback);
        }

        ArrayList<CICodeNameEntity> arDatalList = m_AddressModel.getCurrAreaList();

        if ( null == arDatalList || arDatalList.size() <= 0 ){
            if( null != m_Listener ){
                m_Listener.showProgress();
            }
            String strPara = CIInquiryAddressModel.COUNTY;
            if ( !TextUtils.equals("TW", m_strCountryCode) && !TextUtils.equals("CN", m_strCountryCode)  ){
                strPara = CIInquiryAddressModel.CITY;
            }
            m_AddressModel.getAddressFrowWS(strPara, m_strCountryCode, m_strCityCode, m_strCountyCode, m_strStreetCode, "");
        }

        return arDatalList;
    }

    public void interrupt(){
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_AddressModel ){
            m_AddressModel.CancelRequest();
        }
    }

    public void setCountryCode(String strCountryCode) {
        this.m_strCountryCode = strCountryCode;
    }

    public void setCityCode(String strCityCode) {
        this.m_strCityCode = strCityCode;
    }

    public void setCountyCode(String strCountyCode) {
        this.m_strCountyCode = strCountyCode;
    }

    public void setStreetCode(String strStreetCode) {
        this.m_strStreetCode = strStreetCode;
    }

    public void clearDatabyChangeNational(){
        if ( null == m_AddressModel ){
            m_AddressModel = new CIInquiryAddressModel(m_callback);
        }
        m_AddressModel.clearAllData();
        m_strCityCode = "";
        m_strCountyCode = "";
        m_strStreetCode = "";
    }


    public void clearDatabyChangeCity(){
        if ( null == m_AddressModel ){
            m_AddressModel = new CIInquiryAddressModel(m_callback);
        }
        if ( null != m_AddressModel.getCountyList() ){
            m_AddressModel.getCountyList().clear();
        }
        if ( null != m_AddressModel.getStreetList() ){
            m_AddressModel.getStreetList().clear();
        }
        if ( null != m_AddressModel.getZipCodeList() ){
            m_AddressModel.getZipCodeList().clear();
        }
        if ( null != m_AddressModel.getCurrAreaList() ){
            m_AddressModel.getCurrAreaList().clear();
        }

        m_strCountyCode = "";
        m_strStreetCode = "";
    }


    public void clearDatabyChangeCounty(){
        if ( null == m_AddressModel ){
            m_AddressModel = new CIInquiryAddressModel(m_callback);
        }
        if ( null != m_AddressModel.getStreetList() ){
            m_AddressModel.getStreetList().clear();
        }
        if ( null != m_AddressModel.getZipCodeList() ){
            m_AddressModel.getZipCodeList().clear();
        }
        if ( null != m_AddressModel.getCurrAreaList() ){
            m_AddressModel.getCurrAreaList().clear();
        }

        m_strStreetCode = "";
    }


    public void clearDatabyChangeStreet(){
        if ( null == m_AddressModel ){
            m_AddressModel = new CIInquiryAddressModel(m_callback);
        }
        if ( null != m_AddressModel.getZipCodeList() ){
            m_AddressModel.getZipCodeList().clear();
        }
    }


    CIInquiryAddressModel.InquiryCallback m_callback = new CIInquiryAddressModel.InquiryCallback() {

        @Override
        public void onSuccess(final String rt_code,final String rt_msg, final String strPara) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquirySuccess(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onError(final String rt_code,final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };

}
