package ci.function.Checkin;


import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashMap;

import ci.function.Base.BaseFragment;
import ci.function.Core.SLog;
import ci.ui.APIS.CIAPISFragment;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ShadowBar.ShadowBarScrollview;
import ci.ws.Models.entities.CIApisQryRespEntity;
import ci.ws.Models.entities.CICheckInApisEntity;
import ci.ws.Models.entities.CICheckInDocaEntity;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CICheckInAllPaxResp;

/**
 * Created by Ryan on 16/3/10.
 */
public class CIInputAPISFragment extends BaseFragment {

    class viewHolder {

        FrameLayout     flayout = null;
        CIAPISFragment  fragment= null;
    }

    private class APISHolder {
        String strName;
        String strFirstName;
        String strLastName;
//        CIApisEntity Apis;

        CICheckInApisEntity Apis;
        CICheckInDocaEntity Doca;
        //2018-08-28 新增五大航線組合，故直接將航線資訊傳入
        ArrayList<CICheckInPax_ItineraryInfoEntity> m_Itinerary_InfoList;
        ArrayList<CIApisQryRespEntity.ApisRespDocObj> m_arApisRespDocObj;
    }

    private ShadowBarScrollview     m_shadowScrollView  = null;
    private ScrollView              m_ScrollView        = null;
    private LinearLayout            m_llayout_Content   = null;
    private ProgressBar             m_progressBar       = null;
    //假資料
    private ArrayList<APISHolder>   m_arAPISList        = null;
    private ArrayList<viewHolder>   m_arAPISHolderList  = null;

//    private boolean                 m_bArrivalUSA       = false;
//    private String                  m_strArrivalStation = null;

    /**2018-08-22 調整航線邏輯*/
    private CIAPISDef.CIRouteType   m_enRouteType           = CIAPISDef.CIRouteType.normal;

