package ci.function.HomePage;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.MyTrips.Detail.CIMyTripsDetialActivity;
import ci.ui.define.HomePage_Status;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.item.CIHomeStatusEntity;
import ci.ws.Models.entities.CITripListResp_Itinerary;

/**
 * 首頁狀態 C 選餐, 選位
 * Created by Ryan on 2016/2/26.
 */
public class CIMainSelectionFragment extends BaseFragment {

    private boolean m_bSelectSeat = false;
    private boolean m_bSelectMeal = false;

    private LinearLayout m_llayout_bg = null;
    private CIMainInfoView m_vInfoView = null;
    private View m_vSelectionButton = null;

    private RelativeLayout m_rlayout_Seat = null;
    private ImageView m_imgSeat = null;
    private TextView m_tvSeatText = null;
    private ImageView m_imgSeatAdd = null;

    private RelativeLayout m_rlayout_Meal = null;
    private ImageView m_imgMeal = null;
    private TextView m_tvMealText = null;
    private ImageView m_imgMealAdd = null;

    private CIHomeStatusEntity m_HomeStatusEntity = null;
//    private CITripListResp_Itinerary m_Itinerary_Info = null;
    private Boolean m_bonCreateOK = false;
    private Context m_context = CIApplication.getContext();
    private CITripListResp_Itinerary m_PNRItinerary_Info = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_main_selection;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_llayout_bg = (LinearLayout) view.findViewById(R.id.llayout_bg);
        m_vInfoView = (CIMainInfoView) view.findViewById(R.id.vInfoView);

        m_vSelectionButton = view.findViewById(R.id.vSelectionButton);
        m_rlayout_Seat = (RelativeLayout) m_vSelectionButton.findViewById(R.id.rlayout_seat);
        m_imgSeat = (ImageView) m_vSelectionButton.findViewById(R.id.img_seat);
        m_tvSeatText = (TextView) m_vSelectionButton.findViewById(R.id.tv_seat);
        m_imgSeatAdd = (ImageView) m_vSelectionButton.findViewById(R.id.img_seat_add);
        m_rlayout_Seat.setOnClickListener(m_onClick);

        m_rlayout_Meal = (RelativeLayout) m_vSelectionButton.findViewById(R.id.rlayout_meal);
        m_imgMeal = (ImageView) m_vSelectionButton.findViewById(R.id.img_meal);
        m_tvMealText = (TextView) m_vSelectionButton.findViewById(R.id.tv_meal);
        m_imgMealAdd = (ImageView) m_vSelectionButton.findViewById(R.id.img_meal_add);
        m_rlayout_Meal.setOnClickListener(m_onClick);

        m_bonCreateOK = true;
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        m_llayout_bg.getLayoutParams().height = vScaleDef.getLayoutHeight(361.3);

