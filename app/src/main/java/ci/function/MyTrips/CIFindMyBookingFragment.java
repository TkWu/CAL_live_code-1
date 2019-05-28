package ci.function.MyTrips;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Signup.CIBecomeDynastyFlyerActivity;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIBookingRefTicketTextFieldFragment;
import ci.ui.TextField.CIOnlyEnglishTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewIdDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CILoginInfo;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.object.CIPNRStatusModel;
import ci.ws.Models.entities.CIBoardPassResp;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CICheckInAllPaxResp;
import ci.ws.Models.entities.CIEWalletReq;
import ci.ws.Models.entities.CIInquiryTripEntity;
import ci.ws.Models.entities.CITripListResp;
import ci.ws.Models.entities.CITripbyPNRReq;
import ci.ws.Models.entities.CITripbyTicketReq;
import ci.ws.Presenter.CIFindMyBookingPresenter;
import ci.ws.Presenter.CIInquiryBoardPassPresenter;
import ci.ws.Presenter.CIInquiryCheckInPresenter;
import ci.ws.Presenter.Listener.CIFindMyBookingListener;
import ci.ws.Presenter.Listener.CIInquiryBoardPassListener;
import ci.ws.Presenter.Listener.CIInquiryCheckInListener;

import static ci.function.Login.CILoginActivity.LoginMode;
import static ci.ui.TextField.CIBookingRefTicketTextFieldFragment.Type;

/**
 * Created by kevin on 2016/2/26.
 */
