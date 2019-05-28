package ci.function.MyTrips;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Main.CIMainActivity;
import ci.function.MyTrips.Detail.CIMyTripsDetialActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.object.CIPNRStatusModel;
import ci.ui.object.CITripUtils;
import ci.ui.view.TwoItemNavigationBar;
import ci.ws.Models.entities.CIInquiryTripEntity;
import ci.ws.Models.entities.CIMileageRecordResp;
import ci.ws.Models.entities.CIMileageRecord_Info;
import ci.ws.Models.entities.CITripListResp;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Models.entities.CITripbyCardReq;
import ci.ws.Models.entities.CITripbyPNRReq;
import ci.ws.Presenter.CIFindMyBookingPresenter;
import ci.ws.Presenter.Listener.CIFindMyBookingForRecordListener;
import ci.ws.Presenter.Listener.CIFindMyBookingListener;
import ci.ws.define.CIWSResultCode;

import static ci.function.MyTrips.CIPastTripsFragment.onPastTripsFragmentListener;
import static ci.function.MyTrips.CIPastTripsFragment.onPastTripsFragmentParameter;
import static ci.function.MyTrips.CIUpComingTripsFragment.onUpComingTripsFragmentListener;
import static ci.function.MyTrips.CIUpComingTripsFragment.onUpComingTripsFragmentParameter;

/**
 * 行程管理
 * 分為 華航登入(有近期航程與歷史紀錄兩種選項)
 *   或 只顯示使用者某一個近期行程的頁面(只有一頁, 沒有選單)
 */
public class CIMyTripsFragment extends BaseFragment implements TwoItemNavigationBar.ItemClickListener{

    private onUpComingTripsFragmentParameter m_UpComingTripsFragmentParameter = new onUpComingTripsFragmentParameter() {
        //通知近期行程頁面 現在是否為登入狀態
        @Override
        public int GetMyTripsMode() {
            return m_iMode;
        }

        //取得近期行程資料
        @Override
        public ArrayList<CITripListResp_Itinerary> GetUpComingTrips() {
            return m_alUpComingTrips;
        }
    };

    private onUpComingTripsFragmentListener m_UpComingTripsFragmentListener = new onUpComingTripsFragmentListener() {
        //向ws請求近期行程資料
        @Override
        public void onReciverDataForNonMember() {
            clearAllData();
            getDBList();
            setUpComingTripsData();
            m_UpComingTripsFragment.ResetListAdapter();
            ((CIMainActivity)getActivity()).refreshHomePage();
        }
        @Override
        public void loadUpComingTripsData() {
            if (m_bHide)
                return;

            if ( 0 == m_alUpComingTrips.size() ){
                m_bProDlgShow = true;
            }

            //會員卡號查詢近期行程
            if ( 0 < CIApplication.getLoginInfo().GetUserMemberCardNo().length()){

                m_tripbyCardReq.Card_Id     = CIApplication.getLoginInfo().GetUserMemberCardNo();
                m_tripbyCardReq.Page_Count  = PAGE_COUNT;
                m_tripbyCardReq.Page_Number = "" + m_iPageNumberU;
                m_tripbyCardReq.Type        = CITripbyCardReq.TRIP_TYPE_FLIGHTS;

                //2018-06-29 第二階段CR
                // 將DB的PNR資料傳上去Server，由Server檢核PNR的有效性，再回傳給APP
                // 新增FirstName LastName 避免PNRId重複使用導致看到別人的資料
                m_tripbyCardReq.First_Name  = CIApplication.getLoginInfo().GetUserFirstName();
                m_tripbyCardReq.Last_Name   = CIApplication.getLoginInfo().GetUserLastName();
                List<CIInquiryTripEntity> lDBTrip = CIPNRStatusManager.getInstance(null).getAllTripData();
                Set<String> Pnr_List = new LinkedHashSet<>();
                if ( null != lDBTrip ){
                    for ( int i = 0 ; i < lDBTrip.size() ; i ++ ){
                        if ( 0 < lDBTrip.get(i).PNR.length() )
                            Pnr_List.add(lDBTrip.get(i).PNR);
                    }
                }
                m_tripbyCardReq.Pnr_List    = Pnr_List;
                //

                CIFindMyBookingPresenter.getInstance(m_FindMyBookingListener).
                        InquiryTripByCardFromWS(m_tripbyCardReq);
            }
        }

        @Override
        public void addUpComingTripsData(CITripListResp Tripslist) {
            setLoadTripsData(Tripslist);
            setUpComingTripsData();
            m_UpComingTripsFragment.ResetListAdapter();
    }

        @Override
        public void onItineraryUpdateByPNR( int ipostion ) {
            onUpdatePNRInfo(ipostion);
        }
    };

