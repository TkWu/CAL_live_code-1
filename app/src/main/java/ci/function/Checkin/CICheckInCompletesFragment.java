package ci.function.Checkin;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import ci.function.Base.BaseFragment;
import ci.function.BoardingPassEWallet.BoardingPass.CIBoardingPassCardActivity;
import ci.function.Core.SLog;
import ci.function.SeatSelection.CISelectSeatMapActivity;
import ci.ui.WebView.CIWithoutInternetActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ui.view.ShadowBar.ShadowBarRecycleView;
import ci.ws.Models.entities.CIAllocateSeatCheckInReq;
import ci.ws.Models.entities.CIAllocateSeatCheckInReq_Pax;
import ci.ws.Models.entities.CIBoardPassResp;
import ci.ws.Models.entities.CIBoardPassResp_Itinerary;
import ci.ws.Models.entities.CIBoardPassResp_PnrInfo;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckIn_ItineraryInfo_Resp;
import ci.ws.Models.entities.CICheckIn_Resp;
import ci.ws.Models.entities.CICheckInAllPaxResp;
import ci.ws.Models.entities.CIMarkBPAsPrintedEntity;
import ci.ws.Models.entities.CIPassengerListResp;
import ci.ws.Models.entities.CIPassengerListResp_PaxInfo;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CIInquiryBoardPassPresenter;
import ci.ws.Presenter.CIMarkBPAsPrintedPresenter;
import ci.ws.Presenter.Listener.CIInquiryBoardPassListener;
import ci.ws.Presenter.Listener.CIMarkBPAsPrintedListener;

/**
 * Created by Ryan on 16/3/10.
 */
public class CICheckInCompletesFragment extends BaseFragment {

    class CheckInItem {

        String strAccount;
        String strName;
        String strSeat;

        //是否為嬰兒
        boolean bIsINF = false;
        boolean bIsChangeSeat = false;

        int iPaxInfoPos;
        int iItineraryPos;
    }

    private RelativeLayout m_rlayout_bg = null;

    private ShadowBarRecycleView m_LastItemRecycleView = null;
    private RecyclerView m_RecyclerView = null;
    private RecyclerAdpter m_RecyclerAdpter = null;

    private ArrayList<CheckInItem> m_arCheckinList = null;

    private ArrayList<CICheckIn_Resp> m_arCheckInPaxInfoList = null;

    private ArrayList<String> m_arPnrId = null;

    private CIBoardPassResp m_BoardPassResp = null;
    private ArrayList<CIBoardPassResp_Itinerary> m_alBoardPass_Itinerary = new ArrayList<>();
    private CICheckInAllPaxResp m_arPassenger = null;
//    private CIInquiryFlightStationPresenter m_InquiryFlightStationPresenter = null;

//    //2016-10-21 Ryan, 美國線邏輯判斷只要其中一站是美國線就不顯示登機證
//    private Boolean m_bIsUSAStation = false;

    //2016-10-28 ryan , 新增記錄Mark的航段
    private CIMarkBPAsPrintedEntity m_markBPAsPrintedEntity = null;
    //2016-10-28 ryan , 記錄可以Mark的航段資訊
    private LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity> m_mapMarkList = new LinkedHashMap<>();

    private CIInquiryBoardPassListener m_OnInquiryBoardPassListener = new CIInquiryBoardPassListener() {
        @Override
        public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
            m_BoardPassResp = datas;

            if (null == m_BoardPassResp)
                return;

            //依航段整理出需要的arrayList
            m_alBoardPass_Itinerary.clear();

            for (int i = 0; i < m_BoardPassResp.Pnr_Info.size(); i++) {

                boolean bIsEffectiveData = true;
                CIBoardPassResp_PnrInfo pnrInfo = m_BoardPassResp.Pnr_Info.get(i);

                //rt_code不為000時, 該pnr資料已失效
                if (!pnrInfo.rt_code.equals("000"))
                    bIsEffectiveData = false;

                //確認該pnr資料是否有航段資料
                if (true == bIsEffectiveData) {
                    if (null == pnrInfo.Itinerary
                            || 0 >= pnrInfo.Itinerary.size())
                        bIsEffectiveData = false;
                }

                //確認該pnr的各個航段是否有乘客資料
                if (true == bIsEffectiveData) {

                    for (int j = 0; j < pnrInfo.Itinerary.size(); j++) {
                        CIBoardPassResp_Itinerary itinerary = pnrInfo.Itinerary.get(j);
                        if (null == itinerary.Pax_Info || 0 >= itinerary.Pax_Info.size()) {
                            bIsEffectiveData = false;
                        }

                        if (true == bIsEffectiveData) {
                            m_alBoardPass_Itinerary.add(itinerary);
                        }
                    }
                }
            }

            showBoardingPassCard();
        }