    //錯誤訊息，調整為依內部資料格式為判斷
    private String          m_strErrorMsg           = "";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_checkin_input_apis;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_shadowScrollView  = (ShadowBarScrollview)view.findViewById(R.id.shadowlayout);
        m_progressBar       = (ProgressBar)view.findViewById(R.id.progress_bar);
        m_ScrollView        = m_shadowScrollView.getScrollView();
        m_llayout_Content   = m_shadowScrollView.getContentView();
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

    }

    @Override
    protected void setOnParameterAndListener(View view) {

    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {

    }

    @Override
    public void onLanguageChangeUpdateUI() {

    }

    private void onCreateAPISViews() {
        if( null == m_arAPISHolderList ) {
            m_arAPISHolderList = new ArrayList<>();

            int isize = m_arAPISList.size();
            for ( APISHolder apisHolder : m_arAPISList ){

                viewHolder vholder = new viewHolder();
                vholder.flayout = new FrameLayout(getActivity());
                vholder.flayout.setId(isize++);
                //2018-08-28 調整傳入資料的方式，統一使用 bundle 傳遞
                //vholder.fragment= new CIAPISFragment( apisHolder.strFirstName, apisHolder.strLastName, apisHolder.Apis, apisHolder.Doca,m_strArrivalStation ,m_bArrivalUSA);
                //
                vholder.fragment = new CIAPISFragment();
                Bundle args = new Bundle();
                args.putString( CIAPISFragment.BUNDLE_PARA_FIRSTNAME,           apisHolder.strFirstName);
                args.putString( CIAPISFragment.BUNDLE_PARA_LASTNAME,            apisHolder.strLastName);
                args.putSerializable( CIAPISFragment.BUNDLE_PARA_APIS,          apisHolder.Apis);
                args.putSerializable( CIAPISFragment.BUNDLE_PARA_DOCA,          apisHolder.Doca);
                args.putSerializable( CIAPISFragment.BUNDLE_SAVED_APIS,         apisHolder.m_arApisRespDocObj);
                args.putSerializable( CIAPISFragment.BUNDLE_PARA_ITINERARY_INFO,apisHolder.m_Itinerary_InfoList);
                vholder.fragment.setArguments(args);
                //
                m_arAPISHolderList.add(vholder);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                m_llayout_Content.addView(vholder.flayout, params);
            }


            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            for ( viewHolder vholder : m_arAPISHolderList ){
                fragmentTransaction.replace( vholder.flayout.getId(), vholder.fragment, vholder.fragment.toString() );
            }

            fragmentTransaction.commitAllowingStateLoss();
            //確保滑動到最上方
            m_ScrollView.smoothScrollTo(0, 0);
            m_progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        //Check if the superclass already created the animation
        Animation anim = super.onCreateAnimation(transit, enter, nextAnim);

        //If not, and an animation is defined, load it now
        if (anim == null && nextAnim != 0) {
            anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        }

        //If there is an animation for this fragment, add a listener.
        if (anim != null) {
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    onCreateAPISViews();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        return anim;
    }

    /**2018-8-23 新增一介面<
     * 增加航線邏輯的判斷*/
    public void setPassengerInfoList(CICheckInAllPaxResp CheckInResp, CIAPISDef.CIRouteType enRouteType ) {

        m_enRouteType = enRouteType;

        m_arAPISList = new ArrayList<>();

        if( null == CheckInResp ) {
            return;
        }

        for(CICheckInPax_InfoEntity entity : CheckInResp ) {
            if( null == entity.m_Itinerary_InfoList || 0 >= entity.m_Itinerary_InfoList.size() ) {
                continue;
            }

            APISHolder apis = new APISHolder();
            apis.strName = entity.First_Name + " " + entity.Last_Name;
            apis.strFirstName = entity.First_Name;
            apis.strLastName = entity.Last_Name;
            apis.Apis = getApis(entity.m_Itinerary_InfoList.get(0).Apis);
            apis.Doca = getDoca(entity.m_Itinerary_InfoList.get(0).Doca);
            //
            apis.m_Itinerary_InfoList = entity.m_Itinerary_InfoList;
            //
            m_arAPISList.add(apis);

            //final int iItinereryPos = entity.m_Itinerary_InfoList.size() - 1;
            //m_strArrivalStation = entity.m_Itinerary_InfoList.get(iItinereryPos).Arrival_Station;
        }

    }

    public void setSavedAPIS(CIApisQryRespEntity savedAPISEntity) {
        if (savedAPISEntity == null) {
            return;
        }

        for (APISHolder apisEntity: m_arAPISList) {
            for (CIApisQryRespEntity.CIApispaxInfo paxEntity: savedAPISEntity.paxInfo) {
                if (apisEntity.strFirstName.equals(paxEntity.firstName) &&
                        apisEntity.strLastName.equals(paxEntity.lastName)) {
                    apisEntity.m_arApisRespDocObj = paxEntity.documentInfos;
                    SLog.d("apisEntity.m_arApisRespDocObj: "+apisEntity.m_arApisRespDocObj.size());
                    break;
                }else{
                    if (m_arAPISList.indexOf(apisEntity) == m_arAPISList.size()-1) {
                        apisEntity.m_arApisRespDocObj =null;
                    }else{
                        continue;
                    }
                }
            }
        }

    }

    public void setPassengerInfoList(CICheckInAllPaxResp CheckInResp, boolean bArrivalUSA) {

        //m_bArrivalUSA = bArrivalUSA;

        m_arAPISList = new ArrayList<>();

        if( null == CheckInResp ) {
            return;
        }

        for(CICheckInPax_InfoEntity entity : CheckInResp ) {
            if( null == entity.m_Itinerary_InfoList || 0 >= entity.m_Itinerary_InfoList.size() ) {
                continue;
            }

            APISHolder apis = new APISHolder();
            apis.strName = entity.First_Name + " " + entity.Last_Name;
            apis.strFirstName = entity.First_Name;
            apis.strLastName = entity.Last_Name;
            apis.Apis = getApis(entity.m_Itinerary_InfoList.get(0).Apis);
             apis.Doca = getDoca(entity.m_Itinerary_InfoList.get(0).Doca);
            m_arAPISList.add(apis);

            //final int iItinereryPos = entity.m_Itinerary_InfoList.size() - 1;
            //m_strArrivalStation = entity.m_Itinerary_InfoList.get(iItinereryPos).Arrival_Station;
        }

    }

    private CICheckInDocaEntity getDoca(ArrayList<CICheckInPax_ItineraryInfoEntity.Doca> arDoca) {

        if( null == arDoca || 0 >= arDoca.size() ) {
            return null;
        }

        CICheckInDocaEntity DocaEntity = new CICheckInDocaEntity();

        CICheckInPax_ItineraryInfoEntity.Doca doca = arDoca.get(0);

        DocaEntity.Traveler_City = doca.Traveler_City;
        DocaEntity.Country_Sub_Entity = doca.Country_Sub_Entity;
        DocaEntity.Traveler_Address = doca.Traveler_Address;
        DocaEntity.Traveler_Postcode = doca.Traveler_Postcode;

        return DocaEntity;
    }

    private CICheckInApisEntity getApis(ArrayList<CICheckInPax_ItineraryInfoEntity.Apis> arApis) {

        if( null == arApis || 0 >= arApis.size() ) {
            return null;
        }

        //2016-09-28 修正邏輯改為只抓護照類別的APIS
        //CICheckInPax_ItineraryInfoEntity.Apis entity = arApis.get(0);
        CICheckInPax_ItineraryInfoEntity.Apis tmpAPIS = null;
        CICheckInPax_ItineraryInfoEntity.Apis entity = null;
        for ( CICheckInPax_ItineraryInfoEntity.Apis apis : arApis ){
            //先暫時把第一筆APIS 保留下來, 如果沒有找到護照資訊則把第一筆資料都給ui
            if ( null == tmpAPIS ){
                tmpAPIS = apis;
            }
            if ( TextUtils.equals("P", apis.Document_Type ) ){
                entity = apis;
                break;
            }
        }
        //整個Array 檢查完, 還是沒有 護照APIS 則把剛剛保留的第一筆資料丟給ui
        if ( null == entity && null != tmpAPIS ){
            entity = tmpAPIS;
        }

        CICheckInApisEntity apisEntity = new CICheckInApisEntity();

        if ( null == entity ){

            apisEntity.Document_Type    = "";
            apisEntity.Pax_Sex          = "";
            apisEntity.Pax_Birth        = "";
            apisEntity.Resident_Country = "";
            apisEntity.Nationality      = "";
            apisEntity.Document_No      = "";
            apisEntity.Docuemnt_Eff_Date = "";
            apisEntity.Issue_Country    = "";

        } else {

            apisEntity.Pax_Sex = entity.Pax_Sex;
            apisEntity.Pax_Birth = entity.Pax_Birth;
            if( null == entity.Resident_Country || 0 == entity.Resident_Country.size() ) {
                apisEntity.Resident_Country = "";
            } else {
                apisEntity.Resident_Country = entity.Resident_Country.get(0);
            }
            apisEntity.Nationality = entity.Nationality;
            apisEntity.Document_Type = entity.Document_Type;
            apisEntity.Document_No = entity.Document_No;
            apisEntity.Docuemnt_Eff_Date = entity.Docuemnt_Eff_Date;
            apisEntity.Issue_Country = entity.Issue_Country;
        }

        //
        if ( TextUtils.isEmpty( apisEntity.Document_Type ) ){

            apisEntity.Document_Type = "P";
            apisEntity.Resident_Country = "TWN";
            apisEntity.Nationality = "TWN";
        }

        return apisEntity;
    }

    public boolean isFillCompleteAndCorrect() {

        if( null == m_arAPISHolderList ) {
            return false;
        }

        for( viewHolder holder : m_arAPISHolderList ) {
            if( false == holder.fragment.isFillCompleteAndCorrect() ) {
                m_strErrorMsg = holder.fragment.getErrorMsg();
                return false;
            }
        }

        return true;
    }

    /**錯誤訊息*/
    public String getErrorMsg(){ return  m_strErrorMsg; }

    public ArrayList<HashMap<String,Object>> getInputApisList() {

        ArrayList<HashMap<String,Object>> arApis = new ArrayList<>();

        for( viewHolder holder : m_arAPISHolderList ) {
            arApis.add( holder.fragment.getInputAPIS() );
        }

        return arApis;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//    }

}