    private void onUpdatePNRInfo( final int iPostion ){

        final CITripListResp_Itinerary olditineraryInfo = m_alUpComingTrips.get(iPostion);
        final CITripListResp tripList = m_hmUpComingTrips.get(olditineraryInfo.Pnr_Id);
        if ( null == tripList ){
            return;
        }
        CITripbyPNRReq PnrReq = new CITripbyPNRReq();
        PnrReq.Pnr_id = tripList.PNR_Id;
        PnrReq.First_Name = tripList.First_Name;
        PnrReq.Last_Name = tripList.Last_Name;
        CIFindMyBookingPresenter.getInstance(new CIFindMyBookingListener() {
            @Override
            public void onInquiryTripsSuccess(String rt_code, String rt_msg, CITripListResp Tripslist ) {

                CITripListResp_Itinerary newItineraryInfo = null;
                for ( CITripListResp_Itinerary itinerary  : Tripslist.Itinerary_Info ){

                    if ( TextUtils.equals(itinerary.Segment_Number, olditineraryInfo.Segment_Number) &&
                            TextUtils.equals(itinerary.Itinerary_Num, olditineraryInfo.Itinerary_Num) ){
                        newItineraryInfo = itinerary;
                        //直接更新清單資料
                        m_hmUpComingTrips.put(Tripslist.PNR_Id, Tripslist);
                        setUpComingTripsData();
                        m_UpComingTripsFragment.ResetListAdapter();

                        //2018-07-02 更新Db的機制應該移到航段比對外面，避免因為舊航段資料有誤導致不存新的資料
//                        //2017-8-10 CR 當點擊行程進行更新後，須確認DB是否有此PNR，若有需更新資料
//                        CIPNRStatusManager  manager = CIPNRStatusManager.getInstance(null);
//                        CIPNRStatusModel    model   = manager.getPNRstatusModel();
//                        CIInquiryTripEntity data    = model.findTripDataByPNR(Tripslist.PNR_Id);
//                        if(null != data){
//                            manager.insertOrUpdatePNRDataToDB(Tripslist, data.isVisibleAtHome);
//                        }
                    }
                }
                if ( null == newItineraryInfo ){
                    newItineraryInfo = olditineraryInfo;
                }
                //2017-8-10 CR 當點擊行程進行更新後，須確認DB是否有此PNR，若有需更新資料
                CIPNRStatusManager  manager = CIPNRStatusManager.getInstance(null);
                CIPNRStatusModel    model   = manager.getPNRstatusModel();
                CIInquiryTripEntity data    = model.findTripDataByPNR(Tripslist.PNR_Id);
                if(null != data){
                    manager.insertOrUpdatePNRDataToDB(Tripslist, data.isVisibleAtHome);
                }
                //
                gotoTripDetailActivity(tripList.First_Name, tripList.Last_Name, newItineraryInfo);
            }
            @Override
            public void onInquiryTripsError(String rt_code, String rt_msg) {
                gotoTripDetailActivity(tripList.First_Name, tripList.Last_Name, olditineraryInfo);
            }
            @Override
            public void showProgress() { showProgressDialog(); }
            @Override
            public void hideProgress() { hideProgressDialog(); }

        }).InquiryTripByPNRFromWS(PnrReq);
    }

