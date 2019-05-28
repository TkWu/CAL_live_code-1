package ci.function.SeatSelection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashMap;

import ci.function.Core.SLog;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CIProgressDialog;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ui.view.TwoItemNavigationBar;
import ci.ws.Models.CISeatFloor;
import ci.ws.Models.entities.CIAllocateSeatCheckInReq;
import ci.ws.Models.entities.CIAllocateSeatCheckInReq_Pax;
import ci.ws.Models.entities.CIAllocateSeatReq;
import ci.ws.Models.entities.CIAllocateSeatReq_Pax;
import ci.ws.Models.entities.CIGetSeatReq;
import ci.ws.Models.entities.CIPassengerListResp;
import ci.ws.Models.entities.CISeatInfoList;
import ci.ws.Models.entities.CISeatMapPaxInfo;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CISelectSeatPresenter;
import ci.ws.Presenter.Listener.CISelectSeatListener;

import static ci.function.SeatSelection.CISelectSeatMapFragment.OnSeatFloorFragmentInterface;
import static ci.function.SeatSelection.CISelectSeatMapFragment.OnSeatFloorFragmentListener;
import static ci.function.SeatSelection.CISelectSeatMapFragment.OnSeatFloorFragmentParameter;
import static ci.ui.view.NavigationBar.onNavigationbarListener;
import static ci.ui.view.NavigationBar.onNavigationbarParameter;

/**
 * 選位頁面，多段式
 * Created by jlchen on 2016/6/28.
 */
public class CISelectSeatMapActivity extends BaseActivity
        implements View.OnClickListener, TwoItemNavigationBar.ItemClickListener {

    private onNavigationbarParameter m_onNavigationParameter = new onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return String.format(
                    m_Context.getString(R.string.select_seat_flight),
                    m_strDepartureStation,
                    m_strArrivalStation);
        }
    };

    private onNavigationbarListener m_onNavigationbarListener = new onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    private OnSeatFloorFragmentListener m_OnSeatFloorFragmentListener = new OnSeatFloorFragmentListener() {
        @Override
        public void SetSeatMapComplete() {
            m_bGetMap = true;
            hideProgressDialog();
        }

        @Override
        public void onSelectSeatClickGetView(ArrayList<CIPassengerSeatItem> arrayList) {

            m_alPassengerSeatData = arrayList;

            m_tvSelectSeatNumber.setText(m_alPassengerSeatData.get(m_iPassengerIndex).m_strPassengerSeat);
        }

        @Override
        public void onSelectSeatClick(View v, String strSeatNum) {

            //選擇的位置與當前已選的位置一樣, 不做任何事
            if (strSeatNum.equals(m_tvSelectSeatNumber.getText().toString())) {
                return;
//                //當前顯示的座位資料清空
//                if (m_iCheckReturnFlight == 0) {
//                    m_alPassengerSeatData.get(m_iPassengerNum).m_strPassengerSeat = "";
//                } else {
//                    m_alPassengerSeatData.get(m_iPassengerNum).m_strPassengerSeatReturn = "";
//                }
//                m_tvSelectSeatNumber.setText("");
            }

            //更新當前乘客的選位資料
//            if ( false == m_alPassengerSeatData.get(m_iPassengerIndex).m_bReturn ){
                m_alPassengerSeatData.get(m_iPassengerIndex).m_strPassengerSeat = strSeatNum;
                if ( -1 == m_alPassengerSeatData.get(m_iPassengerIndex).m_iPassengerSeq ){
                    m_alPassengerSeatData.get(m_iPassengerIndex).m_iPassengerSeq = searchSeqMax()+1;
                }
//            }else {
//                m_alPassengerSeatData.get(m_iPassengerIndex).m_strPassengerSeatReturn = strSeatNum;
//                if ( -1 == m_alPassengerSeatData.get(m_iPassengerIndex).m_iPassengerSeqReturn ){
//                    m_alPassengerSeatData.get(m_iPassengerIndex).m_iPassengerSeqReturn = searchSeqMax()+1;
//                }
//            }
            m_tvSelectSeatNumber.setText(strSeatNum);

            SetNowSelectSeat(false);
        }
    };

    //toolbar
    private NavigationBar m_Navigationbar = null;
    private ImageButton m_aboutButton = null;

    private RelativeLayout m_rlBg = null;
    //當前乘客資訊
    private TextView m_tvWhoSelectSeat = null;
    private TextView m_tvSelectSeatNumber = null;
    //切換上or下一個乘客乘客的按鈕
    private RelativeLayout m_rlLeftArrow = null;
    private RelativeLayout m_rlRightArrow = null;
    private ImageButton m_LeftArrowImage = null;
    private ImageButton m_RightArrowImage = null;
    //顯示當前乘客與乘客數量用的圓點
    private LinearLayout m_llSeatPeopleNumber = null;
    private View[] m_vPeopleCircle = null;

    //切換上下層用的twoItemBar
    private FrameLayout m_flSelect = null;
    private TwoItemNavigationBar m_TwoItemBar = null;

    //座位圖
    private FrameLayout m_flSeat = null;
    private CISelectSeatMapFragment m_UpSelectSeatFragment = null;
    private CISelectSeatMapFragment m_DownSelectSeatFragment = null;
    private OnSeatFloorFragmentInterface m_UpSeatFragmentInterface = null;
    private OnSeatFloorFragmentInterface m_DownSeatFragmentInterface = null;

    //說明
    private TextView m_tvDescriptionYour = null;
    private TextView m_tvDescriptionPartner = null;
    private TextView m_tvDescriptionAvailable = null;
    private TextView m_tvDescriptionOccupied = null;

    //確認按鈕
    private Button m_NextButton = null;

    private CISelectSeatPresenter m_presenter;
    private CISeatInfoList m_seatDataList;
    private ArrayList<CISeatFloor> m_alUpFloor = new ArrayList<>();
    private ArrayList<CISeatFloor> m_alDownFloor = new ArrayList<>();

    //乘客的初始座位
    private HashMap<Integer, String> m_hmSeatData = null;

    //所有乘客座位資料
    private ArrayList<CIPassengerSeatItem> m_alPassengerSeatData = new ArrayList<>();

    //航段資料
    private CITripListResp_Itinerary m_tripData = null;
    //出發地
    private String m_strDepartureStation = "";
    //目的地
    private String m_strArrivalStation = "";

    //乘客總數
    private int m_iPassengerMax = 0;
    //當前乘客的index
    private int m_iPassengerIndex = 0;
    //乘客選位順序index
    private int m_iSeq = 0;
