package ci.function.Checkin;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ShadowBar.ShadowBarRecycleView;
import ci.ws.Models.entities.CICancelCheckInReq;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CICheckInAllPaxResp;

/**
 * 選擇乘客頁面
 * zeplin: 11.8-2
 * wireframe: p.77
 * Created by Ryan on 16/3/08.
 */
public class CISelectPassengersFragment extends BaseFragment {

    //報到-可以新增或選擇乘客; 取消報到-只能選擇要取消報到的乘客 - by Ling
    public enum SelectPassengersType{
        ADD_PASSENGER, CANCEL_CHECK_IN;
    }

    private SelectPassengersType        m_type                  = SelectPassengersType.ADD_PASSENGER;
    private String                      m_strPnrId              = null;
    private ArrayList<String>           m_arSegmentNo           = null;

    private ShadowBarRecycleView        m_LastItemRecycleView   = null;
    private RecyclerView                m_RecyclerView          = null;
    private RecyclerAdpter              m_RecyclerAdpter        = null;

    private ArrayList<PassengersItem>   m_arPassengersList      = null;

    private static final int            MAX_PASSENGER_COUNT     = 9;
    private static final String         TYPE                    = "TYPE";
    private static final String         PNR_ID                  = "PnrId";
    private static final String         SEGMENT_NO              = "SegmentNo";
    private static final String         UCI                     = "Uci";
    private static final String         CANCEL_CHECK_IN_DATA    = "CANCEL_CHECK_IN_DATA";

    private CICheckInAllPaxResp m_CheckInList           = null;
    private String                      m_strUci                = null;
    private CICancelCheckInReq          m_CancelCheckInReq      = null;