        ((LinearLayout.LayoutParams) m_vSelectionButton.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(18);

        m_rlayout_Seat.getLayoutParams().height = vScaleDef.getLayoutWidth(42);
        ((LinearLayout.LayoutParams) m_rlayout_Seat.getLayoutParams()).leftMargin = vScaleDef.getLayoutWidth(45);
        ((LinearLayout.LayoutParams) m_rlayout_Seat.getLayoutParams()).rightMargin = vScaleDef.getLayoutWidth(10);
        int iSeat = vScaleDef.getLayoutWidth(10);
        m_rlayout_Seat.setPadding(iSeat, 0, iSeat, 0);
        m_imgSeat.getLayoutParams().height = vScaleDef.getLayoutMinUnit(24);
        m_imgSeat.getLayoutParams().width = vScaleDef.getLayoutMinUnit(24);
        m_imgSeatAdd.getLayoutParams().height = vScaleDef.getLayoutMinUnit(24);
        m_imgSeatAdd.getLayoutParams().width = vScaleDef.getLayoutMinUnit(24);
        vScaleDef.setTextSize(16, m_tvSeatText);

        m_rlayout_Meal.getLayoutParams().height = vScaleDef.getLayoutWidth(42);
        ((LinearLayout.LayoutParams) m_rlayout_Meal.getLayoutParams()).rightMargin = vScaleDef.getLayoutWidth(45);
        int iMeal = vScaleDef.getLayoutWidth(10);
        m_rlayout_Meal.setPadding(iMeal, 0, iMeal, 0);
        m_imgMeal.getLayoutParams().height = vScaleDef.getLayoutMinUnit(24);
        m_imgMeal.getLayoutParams().width = vScaleDef.getLayoutMinUnit(24);
        m_imgMealAdd.getLayoutParams().height = vScaleDef.getLayoutMinUnit(24);
        m_imgMealAdd.getLayoutParams().width = vScaleDef.getLayoutMinUnit(24);
        vScaleDef.setTextSize(16, m_tvMealText);

        m_vInfoView.setIcon(R.drawable.ic_homepage_b);

        HomeStatusUpdate();
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

    protected void onFragmentResume() {
        HomeStatusUpdate();
        if ( null != m_tvSeatText ){
            m_tvSeatText.setText(R.string.seat);
        }
        if ( null != m_tvMealText ){
            m_tvMealText.setText(R.string.meal);
        }
    }

    public void setHomePageStatus( CIHomeStatusEntity Entity ){

        m_HomeStatusEntity = Entity;
        //2016-06-29 ryan for 直接抓取外面過濾好的CPR以及PNR資料,
        if ( null != Entity ){
            m_PNRItinerary_Info = Entity.Itinerary_Info;
        } else {
            m_PNRItinerary_Info = null;
        }
        //是否已經選餐
        if ( null != Entity.Is_Select_Meal && Entity.Is_Select_Meal.equals("Y")){
            m_bSelectMeal = true;
        }else {
            m_bSelectMeal = false;
        }

        if ( m_bonCreateOK ){
            HomeStatusUpdate();
        }
    }

    public void HomeStatusUpdate(){

        //2016-11-15 Modify by Ryan for 調整PNR時間邏輯, 不再抓取CPR的時間來更新, 調整為抓取DISPLAY TAG
        String strDate = "";
        if ( null != m_PNRItinerary_Info ){

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = sdf.parse( m_PNRItinerary_Info.getDisplayDepartureDate() );
                //2016-11-24 Modify by Ryan for 直接將時間轉成 milliseconds 扣掉一天 (3600 * 24h *1000)
                long lDeparture = date.getTime() - (3600 * 24 * 1000);

                Date SelectDate = new Date();
                SelectDate.setTime(lDeparture);

                SimpleDateFormat sdfNew = new SimpleDateFormat("MM-dd");
                strDate = sdfNew.format(SelectDate);
                strDate = strDate + " " + m_PNRItinerary_Info.getDisplayDepartureTime();
                //
                //Date date = sdf.parse( m_PNRItinerary_Info.Departure_Date );
                //Calendar cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.DAY_OF_MONTH, -1);
                //
                //SimpleDateFormat sdfNew = new SimpleDateFormat("MM-dd");
                //strDate = sdfNew.format(cal.getTime());
                //strDate = strDate + " " + m_PNRItinerary_Info.getDisplayDepartureTime();
                //strDate = strDate + " " + m_PNRItinerary_Info.Departure_Time;
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }

        m_vInfoView.setTitleText(String.format(m_context.getString(R.string.home_page_select_meal), strDate));

        //飛機沒有站票, 所以一定有位置 2016-05-24 by ryan
        m_rlayout_Seat.setBackgroundResource(R.drawable.btn_main_meal_seat_select);
        m_imgSeatAdd.setImageResource(R.drawable.ic_done);

        //如果已選餐.按鈕背景改為綠色
        if ( true == m_bSelectMeal ){
            m_rlayout_Meal.setBackgroundResource(R.drawable.btn_main_meal_seat_select);
            m_imgMealAdd.setImageResource(R.drawable.ic_done);
        }else {
            m_rlayout_Meal.setBackgroundResource(R.drawable.btn_main_meal_seat_unselect);
            m_imgMealAdd.setImageResource(R.drawable.ic_add);
        }
    }

    View.OnClickListener m_onClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            //2016-06-26 modifly by ryan for 因應訊息顯示方式調整判斷
            //判斷艙等是否可以選位
            if(v.getId() == m_rlayout_Seat.getId()){

                //異常,
                if(null == m_PNRItinerary_Info || null == m_PNRItinerary_Info.Booking_Class ){
                    showDialog(getString(R.string.warning), getString(R.string.home_page_seat_meal_no_select_seat_time));
                    return;
                }

                //非華航的班機, 不能選位
                if ( false == m_PNRItinerary_Info.bIs_Do_Tag ){
                    showDialog(getString(R.string.warning), getString(R.string.home_page_seat_meal_airline_cantnot_select_seat));
                    return;

                    // 下列艙等不可選位
                } else if (
                        //2017-04-16 Modify 修正不可選位的艙等，僅G,S不可選
                        //TextUtils.equals("L",m_PNRItinerary_Info.Booking_Class )||
                        //TextUtils.equals("X",m_PNRItinerary_Info.Booking_Class )||
                        TextUtils.equals("G",m_PNRItinerary_Info.Booking_Class )||
                        TextUtils.equals("S",m_PNRItinerary_Info.Booking_Class )   ){
                    showDialog(getString(R.string.warning), getString(R.string.home_page_seat_meal_cabin_cannot_selectseat));
                    return;
                }

                //判斷艙等是否可以選餐
            } else if(v.getId() == m_rlayout_Meal.getId()){

                if(null == m_PNRItinerary_Info || null == m_PNRItinerary_Info.Booking_Class ||
                        ( null != m_HomeStatusEntity && m_HomeStatusEntity.iStatus_Code == HomePage_Status.TYPE_C_SELECT_SEAT_180D_14D) ){
                    showDialog(getString(R.string.warning), getString(R.string.home_page_seat_meal_no_select_meal_time));
                    return;
                }

                //非華航的班機, 不能選餐
                if ( false == m_PNRItinerary_Info.bIs_Do_Tag ) {
                    showDialog(getString(R.string.warning), getString(R.string.home_page_seat_meal_airline_cantnot_select_meal));
                    return;

                    //符合下列艙等, 才可選餐
                } else if ( TextUtils.equals("J",m_PNRItinerary_Info.Booking_Class ) ||
                        TextUtils.equals("C",m_PNRItinerary_Info.Booking_Class )||
                        TextUtils.equals("D",m_PNRItinerary_Info.Booking_Class )||
                        TextUtils.equals("I",m_PNRItinerary_Info.Booking_Class )||
                        TextUtils.equals("O",m_PNRItinerary_Info.Booking_Class )   ){
                    //可選餐
                } else {
                    //其餘艙等接不可選餐
                    showDialog(getString(R.string.warning), getString(R.string.home_page_seat_meal_cabin_cannot_selectmeal));
                    return;
                }
            }

            OpenPassengerTab();
        }
    };

    private void OpenPassengerTab(){

        if ( null == m_PNRItinerary_Info ){
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putInt(UiMessageDef.BUNDLE_TRIPS_DETIAL_CURRENT_PAGE, 1);
        bundle.putSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, m_PNRItinerary_Info);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(getActivity(), CIMyTripsDetialActivity.class);
        getActivity().startActivityForResult(intent, UiMessageDef.REQUEST_CODE_TRIP_DETAIL);

        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

}