//    //判斷是否為來回票的tag
//    private int m_iCheckReturnFlight = 0;
//    private boolean m_bReturnOrNot = false;
    //判斷是否可切換上下層的tag
    private boolean m_bIsTwoDeck;
    //判斷當前為上或下層畫面的tag
    private boolean m_bFragment;
    //是否取到座位圖
    private boolean m_bGetMap;
    //是否切換過上下層
    private boolean m_bChangeFloor;

    private String m_strDoWhat = null;
    private static final String SELECT_SEAT = "select_seat";
    private static final String SEND_SEAT = "send_seat";

    //判斷是否為check-in選位
    private boolean m_bCheckIn = false;

    //選位Req
    private CIAllocateSeatReq m_SeatEntity;
    private CIAllocateSeatCheckInReq m_SeatCheckInEntity;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_select_seat_map;
    }

    @Override
    protected void initialLayoutComponent() {

        m_strDoWhat = SELECT_SEAT;

        m_hmSeatData = new HashMap<>();

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_aboutButton = (ImageButton) findViewById(R.id.select_seat_top_about);

        m_rlBg = (RelativeLayout) findViewById(R.id.root);

        //切換乘客的左右按鈕
        m_rlLeftArrow = (RelativeLayout) findViewById(R.id.rl_arrow_left);
        m_rlRightArrow = (RelativeLayout) findViewById(R.id.rl_arrow_right);
        m_LeftArrowImage = (ImageButton) findViewById(R.id.btn_arrow_left);
        m_RightArrowImage = (ImageButton) findViewById(R.id.btn_arrow_right);
        //顯示共幾個,並且當前為第幾個乘客的圓點列
        m_llSeatPeopleNumber = (LinearLayout) findViewById(R.id.llayout_select_seat_people_number);
        //切換上下層的two item m_TwoItemBar
        m_flSelect = (FrameLayout) findViewById(R.id.fl_select);
//        //顯示座位的列名(ex:A~H)
//        m_SeatEnglish = (LinearLayout) findViewById(R.id.llayout_seat_english);
        //當前乘客名
        m_tvWhoSelectSeat = (TextView) findViewById(R.id.tv_seat_title_name);
        //當前乘客座位
        m_tvSelectSeatNumber = (TextView) findViewById(R.id.tv_select_seat_number);

        //座位位置圖
        m_flSeat = (FrameLayout) findViewById(R.id.fl_select_seat);

        //位置圖下方的說明欄
        //您的座位
        m_tvDescriptionYour = (TextView) findViewById(R.id.your_seat_image);
        //同行旅客座位
        m_tvDescriptionPartner = (TextView) findViewById(R.id.partner_seat_image);
        //可用
        m_tvDescriptionAvailable = (TextView) findViewById(R.id.available_seat_image);
        //沒有提供
        m_tvDescriptionOccupied = (TextView) findViewById(R.id.occupied_seat_image);

        //確認按鈕
        m_NextButton = (Button) findViewById(R.id.btn_next);

        m_presenter = CISelectSeatPresenter.getInstance(new CISelectSeatListener() {
            @Override
            public void onGetSeatMapSuccess(String rt_code, String rt_msg, CISeatInfoList SeatInfoList) {
                //預設先顯示下層
                m_bFragment = false;

                m_seatDataList = SeatInfoList;

                m_alUpFloor = m_seatDataList.arUpSeatFloorList;
                m_alDownFloor = m_seatDataList.arMainSeatFloorList;

                if (null == m_alUpFloor)
                    m_alUpFloor = new ArrayList<>();

                if (null == m_alDownFloor)
                    m_alDownFloor = new ArrayList<>();

                // 開始Fragment的事務Transaction
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                //若同時上層和下層都有資料時 需增加切換上下層的選單
                if (m_alUpFloor.size() != 0 && m_alDownFloor.size() != 0) {

                    m_bIsTwoDeck = true;
                    m_flSelect.setVisibility(View.VISIBLE);

                    m_UpSelectSeatFragment = new CISelectSeatMapFragment();
                    m_UpSeatFragmentInterface = m_UpSelectSeatFragment.uiSetParameterListener(
                            new OnSeatFloorFragmentParameter() {
                                @Override
                                public ArrayList<CISeatFloor> getSeatFloorList() {
                                    return m_alUpFloor;
                                }

                                @Override
                                public ArrayList<CIPassengerSeatItem> getPassengerSeatData() {
                                    return m_alPassengerSeatData;
                                }

                                @Override
                                public HashMap<Integer, String> getSeatHmData() {
                                    return m_hmSeatData;
                                }
                            }, m_OnSeatFloorFragmentListener);

                    //將上層fragment add進來 並且先隱藏
                    fragmentTransaction
                            .add(m_flSeat.getId(), m_UpSelectSeatFragment)
                            .hide(m_UpSelectSeatFragment);

                } else {
                    m_bIsTwoDeck = false;
                    m_flSelect.setVisibility(View.GONE);
                }

                m_DownSelectSeatFragment = new CISelectSeatMapFragment();
                m_DownSeatFragmentInterface = m_DownSelectSeatFragment.uiSetParameterListener(
                        new OnSeatFloorFragmentParameter() {
                            @Override
                            public ArrayList<CISeatFloor> getSeatFloorList() {
                                return m_alDownFloor;
                            }

                            @Override
                            public ArrayList<CIPassengerSeatItem> getPassengerSeatData() {
                                return m_alPassengerSeatData;
                            }

                            @Override
                            public HashMap<Integer, String> getSeatHmData() {
                                return m_hmSeatData;
                            }
                        }, m_OnSeatFloorFragmentListener);

                //將下層fragment add進來 並且顯示
                fragmentTransaction
                        .add(m_flSeat.getId(), m_DownSelectSeatFragment)
                        .show(m_DownSelectSeatFragment);

                // 提交事務
                fragmentTransaction.commitAllowingStateLoss();
//                //切換fragment
//                ChangeSeatFragment();
            }

            @Override
            public void onGetSeatMapError(String rt_code, String rt_msg) {
                showDialog(getString(R.string.warning), rt_msg, getResources().getString(R.string.confirm), null,
                        new CIAlertDialog.OnAlertMsgDialogListener() {
                            @Override
                            public void onAlertMsgDialog_Confirm() {
                                finish();
                                overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
                            }

                            @Override
                            public void onAlertMsgDialogg_Cancel() {}
                        });
            }

            @Override
            public void onAllocateSeatError(String rt_code, String rt_msg) {
                if (m_strDoWhat.equals(SEND_SEAT)) {
                    showDialog(getString(R.string.warning), rt_msg, getResources().getString(R.string.confirm));
                    m_strDoWhat = SELECT_SEAT;
                }
            }

            @Override
            public void onAllocateSeatSuccess(String rt_code, String rt_msg, String strSeat) {
               SLog.i("seatmsg", rt_msg);
//                if (m_iSendCount < m_alEntity.size() && m_strDoWhat.equals(SEND_SEAT)) {
////                    m_presenter.AllocateSeat(m_alEntity.get(m_iSendCount));
//                    m_iSendCount++;
//                } else if (m_iSendCount == m_alEntity.size() && m_strDoWhat.equals(SEND_SEAT)) {
//                    if (m_bReturnOrNot) {
//                        if (m_iCheckReturnFlight != 0) {
//                            changeActivity(CISelectSeatResultActivity.class);
//                        }
//                        //去回程情況要傳送去回程資料，回程沒做
//                    } else {
                        changeActivity(CISelectSeatResultActivity.class);
//                    }
//                }
            }

            @Override
            public void showProgress() {
                showProgressDialog(new CIProgressDialog.CIProgressDlgListener() {
                    @Override
                    public void onBackPressed() {
                        if (m_strDoWhat.equals(SELECT_SEAT)) {
                            m_presenter.CancelGetSeatMap();
                            finish();
                            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
                        } else {
                            m_presenter.CancelAllocateSeat();
                            m_strDoWhat = SELECT_SEAT;
                        }
                    }
                });
            }

            @Override
            public void hideProgress() {
                if ( false == m_bGetMap )
                    return;
                hideProgressDialog();
            }
        });

        initData();
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_rlBg);
        vScaleDef.selfAdjustSameScaleView(m_LeftArrowImage, 24, 24);
        vScaleDef.selfAdjustSameScaleView(m_RightArrowImage, 24, 24);
        vScaleDef.selfAdjustSameScaleView(m_aboutButton, 32, 32);
        vScaleDef.selfAdjustSameScaleView(m_tvDescriptionYour, 20, 20);
        vScaleDef.selfAdjustSameScaleView(m_tvDescriptionPartner, 20, 20);
        vScaleDef.selfAdjustSameScaleView(m_tvDescriptionAvailable, 20, 20);
        vScaleDef.selfAdjustSameScaleView(m_tvDescriptionOccupied, 20, 20);

        initDots();
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_NextButton.setOnClickListener(this);
        m_rlLeftArrow.setOnClickListener(this);
        m_rlRightArrow.setOnClickListener(this);
        m_LeftArrowImage.setOnClickListener(this);
        m_RightArrowImage.setOnClickListener(this);
        m_aboutButton.setOnClickListener(this);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        m_TwoItemBar = TwoItemNavigationBar
                .newInstance(
                        getString(R.string.upper_deck),
                        getString(R.string.lower_deck), TwoItemNavigationBar.EInitItem.RIGHT);
        m_TwoItemBar.setListener(this);
        fragmentTransaction.replace(m_flSelect.getId(), m_TwoItemBar, m_TwoItemBar.getTag());

        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    protected void onLanguageChangeUpdateUI() {}

    private void initData() {

        //Bundle取得初始化資料
        Bundle bundle = getIntent().getExtras();
        String strIsCheckSelectSeat = bundle.getString(UiMessageDef.BUNDLE_IS_CHECK_IN_SELECT_SEAT, "N");

        m_tripData = (CITripListResp_Itinerary) bundle.getSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);
        CIPassengerListResp passengerData = (CIPassengerListResp) bundle.getSerializable(UiMessageDef.BUNDLE_PASSENGER_INFO);

        CIGetSeatReq getSeatReq = new CIGetSeatReq();

        if (strIsCheckSelectSeat.equals("Y")){
            m_bCheckIn = true;
        }else {
            //2017.1.4 CR - PNR GetSeatMap 需帶入 PNR_Id
            getSeatReq.PNR_Id = m_tripData.Pnr_Id;
        }

        //2016-07-11 modifly by ryan for 需要將是否為Check-in 期間進行選位的狀態帶給Server
        getSeatReq.Is_CPR = strIsCheckSelectSeat;

        //-------------------------//
        //乘客數
        m_iPassengerMax = passengerData.Pax_Info.size();

        if ( 1 < m_iPassengerMax ){
            m_rlRightArrow.setVisibility(View.VISIBLE);
        }

        //整理傳入的乘客座位資料
        m_alPassengerSeatData.clear();

        int tempSeq = 0;

        for (int i = 0; i < m_iPassengerMax; i++) {

            CIPassengerSeatItem item = new CIPassengerSeatItem();

            item.m_strPassengerName = passengerData.Pax_Info.get(i).First_Name +" "+ passengerData.Pax_Info.get(i).Last_Name;
            item.m_strPaxNum = passengerData.Pax_Info.get(i).Pax_Number;
            item.m_strDid = passengerData.Pax_Info.get(i).Did;

            //如果該乘客有座位
            if (!TextUtils.isEmpty(passengerData.Pax_Info.get(i).Seat_Number)) {

                String seatNumber = passengerData.Pax_Info.get(i).Seat_Number;
                //座位要去掉前面的0
                if ( (seatNumber.substring(0, 1).equals("0") && seatNumber.length() == 3) ) {
                    seatNumber =  seatNumber.substring(1);
                }
                //因應check-in 選位可能給的座位值: 001A, 010A... (前面的0都要一律去掉)
                if ( (seatNumber.substring(0, 1).equals("0") && seatNumber.length() == 4) ){
                    seatNumber =  seatNumber.substring(1);
                    if ( (seatNumber.substring(0, 1).equals("0") && seatNumber.length() == 3) ) {
                        seatNumber =  seatNumber.substring(1);
                    }
                }
                //設定已有人的座位
                m_hmSeatData.put(tempSeq, seatNumber);
                item.m_strPassengerSeat = seatNumber;

                //按順序填入選位序號
                item.m_iPassengerSeq = m_iSeq;
                m_iSeq++;
                tempSeq++;

            } else {
                //該乘客沒選位
                item.m_strPassengerSeat = "";
                item.m_iPassengerSeq = -1;
            }

            m_alPassengerSeatData.add(item);
            //CPR選為需加上 乘客資訊
            if ( m_bCheckIn ){
                CISeatMapPaxInfo pax_info = new CISeatMapPaxInfo();

                pax_info.first_name = passengerData.Pax_Info.get(i).First_Name;
                pax_info.last_name  = passengerData.Pax_Info.get(i).Last_Name;
                pax_info.did        = passengerData.Pax_Info.get(i).Did;

                getSeatReq.pax_info.add(pax_info);
            }
        }