public class CIFindMyBookingFragment extends BaseFragment
        implements View.OnClickListener{

    CIInquiryBoardPassListener m_BoardingPassWSListener = new CIInquiryBoardPassListener() {
        @Override
        public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
            m_BoardPassResp = datas;
            //找到資料就準備Finish這個頁面
            FindMyBookingFinish();
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

    CIFindMyBookingListener m_FindMyBookingWSListener = new CIFindMyBookingListener() {
        @Override
        public void onInquiryTripsSuccess(String rt_code, String rt_msg, CITripListResp Tripslist) {
            CIPNRStatusModel model = new CIPNRStatusModel();

            //當查詢行程成功時，將行程資料寫入DB
            if(null != Tripslist){
                CILoginInfo info = CIApplication.getLoginInfo();
                CIPNRStatusManager manager = CIPNRStatusManager.getInstance(null);
                if(!info.isDynastyFlyerMember()){
                    //因為沒登入或不是正式會員的狀況下，只留一組資料
                    manager.getPNRstatusModel().Clear();
                }
                
                //由這邊找到的PNR資料需過濾是否超過24小時
//                int size = Tripslist.Itinerary_Info.size();
//                List<CITripListResp_Itinerary> newData = new ArrayList<>();
//                for(int loop = 0; loop < size ; loop++){
//                    CITripListResp_Itinerary data = Tripslist.Itinerary_Info.get(loop);
//                    if(!manager.CheckPNRTimeIsOver24hr(data)){
//                        newData.add(data);
//                    }
//                }

//                Tripslist.Itinerary_Info = newData;

                //如果已經過濾到沒有任何航段資料，就不儲存到DB直接結束頁面
                if(Tripslist.Itinerary_Info.size() <= 0){
                    showDialog(getString(R.string.warning), getString(R.string.my_trips_not_find));
                    m_TripListResp = null;
                    return;
                }


                if(true == m_bIsFromHome){
                    //無論身份為何，如果是從首頁進入findMyBooking而且查詢資料成功則初始化DB所有資料不顯示在首頁
                    model.UpdateAllPNRisVisibleToFalse();
                    manager.insertOrUpdatePNRDataToDB(Tripslist, true);
                } else {
                    CIInquiryTripEntity data = model.findTripDataByPNR(Tripslist.PNR_Id);
                    if(null == data){
                        manager.insertOrUpdatePNRDataToDB(Tripslist, false);
                    } else {
                        manager.insertOrUpdatePNRDataToDB(Tripslist, data.isVisibleAtHome);
                    }
                }
            }

            m_TripListResp = Tripslist;
            //找到資料就準備Finish這個頁面
            FindMyBookingFinish();
        }

        @Override
        public void onInquiryTripsError(final String rt_code, final String rt_msg) {
            m_TripListResp = null;
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

    private CIInquiryCheckInListener m_CheckInListener = new CIInquiryCheckInListener() {
        @Override
        public void onInquiryCheckInSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {

            if(isFlightExist(CheckInList)) {
                m_CICheckInList = CheckInList;
                //找到資料就準備Finish這個頁面
                FindMyBookingFinish();

            } else {
                showDialog(getString(R.string.warning),
                        getString(R.string.pnr_not_found),
                        getString(R.string.confirm));
            }
        }

        @Override
        public void onInquiryCheckInError(String rt_code, String rt_msg) {

            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void onInquiryCheckInAllPaxSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {

        }

        @Override
        public void onInquiryCheckInAllPaxError(String rt_code, String rt_msg) {

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

    private boolean isFlightExist(CICheckInAllPaxResp CheckInList) {

        for( int iPos = 0 ; iPos < CheckInList.size() ; iPos ++ ) {
            CICheckInPax_InfoEntity entity = CheckInList.get(iPos);

            for( int iInfoPos = 0 ; iInfoPos < entity.m_Itinerary_InfoList.size() ; iInfoPos ++ ) {

                CICheckInPax_ItineraryInfoEntity infoEntity = entity.m_Itinerary_InfoList.get(iInfoPos);

                if( !infoEntity.Is_Black && !infoEntity.Is_Check_In && infoEntity.Is_Do_Check_In ) {
                    return true;
                }
            }
        }
        return false;
    }

    //Boarding Pass 與 Check-in 的findMyBooking邏輯不一樣
    public enum FindMyBookingType{
        BASE, BOARDING_PASS, CHECK_IN
    }

    public static CIFindMyBookingFragment newInstance(LoginMode mode, FindMyBookingType type, int viewId) {
        Bundle args = new Bundle();
        args.putInt(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, viewId);
        args.putString(FIND_MY_BOOKING_TYPE, type.name());
        args.putString(LOGIN_MODE, mode.name());
        CIFindMyBookingFragment fragment = new CIFindMyBookingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(null != bundle){
            String mode = bundle.getString(LOGIN_MODE);
            if(!TextUtils.isEmpty(mode)){
                this.m_LoginMode = LoginMode.valueOf(mode);
            }
            String type = bundle.getString(FIND_MY_BOOKING_TYPE);
            if(!TextUtils.isEmpty(type)){
                this.m_FindMyBookingType = FindMyBookingType.valueOf(type);
            }

            m_iViewId = bundle.getInt(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, 0);

            //是否由首頁點擊按鈕進入此頁面
            if(m_iViewId == ViewIdDef.VIEW_ID_HOME){
                this.m_bIsFromHome = true;
            }
        }
        super.onCreate(savedInstanceState);
    }

    private static final String FIND_MY_BOOKING_TYPE = "FIND_MY_BOOKING_TYPE";
    private static final String LOGIN_MODE = "LOGIN_MODE";

    private FindMyBookingType m_FindMyBookingType = FindMyBookingType.BASE;

    private LoginMode m_LoginMode = LoginMode.FIND_MYBOOKING_ONLY_RETRIEVE;

    private CITextFieldFragment m_retrieveBookingFragment,
                                m_firstNameFragment,
                                m_lastNameFragment;

    private CIEWalletReq        m_EWalletReq    = null;
    private CIBoardPassResp m_BoardPassResp  = null;
    private CITripListResp      m_TripListResp  = null;
    private CICheckInAllPaxResp m_CICheckInList = null;
    private String              m_strFirstName  = null;
    private String              m_strLastName   = null;
    private boolean             m_bIsFromHome   = false;
    private int                 m_iViewId       = 0;

    //新增接CPR的資料
    private CICheckInAllPaxResp m_CheckInResp   = null;

    //2016-07-15 ryan 調整Check-in Presenter 使用方式
    private CIInquiryCheckInPresenter m_InquiryCheckInPresenter = null;

    /**
     * BaseFragment在
     * {@link BaseFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
     * onCreateView()}時 設定{@link LayoutInflater#inflate(int, ViewGroup, boolean)
     * inflate()}用
     *
     * @return 此畫面的 Layout Resource Id
     */
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_find_my_booking;
    }

    /**
     * 元件初始化，靜態取得元件實體、動態建製元件實體…等
     *
     * @param inflater
     * @param view
     */
    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) { }

    /**
     * 設定字型大小及版面大小
     *
     * @param view
     * @param vScaleDef 請參閱{@link ViewScaleDef}
     */
    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_arrow), 17, 17);
    }

    /**
     * 通知父BaseActivity對子BaseFragment設定客製的「OnParameter(參數讀取)」跟「OnListener(
     * 動線通知)」介面
     *
     * @param view
     */
    @Override
    protected void setOnParameterAndListener(View view) {
        view.findViewById(R.id.iv_arrow).setOnClickListener(this);
        view.findViewById(R.id.tv_can_find_the_trip).setOnClickListener(this);
        view.findViewById(R.id.btn_findmybooking_retrieve).setOnClickListener(this);


        //2016-07-15 ryan 調整Check-in Presenter 使用方式
        m_InquiryCheckInPresenter = new CIInquiryCheckInPresenter(m_CheckInListener);
    }

    /**
     * 依需求調用以下函式
     *
     * @param fragmentManager
     * @see FragmentTransaction FragmentTransaction相關操作
     */
    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        m_retrieveBookingFragment = CIBookingRefTicketTextFieldFragment.newInstance(getActivity());
        m_firstNameFragment = CIOnlyEnglishTextFieldFragment
                .newInstance(getString(R.string.inquiry_input_box_first_name_hint));
        m_lastNameFragment = CIOnlyEnglishTextFieldFragment
                .newInstance(getString(R.string.inquiry_input_box_last_name_hint));
        transaction
                .replace(R.id.fragment1, m_retrieveBookingFragment)
                //2016-11-09 Ryan , 對調姓氏與名字
                .replace(R.id.fragment2, m_lastNameFragment)
                .replace(R.id.fragment3, m_firstNameFragment)
                //.replace(R.id.fragment2, m_firstNameFragment)
                //.replace(R.id.fragment3, m_lastNameFragment)
                .commitAllowingStateLoss();
        if(null != getActivity()){
            getActivity().getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    //2016-11-14 Ryan, for 調整元件順序
                    m_firstNameFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    //m_lastNameFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
                }
            });
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                CILoginInfo info = CIApplication.getLoginInfo();
                if(info.isDynastyFlyerMember()) {
                    String firstName = info.GetUserFirstName();
                    String lastName = info.GetUserLastName();
                    if(!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)){
                        m_firstNameFragment.setText(firstName);
                        m_lastNameFragment.setText(lastName);
                        m_firstNameFragment.setLock(true);
                        m_lastNameFragment.setLock(true);
                    }
                }
            }
        });
    }

    /**
     * 若收到Handle Message且BaseActivity不認得時，
     * 視為子class自訂Message，可經由此Function接收通知。
     *
     * @param msg
     * @return true：代表此Message已處理<p> false：代表此Message連子class也不認得<p>
     */
    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    /**
     * 若子class有自訂Message，請經由此Function清空Message。
     */
    @Override
    protected void removeOtherHandleMessage() {}

    /**
     * 當App語言變更後, 會呼叫此介面，藉此更新畫面UI,需要重新呼叫setText
     */
    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onPause() {

        CIInquiryBoardPassPresenter.getInstance(m_BoardingPassWSListener).interrupt();
        CIFindMyBookingPresenter.getInstance(m_FindMyBookingWSListener).InquiryTripCancel();
        //CIInquiryCheckInPresenter.getInstance(m_CheckInListener).InquiryCheckInCancel();
        m_InquiryCheckInPresenter.InquiryCheckInCancel();

        super.onPause();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btn_findmybooking_retrieve:

                //尚未輸入訂位代號或機票號
                String strBooking = m_retrieveBookingFragment.getText().toString();
                if ( 0 >= strBooking.length() ){

                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field),
                                    m_retrieveBookingFragment.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                //尚未輸入名
                m_strFirstName = m_firstNameFragment.getText().toString().trim();
                if ( 0 >= m_strFirstName.length() ){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field),
                                    m_firstNameFragment.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                //尚未輸入姓
                m_strLastName  = m_lastNameFragment.getText().toString().trim();
                if ( 0 >= m_strLastName.length() ){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field),
                                    m_lastNameFragment.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                //格式不符
                Type type = ((CIBookingRefTicketTextFieldFragment)m_retrieveBookingFragment)
                        .getTextFeildType();
                if ( type.equals(Type.NONE) ) {
                    showDialog(getString(R.string.warning),
                            getString(R.string.member_login_input_correvt_format_msg),
                            getString(R.string.confirm));
                } else if ( type.equals(Type.BOOKING_REF) ){

                    //依訂位代號查詢
                    if ( FindMyBookingType.BOARDING_PASS == m_FindMyBookingType ) {
                        //查登機證
                        m_EWalletReq = new CIEWalletReq();
                        m_EWalletReq.PNR_ID = strBooking;
                        m_EWalletReq.First_Name_P = m_strFirstName;
                        m_EWalletReq.Last_Name_P = m_strLastName;

                        CIInquiryBoardPassPresenter.getInstance(m_BoardingPassWSListener).
                                InquiryBoardPassFromWSByPNRNo(strBooking, m_strFirstName, m_strLastName);

                    } else if (FindMyBookingType.CHECK_IN == m_FindMyBookingType){
                        m_InquiryCheckInPresenter.InquiryCheckInByPNRFromWS(strBooking,m_strFirstName,m_strLastName);
                    } else {
                        //查行程
                        CITripbyPNRReq tripbyPNRReq = new CITripbyPNRReq();
                        tripbyPNRReq.Pnr_id     = strBooking;
                        tripbyPNRReq.First_Name = m_strFirstName;
                        tripbyPNRReq.Last_Name  = m_strLastName;

                        CIFindMyBookingPresenter.getInstance(m_FindMyBookingWSListener).
                                InquiryTripByPNRFromWS(tripbyPNRReq);
                    }
                }else if(type.equals(Type.TICKET)){
                    //依機票號碼查詢
                    if ( FindMyBookingType.BOARDING_PASS == m_FindMyBookingType ){
                        //查登機證
                        m_EWalletReq = new CIEWalletReq();
                        m_EWalletReq.Ticket = strBooking;
                        m_EWalletReq.First_Name_T = m_strFirstName;
                        m_EWalletReq.Last_Name_T = m_strLastName;

                        CIInquiryBoardPassPresenter.getInstance(m_BoardingPassWSListener).
                                InquiryBoardPassFromWSByTicket(strBooking, m_strFirstName, m_strLastName);

                    } else if (FindMyBookingType.CHECK_IN == m_FindMyBookingType) {
                        m_InquiryCheckInPresenter.InquiryCheckInByTicketFromWS(strBooking,m_strFirstName,m_strLastName);
                    } else {
                        //查行程
                        CITripbyTicketReq tripbyTicketReq = new CITripbyTicketReq();
                        tripbyTicketReq.Ticket      = strBooking;
                        tripbyTicketReq.First_Name  = m_strFirstName;
                        tripbyTicketReq.Last_Name   = m_strLastName;

                        CIFindMyBookingPresenter.getInstance(m_FindMyBookingWSListener).
                                InquiryTripByTicketFromWS(tripbyTicketReq);
                    }
                }
                break;
            case R.id.iv_arrow:
            case R.id.tv_can_find_the_trip:
                changeActivity(CIFindMyBookingNotesActivity.class, UiMessageDef.BUNDLE_CHKIN_FINDMYBOOKING_NOTES, "1");
                break;
        }
    }

    private void FindMyBookingFinish(){

        Intent intent = new Intent();

        if ( FindMyBookingType.BOARDING_PASS == m_FindMyBookingType ){
            if ( null != m_BoardPassResp ){
                //將EWallet Req資料放入Bundle
                intent.putExtra( UiMessageDef.BUNDLE_EWALLET_REQUEST, m_EWalletReq);
                //將取回的登機證資料放入Bundle
                intent.putExtra( UiMessageDef.BUNDLE_BOARDING_PASS_DATA, m_BoardPassResp);
            }
        }else {
            if ( null != m_TripListResp ){
                //將取回的行程資料放入Bundle
                intent.putExtra( UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, m_TripListResp);
            }
        }


        /**
         * 此條件判斷式主要是用來判斷分別從哪些入口進入
         *
         * 如果Loing mode 是只有FindMyBooking獨立頁面(FIND_MYBOOKING_ONLY_RETRIEVE)
         * ，代表是從首頁 CIMainActivity 及CIMyTripFragment 內的 CIUpComingTripsFragment(近期行程)
         * 及 CIPastTripsFragment (歷史紀錄) 的新增行程按鈕
         * 以及 正式會員登入後從首頁按「搜尋行程」 進入才會有的模式
         */
        if ( LoginMode.FIND_MYBOOKING_ONLY_RETRIEVE == m_LoginMode ){
            //整個版面都是FIND MYBOOKING 用在兩個地方
            //1. my trip新增行程
            //2. 首頁A Type時 查詢行程
            //3. 點boarding Pass - 已登入, 但db裏沒有行程時
            //TODO: 流程9.2-2 原本是詢問是否有同行乘客 改為直接進入trip detail
//            Intent intent = new Intent();
//            intent.putExtra(
//                    UiMessageDef.BUNDLE_ACTIVITY_MODE,
//                    CIAddPassengerActivity.EMode.PRECURSOR.name());
//            intent.setClass(getActivity(), CIAddPassengerActivity.class);
//            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_HOME);
//            getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);

            //--------------------------------------------------------------------------------------
            if(true == m_bIsFromHome){
                /*
                * 如果是從首頁進入FindMyBooking獨立頁面(FIND_MYBOOKING_ONLY_RETRIEVE)
                * 則完成後設Result_ok
                * */
                getActivity().setResult( getActivity().RESULT_OK, intent );
                getActivity().finish();

            } else {
                /*
                * 無論登入於否，取得行程資料，跳轉至trip detail，result code將會在code將會在CIMainActivity 及
                * CIMyTripFragment 內的 CIUpComingTripsFragment(近期行程)及 CIPastTripsFragment (歷史紀錄)
                * 接收做處理，使用ＷＳ取得的行程資料也將在這些頁面中接收來使用
                * 目前這些頁面onActivityResult接收到request code的處理方式：
                * CIMainActivity：           回首頁(暫時處理)
                * CIUpComingTripsFragment：  跳轉CIMyTripDetailActivity
                * CIPastTripsFragment：      跳轉CIMyTripDetailActivity
                * */

                getActivity().setResult(UiMessageDef.RESULT_CODE_GO_TRIP_DETAIL, intent);
                getActivity().finish();
            }

        }else {

            /**
            * 如果Loing mode 是有登入和搜尋機票和訂位代號的頁面，代表是從首頁 SideMenu 進入
            */
            if ( FindMyBookingType.CHECK_IN == m_FindMyBookingType ){
                //首頁點選check in頁面 但未找到行程時 需查詢行程 查詢到後就直接顯示check in頁面

                /*
                * 如果是從SideMenu 點擊 Check in 進入，則回到首頁 並帶view id, 讓首頁自動跳轉至check-in頁面，
                * result code將會在code將會在CIMainActivity 接收做處理，使用ＷＳ取得的行程資料也將在這些頁面中
                * 接收來使用目前這些頁面onActivityResult接收到request code的處理方式：
                * CIMainActivity：           回首頁之後並跳轉Check-in頁面
                * */

                if( null != m_CICheckInList ) {
                    //將取回的航班資料放入Bundle
                    intent.putExtra( UiMessageDef.BUNDLE_CHECK_IN_FLIGHT_LIST, m_CICheckInList);
                }

                intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, ViewIdDef.VIEW_ID_CHECK_IN);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            } else {
                /*
                * 未登入狀態 如果是從SideMenu 點擊 my trips 或 登機證 進入，則查詢到行程資料後將詢問是否成為正式會員
                * 跳轉至CIBecomeDynastyFlyerActivity頁面，本頁面不會Finish，會等待註冊流程成功或取消後在onActivityResult
                * 接收requestcode 跳轉處理進行處理
                * */
                intent.setClass(getActivity(), CIBecomeDynastyFlyerActivity.class);
                intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE,
                        CIBecomeDynastyFlyerActivity.EMode.TEMPORARY_MEMBER.name());
                intent.putExtra(UiMessageDef.BUNDLE_NOT_LOGIN_USERNAME_TAG,
                        m_strFirstName + " " + m_strLastName );
                intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, m_iViewId);
                startActivityForResult(intent, UiMessageDef.REQUEST_CODE_BECOME_MEMBER);
                getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO: 流程9.2-2 原本是詢問是否有同行乘客 改為直接進入trip detail
