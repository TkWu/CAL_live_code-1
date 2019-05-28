package ci.function.HomePage;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.MyTrips.Detail.CIMyTripsDetialActivity;
import ci.ui.FlightCard.CIFlightCardView;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.item.CIHomeStatusEntity;

/**
 * 目前使用在首頁, 用來顯示當天的航班資訊
 * Created by Ryan on 2016/2/25.
 */
public class CIFlightInfoFragment extends BaseFragment implements View.OnClickListener{

    public interface OnFlightInfoListener {
        //按下“其他行程“, 將開啟 行程管理 頁面 - by Ling
        void OnMyTripsClick();
    }

    private OnFlightInfoListener m_Listener;

    private LinearLayout    m_llayoutInfoContent= null;
    private LinearLayout    m_llayoutOtherTrips = null;
    private TextView        m_tvOtherTrips      = null;
    private ImageView       m_imgArrow          = null;

    private ArrayList<CIFlightCardView> m_arInfoList  = null;

    private CIHomeStatusEntity m_HomeStatusEntity = null;
    private Boolean m_bonCreateOK = false;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_main_flight_info;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_llayoutInfoContent= (LinearLayout)view.findViewById(R.id.llayout_info_content);
        m_llayoutOtherTrips = (LinearLayout)view.findViewById(R.id.llayout_other_trips);
        m_tvOtherTrips      = (TextView)view.findViewById(R.id.tv_other_trips);
        m_imgArrow          = (ImageView)view.findViewById(R.id.img_arrow);
        m_llayoutOtherTrips.setOnClickListener(m_onClick);

        m_arInfoList = new ArrayList<>();
        m_arInfoList.clear();
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        m_llayoutOtherTrips.setPadding(
                vScaleDef.getLayoutWidth(40),
                vScaleDef.getLayoutHeight(10),
                0,
                vScaleDef.getLayoutHeight(40));
        ((LinearLayout.LayoutParams)m_tvOtherTrips.getLayoutParams()).rightMargin = vScaleDef.getLayoutWidth(4);
        vScaleDef.setTextSize( 16, m_tvOtherTrips );
        m_imgArrow.getLayoutParams().height = vScaleDef.getLayoutMinUnit(16);
        m_imgArrow.getLayoutParams().width  = vScaleDef.getLayoutMinUnit(16);

        initialTripCard(vScaleDef);
        m_bonCreateOK = true;
    }

    private void initialTripCard( ViewScaleDef vScaleDef ){

        if ( null == m_HomeStatusEntity || null == m_HomeStatusEntity.ItineraryInfoList ){
            return;
        }

        //initial trip card
        int iSize = m_HomeStatusEntity.ItineraryInfoList.size();
        for ( int iIdx = 0; iIdx < iSize; iIdx++ ){

            CIFlightCardView ciFlightCardView = new CIFlightCardView(CIApplication.getContext(), true);

            //航段圓角顯示邏輯
            if ( iSize == 1 ){
                ciFlightCardView.onOnlyOneFlightCard();
            } else {
                if ( iIdx == 0 ){
                    ciFlightCardView.onFirstFlightCard();
                } else if ( iSize -1 == iIdx ){
                    ciFlightCardView.onLastFlightCard();
                } else {
                    ciFlightCardView.onCenterFlightCard();
                }
            }
            //

//            //Flight Status 狀態模擬
//            if ( iIdx == 0 ){
//                ciFlightCardView.onFlightStatus_Normal();
//            } else if ( iIdx == 1 ){
//                ciFlightCardView.onFlightStatus_Delay();
//            } else {
//                ciFlightCardView.onFlightStatus_Cancel();
//            }
//            //

            ciFlightCardView.FlightInfoUpdate(m_HomeStatusEntity.ItineraryInfoList.get(iIdx));
            ciFlightCardView.setOnClickListener(this);
            ciFlightCardView.setTag(iIdx);

            m_arInfoList.add(ciFlightCardView);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, vScaleDef.getLayoutHeight(140));
            if ( iIdx != 0 ){
                params.topMargin = vScaleDef.getLayoutHeight(4);
            }
            m_llayoutInfoContent.addView( ciFlightCardView, params );
        }
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

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

    View.OnClickListener m_onClick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            if ( null != m_Listener ){
                m_Listener.OnMyTripsClick();
            }
        }
    };

    public void uiSetParameterListener(OnFlightInfoListener onListener) {
        m_Listener = onListener;
    }

    @Override
    public void onClick(View v) {

        if ( null == m_HomeStatusEntity || null == m_HomeStatusEntity.ItineraryInfoList ){
            return;
        }

        int iIdx = (Integer)v.getTag();
        if ( iIdx >= m_HomeStatusEntity.ItineraryInfoList.size() ){
            return;
        }

        //前往trip detail flight tab頁面

        Bundle bundle = new Bundle();
        bundle.putInt(UiMessageDef.BUNDLE_TRIPS_DETIAL_CURRENT_PAGE, 0);
        bundle.putSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST,
                m_HomeStatusEntity.ItineraryInfoList.get(iIdx));
        //2018-12-06 需要加上 姓名 不然行李追蹤比對不出來
        bundle.putString(UiMessageDef.BUNDLE_FIRST_NAME, m_HomeStatusEntity.strFirst_name);
        bundle.putString(UiMessageDef.BUNDLE_LAST_NAME, m_HomeStatusEntity.strLast_name);
        //
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(getActivity(), CIMyTripsDetialActivity.class);
        getActivity().startActivityForResult(intent, UiMessageDef.REQUEST_CODE_TRIP_DETAIL);

        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }


    public void setHomePageStatus( CIHomeStatusEntity StatusEntity ){
        m_HomeStatusEntity = StatusEntity;
        if ( m_bonCreateOK ){
            HomePageStatusUpdate();
        }
    }

    public void HomePageStatusUpdate(){

        if ( null == m_HomeStatusEntity || null == m_HomeStatusEntity.ItineraryInfoList ){
            return;
        }

        int iSize = m_HomeStatusEntity.ItineraryInfoList.size();
        if ( iSize != m_arInfoList.size() ){
            m_arInfoList.clear();
            m_llayoutInfoContent.removeAllViews();
            initialTripCard( ViewScaleDef.getInstance(getActivity()));
        } else{
            //卡片已存在則更新牌卡
            for ( int iIdx = 0; iIdx < iSize; iIdx++ ){
                m_arInfoList.get(iIdx).FlightInfoUpdate(m_HomeStatusEntity.ItineraryInfoList.get(iIdx));
            }
        }

    }

    protected void onFragmentResume() {
        HomePageStatusUpdate();
        if ( null != m_tvOtherTrips ){
            m_tvOtherTrips.setText(R.string.my_others_trips);
        }
    }

}