//            if ( null != m_tripData ){
                m_strDepartureStation = m_tripData.Departure_Station;
                m_strArrivalStation = m_tripData.Arrival_Station;

                getSeatReq.Booking_Class = m_tripData.Booking_Class;
                getSeatReq.Departure_Date = m_tripData.Departure_Date;
                getSeatReq.Airlines = m_tripData.Airlines;
                getSeatReq.Flight_Number = m_tripData.Flight_Number;
                getSeatReq.Departure_Station = m_tripData.Departure_Station;
                getSeatReq.Arrival_Station = m_tripData.Arrival_Station;
//
//            }
//        }

        if ( 0 < m_alPassengerSeatData.size() ){

            m_iPassengerIndex = 0;
            String strName = m_alPassengerSeatData.get(m_iPassengerIndex).m_strPassengerName;
            String strSeat = m_alPassengerSeatData.get(m_iPassengerIndex).m_strPassengerSeat;

            m_tvWhoSelectSeat.setText(strName);
            m_tvSelectSeatNumber.setText(strSeat);

            //取得座位資料
            m_presenter.GetSeatMap(getSeatReq);
//        getSeatMap();

            //是否為轉機, 若是則需選下一個航班的座位
//        if (m_bReturnOrNot)
//            m_NextButton.setText(getText(R.string.next_flight));
//        else
        }else {
            showDialog(
                    getString(R.string.warning),
                    getString(R.string.no_match_data),
                    getString(R.string.confirm),
                    null,
                    new CIAlertDialog.OnAlertMsgDialogListener() {
                @Override
                public void onAlertMsgDialog_Confirm() {
                    finish();
                    overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
                }

                @Override
                public void onAlertMsgDialogg_Cancel() {}
            });
        }
    }

    private void initDots() {
        m_llSeatPeopleNumber.removeAllViews();

        m_vPeopleCircle = new View[m_iPassengerMax];

        for (int i = 0; i < m_iPassengerMax; i++) {
            m_vPeopleCircle[i] = new View(m_Context);

            m_vPeopleCircle[i].setTag(i);
            m_vPeopleCircle[i].setBackgroundResource(R.drawable.bg_select_seat_people_circle);
            if (i != m_iPassengerIndex ){
                m_vPeopleCircle[i].setAlpha(0.5f);
            }else {
                m_vPeopleCircle[i].setAlpha(1f);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i == 0) {
                params.leftMargin = 0;
            } else {
                params.leftMargin = ViewScaleDef.getInstance(m_Context).getLayoutMinUnit(6);
            }
            params.height = ViewScaleDef.getInstance(m_Context).getLayoutMinUnit(6);
            params.width = ViewScaleDef.getInstance(m_Context).getLayoutMinUnit(6);
            m_llSeatPeopleNumber.addView(m_vPeopleCircle[i], params);
        }
    }

    private void setPeopleCircleColor() {
        for (int i = 0; i < m_iPassengerMax; i++) {
            if ( i == m_iPassengerIndex){
                m_vPeopleCircle[i].setAlpha(1);
            }else{
                m_vPeopleCircle[i].setAlpha(0.5f);
            }
        }
    }

    private void ChangeSeatFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if ( true == m_bFragment ){
            transaction.hide(m_DownSelectSeatFragment);
            transaction.show(m_UpSelectSeatFragment);
        }else {
            transaction.hide(m_UpSelectSeatFragment);
            transaction.show(m_DownSelectSeatFragment);
        }
        transaction.commitAllowingStateLoss();
    }

    //切換上下層時, 重設該乘客當前座位資料
    private void SetNowSelectSeat(boolean bIsChangePassenger) {

        if ( true == m_bFragment ){
            if ( null != m_UpSelectSeatFragment && null != m_UpSeatFragmentInterface ){
                if ( true == bIsChangePassenger )
                    m_UpSeatFragmentInterface.ChangePassenger(m_iPassengerIndex);
                m_UpSeatFragmentInterface.SetNowSelectSeat(m_iPassengerIndex, m_alPassengerSeatData);
            }
        }else {
            if ( null != m_DownSelectSeatFragment && null != m_DownSeatFragmentInterface ){
                if ( true == bIsChangePassenger )
                    m_DownSeatFragmentInterface.ChangePassenger(m_iPassengerIndex);
                m_DownSeatFragmentInterface.SetNowSelectSeat(m_iPassengerIndex, m_alPassengerSeatData);
            }
        }
    }

    private void sendSelectSeatReq() {

        if ( false == m_bCheckIn ){
            //一般選位
            m_SeatEntity = new CIAllocateSeatReq();
            m_SeatEntity.Pnr_id     = m_tripData.Pnr_Id;
            m_SeatEntity.Pnr_Seq    = m_tripData.Pnr_Seq;
            
            ArrayList<CIAllocateSeatReq_Pax> allocateSeatPaxes = new ArrayList<>();
            for (int i = 0; i < m_iPassengerMax; i++) {
                if (null != m_alPassengerSeatData.get(i).m_strPassengerSeat
                        && !m_alPassengerSeatData.get(i).m_strPassengerSeat.equals("")) {

                    //2018-10-15 修正選同樣的座位還是會送出選為的bug
                    //座位不同才需送出
                    if (    m_hmSeatData.size() <= 0 ||
                            !TextUtils.equals( m_alPassengerSeatData.get(i).m_strPassengerSeat , m_hmSeatData.get(i) ) ){

                        CIAllocateSeatReq_Pax pax = new CIAllocateSeatReq_Pax();
                        pax.Pax_Number  = m_alPassengerSeatData.get(i).m_strPaxNum;
                        pax.Seat_Number = m_alPassengerSeatData.get(i).m_strPassengerSeat;
                        allocateSeatPaxes.add(pax);
                    }
                }
            }

            if (allocateSeatPaxes.size() > 0) {
                m_SeatEntity.Pax_Info = allocateSeatPaxes;
                m_presenter.AllocateSeat(m_SeatEntity);
            } else {
                changeActivity(CISelectSeatResultActivity.class);
            }
        }else {
            //check-in選位
            m_SeatCheckInEntity = new CIAllocateSeatCheckInReq();
            m_SeatCheckInEntity.Departure_Date     = m_tripData.Departure_Date;
            m_SeatCheckInEntity.Departure_Station  = m_tripData.Departure_Station;
            m_SeatCheckInEntity.Arrival_Station    = m_tripData.Arrival_Station;
            m_SeatCheckInEntity.Airlines           = m_tripData.Airlines;
            m_SeatCheckInEntity.Flight_Number      = m_tripData.Flight_Number;

//            //缺
//            seat.Departure_Date     = m_CheckInTripData.get(0).m_Itinerary_InfoList.get(0).Departure_Date;
//            seat.Departure_Station  = m_CheckInTripData.get(0).m_Itinerary_InfoList.get(0).Departure_Station;
//            seat.Arrival_Station    = m_CheckInTripData.get(0).m_Itinerary_InfoList.get(0).Arrival_Station;
//            seat.Airlines           = m_CheckInTripData.get(0).m_Itinerary_InfoList.get(0).Airlines;
//            seat.Flight_Number      = m_CheckInTripData.get(0).m_Itinerary_InfoList.get(0).Flight_Number;

            ArrayList<CIAllocateSeatCheckInReq_Pax> allocateSeatPaxes = new ArrayList<>();
            for (int i = 0; i < m_iPassengerMax; i++) {
                if (null != m_alPassengerSeatData.get(i).m_strPassengerSeat
                        && !m_alPassengerSeatData.get(i).m_strPassengerSeat.equals("")) {

                    //2018-10-15 修正選同樣的座位還是會送出選為的bug
                    //座位不同才需送出
                    if (    m_hmSeatData.size() <= 0 ||
                            !TextUtils.equals( m_alPassengerSeatData.get(i).m_strPassengerSeat , m_hmSeatData.get(i) ) ) {

                        CIAllocateSeatCheckInReq_Pax pax = new CIAllocateSeatCheckInReq_Pax();
                        pax.Did = m_alPassengerSeatData.get(i).m_strDid;
                        pax.Seat_Number = m_alPassengerSeatData.get(i).m_strPassengerSeat;
                        allocateSeatPaxes.add(pax);
                    }
                }
            }

            if (allocateSeatPaxes.size() > 0) {
                m_SeatCheckInEntity.Pax_Info = allocateSeatPaxes;
                m_presenter.AllocateSeatCheckIn(m_SeatCheckInEntity);
            } else {
                changeActivity(CISelectSeatResultActivity.class);
            }
        }
    }

    private void changeActivity(Class clazz) {
        String[] strPassengerName = new String[m_iPassengerMax];
        String[] strPassengerSeat = new String[m_iPassengerMax];
//        String[] strPassengerSeatReturn = new String[m_iPassengerMax];
        for (int i = 0; i < m_iPassengerMax; i ++){
            strPassengerName[i] = m_alPassengerSeatData.get(i).m_strPassengerName;
            strPassengerSeat[i] = m_alPassengerSeatData.get(i).m_strPassengerSeat;
//            strPassengerSeatReturn[i] = m_alPassengerSeatData.get(i).m_strPassengerSeatReturn;
        }

        Intent intent = new Intent();

        if ( true == m_bCheckIn ){
            intent.putExtra(UiMessageDef.BUNDLE_IS_CHECK_IN_SELECT_SEAT, "Y");
            intent.putExtra(UiMessageDef.BUNDLE_CHECK_IN_SELECT_SEAT_DATA, m_SeatCheckInEntity);
        }else {
            intent.putExtra(UiMessageDef.BUNDLE_IS_CHECK_IN_SELECT_SEAT, "N");
            intent.putExtra(UiMessageDef.BUNDLE_SELECT_SEAT_DATA, m_SeatEntity);
        }
        intent.putExtra(UiMessageDef.BUNDLE_CHECK_IN_TRIP_PNR_ID, m_tripData.Pnr_Id);

        intent.putExtra("PassengerName", strPassengerName);
        intent.putExtra("PassengerNum", m_iPassengerMax);
        intent.putExtra("Seat", strPassengerSeat);
        intent.putExtra("Departure", m_strDepartureStation);
        intent.putExtra("Arrvial", m_strArrivalStation);
//        intent.putExtra("ReturnOrNot", m_bReturnOrNot);
//        if (m_bReturnOrNot)
//            intent.putExtra("SeatReturn", strPassengerSeatReturn);

        intent.setClass(m_Context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private int searchSeqMax() {  //遍尋最大的已儲存的seq值
        int re = -1;
//        if (m_iCheckReturnFlight == 0) {
            for (int i = 0; i < m_iPassengerMax; i++) {
                if (m_alPassengerSeatData.get(i).m_iPassengerSeq > re)
                    re = m_alPassengerSeatData.get(i).m_iPassengerSeq;
            }
//        } else {
//            for (int i = 0; i < m_iPassengerMax; i++) {
//                if (m_alPassengerSeatData.get(i).m_iPassengerSeqReturn > re)
//                    re = m_alPassengerSeatData.get(i).m_iPassengerSeqReturn;
//            }
//        }
        return re;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //按下方的確認按鈕, 送出選位結果至WS
            case R.id.btn_next:
                //目前沒有用到此邏輯
//                if (m_bReturnOrNot) { //是否為來回
//                    if (m_iCheckReturnFlight == 0) { //是否為去程
//                        m_iCheckReturnFlight = 1;
//                        //改為返程
//                        for (int i = 0; i < m_iPassengerMax; i++) {
//                            m_alPassengerSeatData.get(i).m_bReturn = true;
//                        }
//
//                        //設定回程選位資料
//                        if ( true == m_bIsTwoDeck ){
//                            if ( null != m_UpSeatFragmentInterface )
//                                m_UpSeatFragmentInterface.SetNowSelectSeat(m_alPassengerSeatData);
//                        }
//                        if ( null != m_DownSeatFragmentInterface )
//                            m_DownSeatFragmentInterface.SetNowSelectSeat(m_alPassengerSeatData);
//
//                        m_tvSelectSeatNumber.setText("");
//                        m_NextButton.setText(getText(R.string.confirm));
//                        m_iSeq = 0;
//                    } else {
//                        //傳給ws新的選位資料
//                        m_strDoWhat = SEND_SEAT;
//                        sendSelectSeatReq();
//                    }
//                } else {
                    //傳給ws新的選位資料
                    m_strDoWhat = SEND_SEAT;
                    sendSelectSeatReq();
//                }
                break;

            //切換至前一個乘客
            case R.id.rl_arrow_left:
            case R.id.btn_arrow_left:
                if ( 0 < m_iPassengerIndex ) {
                    //顯示前一個乘客的名字
                    m_tvWhoSelectSeat.setText(
                            m_alPassengerSeatData.get(m_iPassengerIndex-1).m_strPassengerName);
                    //顯示前一個乘客的座位
//                    if ( false == m_alPassengerSeatData.get(m_iPassengerIndex-1).m_bReturn ){
                        m_tvSelectSeatNumber.setText(
                                m_alPassengerSeatData.get(m_iPassengerIndex-1).m_strPassengerSeat);
//                    }else {
//                        m_tvSelectSeatNumber.setText(m_alPassengerSeatData.get(m_iPassengerIndex-1).m_strPassengerSeatReturn);
//                    }
                    //更新index
                    m_iPassengerIndex--;
                    //切換乘客 需重設白色圓圈
                    setPeopleCircleColor();
                    //設定當前乘客選位資料
                    if( true == m_bFragment ){
                        if ( null != m_UpSelectSeatFragment && null != m_UpSeatFragmentInterface ){
                            m_UpSeatFragmentInterface.ChangePassenger(m_iPassengerIndex);
                        }
                    }
                    if ( null != m_DownSelectSeatFragment && null != m_DownSeatFragmentInterface ){
                        m_DownSeatFragmentInterface.ChangePassenger(m_iPassengerIndex);
                    }
//                    SetNowSelectSeat(true);

                    if ( m_iPassengerIndex == 0 ){
                        m_rlLeftArrow.setVisibility(View.GONE);
                    }else {
                        m_rlLeftArrow.setVisibility(View.VISIBLE);
                    }
                    if ( m_iPassengerIndex == (m_iPassengerMax-1) ){
                        m_rlRightArrow.setVisibility(View.GONE);
                    }else {
                        m_rlRightArrow.setVisibility(View.VISIBLE);
                    }
                }
                break;

            //切換至下一個乘客
            case R.id.rl_arrow_right:
            case R.id.btn_arrow_right:
                if ( (m_iPassengerIndex + 1) < m_iPassengerMax ) {
                    //顯示下一個乘客的名字
                    m_tvWhoSelectSeat.setText(
                            m_alPassengerSeatData.get(m_iPassengerIndex+1).m_strPassengerName);
                    //顯示下一個乘客的座位
//                    if ( false == m_alPassengerSeatData.get(m_iPassengerIndex+1).m_bReturn ){
                        m_tvSelectSeatNumber.setText(
                                m_alPassengerSeatData.get(m_iPassengerIndex+1).m_strPassengerSeat);
//                    }else {
//                        m_tvSelectSeatNumber.setText(m_alPassengerSeatData.get(m_iPassengerIndex+1).m_strPassengerSeatReturn);
//                    }
                    //更新index
                    m_iPassengerIndex++;
                    //切換乘客 需重設白色圓圈
                    setPeopleCircleColor();
                    //設定當前乘客選位資料
                    if( true == m_bFragment ){
                        if ( null != m_UpSelectSeatFragment && null != m_UpSeatFragmentInterface ){
                            m_UpSeatFragmentInterface.ChangePassenger(m_iPassengerIndex);
                        }
                    }
                    if ( null != m_DownSelectSeatFragment && null != m_DownSeatFragmentInterface ){
                        m_DownSeatFragmentInterface.ChangePassenger(m_iPassengerIndex);
                    }
//                    SetNowSelectSeat(true);

                    if ( m_iPassengerIndex == 0 ){
                        m_rlLeftArrow.setVisibility(View.GONE);
                    }else {
                        m_rlLeftArrow.setVisibility(View.VISIBLE);
                    }
                    if ( m_iPassengerIndex == (m_iPassengerMax-1) ){
                        m_rlRightArrow.setVisibility(View.GONE);
                    }else {
                        m_rlRightArrow.setVisibility(View.VISIBLE);
                    }
                }
                break;

            //點擊右上角按鈕 顯示該航班的座位資訊圖
            case R.id.select_seat_top_about:
                Bitmap bitmap = ImageHandle.getScreenShot((Activity)m_Context);
                Bitmap blur = ImageHandle.BlurBuilder(m_Context, bitmap, 13.5f, 0.15f);

                Bundle bundle = new Bundle();
                bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
                if ( null != m_seatDataList && null != m_seatDataList.iataAircraftTypeCode )
                    bundle.putString(UiMessageDef.BUNDLE_SELECT_SEAT_WEBVIEW_URL, m_seatDataList.iataAircraftTypeCode);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(m_Context, CISelectSeatMapWebActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.anim_alpha_in, 0);

                bitmap.recycle();

//                CISelectSeatMapWebActivity m_popupWindow = new CISelectSeatMapWebActivity(m_Context,
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        m_seatDataList.iataAircraftTypeCode);
//                Rect rect = new Rect();
//                CISelectSeatMapActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//                m_popupWindow.showAtLocation(CISelectSeatMapActivity.this.getWindow().getDecorView(),
//                        Gravity.CENTER, 0, rect.top);
                break;
        }
    }

    //切換上下層
    @Override
    public void onItemClick(View v) {
        switch (v.getId()) {
            //上層
            case R.id.rl_left_bg:
                if (m_bFragment)
                    return;

                m_bFragment = true;

                //通知fragment 切換上下層 要更新座位圖
                if ( true == m_bIsTwoDeck ){
                    if ( null != m_UpSelectSeatFragment && null != m_UpSeatFragmentInterface ){
                        m_UpSeatFragmentInterface.ChangeFloor(m_iPassengerIndex, m_alPassengerSeatData);
                    }
                }
                if ( null != m_DownSelectSeatFragment && null != m_DownSeatFragmentInterface ){
                    m_DownSeatFragmentInterface.ChangeFloor(m_iPassengerIndex, m_alPassengerSeatData);
                }

                //切換至上層畫面
                ChangeSeatFragment();

                if ( false == m_bChangeFloor){
                    //設定當前乘客選為資料
                    SetNowSelectSeat(true);

                    m_bChangeFloor = true;
                }
                break;

            //下層
            case R.id.rl_right_bg:
                if (!m_bFragment)
                    return;

                m_bFragment = false;

                //通知fragment 切換上下層 要更新座位圖
                if ( true == m_bIsTwoDeck ){
                    if ( null != m_UpSelectSeatFragment && null != m_UpSeatFragmentInterface ){
                        m_UpSeatFragmentInterface.ChangeFloor(m_iPassengerIndex, m_alPassengerSeatData);
                    }
                }
                if ( null != m_DownSelectSeatFragment && null != m_DownSeatFragmentInterface ){
                    m_DownSeatFragmentInterface.ChangeFloor(m_iPassengerIndex, m_alPassengerSeatData);
                }

                //切換至下層畫面
                ChangeSeatFragment();
                //設定當前乘客選為資料
//                SetNowSelectSeat(true);
                break;
        }
    }

    //按返回上一頁
    public void onBackPressed() {
        CISelectSeatMapActivity.this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    public void onPause() {
        super.onPause();

        m_presenter.CancelGetSeatMap();
        m_presenter.CancelAllocateSeat();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        m_presenter = CISelectSeatPresenter.getInstance(null);

        if ( null != m_rlBg ){
            if ( m_rlBg.getBackground() instanceof BitmapDrawable){
                m_rlBg.setBackground(null);
            }
        }

        System.gc();
    }
}