    private void gotoTripDetailActivity( String strFirstName, String strLastName, CITripListResp_Itinerary itineraryInfo ){

        Bundle bundle = new Bundle();
        bundle.putInt(UiMessageDef.BUNDLE_TRIPS_DETIAL_CURRENT_PAGE, 0);
        //傳入my trips info物件, trip detail頁面可接收並取出此物件的資料
        bundle.putSerializable( UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, itineraryInfo );
        //2018-11-01 加入FirstName LastName 來讓未登入者驗證是否為同一人
        bundle.putString(UiMessageDef.BUNDLE_FIRST_NAME, strFirstName);
        bundle.putString(UiMessageDef.BUNDLE_LAST_NAME, strLastName);

        //Activity activity = (Activity) getActivity();
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass( getActivity(), CIMyTripsDetialActivity.class);
        getActivity().startActivityForResult(intent, UiMessageDef.REQUEST_CODE_TRIP_DETAIL);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private onPastTripsFragmentParameter m_PastTripsFragmentParameter = new onPastTripsFragmentParameter() {
        //取得歷史行程資料
        @Override
        public ArrayList<CIMileageRecord_Info> GetPastTrips() {
            return m_alPastTrips;
        }
    };

    private onPastTripsFragmentListener m_PastTripsFragmentListener = new onPastTripsFragmentListener() {
        //向ws請求歷史行程資料
        @Override
        public void loadPastTripsData() {
            if ( true == m_bHide ){
                return;
            }


            if ( 0 == m_alPastTrips.size() ){
                m_bProDlgShow = true;
            }

            //會員卡號查詢歷史行程
            if ( 0 < CIApplication.getLoginInfo().GetUserMemberCardNo().length()){
                CIFindMyBookingPresenter.getInstance(m_FindMyBookingListener).
                        InquiryMileageRecordFromWS();
            }
        }

        //一律只更新近期行程
        @Override
        public void addUpComingTripsData(CITripListResp Tripslist) {
            setLoadTripsData(Tripslist);
            setUpComingTripsData();
            m_UpComingTripsFragment.ResetListAdapter();
        }

        @Override
        public void onItineraryUpdateByPNR( final int iPostion) {

        }
    };



    CIFindMyBookingListener m_FindMyBookingListener = new CIFindMyBookingForRecordListener() {

        //請求行程資料成功
        @Override
        public void onInquiryTripsSuccess(String rt_code, String rt_msg, CITripListResp Tripslist) {
            if ( TextUtils.isEmpty(Tripslist.First_Name) || TextUtils.isEmpty(Tripslist.Last_Name) ){
                Tripslist.First_Name = m_tripbyCardReq.First_Name;
                Tripslist.Last_Name = m_tripbyCardReq.Last_Name;
            }
            setLoadTripsData(Tripslist);

            if (m_bFragment){
                //當前頁面為近期行程

                m_iPageNumberU = m_iPageNumberU + 1;

                //第一次撈WS, 要加上db資料
                if (m_bProDlgShow){
                    //2018-06-29 CR 調整整理資料邏輯，將Server回覆的資料更新至DB，僅保留有效的pnr
                    //getDBList();
                    updateDataBaseTrip(Tripslist, true);
                } else {
                    updateDataBaseTrip(Tripslist, false);
                }

                //hashmap資料彙整成一包arrayList給近期行程頁面
                setUpComingTripsData();

                m_UpComingTripsFragment.ResetListAdapter();

                //後面沒有資料了 不用在向ws要資料
                if ( Integer.valueOf(PAGE_COUNT) > Tripslist.Itinerary_Info.size() ){
                    m_UpComingTripsFragment.RemoveRecycleViewListener();
                }
            }
        }

        @Override
        public void onInquiryTripsError(String rt_code, String rt_msg) {
            m_hmLoadTrips.clear();
            //即使卡號要不到資料且回覆error，也要向DB要看看資料並顯示 by Kevin
            if ( true == m_bFragment ){
                //當前頁面為近期行程
                //第一次撈WS如果使用卡號向ＷＳ要資料失敗，也要跟DB要資料，
                if ( true == m_bProDlgShow ){
                    getDBList();

                    //hashmap資料彙整成一包arrayList給近期行程頁面
                    setUpComingTripsData();

                    m_UpComingTripsFragment.ResetListAdapter();
                }
            }

            //查無資料時, 不再繼續向ws要資料
            if ( rt_code.equals(CIWSResultCode.NO_RESULTS) ){
                if ( true == m_bFragment ){
                    m_UpComingTripsFragment.RemoveRecycleViewListener();
                }
            }

            //2016-07-12 如果都沒有資料, 才顯示錯誤訊息-Ling
            if ( true == m_bFragment ){
                if ( 0 >= m_alUpComingTrips.size() ){
                    showDialog(getString(R.string.warning), rt_msg, getString(R.string.confirm));
                }
            }
        }

        @Override
        public void onInquiryMileageRecordSuccess(String rt_code, String rt_msg, CIMileageRecordResp mileageRecordResp) {

            if ( false == m_bFragment ) {
                m_alPastTrips = mileageRecordResp;
                m_PastTripsFragment.ResetListAdapter();
            }
        }

        @Override
        public void onInquiryMileageRecordError(String rt_code, String rt_msg) {
            if ( false == m_bFragment ){
                if ( 0 >= m_alPastTrips.size() ){
                    showDialog(getString(R.string.warning), rt_msg, getString(R.string.confirm));
                }
            }
        }

        @Override
        public void onAuthorizationFailedError(String rt_code, String rt_msg) {
            if ( false == m_bFragment ){
                if ( 0 >= m_alPastTrips.size() ){
                    showDialog(getString(R.string.warning), rt_msg, getString(R.string.confirm));
                }
            }
        }

        @Override
        public void showProgress() {
            if ( true == m_bProDlgShow ){
                showProgressDialog();
            }else {
                //show小圈圈
                if ( true == m_bFragment ){
                    m_UpComingTripsFragment.IsShowProBar(true);
                }else {
                    m_PastTripsFragment.IsShowProBar(true);
                }
            }
        }

        @Override
        public void hideProgress() {
            if ( true == m_bProDlgShow ){
                hideProgressDialog();

                m_bProDlgShow = false;
            }else {
                //隱藏小圈圈
                if ( true == m_bFragment ){
                    m_UpComingTripsFragment.IsShowProBar(false);
                }else {
                    m_PastTripsFragment.IsShowProBar(false);
                }
            }
        }
    };

    public enum MyTripsMode{
        BASE, CI_MEMBER
    }
    private int                                     m_iMode             = MyTripsMode.BASE.ordinal();

    private RelativeLayout                          m_rlBg              = null;

    private FrameLayout                             m_flSelect          = null;
    private FrameLayout                             m_flContent         = null;

    private TwoItemNavigationBar                    m_ItemSelect        = null;
    private CIUpComingTripsFragment                 m_UpComingTripsFragment = null;
    private CIPastTripsFragment                     m_PastTripsFragment = null;

    //true = UpComingTripsFragment;  false = PastTripsFragment
    private boolean                                 m_bFragment         = true;

    private ArrayList<CITripListResp_Itinerary>     m_alUpComingTrips   = new ArrayList<>();
    private ArrayList<CIMileageRecord_Info>         m_alPastTrips       = new ArrayList<>();

    //當前畫面的近期行程
//    private LinkedHashMap<String, ArrayList<CITripListResp_Itinerary>>
//                                                    m_hmUpComingTrips   = new LinkedHashMap<>();
    private LinkedHashMap<String, CITripListResp>   m_hmUpComingTrips   = new LinkedHashMap<>();
    //當前畫面的歷史行程
//    private LinkedHashMap<String, ArrayList<CITripListResp_Itinerary> >
//                                                    m_hmPastTrips       = new LinkedHashMap<>();
    private LinkedHashMap<String, CITripListResp>   m_hmPastTrips       = new LinkedHashMap<>();

    //剛回補的資料
//    private LinkedHashMap<String, ArrayList<CITripListResp_Itinerary>>
//                                                    m_hmLoadTrips       = new LinkedHashMap<>();
    private LinkedHashMap<String, CITripListResp>   m_hmLoadTrips       = new LinkedHashMap<>();

    private List<CIInquiryTripEntity>               m_listPNR           = new ArrayList<CIInquiryTripEntity>();
    private CITripbyCardReq                         m_tripbyCardReq     = new CITripbyCardReq();
    private final static String                     PAGE_COUNT          = "10";
    private int                                     m_iPageNumberU      = 1;
    private int                                     m_iPageNumberP      = 1;

    //當前mytripsFragment是否為隱藏
    private boolean                                 m_bHide             = true;
    //是否顯示大的loading
    private boolean                                 m_bProDlgShow       = true;

    private FragmentManager                         m_fragmentManager   = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_my_trips;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_rlBg              = (RelativeLayout) view.findViewById(R.id.rl_bg);

        m_flSelect          = (FrameLayout) view.findViewById(R.id.fl_select);
        m_flContent         = (FrameLayout) view.findViewById(R.id.fl_content);
        m_bFragment         = true;

        m_UpComingTripsFragment = new CIUpComingTripsFragment();
        m_PastTripsFragment     = new CIPastTripsFragment();

        m_ItemSelect = TwoItemNavigationBar.newInstance(
                getString(R.string.my_trips_upcoming_trips),
                getString(R.string.my_trips_past_trips));
        m_ItemSelect.setListener(CIMyTripsFragment.this);
    }

