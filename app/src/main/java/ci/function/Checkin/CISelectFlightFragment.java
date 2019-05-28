package ci.function.Checkin;


import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.ui.FlightCard.CICheckInFlightCardView;
import ci.ui.FlightCard.item.FlightCardItem;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.view.ShadowBar.ShadowBarRecycleView;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CICheckInAllPaxResp;
import ci.ws.Models.entities.CIInquiryTripEntity;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CIInquiryCheckInPresenter;
import ci.ws.Presenter.Listener.CIInquiryCheckInListener;


/**
 *
 * Created by Ryan on 16/3/07.
 */
public class CISelectFlightFragment extends BaseFragment {

    private ShadowBarRecycleView m_LastItemRecycleView   = null;

    private RecyclerView    m_RecyclerView  = null;
    private RecyclerAdpter  m_RecyclerAdpter= null;

    private CICheckInAllPaxResp m_CheckInList   = null;

    private ArrayList<FlightCardItem> m_arFlighCardtList = null;

    //2016-07-15 ryan 調整Check-in Presenter 使用方式
    private CIInquiryCheckInPresenter m_InquiryCheckInPresenter = null;

    private CIInquiryCheckInListener m_InquiryCheckInListener = new CIInquiryCheckInListener() {
        @Override
        public void onInquiryCheckInSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {
            setCheckInResp(CheckInList);
            setCheckInList();
        }

        @Override
        public void onInquiryCheckInError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm),
                    "",
                    new CIAlertDialog.OnAlertMsgDialogListener() {
                        @Override
                        public void onAlertMsgDialog_Confirm() {
                            getActivity().onBackPressed();
                        }

                        @Override
                        public void onAlertMsgDialogg_Cancel() {}
                    });
        }

        @Override
        public void onInquiryCheckInAllPaxSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {}

        @Override
        public void onInquiryCheckInAllPaxError(String rt_code, String rt_msg) {}

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    public void setCheckInResp(CICheckInAllPaxResp CheckInList) {

        if( null != m_CheckInList ) {
            m_CheckInList.clear();
        }

        m_CheckInList = CheckInList;
    }

    public void setCheckInResp(ArrayList<CICheckInPax_InfoEntity> arCheckInResp) {
        if( null != m_CheckInList ) {
            m_CheckInList.clear();
        } else {
            m_CheckInList = new CICheckInAllPaxResp();
        }

        if( null == arCheckInResp ) {
            return;
        }

        for( CICheckInPax_InfoEntity pax_infoEntity : arCheckInResp ) {
            m_CheckInList.add(pax_infoEntity);
        }
    }

    private void setCheckInList() {

        if( null == m_arFlighCardtList ) {
            m_arFlighCardtList = new ArrayList<>();
        } else {
            m_arFlighCardtList.clear();
        }

        if( null == m_CheckInList ) {
            m_CheckInList = new CICheckInAllPaxResp();
        }

        HashMap<String, Object> pnrMap = new HashMap<>();

        for( int iPos = 0 ; iPos < m_CheckInList.size() ; iPos ++ ) {
            CICheckInPax_InfoEntity entity = m_CheckInList.get(iPos);
            boolean bIsFirstSegment = true;
            if( null != pnrMap.get(entity.Pnr_Id) ) {
                continue;
            }

            for( int iInfoPos = 0 ; iInfoPos < entity.m_Itinerary_InfoList.size() ; iInfoPos ++ ) {

                CICheckInPax_ItineraryInfoEntity infoEntity = entity.m_Itinerary_InfoList.get(iInfoPos);

                if( infoEntity.Is_Black || infoEntity.Is_Check_In || !infoEntity.Is_Do_Check_In ) {
                    continue;
                }
                FlightCardItem flightCard = new FlightCardItem();
                flightCard.strFlightNo = infoEntity.Flight_Number;
                flightCard.strAirlines = infoEntity.Airlines;
                flightCard.strDate = infoEntity.Display_Departure_Date;
                flightCard.strDeparture = infoEntity.Departure_Station;
                flightCard.strArrival = infoEntity.Arrival_Station;
                flightCard.strDepartureAirport = infoEntity.Departure_Station_Name;
                flightCard.strArrivalAirport = infoEntity.Arrival_Station_Name;
                flightCard.strDepartureTime = infoEntity.Display_Departure_Time;
                flightCard.strArrivalTime = infoEntity.Display_Arrival_Time;
                flightCard.bSelect = (m_arFlighCardtList.size() == 0) ? true: false;

                if( true == bIsFirstSegment ) {

                    pnrMap.put(entity.Pnr_Id, m_arFlighCardtList.size());

                    flightCard.bFirstSegment = true;
                    bIsFirstSegment = false;
                } else {
                    flightCard.bFirstSegment = false;
                }
                flightCard.bIsTransition  = ( null == infoEntity.Oids )? false : (( 0 == infoEntity.Oids.size())? false : true );
                flightCard.iGroupIndex = iPos;
                flightCard.iChildIndex = iInfoPos;
                m_arFlighCardtList.add(flightCard);
            }
        }


        //2018-06-29 更換字串，將查無資料換成->無可預辦登機航班
        if( 0 >= m_arFlighCardtList.size() ) {
            showDialog(getString(R.string.warning),
                    getString(R.string.no_checkin_flight_available),
                    getString(R.string.confirm),
                    "",
                    new CIAlertDialog.OnAlertMsgDialogListener() {
                        @Override
                        public void onAlertMsgDialog_Confirm() {
                            getActivity().onBackPressed();
                        }

                        @Override
                        public void onAlertMsgDialogg_Cancel() {}
                    });
        }

        for( Object object : pnrMap.values() ) {
            setCheckInList((int)object, true);
        }

        if( null == m_RecyclerAdpter ) {
            m_RecyclerAdpter = new RecyclerAdpter();
        }
        m_RecyclerView.setAdapter(m_RecyclerAdpter);
        m_RecyclerAdpter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_checkin_select_flight;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_LastItemRecycleView = (ShadowBarRecycleView)view.findViewById(R.id.layout_recycleview);
        m_RecyclerView = m_LastItemRecycleView.getRecyclerView();
        //m_RecyclerView.setHasFixedSize(true);

        m_InquiryCheckInPresenter = new CIInquiryCheckInPresenter(m_InquiryCheckInListener);

        if( null == m_CheckInList || 0 >= m_CheckInList.size() ) {
            //2018-06-29 第二階段CR 新增FirstName LastName 避免PNRId重複使用導致看到別人的資料
            List<CIInquiryTripEntity> lDBTrip = CIPNRStatusManager.getInstance(null).getAllTripData();
            Set<String> Pnr_List = new LinkedHashSet<>();
            if ( null != lDBTrip ){
                for ( int i = 0 ; i < lDBTrip.size() ; i ++ ){
                    if ( 0 < lDBTrip.get(i).PNR.length() )
                        Pnr_List.add(lDBTrip.get(i).PNR);
                }
            }
            //
            m_InquiryCheckInPresenter.InquiryCheckInByCardFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo(),
                    CIApplication.getLoginInfo().GetUserFirstName(),
                    CIApplication.getLoginInfo().GetUserLastName(),
                    Pnr_List);
        } else {
            setCheckInList();
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        int iWidth  = vScaleDef.getLayoutWidth(20);
        m_RecyclerView.setPadding( iWidth, 0, iWidth, 0);
    }

    @Override
    protected void setOnParameterAndListener(View view) {

    }

    @Override
    protected void registerFragment( FragmentManager fragmentManager) {

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

    public static class RecyvlerViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout  m_rlayoutbg;
        ImageButton     m_imgbtnCheckBox;
        CICheckInFlightCardView m_ciFlightCard;
        Boolean         m_bSelect;

        RecyvlerViewHolder(View view) {
            super(view);
            m_rlayoutbg     = (RelativeLayout)view.findViewById(R.id.rlayout_bg);
            m_imgbtnCheckBox= (ImageButton)view.findViewById(R.id.imgBtnCheckBox);
            m_ciFlightCard  = (CICheckInFlightCardView)view.findViewById(R.id.vflightCard);
            m_bSelect       = false;
        }
    }

   class RecyclerAdpter extends RecyclerView.Adapter<RecyvlerViewHolder> implements View.OnClickListener{


       private LayoutInflater   m_LayoutInflate = null;
       private ViewScaleDef     m_viewScaleDef  = null;

       RecyclerAdpter(){
           m_LayoutInflate = LayoutInflater.from(getActivity());
           m_viewScaleDef = ViewScaleDef.getInstance(getActivity());
       }

       /**Create View*/
       @Override
       public RecyvlerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           View view = m_LayoutInflate.inflate( R.layout.fragment_checkin_select_flight_item, parent, false);
           RecyvlerViewHolder recyclerholder = new RecyvlerViewHolder(view);


           (recyclerholder.m_rlayoutbg.getLayoutParams()).height = m_viewScaleDef.getLayoutHeight(114);
           ((LinearLayout.LayoutParams)recyclerholder.m_rlayoutbg.getLayoutParams()).bottomMargin = m_viewScaleDef.getLayoutHeight(7);
           (recyclerholder.m_imgbtnCheckBox.getLayoutParams()).height = m_viewScaleDef.getLayoutMinUnit(32);
           (recyclerholder.m_imgbtnCheckBox.getLayoutParams()).width = m_viewScaleDef.getLayoutMinUnit(32);
           (recyclerholder.m_ciFlightCard.getLayoutParams()).width  = m_viewScaleDef.getLayoutWidth(276);

           recyclerholder.m_rlayoutbg.setOnClickListener(this);

           return recyclerholder;
       }

       /**讓畫面與資料進行綁定*/
       @Override
       public void onBindViewHolder(RecyvlerViewHolder holder, int position) {

           holder.m_ciFlightCard.FlightInfoUpdate( getItineraryInfo(m_arFlighCardtList.get(position)));//m_arFlighCardtList.get(position).iGroupIndex, m_arFlighCardtList.get(position).iItineraryIndex) );
            if ( getItemCount() <= 1 ){

                holder.m_ciFlightCard.onOnlyOneFlightCard();
            } else {

                if ( position == 0 ){
                    holder.m_ciFlightCard.onFirstFlightCard();
                } else if ( position == getItemCount() -1 ){
                    holder.m_ciFlightCard.onLastFlightCard();
                } else {
                    holder.m_ciFlightCard.onCenterFlightCard();
                }
            }
           holder.m_bSelect     = m_arFlighCardtList.get(position).bSelect;
           if ( holder.m_bSelect ){
               holder.m_imgbtnCheckBox.setImageResource(R.drawable.btn_checkbox_on);
           } else {
               holder.m_imgbtnCheckBox.setImageResource(R.drawable.btn_checkbox_off);
           }
           holder.m_rlayoutbg.setTag(position);
       }

       private CITripListResp_Itinerary getItineraryInfo(FlightCardItem item) {
           CITripListResp_Itinerary itinerary_info = new CITripListResp_Itinerary();
           itinerary_info.Flight_Number     = item.strFlightNo;
           itinerary_info.Airlines          = item.strAirlines;
           itinerary_info.Departure_Date    = item.strDate;
           itinerary_info.Departure_Station = item.strDeparture;
           itinerary_info.Arrival_Station   = item.strArrival;
           itinerary_info.Departure_Time    = item.strDepartureTime;
           itinerary_info.Arrival_Time      = item.strArrivalTime;
           itinerary_info.Arrival_Station_Name      = item.strArrivalAirport;
           itinerary_info.Departure_Station_Name    = item.strDepartureAirport;
           return itinerary_info;
       }

       @Override
       public int getItemCount() {
           if ( null != m_arFlighCardtList ){
               return m_arFlighCardtList.size();
           }
           return 0;
       }

       @Override
       public void onClick(View v) {
           int iPosition = (Integer)v.getTag();
           //0為第一班航班, 一定要Check In
//            if ( 0 != iPosition ){
//                m_arFlighCardtList.get(iPosition).bSelect = !(m_arFlighCardtList.get(iPosition).bSelect);
//                RecyclerAdpter.this.notifyDataSetChanged();
//            }

           if(!m_arFlighCardtList.get(iPosition).bFirstSegment
                   || (m_arFlighCardtList.get(iPosition).bFirstSegment && !m_arFlighCardtList.get(iPosition).bSelect)) {
               setCheckInList(iPosition, false);
           }
       }

   }

    private void setCheckInList(int iPosition, boolean bCheckFirstSegment) {

        FlightCardItem flightCardItem = m_arFlighCardtList.get(iPosition);
        final int iSelectGroup = flightCardItem.iGroupIndex;

        if( iPosition == 0 ) {
            flightCardItem.bSelect = true;

        } else {
            flightCardItem.bSelect = !flightCardItem.bSelect;
        }

        final boolean bSelect = flightCardItem.bSelect;

        for( int iPos = 0 ; iPos <  m_arFlighCardtList.size() ; iPos ++ ) {

//            if( 0 == flightCardItem.iGroupIndex && 0 == m_arFlighCardtList.get(iPos).iGroupIndex ) {
//                continue;
//            }

            if( iSelectGroup == m_arFlighCardtList.get(iPos).iGroupIndex ) {
                continue;
            }

            m_arFlighCardtList.get(iPos).bSelect = false;
        }

        flightCardItem.bSelect = bSelect;

        CICheckInPax_ItineraryInfoEntity entity = m_CheckInList.get(flightCardItem.iGroupIndex).m_Itinerary_InfoList.get(flightCardItem.iChildIndex);

        if( true == flightCardItem.bIsTransition ) {

            if( !TextUtils.isEmpty(entity.Did)) {
                searchForward(flightCardItem.iGroupIndex, iPosition - 1, entity.Did, bSelect,bCheckFirstSegment);
            }

            if( entity.Oids.size() > 0 ) {
                searchBackward(flightCardItem.iGroupIndex, iPosition + 1, entity.Oids.get(0).Oid, bSelect,bCheckFirstSegment);
            }
        } else {
            if( !TextUtils.isEmpty(entity.Did)) {
                searchForward(flightCardItem.iGroupIndex, iPosition - 1, entity.Did, bSelect,bCheckFirstSegment);
            }
        }

        if( null != m_RecyclerAdpter ) {
            m_RecyclerAdpter.notifyDataSetChanged();
        }

        //RecyclerAdpter.this.notifyDataSetChanged();
    }


    private void searchForward(int iGroupId , int iFlighCardListIndex, String Oid, boolean bSelect, boolean bCheckFirstSegment) {

        if( -1 == iFlighCardListIndex || m_arFlighCardtList.size() == iFlighCardListIndex ) {
            return;
        }

        FlightCardItem item = m_arFlighCardtList.get(iFlighCardListIndex);

        if( iGroupId != item.iGroupIndex ) {
            return;
        }

        if( item.iGroupIndex == iGroupId ) {

            CICheckInPax_ItineraryInfoEntity entity = m_CheckInList.get(item.iGroupIndex).m_Itinerary_InfoList.get(item.iChildIndex);
            if( entity.Oids.size() > 0 && Oid.equals( entity.Oids.get(0).Oid) ) {

                if( true == bCheckFirstSegment && false == item.bFirstSegment ) {
                    item.bFirstSegment = true;
                }

                item.bSelect = bSelect;

                searchForward( iGroupId, iFlighCardListIndex - 1 , entity.Did ,bSelect,bCheckFirstSegment);
            } else {
                searchForward( iGroupId, iFlighCardListIndex - 1 , Oid ,bSelect,bCheckFirstSegment);
            }

        } else {
            return ;
        }


    }

    private void searchBackward(int iGroupId , int iFlighCardListIndex, String Oid, boolean bSelect, boolean bCheckFirstSegment) {
        if( m_arFlighCardtList.size() == iFlighCardListIndex ) {
            return;
        }

        FlightCardItem item = m_arFlighCardtList.get(iFlighCardListIndex);

        if( iGroupId != item.iGroupIndex ) {
            return;
        }

        if( item.iGroupIndex == iGroupId ) {

            CICheckInPax_ItineraryInfoEntity entity = m_CheckInList.get(item.iGroupIndex).m_Itinerary_InfoList.get(item.iChildIndex);
            if( !TextUtils.isEmpty(entity.Did) && Oid.equals( entity.Did) ) {

                if( true == bCheckFirstSegment && false == item.bFirstSegment ) {
                    item.bFirstSegment = true;
                }

                item.bSelect = bSelect;

                if( 0 < entity.Oids.size() ) {
                    searchBackward(iGroupId, iFlighCardListIndex + 1, entity.Oids.get(0).Oid, bSelect,bCheckFirstSegment);
                }
            } else {
                searchBackward( iGroupId , iFlighCardListIndex + 1 , Oid ,bSelect,bCheckFirstSegment);
            }

        } else {
            return ;
        }

    }

    public boolean isFlighCardSelectd() {
        if( null == m_arFlighCardtList ) {
            return false;
        }

        for( FlightCardItem item : m_arFlighCardtList) {
            if( true == item.bSelect ) {
                return true;
            }
        }

        return false;
    }

    public CICheckInPax_InfoEntity getSelectedFlightList() {

        if( isFlighCardSelectd() ) {
            CICheckInPax_InfoEntity selectedFlightEntity = null;

            for( FlightCardItem item : m_arFlighCardtList) {
                if( true == item.bSelect ) {
                    CICheckInPax_InfoEntity entity = m_CheckInList.get(item.iGroupIndex);
//                    ar_SelectedFlight.add( m_CheckInList.get(item.iGroupIndex).m_Itinerary_InfoList.get(item.iChildIndex));

                    if( null == selectedFlightEntity ) {
                        selectedFlightEntity = new CICheckInPax_InfoEntity();

                        selectedFlightEntity.First_Name = entity.First_Name;
                        selectedFlightEntity.Last_Name = entity.Last_Name;
                        selectedFlightEntity.Pnr_Id = entity.Pnr_Id;
                        selectedFlightEntity.Uci = entity.Uci;
                    }

                    if( null == selectedFlightEntity.m_Itinerary_InfoList ) {
                        selectedFlightEntity.m_Itinerary_InfoList = new ArrayList<>();
                    }

                    selectedFlightEntity.m_Itinerary_InfoList.add( entity.m_Itinerary_InfoList.get(item.iChildIndex) );

                }


            }

            return selectedFlightEntity;
        } else {
            return null;
        }
    }

    public ArrayList<String> getSelectedFlightSegmentNoList() {

        ArrayList<String> arSegmentNo = new ArrayList<>();

        if( isFlighCardSelectd() ) {

            for( FlightCardItem item : m_arFlighCardtList) {
                if( true == item.bSelect ) {

                    CICheckInPax_ItineraryInfoEntity entity = m_CheckInList.get(item.iGroupIndex).m_Itinerary_InfoList.get(item.iChildIndex);
                    arSegmentNo.add( entity.Segment_Number );
                }
            }
        }

        return arSegmentNo;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //CIInquiryCheckInPresenter.getInstance(null);
        m_InquiryCheckInPresenter.setCallBack(null);
    }
}