//        if ( requestCode == UiMessageDef.REQUEST_CODE_HOME && resultCode == getActivity().RESULT_OK ){
//            //是否已登入
//            if ( true == CIApplication.getLoginInfo().GetLoginStatus()) {
//                //已登入時取到行程 跳轉至trip detail
//                Intent intent = new Intent();
//                if ( null != m_TripListResp ){
//                    intent.putExtra( UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, m_TripListResp);
//                    getActivity().setResult( UiMessageDef.RESULT_CODE_GO_TRIP_DETAIL, intent );
//                }else {
//                    getActivity().setResult( UiMessageDef.RESULT_CODE_GO_TRIP_DETAIL);
//                }
//            }else {
//                //其他一律回首頁
//                getActivity().setResult( getActivity().RESULT_OK );
//            }
//            getActivity().finish();
//        }else


        /*
        * ADD_PASSENGER流程，目前沒用到！
        * */
//        if ( requestCode == UiMessageDef.REQUEST_CODE_HOME && resultCode == UiMessageDef.RESULT_CODE_ADD_PASSENGER ){
//            Intent intent = new Intent();
//            intent.putExtra(
//                    UiMessageDef.BUNDLE_ACTIVITY_MODE,
//                    CIAddPassengerActivity.EMode.BASE_MORE.name());
//            intent.setClass(getActivity(), CIAddPassengerActivity.class);
//            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_HOME);
//            getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
//            return;
//        }

        /*
        * 由本頁面（FindMyBooking）轉跳至其他Activity（如開始加入會員頁面）會回來在onResultActivity的處理
        * 無論是否有加入會員將finish本頁面並且將result code帶回CIMainActivity 處理以及跳轉至由Side Menu
        * 那個頁面進入就跳轉到哪個頁面，使用ＷＳ取得的行程資料也將會帶過去，如果原本是用行程管理進入此頁面，
        * 則會將ＷＳ取得的行程資料帶入行程管理頁面顯示。
        * */
        if  ( requestCode == UiMessageDef.REQUEST_CODE_BECOME_MEMBER ){
            if(null == data){
                data = new Intent();
            }

            if ( FindMyBookingType.BOARDING_PASS == m_FindMyBookingType ){
                if ( null != m_BoardPassResp ){
                    //將EWallet Req資料放入Bundle
                    data.putExtra( UiMessageDef.BUNDLE_EWALLET_REQUEST, m_EWalletReq);
                    //將取回的登機證資料放入Bundle
                    data.putExtra( UiMessageDef.BUNDLE_BOARDING_PASS_DATA, m_BoardPassResp);
                }
            }else {
                if ( null != m_TripListResp ){
                    data.putExtra( UiMessageDef.BUNDLE_TRIP_LIST_RESP, m_TripListResp);
                }
            }
            data.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, m_iViewId);

            getActivity().setResult(getActivity().RESULT_OK, data);
            getActivity().finish();
            return;
        }
    }

    /**
     * 轉換Activity
     * @param clazz 目標activity名稱
     */
    private void changeActivity(Class clazz){
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        //測試切換activity滑入滑出動畫
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    /**
     * 轉換Activity
     * @param clazz 目標activity名稱
     * @param key   extra key
     * @param extra extra value
     */
    private void changeActivity(Class clazz,String key,String extra){
        Intent intent = new Intent();
        intent.putExtra(key, extra);
        intent.setClass(getActivity(), clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CIInquiryBoardPassPresenter.getInstance(null);
        CIFindMyBookingPresenter.getInstance(null);
        //CIInquiryCheckInPresenter.getInstance(null);
        m_InquiryCheckInPresenter.setCallBack(null);
    }
}
