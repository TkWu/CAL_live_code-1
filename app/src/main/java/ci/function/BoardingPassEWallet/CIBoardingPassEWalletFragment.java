package ci.function.BoardingPassEWallet;

import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.List;

import ci.function.Base.BaseFragment;
import ci.function.BoardingPassEWallet.item.CIExtraServiceItem;
import ci.function.Core.CIApplication;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.view.ThreeItemNavigationBar;
import ci.ui.view.ThreeItemNavigationBar.EInitItem;
import ci.ws.Models.entities.CIBoardPassResp;
import ci.ws.Models.entities.CIBoardPassRespItineraryList;
import ci.ws.Models.entities.CIBoardPassResp_Itinerary;
import ci.ws.Models.entities.CIBoardPassResp_PaxInfo;
import ci.ws.Models.entities.CIBoardPassResp_PnrInfo;
import ci.ws.Models.entities.CIEWalletReq;
import ci.ws.Models.entities.CIEWallet_ExtraService_Info;
import ci.ws.Models.entities.CIEWallet_ExtraService_List;
import ci.ws.Models.entities.CIExtraServiceResp;
import ci.ws.Models.entities.CIInquiryBoardPassDBEntity;
import ci.ws.Models.entities.CIInquiryCouponResp;
import ci.ws.Models.entities.CIInquiryCoupon_Info;
import ci.ws.Models.entities.CIInquiryTripEntity;
import ci.ws.Presenter.CIInquiryBoardPassPresenter;
import ci.ws.Presenter.CIInquiryCouponInfoPresenter;
import ci.ws.Presenter.CIInquiryExtraServiceByPNRNoSITPresenter;
import ci.ws.Presenter.Listener.CIInquiryBoardPassListener;
import ci.ws.Presenter.Listener.CIInquiryCouponInfoListener;
import ci.ws.Presenter.Listener.CIInquiryExtraServiceByPNRNoSITListener;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.CIWSResultCode;

import static ci.function.BoardingPassEWallet.CIBoardingPassListFragment.CIBoardingPassListInterface;
import static ci.function.BoardingPassEWallet.CIBoardingPassListFragment.CIBoardingPassListListener;
import static ci.function.BoardingPassEWallet.CIBoardingPassListFragment.CIBoardingPassListParameter;
import static ci.function.BoardingPassEWallet.CICouponListFragment.CICouponListInterface;
import static ci.function.BoardingPassEWallet.CIExtraServiceListFragment.CIExtraServiceListInterface;
import static ci.function.BoardingPassEWallet.CIExtraServiceListFragment.CIExtraServiceListParameter;

/**
 * Created by kevincheng on 2016/3/24.
 *
 * EWallet 的部分
 * 1.有登入->直接進去->一進去塞卡號給eWalletReq, 如果trip db有pnr 就也塞進去
 * 2.沒登入->看是否有行程
 * (1).有行程->直接進去->一進去塞trip db pnr給eWalletReq
 * (2).沒行程->走findmybooking流程->find成功時記錄當下的eWalletReq及登機證結果->傳給main再傳給登機證頁面 show出
 *
 * PS:
 * 這個頁面不像My Trip
 * 沒有像my trip一樣下面有新增行程按鈕
 * 會員就是直接進去了 不會有登機證頁面又進入findMyBooking的狀況
 */