    //將剛撈到的資料存入hashmap
    private void setLoadTripsData(CITripListResp resp){
        m_hmLoadTrips.clear();

        //調整資料儲存的結構
        //將撈到的資料丟進hashmap
        CITripListResp newResp = null;
        for ( int i = 0; i < resp.Itinerary_Info.size(); i ++ ){
            String strPNR = resp.Itinerary_Info.get(i).Pnr_Id;
            CITripListResp_Itinerary respPNRInfo = resp.Itinerary_Info.get(i);

            //檢查該pnr是否存在, 將pnr 對應的name 存起來, 方便日後更新
            if ( !m_hmLoadTrips.containsKey(strPNR) ){
                newResp = new CITripListResp();
                newResp.First_Name      = resp.First_Name;
                newResp.Last_Name       = resp.Last_Name;
                newResp.PNR_Id          = strPNR;
                newResp.Itinerary_Num   = respPNRInfo.Itinerary_Num;
                newResp.Status_Code     = respPNRInfo.Status_Code;
                newResp.Is_Select_Meal  = resp.Is_Select_Meal;
                newResp.Segment_Num     = respPNRInfo.Segment_Number;

            }else {
                newResp = m_hmLoadTrips.get(strPNR);
            }
            //將同一個Pnr的行程都整理在一起, 以pnr為key, 用來檢查pnr是否重複
            newResp.Itinerary_Info.add(resp.Itinerary_Info.get(i));
            m_hmLoadTrips.put(strPNR, newResp);
        }

    }

