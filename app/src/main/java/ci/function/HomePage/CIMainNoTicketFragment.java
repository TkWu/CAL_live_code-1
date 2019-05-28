package ci.function.HomePage;


import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import ci.function.Core.SLog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.chinaairlines.mobile30.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Login.CILoginActivity;
import ci.ui.WebView.CIWithoutInternetActivity;
import ci.ui.define.HomePage_Status;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIProgressDialog;
import ci.ui.object.item.CIHomeStatusEntity;
import ci.ws.Models.entities.CITripListResp_Itinerary;

/**
 * 首頁狀態A,B 沒購票或有購票
 * Created by Ryan on 2016/2/19.
 */
public class CIMainNoTicketFragment extends BaseFragment {

    public interface OnMainNoTicketFragmentListener{
        //按下“其他行程“, 將開啟 行程管理 頁面 - by Ling
        void OnMyTripsClick();
        void OnMyTripsNoBoardClick();
    }
    private OnMainNoTicketFragmentListener m_Listener;

    private CIMainInfoView  m_vInfoView     = null;
    private LinearLayout    m_llayout_bg    = null;

    private View        m_vButtonView = null;
    private Button      m_btnTop      = null;
    private Button      m_btnBottom   = null;

    private CIProgressDialog m_proDlg = null;

    private CIHomeStatusEntity  m_HomeStatusEntity = null;
    private CITripListResp_Itinerary m_PNRItinerary_Info = null;
    private Boolean m_bonCreateOK = false;
    private Context m_context         = CIApplication.getContext();
    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_main_noticket_view;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_llayout_bg = (LinearLayout)view.findViewById(R.id.llayout_bg);
        m_vInfoView = (CIMainInfoView)view.findViewById(R.id.vInfoView);

        m_vButtonView = view.findViewById(R.id.btn_my_booking);
        m_btnTop = (Button)m_vButtonView.findViewById(R.id.btnTop);
        m_btnBottom = (Button)m_vButtonView.findViewById(R.id.btnBottom);
        m_btnTop.setOnClickListener(m_onClick);
        m_btnBottom.setOnClickListener(m_onClick);

        m_bonCreateOK = true;
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        m_llayout_bg.getLayoutParams().height = vScaleDef.getLayoutHeight(480);