    public static CISelectPassengersFragment newInstance(SelectPassengersType type, CICancelCheckInReq req) {
        Bundle args = new Bundle();
        args.putString(TYPE, type.name());
        args.putSerializable(CANCEL_CHECK_IN_DATA, req);
        CISelectPassengersFragment fragment = new CISelectPassengersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CISelectPassengersFragment newInstance(SelectPassengersType type,
                                                         String strPnrId,
                                                         ArrayList<String> arSegmentNo,
                                                         String strUci) {
        Bundle args = new Bundle();
        args.putString(TYPE, type.name());
        args.putString(PNR_ID,strPnrId);
        args.putStringArrayList(SEGMENT_NO,arSegmentNo);
        args.putString(UCI,strUci);
        CISelectPassengersFragment fragment = new CISelectPassengersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(null != bundle){
            this.m_type = SelectPassengersType.valueOf(bundle.getString(TYPE));
            this.m_strPnrId = bundle.getString(PNR_ID);
            this.m_arSegmentNo = bundle.getStringArrayList(SEGMENT_NO);
            this.m_strUci = bundle.getString(UCI);

            if ( m_type.name().equals(SelectPassengersType.CANCEL_CHECK_IN.toString()) ){
                this.m_CancelCheckInReq = (CICancelCheckInReq)bundle.getSerializable(CANCEL_CHECK_IN_DATA);
            }
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_checkin_select_passengers;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_LastItemRecycleView = (ShadowBarRecycleView)view.findViewById(R.id.layout_recycleview);
        m_RecyclerView = m_LastItemRecycleView.getRecyclerView();
        //m_RecyclerView.setHasFixedSize(true);
        //

        if( SelectPassengersType.ADD_PASSENGER == m_type ) {
            showPassengerList();
        } else {
            showCancelCheckInList();
        }
    }

    private void showPassengerList(){
        if( null == m_arPassengersList ) {
            m_arPassengersList = new ArrayList<>();
        } else {
            m_arPassengersList.clear();
        }

        if( null != m_CheckInList ) {

            for (CICheckInPax_InfoEntity entity : m_CheckInList) {

                if (entity.m_Itinerary_InfoList != null) {

                    for (CICheckInPax_ItineraryInfoEntity infoEntity : entity.m_Itinerary_InfoList) {
                        if (false == infoEntity.Is_Check_In
                                && false == infoEntity.Is_Black
                                && true == infoEntity.Is_Do_Check_In) {


                            PassengersItem passengers = new PassengersItem();
                            passengers.strAccount = entity.Uci;//"A10000000";
                            passengers.strName = entity.First_Name + " " + entity.Last_Name;

//                            if (0 == m_arPassengersList.size()) {
                            //若旅客資料為使用者本人，則強制勾選
                            if( entity.Uci.equals(m_strUci)) {
                                passengers.bSelect = true;
                                passengers.bOwner = true;
                            } else {
                                passengers.bSelect = false;
                                passengers.bOwner = false;
                            }

                            m_arPassengersList.add(passengers);
                            break;
                        }
                    }
                }
            }
        }

        if( null == m_RecyclerAdpter ) {
            m_RecyclerAdpter = new RecyclerAdpter();
        }
        m_RecyclerView.setAdapter(m_RecyclerAdpter);
        m_RecyclerAdpter.notifyDataSetChanged();
    }

    private void showCancelCheckInList(){

        if ( null == m_arPassengersList ) {
            m_arPassengersList = new ArrayList<>();
        } else {
            m_arPassengersList.clear();
        }

        if ( null != m_CancelCheckInReq ){
            for ( int i = 0; i < m_CancelCheckInReq.Pax_Info.size(); i ++ ){
                PassengersItem item = new PassengersItem();
                item.bOwner = false;
                item.bSelect = false;
                item.strAccount = m_CancelCheckInReq.Pax_Info.get(i).Uci;
                item.strName = m_CancelCheckInReq.Pax_Info.get(i).First_Name
                        + " " + m_CancelCheckInReq.Pax_Info.get(i).Last_Name;

                m_arPassengersList.add(item);
            }
        }

        if ( null == m_RecyclerAdpter ) {
            m_RecyclerAdpter = new RecyclerAdpter();
        }
        m_RecyclerView.setAdapter(m_RecyclerAdpter);
        m_RecyclerAdpter.notifyDataSetChanged();
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        int iWidth  = vScaleDef.getLayoutWidth(20);
        m_RecyclerView.setPadding(iWidth, 0, iWidth, vScaleDef.getLayoutHeight(20));
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

    public void addPassenger( String strAccount, String strName ){

        PassengersItem passengers   = new PassengersItem();
        passengers.strAccount   = strAccount;
        passengers.strName      = strName;

        m_arPassengersList.add(passengers);
        if ( null != m_RecyclerAdpter ){
            m_RecyclerAdpter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == UiMessageDef.REQUEST_CODE_CHECK_IN_ADD_SINGLE_PASSENGER
                && resultCode == UiMessageDef.RESULT_CODE_CHECK_IN_ADD_SINGLE_PASSENGER ){
            addPassenger("12345","Kevin");
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class VIEW_TYPES {
        public static final int Normal = 1;
        public static final int Footer = 2;
    }

    public static class RecyvlerViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout  m_rlayoutbg;
        ImageButton     m_imgbtnCheckBox;
        TextView        m_tvName;
        View            m_vDiv;
        int             m_ViewType;

        RecyvlerViewHolder(View view, int iViewType ) {
            super(view);
            m_rlayoutbg     = (RelativeLayout)view.findViewById(R.id.rlayout_bg);
            m_imgbtnCheckBox= (ImageButton)view.findViewById(R.id.imgBtnCheckBox);
            m_tvName        = (TextView)view.findViewById(R.id.tv_Name);
            m_vDiv          = view.findViewById(R.id.vDiv);
            m_ViewType      = iViewType;
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

            View view = null;
//            if ( viewType == VIEW_TYPES.Normal ){
                view = m_LayoutInflate.inflate( R.layout.fragment_checkin_select_passengers_item, parent, false);
//            } else {
//                view = m_LayoutInflate.inflate( R.layout.fragment_checkin_select_passengers_foot_view, parent, false);
//            }

            RecyvlerViewHolder recyclerholder = new RecyvlerViewHolder(view, viewType);

            (recyclerholder.m_rlayoutbg.getLayoutParams()).height = m_viewScaleDef.getLayoutHeight(60.7);
            (recyclerholder.m_imgbtnCheckBox.getLayoutParams()).height = m_viewScaleDef.getLayoutMinUnit(24);
            (recyclerholder.m_imgbtnCheckBox.getLayoutParams()).width = m_viewScaleDef.getLayoutMinUnit(24);
            ((RelativeLayout.LayoutParams)recyclerholder.m_tvName.getLayoutParams()).leftMargin
                    = m_viewScaleDef.getLayoutWidth(10);
            ((RelativeLayout.LayoutParams)recyclerholder.m_tvName.getLayoutParams()).rightMargin
                    = m_viewScaleDef.getLayoutWidth(10);
            recyclerholder.m_vDiv.getLayoutParams().height = m_viewScaleDef.getLayoutHeight(0.7);
            m_viewScaleDef.setTextSize(16, recyclerholder.m_tvName);

            recyclerholder.m_ViewType = viewType;
            recyclerholder.m_rlayoutbg.setOnClickListener(this);

            return recyclerholder;
        }

        /**讓畫面與資料進行綁定*/
        @Override
        public void onBindViewHolder(RecyvlerViewHolder holder, int position) {

            if ( holder.m_ViewType == VIEW_TYPES.Normal ){

                holder.m_tvName.setText(m_arPassengersList.get(position).strName);

                if ( m_arPassengersList.get(position).bSelect ){
                    holder.m_imgbtnCheckBox.setImageResource(R.drawable.btn_checkbox_on);
                } else {
                    holder.m_imgbtnCheckBox.setImageResource(R.drawable.btn_checkbox_off);
                }

                ((RelativeLayout.LayoutParams)holder.m_imgbtnCheckBox.getLayoutParams()).leftMargin
                        = m_viewScaleDef.getLayoutWidth(0);
                ((RelativeLayout.LayoutParams)holder.m_tvName.getLayoutParams()).leftMargin
                        = m_viewScaleDef.getLayoutWidth(10);
                holder.m_vDiv.setVisibility(View.VISIBLE);
            } else {

                if ( m_type.name().equals(SelectPassengersType.CANCEL_CHECK_IN.toString()) ){

                    //取消報到 不能新增乘客 - by Ling
                    holder.m_rlayoutbg.setVisibility(View.GONE);

                }else {
                    ((RelativeLayout.LayoutParams)holder.m_imgbtnCheckBox.getLayoutParams()).leftMargin
                            = m_viewScaleDef.getLayoutWidth(10);
                    ((RelativeLayout.LayoutParams)holder.m_tvName.getLayoutParams()).leftMargin
                            = m_viewScaleDef.getLayoutWidth(8);

                    holder.m_tvName.setText(R.string.add_passenager);
                    holder.m_imgbtnCheckBox.setImageResource(R.drawable.btn_add_n);
                }

                holder.m_vDiv.setVisibility(View.GONE);
            }

            holder.m_rlayoutbg.setTag(position);
        }

        @Override
        public int getItemCount() {
            if ( null != m_arPassengersList){
                return m_arPassengersList.size() + getFootcount();
            }
            return 0;
        }

        public int getFootcount(){
            return 1;
        }

        @Override
        public int getItemViewType(int position) {

            if( position >= m_arPassengersList.size() && position < getItemCount() ){
                return VIEW_TYPES.Footer;
            } else {
                return VIEW_TYPES.Normal;
            }
        }

        @Override
        public void onClick(View v) {
            int position = (Integer)v.getTag();
            if ( position >= m_arPassengersList.size() && position < getItemCount() ){

                //超過最大旅客數
                if( MAX_PASSENGER_COUNT < m_arPassengersList.size() ) {
                    return;
                }

                Intent intent = new Intent();
                intent.setClass(getActivity(), CIAddPassengerSingleActivity.class);
                intent.putExtra(CIAddPassengerSingleActivity.BUNDLE_PNR_ID, m_strPnrId);
                intent.putExtra(CIAddPassengerSingleActivity.BUNDLE_SEGMENT_NO,m_arSegmentNo);
                intent.putExtra(CIAddPassengerSingleActivity.BUNDLE_UCI_LIST,getPassengerUciList());
                getActivity().startActivityForResult(intent, UiMessageDef.REQUEST_CODE_CHECK_IN_ADD_SINGLE_PASSENGER);
            } else {
                //自己一定要Check In
                if ( m_arPassengersList.get(position).bOwner == false ){
                    m_arPassengersList.get(position).bSelect = !(m_arPassengersList.get(position).bSelect);
                    RecyclerAdpter.this.notifyDataSetChanged();
                }
            }
        }
    }

    private ArrayList<String> getPassengerUciList() {
        if( null == m_arPassengersList ) {
            return null;
        }

        ArrayList<String> arUciList = new ArrayList<>();

        for( PassengersItem item: m_arPassengersList ) {
            arUciList.add(item.strAccount);
        }

        return arUciList;
    }

    public void addPassengerList(CICheckInAllPaxResp CheckInList) {
        m_CheckInList = CheckInList;
    }

    public boolean isPassengerSelected() {
        for( PassengersItem item : m_arPassengersList ) {
            if( true == item.bSelect ) {
                return true;
            }
        }
        return false;
    }

    public CICheckInAllPaxResp getCheckInPassengersInfo() {
        return m_CheckInList;
    }

    public ArrayList<Integer> getSelectedPositionList() {
        if( null == m_CheckInList ) {
            return null;
        }

        ArrayList<Integer> arSelectedPos = new ArrayList<>();

        for( int iPos = 0 ; iPos < m_arPassengersList.size() ; iPos++ ) {

            if( true == m_arPassengersList.get(iPos).bSelect ) {
                arSelectedPos.add(iPos);
            }
        }

        return arSelectedPos;
    }

    public ArrayList<Integer> getSelectCancelCheckInList(){
        ArrayList<Integer> arSelectedPos = new ArrayList<>();

        for( int iPos = 0 ; iPos < m_arPassengersList.size() ; iPos++ ) {

            if( true == m_arPassengersList.get(iPos).bSelect ) {
                arSelectedPos.add(iPos);
            }
        }

        return arSelectedPos;
    }
}