        @Override
        public void onError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };


    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_checkin_completes;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_rlayout_bg = (RelativeLayout) view.findViewById(R.id.llayout_bg);

        m_LastItemRecycleView = (ShadowBarRecycleView) view.findViewById(R.id.layout_recycleview);
        m_RecyclerView = m_LastItemRecycleView.getRecyclerView();
        m_RecyclerView.setHasFixedSize(true);

        setCheckInList();

        m_RecyclerAdpter = new RecyclerAdpter();
        m_RecyclerView.setAdapter(m_RecyclerAdpter);
        m_RecyclerAdpter.notifyDataSetChanged();
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_rlayout_bg);
        m_RecyclerView.setPadding(vScaleDef.getLayoutWidth(10), 0, vScaleDef.getLayoutWidth(10), 0);
        m_LastItemRecycleView.setShadowBarHeight(vScaleDef.getLayoutHeight(16));
    }

    @Override
    protected void setOnParameterAndListener(View view) {

    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        //m_InquiryFlightStationPresenter = CIInquiryFlightStationPresenter.getInstance(null,null);
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

    public void setPaxInfoList(ArrayList<CICheckIn_Resp> arCheckInPaxInfo, CICheckInAllPaxResp arPassenger) {
        m_arCheckInPaxInfoList = arCheckInPaxInfo;
        m_arPassenger = arPassenger;
    }

    public void setMarkInfo( CIMarkBPAsPrintedEntity markBPAsPrintedEntity, LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity> mapMarkList ){
        m_markBPAsPrintedEntity = markBPAsPrintedEntity;
        m_mapMarkList = mapMarkList;
    }

    private void setCheckInList() {
        m_arCheckinList = new ArrayList<>();
        m_arPnrId = new ArrayList<>();

        if (null == m_arCheckInPaxInfoList) {
            return;
        }

        for (int iIdx = 0; iIdx < m_arCheckInPaxInfoList.size(); iIdx++) {

            for (int iPos = 0; iPos < m_arCheckInPaxInfoList.get(iIdx).Itinerary_Info.size(); iPos++) {

                CICheckIn_ItineraryInfo_Resp entity = m_arCheckInPaxInfoList.get(iIdx).Itinerary_Info.get(iPos);

                CheckInItem passengers = new CheckInItem();

                passengers.strAccount = entity.Did;
                passengers.strName = m_arCheckInPaxInfoList.get(iIdx).First_Name + " " + m_arCheckInPaxInfoList.get(iIdx).Last_Name;
                passengers.strSeat = entity.Seat_Number;
                passengers.iPaxInfoPos = iIdx;
                passengers.iItineraryPos = iPos;
                passengers.bIsINF = CIPassengerListResp_PaxInfo.PASSENGER_INFANT.equals(m_arCheckInPaxInfoList.get(iIdx).Pax_Type)? true : false;
                passengers.bIsChangeSeat = m_arCheckInPaxInfoList.get(iIdx).Itinerary_Info.get(iPos).Is_Change_Seat;
                m_arCheckinList.add(passengers);

                m_arPnrId.add(m_arCheckInPaxInfoList.get(iIdx).Pnr_Id);
            }

        }

    }

    public class RecyvlerViewHolder extends RecyclerView.ViewHolder {

        LinearLayout m_llayoutbg;
        ImageView m_ivBoardingPass;
        TextView m_tvName;
        TextView m_tvText;
        Button m_btnView;
        View m_vDiv;
        ImageView m_ivSeat;
        TextView m_tvSeat;
        ImageButton m_ivbtn_Edit;
        Button m_btnSelect;

        //2016-10-21 Ryan, 不支援線上電子登機證
        TextView m_tvUnboarding_pass;


        RecyvlerViewHolder(View view) {
            super(view);
            m_llayoutbg = (LinearLayout) view.findViewById(R.id.root);
            m_tvName = (TextView) view.findViewById(R.id.tv_name);
            m_ivBoardingPass = (ImageView) view.findViewById(R.id.iv_boarding_pass);
            m_tvText = (TextView) view.findViewById(R.id.tv_boarding_pass);
            m_btnView = (Button) view.findViewById(R.id.btn_view);
            m_vDiv = view.findViewById(R.id.vDiv);
            m_ivSeat = (ImageView) view.findViewById(R.id.iv_seat);
            m_tvSeat = (TextView) view.findViewById(R.id.tv_seat);
            m_ivbtn_Edit = (ImageButton) view.findViewById(R.id.ivbtn_edit);
            m_btnSelect = (Button) view.findViewById(R.id.btn_select);

            //2016-10-21 Ryan, 不支援線上電子登機證
            m_tvUnboarding_pass = (TextView) view.findViewById(R.id.tv_unboarding_pass);

            m_btnView.setOnClickListener(m_onclick);
            m_btnSelect.setOnClickListener(m_onclick);
            m_ivbtn_Edit.setOnClickListener(m_onclick);
        }
    }

    View.OnClickListener m_onclick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (R.id.btn_view == v.getId()) {

                CIInquiryBoardPassPresenter.getInstance(m_OnInquiryBoardPassListener)
                        .InquiryBoardPassFromWSByPNRListAndCardNo("", "","",getPnrIdSet());

            } else if (R.id.ivbtn_edit == v.getId()
                    || R.id.btn_select == v.getId()) {


                int iPosition = (int) v.getTag();


                final int iPaxInfoPos = m_arCheckinList.get(iPosition).iPaxInfoPos;
                final int iItineraryPos = m_arCheckinList.get(iPosition).iItineraryPos;

//                final String strPnr = m_arCheckInPaxInfoList.get(iPaxInfoPos).Pnr_Id;
//                final String strSegment_No = m_arCheckInPaxInfoList.get(iPaxInfoPos).Itinerary_Info.get(iItineraryPos).Segment_Number;


                CICheckIn_ItineraryInfo_Resp respItineraryInfoEntity = m_arCheckInPaxInfoList.get(iPaxInfoPos).Itinerary_Info.get(iItineraryPos);

                CITripListResp_Itinerary m_tripData = new CITripListResp_Itinerary();
                m_tripData.Departure_Station = respItineraryInfoEntity.Departure_Station;
                m_tripData.Arrival_Station = respItineraryInfoEntity.Arrival_Station;
                m_tripData.Booking_Class = respItineraryInfoEntity.BookingClass;
                m_tripData.Departure_Date = respItineraryInfoEntity.Departure_Date;
                m_tripData.Airlines = respItineraryInfoEntity.Airlines;
                m_tripData.Flight_Number = respItineraryInfoEntity.Flight_Number;
                m_tripData.Pnr_Id = m_arCheckInPaxInfoList.get(iPaxInfoPos).Pnr_Id;
                m_tripData.Segment_Number = respItineraryInfoEntity.Segment_Number;

                CIPassengerListResp passengerList = getPassengerList(iPaxInfoPos, iItineraryPos);

                //643924-2019-start
                /*Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(UiMessageDef.BUNDLE_IS_CHECK_IN_SELECT_SEAT, "Y");

                //行程資料
                bundle.putSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, m_tripData);
//
                //乘客資料
                bundle.putSerializable(UiMessageDef.BUNDLE_PASSENGER_INFO, passengerList);

                intent.putExtras(bundle);

                ChangeActivity(intent, CISelectSeatMapActivity.class, UiMessageDef.REQUEST_CODE_CHECK_IN_SELECT_SEAT);*/

                Intent intent = new Intent();
                intent.putExtra(
                        UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG,
                        getString(R.string.select_seat));
                for ( int i = 0; i < passengerList.Pax_Info.size(); i ++ ){
                    intent.putExtra(
                            UiMessageDef.BUNDLE_WEBVIEW_URL_TAG,
                            getString(R.string.trip_detail_checkin_url)+"&IIdentification="+m_tripData.Pnr_Id+"&ISurname="+passengerList.Pax_Info.get(i).Last_Name);
                }
                intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_IS_SHOW_CLOSE_BTN_TAG, true);
                ChangeActivity(intent, CIWithoutInternetActivity.class);
                //643924-2019-end
            }
        }
    };


    private void checkIsNeedMark(){

        //有需要Mark 在做
        if ( null != m_markBPAsPrintedEntity && m_markBPAsPrintedEntity.Pax_Info.size() > 0 ){

            CIMarkBPAsPrintedPresenter.getInstance(new CIMarkBPAsPrintedListener() {
                @Override
                public void onMarkBPAsPrintSuccess(String rt_code, String rt_msg) {
                   SLog.i("[CheckIn]", "onMarkBPAsPrintSuccess ");
                }

                @Override
                public void onMarkBPAsPrintError(String rt_code, String rt_msg) {
                   SLog.i("[CheckIn]", "onMarkBPAsPrintError " + rt_msg );
                }

                @Override
                public void showProgress() {
                    showProgressDialog();
                }

                @Override
                public void hideProgress() {
                    hideProgressDialog();
                }

            }).MarkBPAsPrinted(m_markBPAsPrintedEntity);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        //更新乘客資料
        if (requestCode == UiMessageDef.REQUEST_CODE_CHECK_IN_SELECT_SEAT
                && resultCode == Activity.RESULT_OK) {

            if (null != data) {
                CIAllocateSeatCheckInReq SeatCheckInEntity =
                        (CIAllocateSeatCheckInReq) data.getSerializableExtra(
                                UiMessageDef.BUNDLE_CHECK_IN_SELECT_SEAT_DATA);
                String strPnrId = data.getStringExtra(UiMessageDef.BUNDLE_CHECK_IN_TRIP_PNR_ID);

                if (null != SeatCheckInEntity) {
                    updateSeatNumber(strPnrId,SeatCheckInEntity);
                }
                // 2016-10-28 Ryan, 當座位變更時, 需要重新檢查是否要Mark
                checkIsNeedMark();
            }
        }
    }

    private CIPassengerListResp getPassengerList(int iPaxInfoPos, int iItineraryPos) {

        CICheckIn_ItineraryInfo_Resp respItineraryInfoEntity = m_arCheckInPaxInfoList.get(iPaxInfoPos).Itinerary_Info.get(iItineraryPos);

        final String strPnr = m_arCheckInPaxInfoList.get(iPaxInfoPos).Pnr_Id;
        final String strSegment_No = m_arCheckInPaxInfoList.get(iPaxInfoPos).Itinerary_Info.get(iItineraryPos).Segment_Number;


        CIPassengerListResp_PaxInfo pax_info = new CIPassengerListResp_PaxInfo();
        pax_info.First_Name = m_arCheckInPaxInfoList.get(iPaxInfoPos).First_Name;
        pax_info.Last_Name = m_arCheckInPaxInfoList.get(iPaxInfoPos).Last_Name;
        pax_info.Booking_Class = respItineraryInfoEntity.BookingClass;
        pax_info.Did = respItineraryInfoEntity.Did;
        pax_info.Seat_Number = respItineraryInfoEntity.Seat_Number;
        pax_info.Uci = m_arCheckInPaxInfoList.get(iPaxInfoPos).Uci;

        CIPassengerListResp passengerListResp = new CIPassengerListResp();
        passengerListResp.Pax_Info.add(pax_info);

        for (int iPos = 0; iPos < m_arCheckInPaxInfoList.size(); iPos++) {

            if (iPos == iPaxInfoPos) {
                continue;
            }

            CICheckIn_Resp respEntity = m_arCheckInPaxInfoList.get(iPos);
            if (!respEntity.Pnr_Id.equals(strPnr)
                    || CIPassengerListResp_PaxInfo.PASSENGER_INFANT.equals(respEntity.Pax_Type)) {
                continue;
            }

            for (int iItPos = 0; iItPos < respEntity.Itinerary_Info.size(); iItPos++) {
                CICheckIn_ItineraryInfo_Resp itineraryInfoEntity = respEntity.Itinerary_Info.get(iItPos);

                final boolean bIsINF = CIPassengerListResp_PaxInfo.PASSENGER_INFANT.equals(m_arCheckInPaxInfoList.get(iPos).Pax_Type)? true : false;

                if( false == itineraryInfoEntity.Is_Change_Seat || true == bIsINF) {
                    continue;
                }

                if (itineraryInfoEntity.Segment_Number.equals(strSegment_No)) {
                    CIPassengerListResp_PaxInfo pax_info2 = new CIPassengerListResp_PaxInfo();
                    pax_info2.First_Name = respEntity.First_Name;
                    pax_info2.Last_Name = respEntity.Last_Name;
                    pax_info2.Booking_Class = respItineraryInfoEntity.BookingClass;
                    pax_info2.Did = itineraryInfoEntity.Did;
                    pax_info2.Seat_Number = itineraryInfoEntity.Seat_Number;
                    pax_info2.Pax_Type = respEntity.Pax_Type;
                    pax_info2.Uci = respEntity.Uci;

                    passengerListResp.Pax_Info.add(pax_info2);
                }

            }

        }

        return passengerListResp;
    }

    private ArrayList<String> getPnrIdList() {
        if (null == m_arPnrId) {
            m_arPnrId = new ArrayList<>();
        }
        return m_arPnrId;
    }

    private Set<String> getPnrIdSet() {
        Set<String> pnrList = new LinkedHashSet<>();
        if (null == m_arPnrId) {
            m_arPnrId = new ArrayList<>();
        } else {
            for (int i = 0; i < m_arPnrId.size(); i++) {
                pnrList.add(m_arPnrId.get(i));
            }
        }
        return pnrList;
    }

    class RecyclerAdpter extends RecyclerView.Adapter<RecyvlerViewHolder> {


        private LayoutInflater m_LayoutInflate = null;
        private ViewScaleDef m_viewScaleDef = null;

        RecyclerAdpter() {
            m_LayoutInflate = LayoutInflater.from(getActivity());
            m_viewScaleDef = ViewScaleDef.getInstance(getActivity());
        }

        /**
         * Create View
         */
        @Override
        public RecyvlerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = null;
            view = m_LayoutInflate.inflate(R.layout.fragment_checkin_completes_item, parent, false);

            RecyvlerViewHolder recyclerholder = new RecyvlerViewHolder(view);
            m_viewScaleDef.selfAdjustAllView(recyclerholder.m_llayoutbg);
            m_viewScaleDef.selfAdjustSameScaleView(recyclerholder.m_ivbtn_Edit, 24, 24);
            m_viewScaleDef.selfAdjustSameScaleView(recyclerholder.m_ivBoardingPass, 24, 24);
            m_viewScaleDef.selfAdjustSameScaleView(recyclerholder.m_ivSeat, 24, 24);

            return recyclerholder;
        }

        /**
         * 讓畫面與資料進行綁定
         */
        @Override
        public void onBindViewHolder(RecyvlerViewHolder holder, int position) {

            holder.m_tvName.setText(m_arCheckinList.get(position).strName);

            if (!TextUtils.isEmpty(m_arCheckinList.get(position).strSeat)) {
                holder.m_tvSeat.setText(m_arCheckinList.get(position).strSeat);
                holder.m_btnSelect.setVisibility(View.GONE);
                holder.m_ivbtn_Edit.setVisibility(View.VISIBLE);
            } else {
                holder.m_tvSeat.setText("-");
                holder.m_btnSelect.setVisibility(View.VISIBLE);
                holder.m_ivbtn_Edit.setVisibility(View.GONE);
            }

            if( true == m_arCheckinList.get(position).bIsINF
                    || false == m_arCheckinList.get(position).bIsChangeSeat) {
                holder.m_btnSelect.setVisibility(View.GONE);
                holder.m_ivbtn_Edit.setVisibility(View.GONE);
            }

            if ( isCanBoardPass(position) ){
                holder.m_btnView.setVisibility(View.VISIBLE);
                holder.m_tvText.setVisibility(View.VISIBLE);

                holder.m_tvUnboarding_pass.setVisibility(View.GONE);

            } else {
                holder.m_btnView.setVisibility(View.GONE);
                holder.m_tvText.setVisibility(View.INVISIBLE);

                holder.m_tvUnboarding_pass.setVisibility(View.VISIBLE);
            }

            holder.m_btnSelect.setTag(position);
            holder.m_ivbtn_Edit.setTag(position);
            holder.m_btnView.setTag(position);
        }

        @Override
        public int getItemCount() {
            if (null != m_arCheckinList) {
                return m_arCheckinList.size();
            }
            return 0;
        }

    }

    private void showBoardingPassCard() {
        Activity activity = CICheckInCompletesFragment.this.getActivity();

        Bitmap bitmap = ImageHandle.getScreenShot(activity);
        Bitmap blur = ImageHandle.BlurBuilder(activity, bitmap, 13.5f, 0.15f);

        Bundle bundle = new Bundle();
        //傳入是否已使用登機證的tag
        bundle.putBoolean(UiMessageDef.BUNDLE_BOARDING_PASS_IS_EXPIRED_TAG, false);
        bundle.putSerializable(UiMessageDef.BUNDLE_BOARDING_PASS_DATAS, m_alBoardPass_Itinerary);
        bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(activity, CIBoardingPassCardActivity.class);
        activity.startActivity(intent);

        activity.overridePendingTransition(R.anim.anim_alpha_in, 0);

        bitmap.recycle();
        System.gc();
    }

    //643924-2019-start
    private void ChangeActivity(Intent intent, Class clazz){
        intent.setClass(getActivity(), clazz);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }
    //643924-2019-end

    private void ChangeActivity(Intent intent, Class clazz, int iRequestCode) {
        intent.setClass(getActivity(), clazz);
        startActivityForResult(intent, iRequestCode);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private void updateSeatNumber(final String strPnrId , final CIAllocateSeatCheckInReq entity) {

        for (CIAllocateSeatCheckInReq_Pax selectSeatPaxInfo : entity.Pax_Info) {
            if( null == selectSeatPaxInfo ) {
                return;
            }

            for (int iPos = 0; iPos < m_arCheckInPaxInfoList.size(); iPos++) {

                CICheckIn_Resp respEntity = m_arCheckInPaxInfoList.get(iPos);

                if (!strPnrId.equals(respEntity.Pnr_Id)) {
                    continue;
                }

                for (int iItPos = 0; iItPos < respEntity.Itinerary_Info.size(); iItPos++) {
                    CICheckIn_ItineraryInfo_Resp itineraryInfoEntity = respEntity.Itinerary_Info.get(iItPos);

                    if (selectSeatPaxInfo.Did.equals(itineraryInfoEntity.Did)) {
                        itineraryInfoEntity.Seat_Number = selectSeatPaxInfo.Seat_Number;
                    }
                }
            }
        }

        setCheckInList();
        m_RecyclerAdpter.notifyDataSetChanged();
    }
//
//    private void isUSAStation(String strArrivalStation) {
//
//        String[] strUSAStationList = new String[]{"LAX","SFO","JFK","HNL","ANC","GUM","SEA","IAH"};
//
//        for( String strUSAStation: strUSAStationList ) {
//            if( strUSAStation.equals(strArrivalStation)) {
//                //return true;
//                m_bIsUSAStation = true;
//            }
//        }
//        //return false;
//    }

    //2016-10.28 Ryan 修正顯示登機證的邏輯, 此處不在判斷，直接抓取前一頁Mark玩的資料來檢查
    //2016-10-21 Ryan, 修正顯示登機證的邏輯, 此處調整為只檢查航站, 美國線邏輯判斷只要其中一站是美國線就不顯示登機證
    private boolean isCanBoardPass(int iPosition) {

        int iPaxInfoPos = m_arCheckinList.get(iPosition).iPaxInfoPos;
        int iItineraryPos = m_arCheckinList.get(iPosition).iItineraryPos;
        CICheckIn_ItineraryInfo_Resp respEntity = m_arCheckInPaxInfoList.get(iPaxInfoPos).Itinerary_Info.get(iItineraryPos);

        if ( null != m_mapMarkList.get(respEntity.Segment_Number) ){
            return  true;
        } else {
            return false;
        }


//        CIFlightStationEntity flightStationEntity = m_InquiryFlightStationPresenter.getStationInfoByIATA(respEntity.Departure_Station);
//
//        isUSAStation(respEntity.Arrival_Station);
//        //只要其中一站式美國線, 就不可顯示登機證
//        if ( m_bIsUSAStation ){
//            return false;
//        }
//
//        if( null == flightStationEntity || ("Y".equals(flightStationEntity.is_vpass)) ) {
//            return true;
//        } else {
//            return false;
//        }

    }

}