    //近期行程資料存入hashmap
    private void setUpComingTripsData(){
        //2018-06-29 CR 調整邏輯，已回補回來的資料更新畫面上的資料
        for ( String key : m_hmLoadTrips.keySet() ){
            //if ( !m_hmUpComingTrips.containsKey(key) ){
                m_hmUpComingTrips.put(key, m_hmLoadTrips.get(key));
            //}
        }

        m_alUpComingTrips.clear();
        for ( String key : m_hmUpComingTrips.keySet() ){
            CITripListResp trip = m_hmUpComingTrips.get(key);
            if ( null != trip && null != trip.Itinerary_Info && trip.Itinerary_Info.size() > 0 ){
                //
                m_alUpComingTrips.addAll(trip.Itinerary_Info);
            }
        }

        if( m_alUpComingTrips == null ){
            return;
        }
        /*
        行程管理- 近期行程 清單排列邏輯
        (1)以今天日期為分組條件，分為今日之前B組(不包含今日)，及今日之後A組。
        (2)再將A組由行程依照日期近至遠排序，畫面清單上方應優先顯示離今日最近的行程
        (3)B組部份不顯示
        */

        ArrayList<CITripListResp_Itinerary> trips = (ArrayList<CITripListResp_Itinerary>)m_alUpComingTrips.clone();
        ArrayList<CITripListResp_Itinerary> tripsBeforeToday ;

        tripsBeforeToday = CITripUtils.filterTripsBeforeDate(trips);

        //2018-11-08 調整邏輯，避免只有一筆反而沒過濾到
        //小於兩筆就不做排列
        if( m_alUpComingTrips.size() > 1 ){
            trips           = CITripUtils.getSortedTripsByDate(trips);
            tripsBeforeToday = CITripUtils.getSortedTripsByDate(tripsBeforeToday);
        }

        m_alUpComingTrips.clear();
        m_alUpComingTrips.addAll(trips);
        //2017-08-29 CR 行程管理經過排序後分組於今天之前
        // 的群組在下方，目前不做顯示，但保留日後使用
//        m_alUpComingTrips.addAll(tripsBeforeToday);
    }

