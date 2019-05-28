package ci.function.SeatSelection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import ci.function.Core.SLog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashMap;

import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CIProgressDialog;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ui.view.TwoItemNavigationBar;
import ci.ws.Models.CISeatFloor;
import ci.ws.Models.entities.CIAllocateSeatReq;
import ci.ws.Models.entities.CIGetSeatReq;
import ci.ws.Models.entities.CIPassengerListResp;
import ci.ws.Models.entities.CISeatInfo;
import ci.ws.Models.entities.CISeatInfoList;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CISelectSeatPresenter;
import ci.ws.Presenter.Listener.CISelectSeatListener;

/**
 * 選位頁面，未完成
 * Created by flowmahuang on 2016/4/7.
 */
@Deprecated
public class CISelectSeatActivity extends BaseActivity implements View.OnClickListener,
        TwoItemNavigationBar.ItemClickListener {
    private TwoItemNavigationBar bar = null;
    private NavigationBar m_Navigationbar = null;
    private FrameLayout m_flSelect = null;
    private ImageButton m_aboutButton = null;
    private ImageButton m_LeftArrowImage = null;
    private ImageButton m_RightArrowImage = null;
    private TextView m_WhoSelectSeat = null;
    private TextView m_SelectSeatNumber = null;
    private TextView m_DescriptionYour = null;
    private TextView m_DescriptionPartner = null;
    private TextView m_DescriptionAvailable = null;
    private TextView m_DescriptionOccupied = null;
    private TextView[] m_SelectSeatEnglish = null;
    private RecyclerView m_SelectSeatRecyclerView = null;
    private LinearLayout m_SeatEnglish = null;
    private LinearLayout m_SeatPeopleNumber = null;
    private Button m_NextButton = null;
    private View[] m_PeopleCircle = null;
    private View shadow = null;
    private View shadowTop = null;
    private GridLayoutManager m_UpperManager = null;
    private GridLayoutManager m_LowerManager = null;

    private CISelectSeatRecyclerViewAdapter m_adapter;
    private CISelectSeatRecyclerViewAdapter m_UpperAdapter;
    private CISelectSeatRecyclerViewAdapter m_LowerAdapter;
    private CISelectSeatPresenter m_presenter;
    private CISeatInfoList m_DataList;
    private CISeatFloor m_UpFloor;
    private CISeatFloor m_DownFloor;

    private ArrayList<String> passengerResp = null;
    private ArrayList<String> passengerNameResp = null;
    private ArrayList<String> paxNumberResp = null;
    private HashMap<Integer,String> passengerAlreadySelectSeat = null;
    private String[] passengerSeatDisplay = null;
    private String[] passengerSeatDisplayReturn = null;
    private String[] passengerName = null;
    private String departureStation = null;
    private String arrvialStation = null;
    private int[] passengerSelectSeq = null;
    private int[] passengerSelectSeqReturn = null;
    private int passengerMax = 0;
    private int passengerNum = 0;
    private int passengerNumResp;
    private int checkReturnFlight = 0;
    private int seq = 0;
    private int upperColNumber;
    private int lowerColNumber;
    private boolean returnOrNot = false;
    private boolean m_bFragment;
    private boolean isTwoDeck;

    private String pnr;
    private String segment_num;
    private String[] pax_num;
    private String doWhat = null;
    private static final String SELECT_SEAT = "select_seat";
    private static final String SEND_SEAT = "send_seat";

    private int sendCount = 1;
    private ArrayList<CIAllocateSeatReq> entity;

    private String strIsCheckSelectSeat = "N";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_select_seat;
    }

    @Override
    protected void initialLayoutComponent() {
        doWhat = SELECT_SEAT;
        entity = new ArrayList<>();
        passengerAlreadySelectSeat = new HashMap<>();
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_aboutButton = (ImageButton) findViewById(R.id.select_seat_top_about);
        m_LeftArrowImage = (ImageButton) findViewById(R.id.btn_arrow_left);
        m_RightArrowImage = (ImageButton) findViewById(R.id.btn_arrow_right);
        m_flSelect = (FrameLayout) findViewById(R.id.fl_select);
        m_SeatEnglish = (LinearLayout) findViewById(R.id.llayout_seat_english);
        m_SeatPeopleNumber = (LinearLayout) findViewById(R.id.llayout_select_seat_people_number);
        m_WhoSelectSeat = (TextView) findViewById(R.id.tv_seat_title_name);
        m_SelectSeatNumber = (TextView) findViewById(R.id.tv_select_seat_number);
        m_DescriptionYour = (TextView) findViewById(R.id.your_seat_image);
        m_DescriptionPartner = (TextView) findViewById(R.id.partner_seat_image);
        m_DescriptionAvailable = (TextView) findViewById(R.id.available_seat_image);
        m_DescriptionOccupied = (TextView) findViewById(R.id.occupied_seat_image);
        m_SelectSeatRecyclerView = (RecyclerView) findViewById(R.id.select_seat_gridview);
        m_NextButton = (Button) findViewById(R.id.btn_next);
        shadow = findViewById(R.id.vGradient);
        shadowTop = findViewById(R.id.vGradient2);


        m_presenter = CISelectSeatPresenter.getInstance(new CISelectSeatListener() {
            @Override
            public void onGetSeatMapSuccess(String rt_code, String rt_msg, CISeatInfoList SeatInfoList) {
                m_DataList = SeatInfoList;
                m_UpFloor = m_DataList.Up_SeatFloor;
                m_DownFloor = m_DataList.Down_SeatFloor;

                if (m_UpFloor.SeatCol != 0) {
                    upperColNumber = m_UpFloor.SeatCol;
                    m_UpperAdapter = new CISelectSeatRecyclerViewAdapter(m_Context, m_UpFloor);
                    m_UpperManager = new GridLayoutManager(m_Context, upperColNumber);
                }
                if (m_DownFloor.SeatCol != 0) {
                    lowerColNumber = m_DownFloor.SeatCol;
                    m_LowerAdapter = new CISelectSeatRecyclerViewAdapter(m_Context, m_DownFloor);
                    m_LowerManager = new GridLayoutManager(m_Context, lowerColNumber);
                }

                if (m_UpFloor.SeatCol != 0 && m_DownFloor.SeatCol != 0) {
                    isTwoDeck = true;
                    m_flSelect.setVisibility(View.VISIBLE);
                    m_adapter = m_LowerAdapter;
                    setSeatEnglish(lowerColNumber, m_DownFloor);
                    m_SelectSeatRecyclerView.setLayoutManager(m_LowerManager);
                    m_bFragment = false;
                    for (int i = 0; i < passengerMax; i++) {
                        if (!passengerSeatDisplay[i].equals("")) {
                            m_adapter.setSelectSeq(passengerSelectSeq[i]);
                            m_UpperAdapter.setSelectSeq(passengerSelectSeq[i]);
                            m_LowerAdapter.setSelectSeq(passengerSelectSeq[i]);
                            m_adapter.setPartnerSelection(passengerSeatDisplay[i]);
                            m_UpperAdapter.setPartnerSelection(passengerSeatDisplay[i]);
                            m_LowerAdapter.setPartnerSelection(passengerSeatDisplay[i]);
                        }
                    }
                    for (int i = 0; i <passengerAlreadySelectSeat.size(); i++){
                        m_adapter.setAlreadySelect(passengerAlreadySelectSeat.get(i));
                        m_UpperAdapter.setAlreadySelect(passengerAlreadySelectSeat.get(i));
                        m_LowerAdapter.setAlreadySelect(passengerAlreadySelectSeat.get(i));
                    }
                    m_adapter.setNowSelection(passengerSeatDisplay[0]);
                    m_UpperAdapter.setNowSelection(passengerSeatDisplay[0]);
                    m_LowerAdapter.setNowSelection(passengerSeatDisplay[0]);
                    m_adapter.setSelectSeq(passengerSelectSeq[0]);
                    m_UpperAdapter.setSelectSeq(passengerSelectSeq[0]);
                    m_LowerAdapter.setSelectSeq(passengerSelectSeq[0]);
                } else {
                    isTwoDeck = false;
                    m_flSelect.setVisibility(View.GONE);
                    m_adapter = m_LowerAdapter;
                    setSeatEnglish(lowerColNumber, m_DownFloor);
                    m_SelectSeatRecyclerView.setLayoutManager(m_LowerManager);
                    m_bFragment = false;
                    for (int i = 0; i < passengerMax; i++) {
                        if (!passengerSeatDisplay[i].equals("")) {
                            m_adapter.setSelectSeq(passengerSelectSeq[i]);
                            m_adapter.setPartnerSelection(passengerSeatDisplay[i]);
                        }
                    }
                    for (int i = 0; i <passengerAlreadySelectSeat.size(); i++){
                        m_adapter.setAlreadySelect(passengerAlreadySelectSeat.get(i));
                    }
                    if (!passengerSeatDisplay[0].equals("")) {
                        m_adapter.setSelectSeq(passengerSelectSeq[0]);
                    } else {
                        m_adapter.setSelectSeq(seq);
                    }
                    m_adapter.setNowSelection(passengerSeatDisplay[0]);


                }


                m_adapter.setOnItemClickListener(m_RecyclerViewItemClickListener());
                m_SelectSeatRecyclerView.setAdapter(m_adapter);
                m_adapter.notifyDataSetChanged();
            }

            @Override
            public void onGetSeatMapError(String rt_code, String rt_msg) {
                CIAlertDialog dialog = new CIAlertDialog(CISelectSeatActivity.this, new CIAlertDialog.OnAlertMsgDialogListener() {
                    @Override
                    public void onAlertMsgDialog_Confirm() {
                        finish();
                        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
                    }

                    @Override
                    public void onAlertMsgDialogg_Cancel() {

                    }
                });
                dialog.uiSetContentText(rt_msg);
                dialog.uiSetConfirmText(getResources().getString(R.string.confirm));
                dialog.uiSetTitleText(getResources().getString(R.string.warning));
                dialog.show();
            }

            @Override
            public void onAllocateSeatError(String rt_code, String rt_msg) {
                CIAlertDialog dialog = new CIAlertDialog(CISelectSeatActivity.this, new CIAlertDialog.OnAlertMsgDialogListener() {
                    @Override
                    public void onAlertMsgDialog_Confirm() {

                    }

                    @Override
                    public void onAlertMsgDialogg_Cancel() {

                    }
                });
                if (doWhat.equals(SEND_SEAT)) {
                    dialog.uiSetContentText(rt_msg);
                    dialog.uiSetConfirmText(getResources().getString(R.string.confirm));
                    dialog.uiSetTitleText(getResources().getString(R.string.warning));
                    dialog.show();
                    doWhat = SELECT_SEAT;
                }
            }

            @Override
            public void onAllocateSeatSuccess(String rt_code, String rt_msg, String strSeat) {
               SLog.i("seatmsg", rt_msg);
                if (sendCount < entity.size() && doWhat.equals(SEND_SEAT)) {
//                    m_presenter.AllocateSeat(entity.get(sendCount));
                    sendCount++;
                } else if (sendCount == entity.size() && doWhat.equals(SEND_SEAT)) {
                    if (returnOrNot) {
                        if (checkReturnFlight != 0) {
                            changeActivity(CISelectSeatResultActivity.class);
                        }
                        //去回程情況要傳送去回程資料，回程沒做
                    } else {
                        changeActivity(CISelectSeatResultActivity.class);
                    }
                }
            }

            @Override
            public void showProgress() {
                showProgressDialog(new CIProgressDialog.CIProgressDlgListener() {
                    @Override
                    public void onBackPressed() {
                        if (doWhat.equals(SELECT_SEAT)) {
                            m_presenter.CancelGetSeatMap();
                            finish();
                            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
                        } else {
                            m_presenter.CancelAllocateSeat();
                            doWhat = SELECT_SEAT;
                        }
                    }
                });
            }

            @Override
            public void hideProgress() {
                hideProgressDialog();
            }
        });

        initData();
        setSeatNumberCircle();
    }

    private void initData() {
        //Bundle取得初始化資料
        Bundle bundle = getIntent().getExtras();
        CITripListResp_Itinerary m_tripData = (CITripListResp_Itinerary) bundle.getSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);
        CIPassengerListResp m_PassengerData = (CIPassengerListResp) bundle.getSerializable(UiMessageDef.BUNDLE_PASSENGER_INFO);
        strIsCheckSelectSeat = bundle.getString(UiMessageDef.BUNDLE_IS_CHECK_IN_SELECT_SEAT, "N");

        passengerMax = m_PassengerData.Pax_Info.size();
        passengerName = new String[passengerMax];
        passengerSeatDisplay = new String[passengerMax];
        passengerSeatDisplayReturn = new String[passengerMax];
        passengerSelectSeq = new int[passengerMax];

        pax_num = new String[passengerMax];

        int tempSeq = 0;

        for (int i = 0; i < passengerMax; i++) {
            passengerName[i] = m_PassengerData.Pax_Info.get(i).First_Name + m_PassengerData.Pax_Info.get(i).Last_Name;
            passengerSeatDisplayReturn[i] = "";
            pax_num[i] = m_PassengerData.Pax_Info.get(i).Pax_Number;
           SLog.i("pax_num", m_PassengerData.Pax_Info.get(i).Pax_Number);


            if (!TextUtils.isEmpty(m_PassengerData.Pax_Info.get(i).Seat_Number)) {

                String seatNumber = m_PassengerData.Pax_Info.get(i).Seat_Number;
                if (seatNumber.substring(0,1).equals("0") && seatNumber.length() == 3) {
                    passengerAlreadySelectSeat.put(tempSeq,seatNumber.substring(1));
                    passengerSeatDisplay[i] = seatNumber.substring(1);
                } else {
                    passengerAlreadySelectSeat.put(tempSeq,seatNumber);
                    passengerSeatDisplay[i] = seatNumber;
                }

                passengerSelectSeq[i] = seq;
                seq++;
                tempSeq++;
               SLog.i("seq", Integer.toString(seq));
            } else {
                passengerSelectSeq[i] = -1;
                passengerSeatDisplay[i] = "";
            }
        }

        pnr = m_tripData.Pnr_Id;
        segment_num = m_tripData.Pnr_Seq;
        departureStation = m_tripData.Departure_Station;
        arrvialStation = m_tripData.Arrival_Station;

        m_WhoSelectSeat.setText(passengerName[passengerNum]);
        m_SelectSeatNumber.setText(passengerSeatDisplay[passengerNum]);

        CIGetSeatReq getSeatReq = new CIGetSeatReq();
        getSeatReq.Booking_Class = m_tripData.Booking_Class;
        getSeatReq.Departure_Date = m_tripData.Departure_Date;
        getSeatReq.Airlines = m_tripData.Airlines;
        getSeatReq.Flight_Number = m_tripData.Flight_Number;
        getSeatReq.Departure_Station = m_tripData.Departure_Station;
        getSeatReq.Arrival_Station = m_tripData.Arrival_Station;
        m_presenter.GetSeatMap(getSeatReq);

        if (returnOrNot)
            m_NextButton.setText(getText(R.string.next_flight));
        else
            m_NextButton.setText(getText(R.string.confirm));
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.select_seat_flight, departureStation, arrvialStation);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {
        }

        @Override
        public void onLeftMenuClick() {
        }

        @Override
        public void onBackClick() {
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {
        }

        @Override
        public void onDemoModeClick() {

        }
    };

    public void onBackPressed() {
        CISelectSeatActivity.this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(m_LeftArrowImage, 24, 24);
        vScaleDef.selfAdjustSameScaleView(m_RightArrowImage, 24, 24);
        vScaleDef.selfAdjustSameScaleView(m_aboutButton, 32, 32);
        vScaleDef.selfAdjustSameScaleView(m_DescriptionYour, 20, 20);
        vScaleDef.selfAdjustSameScaleView(m_DescriptionPartner, 20, 20);
        vScaleDef.selfAdjustSameScaleView(m_DescriptionAvailable, 20, 20);
        vScaleDef.selfAdjustSameScaleView(m_DescriptionOccupied, 20, 20);

        for (int i = 0; i < lowerColNumber; i++) {
            vScaleDef.setTextSize(13, m_SelectSeatEnglish[i]);
        }

        for (int i = 0; i < passengerMax; i++) {
            vScaleDef.selfAdjustSameScaleView(m_PeopleCircle[i], 6, 6);
            vScaleDef.setMargins(m_PeopleCircle[i], 0, 0, 6, 0);
        }
    }

    @Override
    protected void setOnParameterAndListener() {
        m_SelectSeatRecyclerView.addOnScrollListener(scrollListener());

        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_NextButton.setOnClickListener(this);
        m_LeftArrowImage.setOnClickListener(this);
        m_RightArrowImage.setOnClickListener(this);
        m_aboutButton.setOnClickListener(this);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        bar = TwoItemNavigationBar
                .newInstance(
                        getString(R.string.upper_deck),
                        getString(R.string.lower_deck), TwoItemNavigationBar.EInitItem.RIGHT);
        bar.setListener(this);
        fragmentTransaction.replace(R.id.fl_select, bar, bar.getTag());

        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {

    }

    @Override
    protected void onLanguageChangeUpdateUI() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (returnOrNot) {
                    if (checkReturnFlight == 0) {
                        checkReturnFlight++;
                        //切換另一航程選位前將舊有Partner紀錄與序號刪除
                        for (int i = 0; i < passengerMax; i++) {
                            passengerSelectSeq[i] = -1;
                        }
                        m_UpperAdapter.clearPartnerSelection();
                        m_UpperAdapter.setSelectSeq(0);
                        m_LowerAdapter.clearPartnerSelection();
                        m_LowerAdapter.setSelectSeq(0);

                        m_adapter.setNowSelection("");
                        m_adapter.notifyDataSetChanged();
                        m_SelectSeatNumber.setText("");
                        m_NextButton.setText(getText(R.string.confirm));
                        seq = 0;
                    } else {
                        doWhat = SEND_SEAT;
                        setWhoSelectToResp();
                    }
                } else {
                    doWhat = SEND_SEAT;
                    setWhoSelectToResp();
                }
                break;
            case R.id.btn_arrow_left:
                //上一位乘客，設定條件使其不會低於某個數字讓其顯示正常
                if (passengerNum != 0) {
                    //情況為去程且切換之乘客乘客已選
                    if (checkReturnFlight == 0 && !passengerSeatDisplay[passengerNum - 1].equals("")) {
                        //如果現乘客已選位
                        if (!passengerSeatDisplay[passengerNum].equals("")) {
                            //如果現乘客第一次選位，沒有紀錄seq，就將seq記錄下來
                            if (passengerSelectSeq[passengerNum] == -1)
                                passengerSelectSeq[passengerNum] = seq;
                        }

                        ///更新現有視窗為切換之乘客的選擇
                        passengerNum--;
                        m_WhoSelectSeat.setText(passengerName[passengerNum]);
                        m_adapter.setSelectSeq(passengerSelectSeq[passengerNum]);
                        m_adapter.setNowSelection(passengerSeatDisplay[passengerNum]);
                        m_adapter.notifyDataSetChanged();
                        m_SelectSeatNumber.setText(passengerSeatDisplay[passengerNum]);
                    } else if (checkReturnFlight == 1 && !passengerSeatDisplayReturn[passengerNum - 1].equals("")) {
                        if (!passengerSeatDisplayReturn[passengerNum].equals("")) {
                            //如果現乘客第一次選位，沒有紀錄seq，就將seq記錄下來
                            if (passengerSelectSeqReturn[passengerNum] == -1)
                                passengerSelectSeqReturn[passengerNum] = seq;
                        }
                        passengerNum--;
                        m_WhoSelectSeat.setText(passengerName[passengerNum]);
                        m_adapter.setSelectSeq(passengerSelectSeqReturn[passengerNum]);
                        m_adapter.setNowSelection(passengerSeatDisplayReturn[passengerNum]);
                        m_adapter.notifyDataSetChanged();
                        m_SelectSeatNumber.setText(passengerSeatDisplayReturn[passengerNum]);
                    } else { //切換之乘客並無選擇的情況
                        if (checkReturnFlight == 0) {
                            //如果現乘客已選位
                            if (!passengerSeatDisplay[passengerNum].equals("")) {
                                //如果現乘客第一次選位，沒有紀錄seq，就將seq記錄下來
                                if (passengerSelectSeq[passengerNum] == -1)
                                    passengerSelectSeq[passengerNum] = seq;
                            }
                            //切換之乘客沒有選擇，先確認有無紀錄資訊，如無遍尋全部的已儲存seq值，如果符合條件即seq + 1
                            if (searchSeqMax() == seq)
                                seq++;

                            //因切換之乘客並無選擇，將畫面上資訊清空，並確認其seq是否曾經選過
                            passengerNum--;
                            m_WhoSelectSeat.setText(passengerName[passengerNum]);
                            if (passengerSelectSeq[passengerNum] != -1)
                                m_adapter.setSelectSeq(passengerSelectSeq[passengerNum]);
                            else
                                m_adapter.setSelectSeq(seq);
                            m_adapter.setNowSelection("");
                            m_adapter.notifyDataSetChanged();
                            m_SelectSeatNumber.setText("");
                        } else {
                            //如果現乘客已選位
                            if (!passengerSeatDisplayReturn[passengerNum].equals("")) {
                                //如果現乘客第一次選位，沒有紀錄seq，就將seq記錄下來
                                if (passengerSelectSeqReturn[passengerNum] == -1)
                                    passengerSelectSeqReturn[passengerNum] = seq;
                            }
                            //切換之乘客沒有選擇，先確認有無紀錄資訊，如無遍尋全部的已儲存seq值，如果符合條件即seq + 1
                            if (searchSeqMax() == seq)
                                seq++;

                            //因切換之乘客並無選擇，將畫面上資訊清空，並確認其seq是否曾經選過
                            passengerNum--;
                            m_WhoSelectSeat.setText(passengerName[passengerNum]);
                            if (passengerSelectSeqReturn[passengerNum] != -1)
                                m_adapter.setSelectSeq(passengerSelectSeqReturn[passengerNum]);
                            else
                                m_adapter.setSelectSeq(seq);
                            m_adapter.setNowSelection("");
                            m_adapter.notifyDataSetChanged();
                            m_SelectSeatNumber.setText("");
                        }
                    }
                    setPeopleCircleColor();
                }
                break;
            case R.id.btn_arrow_right:
                //下一位乘客，設定條件使其不會高於某個數字讓其顯示正常
                if (passengerNum != passengerMax - 1) {
                    //情況為去程且切換之乘客乘客已選
                    if (checkReturnFlight == 0 && !passengerSeatDisplay[passengerNum + 1].equals("")) {
                        //如果現乘客已選位
                        if (!passengerSeatDisplay[passengerNum].equals("")) {
                            //如果現乘客第一次選位，沒有紀錄seq，就將seq記錄下來
                            if (passengerSelectSeq[passengerNum] == -1)
                                passengerSelectSeq[passengerNum] = seq;
                        }

                        //更新現有視窗為切換之乘客的選擇
                        passengerNum++;
                        m_WhoSelectSeat.setText(passengerName[passengerNum]);
                        m_adapter.setSelectSeq(passengerSelectSeq[passengerNum]);
                        m_SelectSeatNumber.setText(passengerSeatDisplay[passengerNum]);
                        m_adapter.setNowSelection(passengerSeatDisplay[passengerNum]);
                        m_adapter.notifyDataSetChanged();
                    } else if (checkReturnFlight == 1 && !passengerSeatDisplayReturn[passengerNum + 1].equals("")) {
                        //如果現乘客已選位
                        if (!passengerSeatDisplayReturn[passengerNum].equals("")) {
                            //如果現乘客第一次選位，沒有紀錄seq，就將seq記錄下來
                            if (passengerSelectSeqReturn[passengerNum] == -1)
                                passengerSelectSeqReturn[passengerNum] = seq;
                        }
                        //更新現有視窗為切換之乘客的選擇
                        passengerNum++;
                        m_WhoSelectSeat.setText(passengerName[passengerNum]);
                        m_adapter.setSelectSeq(passengerSelectSeqReturn[passengerNum]);
                        m_SelectSeatNumber.setText(passengerSeatDisplayReturn[passengerNum]);
                        m_adapter.setNowSelection(passengerSeatDisplayReturn[passengerNum]);
                        m_adapter.notifyDataSetChanged();

                    } else {
                        if (checkReturnFlight == 0) {
                            //如果現乘客已選位
                            if (!passengerSeatDisplay[passengerNum].equals("")) {
                                //如果現乘客第一次選位，沒有紀錄seq，就將seq記錄下來
                                if (passengerSelectSeq[passengerNum] == -1)
                                    passengerSelectSeq[passengerNum] = seq;
                            }
                            //切換之乘客沒有選擇，先確認有無紀錄資訊，如無再遍尋全部的已儲存seq值，如果符合條件即seq + 1
                            if (searchSeqMax() == seq)
                                seq++;

                            //因切換之乘客並無選擇，將畫面上資訊清空，並確認其seq是否曾經選過
                            passengerNum++;
                            m_WhoSelectSeat.setText(passengerName[passengerNum]);
                            if (passengerSelectSeq[passengerNum] != -1)
                                m_adapter.setSelectSeq(passengerSelectSeq[passengerNum]);
                            else
                                m_adapter.setSelectSeq(seq);
                            m_adapter.setNowSelection("");
                            m_adapter.notifyDataSetChanged();
                            m_SelectSeatNumber.setText("");
                        } else {
                            //如果現乘客已選位
                            if (!passengerSeatDisplayReturn[passengerNum].equals("")) {
                                //如果現乘客第一次選位，沒有紀錄seq，就將seq記錄下來
                                if (passengerSelectSeqReturn[passengerNum] == -1)
                                    passengerSelectSeqReturn[passengerNum] = seq;
                            }
                            //切換之乘客沒有選擇，先確認有無紀錄資訊，如無再遍尋全部的已儲存seq值，如果符合條件即seq + 1
                            if (searchSeqMax() == seq)
                                seq++;

                            //因切換之乘客並無選擇，將畫面上資訊清空，並確認其seq是否曾經選過
                            passengerNum++;
                            m_WhoSelectSeat.setText(passengerName[passengerNum]);
                            if (passengerSelectSeqReturn[passengerNum] != -1)
                                m_adapter.setSelectSeq(passengerSelectSeqReturn[passengerNum]);
                            else
                                m_adapter.setSelectSeq(seq);
                            m_SelectSeatNumber.setText("");
                            m_adapter.setNowSelection("");
                            m_adapter.notifyDataSetChanged();
                        }
                    }
                    setPeopleCircleColor();
                }
                break;
            case R.id.select_seat_top_about:
                Bitmap bitmap = ImageHandle.getScreenShot((Activity)m_Context);
                Bitmap blur = ImageHandle.BlurBuilder(m_Context, bitmap, 13.5f, 0.15f);

                Bundle bundle = new Bundle();
                bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
                if ( null != m_DataList && null != m_DataList.iataAircraftTypeCode )
                    bundle.putString(UiMessageDef.BUNDLE_SELECT_SEAT_WEBVIEW_URL, m_DataList.iataAircraftTypeCode);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(m_Context, CISelectSeatMapWebActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.anim_alpha_in, 0);

                bitmap.recycle();
//                CISelectSeatMapWebActivity m_popupWindow = new CISelectSeatMapWebActivity(m_Context,
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        m_DataList.iataAircraftTypeCode);
//                Rect rect = new Rect();
//                CISelectSeatActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//                m_popupWindow.showAtLocation(CISelectSeatActivity.this.getWindow().getDecorView(),
//                        Gravity.CENTER, 0, rect.top);
                break;
        }
    }

    @Override
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.rl_left_bg:
                if (m_bFragment)
                    return;
                m_bFragment = true;
                m_adapter = m_UpperAdapter;

                m_SelectSeatRecyclerView.setAdapter(m_adapter);
                m_SelectSeatRecyclerView.setLayoutManager(
                        new GridLayoutManager(m_Context, m_DataList.Up_SeatFloor.SeatCol));

                if (passengerSelectSeq[passengerNum] != -1)
                    m_adapter.setSelectSeq(passengerSelectSeq[passengerNum]);
                else
                    m_adapter.setSelectSeq(seq);
                m_adapter.setNowSelection(passengerSeatDisplay[passengerNum]);

                m_SeatEnglish.removeAllViewsInLayout();
                setSeatEnglish(upperColNumber, m_UpFloor);
                m_adapter.notifyDataSetChanged();
                m_adapter.setOnItemClickListener(m_RecyclerViewItemClickListener());
                break;
            case R.id.rl_right_bg:
                if (!m_bFragment)
                    return;
                m_bFragment = false;
                m_adapter = m_LowerAdapter;
                m_SelectSeatRecyclerView.setAdapter(m_adapter);
                m_SelectSeatRecyclerView.setLayoutManager(
                        new GridLayoutManager(m_Context, m_DataList.Down_SeatFloor.SeatCol));
                if (passengerSelectSeq[passengerNum] != -1)
                    m_adapter.setSelectSeq(passengerSelectSeq[passengerNum]);
                else
                    m_adapter.setSelectSeq(seq);
                m_adapter.setNowSelection(passengerSeatDisplay[passengerNum]);
                m_SeatEnglish.removeAllViewsInLayout();
                setSeatEnglish(lowerColNumber, m_DownFloor);
                m_adapter.notifyDataSetChanged();
                m_adapter.setOnItemClickListener(m_RecyclerViewItemClickListener());
                break;
        }
    }

    private RecyclerView.OnScrollListener scrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                shadow.setVisibility(View.VISIBLE);
                shadowTop.setVisibility(View.VISIBLE);
                if (!recyclerView.canScrollVertically(1) && !recyclerView.canScrollVertically(-1)) {
                    shadow.setVisibility(View.GONE);
                    shadowTop.setVisibility(View.GONE);
                } else if (!recyclerView.canScrollVertically(1)) {
                    shadow.setVisibility(View.GONE);
                } else if (!recyclerView.canScrollVertically(-1)) {
                    shadowTop.setVisibility(View.GONE);
                }
            }
        };
    }

    private void setWhoSelectToResp() {
        passengerNumResp = 0;
        passengerResp = new ArrayList<>();
        passengerNameResp = new ArrayList<>();
        paxNumberResp = new ArrayList<>();
        sendCount = 1;
        for (int i = 0; i < passengerMax; i++) {
            if (!passengerSeatDisplay[i].equals("")) {
                passengerResp.add(passengerSeatDisplay[i]);
                passengerNameResp.add(passengerName[i]);
                paxNumberResp.add(pax_num[i]);
                passengerNumResp++;
            }
        }

        if (passengerNumResp > 0) {
            for (int i = 0; i < passengerNumResp; i++) {
                CIAllocateSeatReq seat = new CIAllocateSeatReq();
//                seat.Seat_Number = passengerResp.get(i);
                seat.Pnr_id = pnr;
//                seat.Pax_Number = paxNumberResp.get(i);
                seat.Pnr_Seq = segment_num;
//                seat.Is_CPR = strIsCheckSelectSeat;
                entity.add(seat);
                m_presenter.AllocateSeat(entity.get(i));
            }
        } else {
            changeActivity(CISelectSeatResultActivity.class);
        }
    }

    private void changeActivity(Class clazz) {
        Intent intent = new Intent();
        intent.setClass(m_Context, clazz);
        intent.putExtra("ReturnOrNot", returnOrNot);
        intent.putExtra("PassengerName", passengerName);
        intent.putExtra("PassengerNum", passengerMax);
        intent.putExtra("Seat", passengerSeatDisplay);
        intent.putExtra("Departure", departureStation);
        intent.putExtra("Arrvial", arrvialStation);
        if (returnOrNot)
            intent.putExtra("SeatReturn", passengerSeatDisplayReturn);

        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private void setSeatEnglish(int col, CISeatFloor s) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        m_SelectSeatEnglish = new TextView[col];
        for (int i = 0; i < col; i++) {
            m_SelectSeatEnglish[i] = new TextView(m_Context);
            m_SelectSeatEnglish[i].setGravity(Gravity.CENTER);
            m_SelectSeatEnglish[i].setLayoutParams(params);
            m_SelectSeatEnglish[i].setTextColor(Color.parseColor("#b2b2b2"));
            if (s.arColTextList.get(i).ColType == CISeatInfo.CISeatType.Aisle)
                m_SelectSeatEnglish[i].setText("");
            else
                m_SelectSeatEnglish[i].setText(s.arColTextList.get(i).ColName);
            m_SeatEnglish.addView(m_SelectSeatEnglish[i]);
        }

        DisplayMetrics m = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(m);
        m_adapter.setItemWidth(m.widthPixels / col / (m.widthPixels / 360) - col / 2);
    }

    private void setSeatNumberCircle() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        m_PeopleCircle = new View[passengerMax];
        for (int i = 0; i < passengerMax; i++) {
            m_PeopleCircle[i] = new View(m_Context);
            m_PeopleCircle[i].setBackgroundResource(R.drawable.bg_select_seat_people_circle);
            if (i != passengerNum)
                m_PeopleCircle[i].setAlpha(0.5f);
            m_PeopleCircle[i].setLayoutParams(params);
            m_SeatPeopleNumber.addView(m_PeopleCircle[i]);
        }
    }

    private int searchSeqMax() {  //遍尋最大的已儲存的seq值
        int re = -1;
        if (checkReturnFlight == 0) {
            for (int i = 0; i < passengerMax; i++) {
                if (passengerSelectSeq[i] > re)
                    re = passengerSelectSeq[i];
            }
        } else {
            for (int i = 0; i < passengerMax; i++) {
                if (passengerSelectSeqReturn[i] > re)
                    re = passengerSelectSeqReturn[i];
            }
        }
        return re;
    }

    private void setPeopleCircleColor() {
        for (int i = 0; i < passengerMax; i++) {
            if (i != passengerNum)
                m_PeopleCircle[i].setAlpha(0.5f);
            else
                m_PeopleCircle[i].setAlpha(1);
        }
    }

    private CISelectSeatRecyclerViewAdapter.OnRecyclerViewItemClickListener m_RecyclerViewItemClickListener() {
        return new CISelectSeatRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String display) {
                //原本有選擇，按下同一位置的取消選擇動作
                if (display.equals(passengerSeatDisplay[passengerNum])) {
                    m_adapter.delPartnerSelection(display);
                    passengerSeatDisplay[passengerNum] = "";
                    m_adapter.setNowSelection("");
                    if (isTwoDeck) {
                        m_LowerAdapter.setNowSelection("");
                        m_UpperAdapter.setNowSelection("");
                    }
                    m_adapter.notifyDataSetChanged();
                    m_SelectSeatNumber.setText("");
                } else {
                    if (checkReturnFlight == 1) {  //返程
                        //現乘客無選位情況，先行將位置加入partner之map
                        if (passengerSeatDisplayReturn[passengerNum].equals("")) {
                            m_adapter.setPartnerSelection(display);
                            if (isTwoDeck) {
                                m_LowerAdapter.setPartnerSelection(display);
                                m_UpperAdapter.setPartnerSelection(display);
                            }
                        } else {  //現乘客有選位情況，先將map原值刪除後重新加入
                            m_adapter.delPartnerSelection(passengerSeatDisplayReturn[passengerNum]);
                            m_adapter.setPartnerSelection(display);
                            if (isTwoDeck) {
                                m_LowerAdapter.delPartnerSelection(passengerSeatDisplayReturn[passengerNum]);
                                m_UpperAdapter.delPartnerSelection(passengerSeatDisplayReturn[passengerNum]);
                                m_LowerAdapter.setPartnerSelection(display);
                                m_UpperAdapter.setPartnerSelection(display);
                            }
                        }
                    } else {  //去程
                        //現乘客無選位情況，先行將位置加入partner之map
                        if (passengerSeatDisplay[passengerNum].equals("")) {
                            m_adapter.setPartnerSelection(display);
                            if (isTwoDeck) {
                                m_LowerAdapter.setPartnerSelection(display);
                                m_UpperAdapter.setPartnerSelection(display);
                            }
                        } else {  //現乘客有選位情況，先將map原值刪除後重新加入map
                            m_adapter.delPartnerSelection(passengerSeatDisplay[passengerNum]);
                            m_adapter.setPartnerSelection(display);
                            if (isTwoDeck) {
                                m_LowerAdapter.delPartnerSelection(passengerSeatDisplay[passengerNum]);
                                m_UpperAdapter.delPartnerSelection(passengerSeatDisplay[passengerNum]);
                                m_LowerAdapter.setPartnerSelection(display);
                                m_UpperAdapter.setPartnerSelection(display);
                            }
                        }
                    }

                    //總之就是更新現乘客現在選位，不管現在有無選位都更新
                    if (checkReturnFlight == 1) {
                        passengerSeatDisplayReturn[passengerNum] = display;
                    } else {
                        passengerSeatDisplay[passengerNum] = display;
                    }
                    m_adapter.setNowSelection(display);
                    if (isTwoDeck) {
                        m_LowerAdapter.setNowSelection(display);
                        m_UpperAdapter.setNowSelection(display);
                    }
                    m_SelectSeatNumber.setText(display);
                    m_adapter.notifyDataSetChanged();
                }
            }
        };
    }
}