        ((LinearLayout.LayoutParams)m_vButtonView.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(18);
        ((LinearLayout.LayoutParams)m_btnBottom.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(20);
        m_btnTop.getLayoutParams().height   = vScaleDef.getLayoutHeight(40);
        m_btnTop.getLayoutParams().width    = vScaleDef.getLayoutWidth(260);
        //由於此button的框線線寬1dp且屬性為outside, 所以長寬要包含線寬1dp
        m_btnBottom.getLayoutParams().height= vScaleDef.getLayoutHeight(41);
        m_btnBottom.getLayoutParams().width = vScaleDef.getLayoutWidth(261);

        vScaleDef.setTextSize(16, m_btnTop);
        vScaleDef.setTextSize(16, m_btnBottom);
    }

    @Override
    protected void setOnParameterAndListener(View view) {

        if ( null == m_HomeStatusEntity ){
            m_HomeStatusEntity = new CIHomeStatusEntity();
        }

        HomeStatusUpdate();
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    protected void onFragmentResume() {
        HomeStatusUpdate();
    }

    public void setHomePageStatus( CIHomeStatusEntity StatusEntity ){

        m_HomeStatusEntity = StatusEntity;
        //2016-06-29 ryan for 直接抓取外面過濾好的CPR以及PNR資料,
        if ( null != StatusEntity ){
            m_PNRItinerary_Info = StatusEntity.Itinerary_Info;
        } else {
            m_PNRItinerary_Info = null;
        }
        if ( null == m_HomeStatusEntity ){
            m_HomeStatusEntity = new CIHomeStatusEntity();
        }

        if ( m_bonCreateOK ){
            HomeStatusUpdate();
        }
    }

    private void HomeStatusUpdate(){

        if ( null != m_HomeStatusEntity && m_HomeStatusEntity.iStatus_Code == HomePage_Status.TYPE_B_HAVE_TICKET ){

            String strArrival = "";
            String strDate = "";
            if ( null != m_PNRItinerary_Info ){
                strArrival = m_PNRItinerary_Info.Arrival_Station_Name;
                //計算旅程開始時間
                //2016-11-15 Modify by Ryan for 調整PNR時間邏輯, 不再抓取CPR的時間來更新, 調整為抓取DISPLAY TAG
                String strDMT = m_PNRItinerary_Info.getDisplayDepartureDate_GMT() + " " + m_PNRItinerary_Info.getDisplayDepartureTime_GMT();
                //String strDMT = m_PNRItinerary_Info.Departure_Date_Gmt + " " + m_PNRItinerary_Info.Departure_Time_Gmt;
                if ( strDMT.length() > 0 ){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));// 因取得的時間是GMT+0時區的時間，所以需調整 by kevin
                    try {
                        Date dGMT = sdf.parse(strDMT);
                        Date dNow = new Date(System.currentTimeMillis());

                        long ldff = dGMT.getTime() - dNow.getTime();
                        long days   = ldff / (1000 * 60 * 60 * 24 );
                        strDate = String.format(" %d %s", days, m_context.getString(R.string.day));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            m_vInfoView.setIcon(R.drawable.ic_homepage_b);
            m_vInfoView.setTitleText(String.format(m_context.getString(R.string.home_page_have_ticket_long_time_ago), strArrival, strDate));

            m_btnTop.setText(R.string.home_page_button_view_my_booking);
        } else {

            m_vInfoView.setIcon(R.drawable.ic_homepage_a);
            if ( isAdded() ){
                m_vInfoView.setTitleText(m_context.getString(R.string.home_page_already_have_a_trip));
            }

            m_btnTop.setText(R.string.home_page_button_find_my_booking);
        }

        m_btnBottom.setText(R.string.home_page_explore_destinations);
    }

    View.OnClickListener m_onClick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            //是否已登入
            String strMode = CILoginActivity.LoginMode.FIND_MYBOOKING_MEMBER.name();
            if ( true == CIApplication.getLoginInfo().GetLoginStatus() &&
                    CIApplication.getLoginInfo().GetLoginType().equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)){
                strMode = CILoginActivity.LoginMode.FIND_MYBOOKING_ONLY_RETRIEVE.name();
            }

            switch (v.getId()){
                case R.id.btnTop:
                    if ( null == m_Listener ){
                        return;
                    }

                    //是否有行程
                    if ( m_HomeStatusEntity.iStatus_Code == HomePage_Status.TYPE_A_NO_TICKET ) {
                        m_Listener.OnMyTripsNoBoardClick();
                    }else {
                        m_Listener.OnMyTripsClick();
                    }
                    break;
                case R.id.btnBottom:
                    Intent intent = new Intent();
                    intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, m_context.getString(R.string.home_page_explore_destinations));
                    intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, getDiscoveryUrl());
                    intent.setClass(getActivity(), CIWithoutInternetActivity.class);
                    startActivity(intent);

                    getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);

                    break;
            }
        }
    };

    public void uiSetParameterListener(OnMainNoTicketFragmentListener onListener) {
        m_Listener = onListener;
    }

    public static String getDiscoveryUrl(){
        /**
         * 深入探索 url 如下
         * ex：https://calec.china-airlines.com/Rec/RecForm?language=zh-TW&cardno=WB0000000
         * 未登入, 則不帶 &cardno=WB0000000, 有登入才需要帶
         * */
        Map<String, String> m_mapParams = new LinkedHashMap<>();

        String url ="https://calec.china-airlines.com/Rec/RecForm?";

        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
        switch (locale.toString()){
            case "zh_TW":
                m_mapParams.put("language" , "zh-TW");
                break;
            case "zh_CN":
                m_mapParams.put("language" , "CN");
                break;
            case "en":
                m_mapParams.put("language" , "EN");
                break;
            case "ja_JP":
                m_mapParams.put("language" , "JP");
                break;
            default:
                m_mapParams.put("language" , "EN");
                break;
        }

        boolean bLogin = CIApplication.getLoginInfo().GetLoginStatus();
        String  strLoginType = CIApplication.getLoginInfo().GetLoginType();
        String  strCardNo = CIApplication.getLoginInfo().GetUserMemberCardNo();

        if(true == bLogin && TextUtils.equals(strLoginType,UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)
                && !TextUtils.isEmpty(strCardNo)){
            m_mapParams.put("cardno" , strCardNo);
        }


        Iterator<Map.Entry<String, String>> iter = m_mapParams.entrySet().iterator();
        int count = 0;
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            if(0 == count){
                url+= key+"="+val;
            } else {
                url+= "&"+key+"="+val;
            }
            count++;
        }
        //Log.e("Discovery Url",url);
        return url;
    }

}