    //db資料存入hashmap
    //從DB取出來的資料,如果跟原來會員的資料有重複的, 依會員資料為主 2016-10-07
    private void getDBList(){
        m_listPNR = (ArrayList)CIPNRStatusManager.getInstance(null).getAllTripData();

        if ( m_listPNR == null ){
            m_listPNR = new ArrayList<>();
            return;
        }

        for (int i = 0; i < m_listPNR.size(); i ++){
            Gson gson = new Gson();
            CITripListResp pnrData = null;

            //將DB存的資料轉成物件,
            try {
                pnrData = gson.fromJson(
                        m_listPNR.get(i).respResult, CITripListResp.class);
            } catch (Exception e ){
                e.printStackTrace();
            }

            if ( null != pnrData ){
                //只取沒有重複的資料,
                //目的是資料以會員資料為主, DB的資料為輔,
                //所以有重複的pnr 依照會員為主
                if ( !m_hmLoadTrips.containsKey(pnrData.PNR_Id) ){
                    //將整份資料都存起來, 為了日後更新需要
                    m_hmLoadTrips.put(pnrData.PNR_Id, pnrData);
                    //m_hmLoadTrips.put(pnrData.PNR_Id, (ArrayList)pnrData.Itinerary_Info);
                }
            }

        }
    }

