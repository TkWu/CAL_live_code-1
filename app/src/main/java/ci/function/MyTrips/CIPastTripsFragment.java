package ci.function.MyTrips;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.Login.CILoginActivity;
import ci.function.MyTrips.Adapter.CIMyTripsRecyclerForRecordViewAdapter;
import ci.function.MyTrips.Detail.CIMyTripsDetialActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ws.Models.entities.CIMileageRecord_Info;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Models.entities.CITripListResp;

import static ci.function.MyTrips.Adapter.CIMyTripsRecyclerForRecordViewAdapter.onMyTripsRecyclerViewAdapterListener;
import static ci.function.MyTrips.Adapter.CIMyTripsRecyclerForRecordViewAdapter.onMyTripsRecyclerViewAdapterParameter;
import static ci.function.MyTrips.CIMyTripsFragment.MyTripsMode;

/**
 * 我的行程資訊 - 歷史紀錄
 */
public class CIPastTripsFragment extends BaseFragment {

    public interface onPastTripsFragmentParameter{
        //取得歷史行程資料
        ArrayList<CIMileageRecord_Info> GetPastTrips();
    }

    public interface onPastTripsFragmentListener{
        //向MyTrips請求資料
        void loadPastTripsData();
        //告知MyTrips有新增行程
        void addUpComingTripsData(CITripListResp itinerary_info);
        //向MyTrips請求更新單一比PNR
        void onItineraryUpdateByPNR( int ipostion );
    }

    private onMyTripsRecyclerViewAdapterParameter m_MyTripsRecyclerViewAdapterParameter
            = new onMyTripsRecyclerViewAdapterParameter() {
        @Override
        public int GetMyTripsMode() {
            return MyTripsMode.CI_MEMBER.ordinal();
        }

        @Override
        public String GetErrorMsg() {
            return "";
        }

        @Override
        public boolean IsShowProgressBar() {
            return m_bIsShowPro;
        }
    };

    private onMyTripsRecyclerViewAdapterListener m_MyTripsRecyclerViewAdapterListener
            = new onMyTripsRecyclerViewAdapterListener() {
        @Override
        public void AddTripsOnClick() {

            Intent intent = new Intent();
            intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE,
                    CILoginActivity.LoginMode.FIND_MYBOOKING_ONLY_RETRIEVE.name());
            intent.setClass(getActivity(), CILoginActivity.class);
            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_LOGIN);
            getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        @Override
        public void onItineraryClick(int ipostion) {
            //do nothing
        }

    };

    private onPastTripsFragmentParameter        m_Parameter;
    private onPastTripsFragmentListener         m_Listener;

    private RecyclerView                        m_rv            = null;
    private CIMyTripsRecyclerForRecordViewAdapter  m_Adapter;

    private ArrayList<CIMileageRecord_Info>     m_arMileageRecordItem = new ArrayList<>();

    private boolean                             m_bIsShowPro    = false;
    private boolean                             m_bIsNoData     = false;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_recycler_view;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        if ( null != m_Parameter ){
            setupData();
        }

        m_rv = (RecyclerView)view.findViewById(R.id.rv);
        m_rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        m_Adapter = new CIMyTripsRecyclerForRecordViewAdapter(
                getActivity(),
                m_arMileageRecordItem,
                false,
                m_MyTripsRecyclerViewAdapterParameter,
                m_MyTripsRecyclerViewAdapterListener);

        m_rv.setAdapter(m_Adapter);
    }

    private void loadData(){

        if ( null != m_Listener ){
            m_Listener.loadPastTripsData();
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.setMargins(m_rv, 0, 20, 0, 0);
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

    public void uiSetParameterListener(onPastTripsFragmentParameter onParameter,
                                       onPastTripsFragmentListener onListener) {
        m_Parameter = onParameter;
        m_Listener = onListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == UiMessageDef.REQUEST_CODE_LOGIN &&
                resultCode == UiMessageDef.RESULT_CODE_GO_TRIP_DETAIL){
            //接收findmybooking查到的資料
            CITripListResp tripListResp = (CITripListResp)data.getSerializableExtra(
                    UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);

            if ( null != tripListResp ) {

                if ( null != m_Listener )
                    m_Listener.addUpComingTripsData(tripListResp);

                Bundle bundle = new Bundle();
                bundle.putInt(UiMessageDef.BUNDLE_TRIPS_DETIAL_CURRENT_PAGE, 0);

                //傳入my trips 航班資料, trip detail頁面可接收並取出此物件的資料
                CITripListResp_Itinerary itinerary_info = null;
                for ( int i = 0; i < tripListResp.Itinerary_Info.size(); i ++){
                    if ( tripListResp.Segment_Num.equals( tripListResp.Itinerary_Info.get(i).Segment_Number) ){
                        itinerary_info = tripListResp.Itinerary_Info.get(i);
                        bundle.putSerializable(
                                UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, itinerary_info);
                        break;
                    }
                }

                if ( null == itinerary_info ){
                    bundle.putSerializable(
                            UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST,
                            tripListResp.Itinerary_Info.get(0));
                }

                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(getActivity(), CIMyTripsDetialActivity.class);
                getActivity().startActivityForResult(intent, UiMessageDef.REQUEST_CODE_TRIP_DETAIL);;

                getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
            }
        }
    }


    public void setupData(){
        if ( null != m_Parameter ){
            ArrayList<CIMileageRecord_Info> datas = m_Parameter.GetPastTrips();
            m_arMileageRecordItem.clear();
            if(null != datas && datas.size() > 0){
                for(CIMileageRecord_Info data: datas){
                    //只有flight_item是1才顯示
                    if(data.flight_item.equals("1")) {
                        m_arMileageRecordItem.add(data);
                    }
                }
            }
        }
    }

    public void ResetListAdapter(){
        setupData();
        NotifyDataSetChanged();
    }

    public void IsShowProBar(boolean bShow){
        m_bIsShowPro = bShow;

        NotifyDataSetChanged();
    }

    private void NotifyDataSetChanged(){
        if ( null != m_Adapter ){
            m_Adapter.setDatas(m_arMileageRecordItem);
            m_Adapter.notifyDataSetChanged();
        }
    }

    public void FirstLoadingData(){
        if ( true == m_bIsNoData )
            return;

        if ( null == m_arMileageRecordItem || 0 >= m_arMileageRecordItem.size() ){
            //取得會員行程資料
            loadData();
        }
    }
}
