package ci.function.MyTrips;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Login.CILoginActivity;
import ci.function.MyTrips.Adapter.CIMyTripsRecyclerViewAdapter;
import ci.function.MyTrips.Adapter.CIMyTripsRecyclerViewAdapter.onMyTripsRecyclerViewAdapterParameter;
import ci.function.MyTrips.Detail.CIMyTripsDetialActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Models.entities.CITripListResp;

import static ci.function.MyTrips.Adapter.CIMyTripsRecyclerViewAdapter.onMyTripsRecyclerViewAdapterListener;
import static ci.function.MyTrips.CIMyTripsFragment.MyTripsMode;

/**
 * 我的行程資訊 - 近期行程
 */
public class CIUpComingTripsFragment extends BaseFragment {

    public interface onUpComingTripsFragmentParameter{
        //是否有two item欄
        int GetMyTripsMode();
        //近期行程資料
        ArrayList<CITripListResp_Itinerary> GetUpComingTrips();
    }

    public interface onUpComingTripsFragmentListener{
        //向MyTrips請求資料
        void loadUpComingTripsData();
        //告知MyTrips有新增行程
        void addUpComingTripsData(CITripListResp itinerary_info);
        //向MyTrips請求更新單一比PNR
        void onItineraryUpdateByPNR( int ipostion );
        //僅要求DB資料並更新畫面
        void onReciverDataForNonMember();
    }

    private onMyTripsRecyclerViewAdapterParameter m_MyTripsRecyclerViewAdapterParameter
            = new onMyTripsRecyclerViewAdapterParameter() {
        @Override
        public int GetMyTripsMode() {
            return m_iMode;
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
            if ( null != m_Listener ){
                m_Listener.onItineraryUpdateByPNR(ipostion);
            }
        }
    };

    RecyclerView.OnScrollListener m_ScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if ( true == m_bIsNoData )
                return;

            //當不滾動時
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                // 判斷是否滾動到底部 取最後一個index
                int lostPos = m_LayoutManager.findLastVisibleItemPosition();
                if (lostPos == m_LayoutManager.getItemCount() - 1) {
                    if (m_bIsShowPro == false) {
                        //撈新資料
                        loadData();
                    }
                }
            }
        }
    };

    private onUpComingTripsFragmentParameter    m_Parameter;
    private onUpComingTripsFragmentListener     m_Listener;

    private RecyclerView                        m_rv            = null;
    private LinearLayoutManager                 m_LayoutManager = null;
    private CIMyTripsRecyclerViewAdapter        m_Adapter;

    private ArrayList<CITripListResp_Itinerary>         m_arMyTripItem  = new ArrayList<CITripListResp_Itinerary>();

    private int                                 m_iMode         = MyTripsMode.CI_MEMBER.ordinal();

    private boolean                             m_bIsShowPro    = false;
    private boolean                             m_bIsNoData     = false;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_recycler_view;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        if ( null != m_Parameter ){
            m_iMode         = m_Parameter.GetMyTripsMode();
            m_arMyTripItem  = m_Parameter.GetUpComingTrips();
        }

        m_rv = (RecyclerView)view.findViewById(R.id.rv);
        m_rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        m_LayoutManager = (LinearLayoutManager) m_rv.getLayoutManager();
        m_rv.addOnScrollListener(m_ScrollListener);

        m_Adapter = new CIMyTripsRecyclerViewAdapter(
                getActivity(),
                m_arMyTripItem,
                false,
                m_MyTripsRecyclerViewAdapterParameter,
                m_MyTripsRecyclerViewAdapterListener);

        m_FragmentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                m_rv.setAdapter(m_Adapter);

                //取得會員行程資料
                if ( m_iMode == MyTripsMode.CI_MEMBER.ordinal() ){
                    FirstLoadingData();
                }
            }
        }, 500);
    }

    private void loadData(){

        if ( null != m_Listener ){
            m_Listener.loadUpComingTripsData();
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        int iMargin;
        if ( m_iMode == MyTripsMode.BASE.ordinal() ){
            //如果沒有上方選擇按鈕, 則listview不設上間距
            iMargin = 0;
        }else {
            iMargin = 20;
        }

        vScaleDef.setMargins(m_rv, 0, iMargin, 0, 0);
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

    public void uiSetParameterListener(onUpComingTripsFragmentParameter onParameter,
                                       onUpComingTripsFragmentListener onListener) {
        m_Parameter = onParameter;
        m_Listener = onListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == UiMessageDef.REQUEST_CODE_LOGIN &&
                resultCode == UiMessageDef.RESULT_CODE_GO_TRIP_DETAIL){
            //接收findmybooking查到的行程資料
            CITripListResp tripListResp = (CITripListResp)data.getSerializableExtra(
                    UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);

            if ( null != tripListResp ) {

                if ( null != m_Listener )
                    if(CIApplication.getLoginInfo().isDynastyFlyerMember()){
                        m_Listener.addUpComingTripsData(tripListResp);
                    } else {
                        m_Listener.onReciverDataForNonMember();
                    }


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
                getActivity().startActivityForResult(intent, UiMessageDef.REQUEST_CODE_TRIP_DETAIL);

                getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
            }
        }
    }

    public void ResetListAdapter(){
        if ( null != m_Parameter )
            m_arMyTripItem  = m_Parameter.GetUpComingTrips();

        NotifyDataSetChanged();
    }

    public void IsShowProBar(boolean bShow){
        m_bIsShowPro = bShow;

        NotifyDataSetChanged();
    }

    private void NotifyDataSetChanged(){
        if ( null != m_Adapter ){
            m_Adapter.notifyDataSetChanged();
        }
    }

    public void RemoveRecycleViewListener(){
        m_bIsNoData = true;
        m_rv.removeOnScrollListener(m_ScrollListener);
    }

    public void FirstLoadingData(){
        if ( true == m_bIsNoData )
            return;

        if ( null == m_arMyTripItem || 0 >= m_arMyTripItem.size() ){
            //取得會員行程資料
            loadData();
        }
    }
}