    /**
     * 2018-06-29 CR
     * 使用跟Server取得新的且正確的PNR 資料來更新DB內的PNR
     * */
    private void updateDataBaseTrip( CITripListResp Tripslist, boolean bHasGetData ){

        //m_hmLoadTrips 內已經是整理過的行程
        //將從Server補回來且整理完的PNR更新至DB
        String strHomePNR = "";
        List<CIInquiryTripEntity> listHome = CIPNRStatusManager.getInstance(null).getAllTripDataForOnlyVisibleInHome();
        if ( null != listHome && listHome.size() > 0 ){
            strHomePNR = listHome.get(0).PNR;
        }
        for ( String key : m_hmLoadTrips.keySet() ){
            CITripListResp newTripslist = m_hmLoadTrips.get(key);
            boolean bVisHome = false;
            if ( TextUtils.equals(strHomePNR, key) ){
                bVisHome = true;
            }
            CIPNRStatusManager.getInstance(null).insertOrUpdatePNRDataToDB(newTripslist, bVisHome);
        }
        //刪除失效的PNR
        if ( null != Tripslist.PNR_List && Tripslist.PNR_List.size() > 0 ){
            CIPNRStatusManager.getInstance(null).deleteTripDataByPNR(new ArrayList<String>(Tripslist.PNR_List));
        }

        //接著將正確的資料出準備顯示
        //並且將行程資訊更新至 m_hmLoadTrips
        //只有第一次進入畫面才需要從DB一次取出資料
        if ( bHasGetData ){

            m_listPNR = (ArrayList)CIPNRStatusManager.getInstance(null).getAllTripData();
            m_hmLoadTrips.clear();

            if ( m_listPNR == null ){
                m_listPNR = new ArrayList<>();
                return;
            }

            for (int i = 0; i < m_listPNR.size(); i ++){
                Gson gson = new Gson();
                CITripListResp pnrData = null;

                //將DB存的資料轉成物件,
                try {
                    pnrData = gson.fromJson(
                            m_listPNR.get(i).respResult, CITripListResp.class);
                } catch (Exception e ){
                    e.printStackTrace();
                }

                //將資料放入準備顯示的容易，再做一層檢查，基本上DB撈出來的資料不會重複。
                if ( null != pnrData ){
                    if ( !m_hmLoadTrips.containsKey(pnrData.PNR_Id) ){
                        m_hmLoadTrips.put(pnrData.PNR_Id, pnrData);
                    }
                }

            }
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.setMargins(m_flSelect, 20, 20, 20, 0);
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(final FragmentManager fragmentManager) {
        m_fragmentManager = fragmentManager;
    }

    private void ChangeFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = m_fragmentManager.beginTransaction();
        fragmentTransaction.replace(m_flContent.getId(), fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void ResetTwoItemFragment(){
        FragmentTransaction fragmentTransaction = m_fragmentManager.beginTransaction();
        fragmentTransaction.replace(m_flSelect.getId(), m_ItemSelect);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onItemClick(View v) {
        switch (v.getId()){
            case R.id.rl_left_bg:
                if (m_bFragment)
                    return;
                CancelWSReq();
                ChangeFragment(m_UpComingTripsFragment);
                m_UpComingTripsFragment.FirstLoadingData();
                m_bFragment = true;
                break;
            case R.id.rl_right_bg:
                if (!m_bFragment)
                    return;
                CancelWSReq();
                ChangeFragment(m_PastTripsFragment);
                m_PastTripsFragment.FirstLoadingData();
                m_bFragment = false;
                break;
        }
    }

    @Override
    public void onFragmentShow() {
        super.onFragmentShow();

        if (CIApplication.getLoginInfo().GetLoginType().equals(
                UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)){
            m_iMode = MyTripsMode.CI_MEMBER.ordinal();
            m_flSelect.setVisibility(View.VISIBLE);
        }else {
            m_iMode = MyTripsMode.BASE.ordinal();
            m_flSelect.setVisibility(View.GONE);

            //從db獲得未登入的行程資料
            getDBList();
            setUpComingTripsData();
        }

        m_iPageNumberU  = 1;
        m_iPageNumberP  = 1;
        m_bFragment     = true;
        m_bHide         = false;
        m_bProDlgShow   = true;

        m_UpComingTripsFragment = new CIUpComingTripsFragment();
        m_UpComingTripsFragment.uiSetParameterListener(
                m_UpComingTripsFragmentParameter,
                m_UpComingTripsFragmentListener);

        m_PastTripsFragment     = new CIPastTripsFragment();
        m_PastTripsFragment.uiSetParameterListener(
                m_PastTripsFragmentParameter,
                m_PastTripsFragmentListener);

        m_ItemSelect = TwoItemNavigationBar.newInstance(
                getString(R.string.my_trips_upcoming_trips),
                getString(R.string.my_trips_past_trips));
        m_ItemSelect.setListener(CIMyTripsFragment.this);


        ChangeFragment(m_UpComingTripsFragment);
        ResetTwoItemFragment();
    }

    @Override
    protected void onFragmentResume() {
        super.onFragmentResume();
    }

    @Override
    protected void onFragmentPause() {
        super.onFragmentPause();
    }

    @Override
    public void onFragmentHide() {
        super.onFragmentHide();

        clearAllData();

        m_bHide         = true;

        if ( null != m_fragmentManager ){

            CancelWSReq();

        }


    }

    private void clearAllData(){
        m_alUpComingTrips.clear();
        m_hmUpComingTrips.clear();
        m_alPastTrips.clear();
        m_hmPastTrips.clear();
        m_hmLoadTrips.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if( null != m_rlBg ){
            m_rlBg.setBackgroundResource(R.drawable.bg_world_map);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if ( null != m_rlBg ){
            if ( m_rlBg.getBackground() instanceof BitmapDrawable){
                m_rlBg.setBackground(null);
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        CIFindMyBookingPresenter.getInstance(null);
    }

    //取消wsu請求
    private void CancelWSReq(){
        CIFindMyBookingPresenter.getInstance(m_FindMyBookingListener).InquiryTripCancel();
    }
}