public class CIBoardingPassEWalletFragment extends BaseFragment
        implements ThreeItemNavigationBar.ItemClickListener {

    CIInquiryBoardPassListener m_BoardPassWSListener = new CIInquiryBoardPassListener() {
        @Override
        public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
            m_BoardPassResp = datas;

            SetBoardPassDataToUIAndDB();
        }

        @Override
        public void onError(String rt_code, String rt_msg) {
            if ( rt_code.equals(CIWSResultCode.NO_RESULTS) ){
                m_bBoardingPassIsNoData = true;
            }
            showDialog( getString(R.string.warning), rt_msg );
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

    private CIBoardingPassListParameter m_BoardingPassListParameter = new CIBoardingPassListParameter() {
        @Override
        public ArrayList<CIBoardPassResp_Itinerary> GetListData() {
            return m_alBoardPass_Itinerary;
        }
    };

    private CIBoardingPassListListener m_BoardingPassListListener = new CIBoardingPassListListener() {
        @Override
        public void loadData() {
            if ( null != m_BoardPassResp )
                return;

            if ( null == s_BoardPassResp ){
                loadBoardPassData();
            }else {
                m_BoardPassResp = s_BoardPassResp;
                SetBoardPassDataToUIAndDB();
            }
        }
    };

    CIInquiryExtraServiceByPNRNoSITListener m_ExtraServiceWSListener = new CIInquiryExtraServiceByPNRNoSITListener(){

        @Override
        public void onSuccess(String rt_code, String rt_msg, CIExtraServiceResp datas) {
            m_alExtraServiceResp = new ArrayList<>();

            for ( int i = 0; i < datas.size(); i ++ ){
                for ( int j = 0; j < datas.get(i).size(); j ++ ){
                    m_alExtraServiceResp.add(datas.get(i).get(j));
                }
            }
//            m_alExtraServiceResp = datas;

            SortOutExtraServicesData();
        }

        @Override
        public void onError(String rt_code, String rt_msg) {
            showDialog( getString(R.string.warning), rt_msg );
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

    private CIExtraServiceListParameter m_ExtraServiceListParameter = new CIExtraServiceListParameter(){

        @Override
        public ArrayList<CIExtraServiceItem> GetListData() {
            return m_ExtraServiceItem;
        }
    };

    CIInquiryCouponInfoListener m_CouponWSListener = new CIInquiryCouponInfoListener() {
        @Override
        public void onSuccess(String rt_code, String rt_msg, CIInquiryCouponResp datas) {
            m_CouponResp = datas;

            m_CouponListInterface.ResetList((ArrayList)datas.CouponInfo);
        }

        @Override
        public void onError(String rt_code, String rt_msg) {
            showDialog( getString(R.string.warning), rt_msg );
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

    private CIBoardingPassListInterface m_BoardingPassListInterface = null;
    private CIExtraServiceListInterface m_ExtraServiceListInterface = null;
    private CICouponListInterface       m_CouponListInterface       = null;

    private FragmentManager             m_fragmentManager           = null;
    private CIBoardingPassListFragment  m_boardingPassListFragment  = null;
    private CIExtraServiceListFragment  m_extraServiceListFragment  = null;
    private CICouponListFragment        m_couponListFragment        = null;
    private EInitItem                   m_selectedItem              = null;
    private ThreeItemNavigationBar      m_bar                       = null;

    private RelativeLayout              m_rlBg                      = null;

    private FrameLayout                 m_flSelect                  = null;
    private FrameLayout                 m_flContent                 = null;

    private static CIEWalletReq         s_EWalletReq                = null;
    private CIEWalletReq                m_EWalletReq                = null;

    private static CIBoardPassResp s_BoardPassResp           = null;
    private CIBoardPassResp m_BoardPassResp             = null;
    private ArrayList<CIBoardPassResp_Itinerary>    m_alBoardPass_Itinerary = new ArrayList<>();
    private ArrayList<CIEWallet_ExtraService_Info>  m_alExtraServiceResp    = null;
    private ArrayList<CIExtraServiceItem> m_ExtraServiceItem        = new ArrayList<>();
    private CIInquiryCouponResp         m_CouponResp                = null;

    //當前Fragment是否為隱藏
    private boolean                     m_bHide                     = true;
    private boolean                     m_bBoardingPassIsNoData     = false;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_boarding_pass_ewallet;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_rlBg      = (RelativeLayout) view.findViewById(R.id.root);
        m_flSelect  = (FrameLayout)view.findViewById(R.id.three_item_navigationbar);
        m_flContent = (FrameLayout)view.findViewById(R.id.fragment);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_rlBg);
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(final FragmentManager fragmentManager) {
        m_fragmentManager = fragmentManager;
//        getActivity().runOnUiThread(new Runnable() {
//            public void run() {
//                m_selectedItem = EInitItem.LEFT;
//                m_bar = ThreeItemNavigationBar.newInstance(getString(R.string.boarding_pass_tab_boarding_pass),
//                        getString(R.string.boarding_pass_tab_services),
//                        getString(R.string.boarding_pass_tab_coupon),
//                        ThreeItemNavigationBar.EInitItem.LEFT);
//                m_bar.setListener(CIBoardingPassEWalletFragment.this);
//                m_boardingPassListFragment = new CIBoardingPassListFragment();
//                FragmentTransaction transaction = m_fragmentManager.beginTransaction();
//                transaction.add(m_flSelect.getId(), m_bar, m_bar.getClass().getSimpleName());
//                transaction.add(m_flContent.getId(), m_boardingPassListFragment, m_boardingPassListFragment.getClass().getSimpleName());
//                transaction.commitAllowingStateLoss();
//            }
//        });
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
        //如果在three item bar 選擇到目前已經顯示畫面則不在做動作
        if(m_bar.getSelectType() == m_selectedItem){
            return;
        }
        FragmentTransaction transaction = m_fragmentManager.beginTransaction();
        switch (v.getId()){
            case R.id.rl_left_bg:
                if ( m_selectedItem.equals(EInitItem.LEFT) )
                    return;

                CancelWSReq();

                transaction.replace(R.id.fragment, m_boardingPassListFragment);
                m_selectedItem = EInitItem.LEFT;

//                loadBoardPassData();

                break;
            case R.id.rl_middle_bg:
                if ( m_selectedItem.equals(EInitItem.MIDDLE) )
                    return;

                CancelWSReq();

                transaction.replace(R.id.fragment, m_extraServiceListFragment);
                m_selectedItem = EInitItem.MIDDLE;

                loadExtraServicesData();

                break;
            case R.id.rl_right_bg:
                if ( m_selectedItem.equals(EInitItem.RIGHT) )
                    return;

                CancelWSReq();

                transaction.replace(R.id.fragment, m_couponListFragment);
                m_selectedItem = EInitItem.RIGHT;

                loadCouponData();

                break;
        }
        transaction.commitAllowingStateLoss();
    }

    private void loadBoardPassData(){

        if ( true == m_bHide )
            return;

        if ( true == m_bBoardingPassIsNoData )
            return;

        if ( null != m_alBoardPass_Itinerary && 0 < m_alBoardPass_Itinerary.size() )
            return;

        if ( false == AppInfo.getInstance(getActivity()).bIsNetworkAvailable() ){

            //沒網路時, 直接撈db裏的資料
            m_alBoardPass_Itinerary = CIInquiryBoardPassPresenter.getInstance(null).InquiryBoardPassFromDB();

            if ( null == m_alBoardPass_Itinerary ){
                showDialog(
                        getString(R.string.warning),
                        getString(R.string.no_match_data),
                        getString(R.string.confirm));

                m_alBoardPass_Itinerary = new ArrayList<>();
            }

            m_BoardingPassListInterface.ResetList();
        }else {

            setEWalletReq();

            //有登入 用卡號+db裡面的pnr搜尋; 沒有登入 卡號帶空值 僅用db裡面的pnr搜尋
            //2018-06-29 第二階段CR 新增FirstName LastName 避免PNRId重複使用導致看到別人的資料
            CIInquiryBoardPassPresenter.getInstance(m_BoardPassWSListener).InquiryBoardPassFromWSByPNRListAndCardNo(m_EWalletReq.Card_Id, m_EWalletReq.First_Name_C, m_EWalletReq.Last_Name_C, m_EWalletReq.Pnr_List);
        }
    }

    //整理BoardingPass資料
    private void SetBoardPassDataToUIAndDB(){

        //要存到db的資料
        CIBoardPassRespItineraryList list = new CIBoardPassRespItineraryList();
        //依航段整理出需要的資料並show於畫面上
        m_alBoardPass_Itinerary.clear();

        for ( int i = 0; i < m_BoardPassResp.Pnr_Info.size(); i ++ ){

            boolean bIsEffectiveData = true;
            CIBoardPassResp_PnrInfo pnrInfo = m_BoardPassResp.Pnr_Info.get(i);

            //rt_code不為000時, 該pnr資料已失效
            if ( !pnrInfo.rt_code.equals("000") )
                bIsEffectiveData = false;

            //確認該pnr資料是否有航段資料
            if ( true == bIsEffectiveData ){
                if ( null == pnrInfo.Itinerary
                        || 0 >= pnrInfo.Itinerary.size() )
                    bIsEffectiveData = false;
            }

            //確認該pnr的各個航段是否有乘客資料
            if ( true == bIsEffectiveData ){
                for ( int j = 0; j < pnrInfo.Itinerary.size(); j ++ ) {
                    CIBoardPassResp_Itinerary itinerary = pnrInfo.Itinerary.get(j);
                    if ( null == itinerary.Pax_Info || 0 >= itinerary.Pax_Info.size() ) {
                        bIsEffectiveData = false;
                    }

                    // 2016-12-20 Modify by Ryan for 移除過期的邏輯判斷！
//                    //確認該pnr是否未過期
//                    if ( true == AppInfo.getInstance(getActivity()).bIsExpired(itinerary.Boarding_Date) )
//                        bIsEffectiveData = false;

                    //只取Is_Check_In為Y的乘客資料
                    if ( true == bIsEffectiveData ){
                        ArrayList<CIBoardPassResp_PaxInfo> alPaxInfo = new ArrayList<>();

                        for (int k = 0; k < itinerary.Pax_Info.size(); k ++ ){
                            if (itinerary.Pax_Info.get(k).Is_Check_In.equals("Y")){
                                alPaxInfo.add(itinerary.Pax_Info.get(k));
                            }
                        }

                        if ( 0 == alPaxInfo.size() ){
                            bIsEffectiveData = false;
                        }else {
                            itinerary.Pax_Info = alPaxInfo;
                        }
                    }

                    if ( true == bIsEffectiveData ) {
                        m_alBoardPass_Itinerary.add(itinerary);
                        list.add(itinerary);
                    }
                }
            }
        }

        //結果需存入db
        CIInquiryBoardPassDBEntity entity = new CIInquiryBoardPassDBEntity();
        entity.respResult = GsonTool.toJson(list);

        CIInquiryBoardPassPresenter.getInstance(m_BoardPassWSListener).saveBoardPassDataToDB(entity);

        m_BoardingPassListInterface.ResetList();
    }

    private void loadExtraServicesData(){

        if ( true == m_bHide )
            return;

        if ( null != m_alExtraServiceResp )
            return;

        if ( false == AppInfo.getInstance(getActivity()).bIsNetworkAvailable() ){

            //沒網路時, 直接撈db裏的資料
            ArrayList<CIEWallet_ExtraService_List> datas =
                    CIInquiryExtraServiceByPNRNoSITPresenter.getInstance(null).InquiryExtraServiceFromDB();

            if ( null == datas ){
                showDialog(
                        getString(R.string.warning),
                        getString(R.string.no_match_data),
                        getString(R.string.confirm));
            }else {
                m_alExtraServiceResp = new ArrayList<>();
                for ( int i = 0; i < datas.size(); i ++ ){
                    for ( int j = 0; j < datas.get(i).size(); j ++ ){
                        m_alExtraServiceResp.add(datas.get(i).get(j));
                    }
                }
                SortOutExtraServicesData();
            }

        } else {

            if ( null != s_EWalletReq ){
                m_EWalletReq = s_EWalletReq;
                //2018-06-29 第二階段CR 新增FirstName LastName 避免PNRId重複使用導致看到別人的資料
                m_EWalletReq.First_Name_C = CIApplication.getLoginInfo().GetUserFirstName();
                m_EWalletReq.Last_Name_C = CIApplication.getLoginInfo().GetUserLastName();
            }

            setEWalletReq();

            CIInquiryExtraServiceByPNRNoSITPresenter.getInstance(
                    m_ExtraServiceWSListener).
                    InquiryExtraServiceByPNRNoSIT(m_EWalletReq);
        }
    }

    //整理其他服務資料, 未過期跟已過期的資料要分開
    private void SortOutExtraServicesData(){

        m_ExtraServiceItem.clear();

        ArrayList<CIEWallet_ExtraService_Info> alValid = new ArrayList<>();
        ArrayList<CIEWallet_ExtraService_Info> alExpired = new ArrayList<>();

        for ( int i = 0; i < m_alExtraServiceResp.size(); i ++ ){

            if ( null == m_alExtraServiceResp.get(i).STATUS )
                m_alExtraServiceResp.get(i).STATUS = "";

            if ( "Y".equals(m_alExtraServiceResp.get(i).STATUS) ){
                alExpired.add(m_alExtraServiceResp.get(i));
            } else {
                alValid.add(m_alExtraServiceResp.get(i));
            }
        }


        if ( 0 < alValid.size() ){
            m_ExtraServiceItem.add(new CIExtraServiceItem(false));
        }

        if ( 0 < alExpired.size() ){
            m_ExtraServiceItem.add(new CIExtraServiceItem(true));
        }

        for ( int i = 0; i < m_ExtraServiceItem.size(); i ++ ){
            if ( true == m_ExtraServiceItem.get(i).getIsExpired() ){
                m_ExtraServiceItem.get(i).m_arExtraServiceDataList = alExpired;
            }else {
                m_ExtraServiceItem.get(i).m_arExtraServiceDataList = alValid;
            }
        }

        m_ExtraServiceListInterface.ResetList();
    }

    private void loadCouponData(){

        if ( true == m_bHide )
            return;

        if ( null != m_CouponResp )
            return;

        ArrayList<CIInquiryCoupon_Info> datas;

        if ( false == AppInfo.getInstance(getActivity()).bIsNetworkAvailable() ){

            //沒網路時, 直接撈db裏的資料
            datas = CIInquiryCouponInfoPresenter.getInstance(null).InquiryCouponInfoFromDB();

            if ( null == datas || 0 >= datas.size() ){
                showDialog(
                        getString(R.string.warning),
                        getString(R.string.no_match_data),
                        getString(R.string.confirm));
            }else {
                m_CouponListInterface.ResetList(datas);
            }

        } else {
            CIInquiryCouponInfoPresenter.getInstance(m_CouponWSListener).InquiryCouponInfoFromWS();;
        }
    }

    private void CancelWSReq(){
        CIInquiryBoardPassPresenter.getInstance(m_BoardPassWSListener).interrupt();
        CIInquiryExtraServiceByPNRNoSITPresenter.getInstance(m_ExtraServiceWSListener).interrupt();
        CIInquiryCouponInfoPresenter.getInstance(m_CouponWSListener).interrupt();
    }

    private void setEWalletReq(){
        if ( null == m_EWalletReq ){
            m_EWalletReq = new CIEWalletReq();

            m_EWalletReq.Card_Id = CIApplication.getLoginInfo().GetUserMemberCardNo();
            //2018-06-29 第二階段CR 新增FirstName LastName 避免PNRId重複使用導致看到別人的資料
            m_EWalletReq.First_Name_C = CIApplication.getLoginInfo().GetUserFirstName();
            m_EWalletReq.Last_Name_C = CIApplication.getLoginInfo().GetUserLastName();

            List<CIInquiryTripEntity> lDBTrip = CIPNRStatusManager.getInstance(null).getAllTripData();
            if ( null != lDBTrip ){
                for ( int i = 0 ; i < lDBTrip.size() ; i ++ ){
                    if ( 0 < lDBTrip.get(i).PNR.length() )
                        m_EWalletReq.Pnr_List.add(lDBTrip.get(i).PNR);
                }
            }
        }
    }

    @Override
    public void onFragmentShow() {
        super.onFragmentShow();

        m_bHide = false;

        m_selectedItem = EInitItem.LEFT;
        m_bar = ThreeItemNavigationBar.newInstance(getString(R.string.boarding_pass_tab_boarding_pass),
                getString(R.string.boarding_pass_tab_services),
                getString(R.string.boarding_pass_tab_coupon),
                ThreeItemNavigationBar.EInitItem.LEFT);
        m_bar.setListener(CIBoardingPassEWalletFragment.this);

        m_boardingPassListFragment  = new CIBoardingPassListFragment();
        m_BoardingPassListInterface = m_boardingPassListFragment.uiSetParameterListener(
                m_BoardingPassListParameter, m_BoardingPassListListener);

        m_extraServiceListFragment  = new CIExtraServiceListFragment();
        m_ExtraServiceListInterface = m_extraServiceListFragment.uiSetParameterListener(
                m_ExtraServiceListParameter);

        m_couponListFragment        = new CICouponListFragment();
        m_CouponListInterface       = m_couponListFragment.uiSetParameterListener();

//        m_FragmentHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
                FragmentTransaction transaction = m_fragmentManager.beginTransaction();
                transaction.replace(m_flSelect.getId(), m_bar);
                transaction.replace(m_flContent.getId(), m_boardingPassListFragment);
                transaction.commitAllowingStateLoss();
//            }
//        }, 250);
    }

    @Override
    public void onFragmentHide() {
        super.onFragmentHide();

        if ( null != m_fragmentManager ){
            CancelWSReq();

//            FragmentTransaction transaction = m_fragmentManager.beginTransaction();
//            if ( null != m_flSelect ){
//                transaction.remove(m_bar);
//            }
//            if ( null != m_flContent ){
//                switch (m_selectedItem){
//                    case LEFT:
//                        transaction.remove(m_boardingPassListFragment);
//                        break;
//                    case MIDDLE:
//                        transaction.remove(m_extraServiceListFragment);
//                        break;
//                    case RIGHT:
//                        transaction.remove(m_couponListFragment);
//                        break;
//                }
//            }
//            transaction.commitAllowingStateLoss();
        }

        m_bHide = true;
        m_bBoardingPassIsNoData = false;

        m_EWalletReq = null;
        s_EWalletReq = null;

        m_BoardPassResp = null;
        s_BoardPassResp = null;
        m_alBoardPass_Itinerary.clear();
        m_alExtraServiceResp = null;
        m_ExtraServiceItem.clear();
        m_CouponResp = null;

        m_boardingPassListFragment = null;
        m_extraServiceListFragment = null;
        m_couponListFragment = null;

//        System.gc();

    }

    @Override
    public void onResume() {
        super.onResume();
        if ( null != m_rlBg ){
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
        CIInquiryBoardPassPresenter.getInstance(null);
        CIInquiryExtraServiceByPNRNoSITPresenter.getInstance(null);
        CIInquiryCouponInfoPresenter.getInstance(null);
//        System.gc();
    }

    public static void setBoardingPassData(CIBoardPassResp data, CIEWalletReq req){
        s_BoardPassResp = data;
        s_EWalletReq = req;
    }
}
