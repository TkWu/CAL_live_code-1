package ci.function.HomePage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.Core.SLog;
import ci.function.HomePage.item.CIHomeBgItem;
import ci.ui.TimeTable.CITimeTableTrack;
import ci.ui.WebView.CIPromotionCard;
import ci.ui.WebView.CIWebViewFragment.onWebViewFragmentParameter;
import ci.ui.define.HomePage_Status;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.object.item.CIHomeStatusEntity;
import ci.ui.view.ImageHandle;

import static ci.function.HomePage.CIFlightInfoFragment.OnFlightInfoListener;
import static ci.function.HomePage.CIMainNoTicketFragment.OnMainNoTicketFragmentListener;
import static ci.ui.WebView.CIWebViewFragment.onWebViewFragmentListener;

/**
 * 首頁
 */
public class CIHomeFragment extends BaseFragment {

    public interface OnHomeFragmentInterface {
        //回到頂部
        void scrollToTop();
    }

    public interface OnHomeFragmentListener {
        //訂票 快捷鍵
        void OnBookTicketClick();

        //航班狀態 快捷鍵
        void OnFightStatusClick();

        //時刻表 快捷鍵
        void OnTimeTableClick();

        //額外服務 快捷鍵
        void OnExtraServicesClick();

        //行程管理 快捷鍵
        void OnMyTripsClick();
        void OnMyTripsNoBoardClick();

        //前往里程管理頁面
        void OnMilesManageClick();
    }

    private OnHomeFragmentListener m_Listener;

    private OnHomeFragmentInterface m_Interface = new OnHomeFragmentInterface() {
        @Override
        public void scrollToTop() {
            if( m_scroollview != null ) {
                m_FragmentHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        m_scroollview.fullScroll(View.FOCUS_UP);
                    }
                });
            }
        }
    };

    private OnFlightInfoListener m_OnFlightInfoListener = new OnFlightInfoListener() {
        @Override
        public void OnMyTripsClick() {

            if (null == m_Listener)
                return;

            m_Listener.OnMyTripsClick();
        }
    };

    private OnMainNoTicketFragmentListener m_OnMainNoTicketFragmentListener = new OnMainNoTicketFragmentListener() {
        @Override
        public void OnMyTripsClick() {

            if (null == m_Listener)
                return;

            m_Listener.OnMyTripsClick();
        }

        @Override
        public void OnMyTripsNoBoardClick() {

            if (null == m_Listener)
                return;

            m_Listener.OnMyTripsNoBoardClick();
        }
    };

//    //2016/04/07 從通知跳轉至里程管理頁面時 需顯示切換動畫 此工作統一交由main處理 - by Ling
//    private OnMilesUpdateNoticeFragmentListener m_OnMilesUpdateNoticeFragmentListener = new OnMilesUpdateNoticeFragmentListener() {
//        @Override
//        public void OnMilesManageClick() {
//
//            if (null == m_Listener)
//                return;
//
//            m_Listener.OnMilesManageClick();
//        }
//    };


    private enum eShortcutType{

        BookTicket,
        FlightStatus,
        TimeTable,
        ExtraService
    }

    public static class ShortcutBtn {

        eShortcutType   eType;
        RelativeLayout  rlayout_bg;
        ImageView       imgIcon;
        TextView        tvName;

    }

    private onWebViewFragmentParameter[] m_onWebViewFragmentParameter = new onWebViewFragmentParameter[4];

    private onWebViewFragmentListener m_onWebViewFragmentListener = new onWebViewFragmentListener() {
        @Override
        public void onPageFinished(WebView view, String url) {}

        @Override
        public void onProgressChanged(int newProgress) {}

        @Override
        public void onNoInternetConnection(String url) {}

        @Override
        public void onInternetConnection() {}
    };

    private static final int MESSAGE_SET_BLUR_IMAGE = 1;

    private ImageView       m_ivBlurrBg = null;
    private RelativeLayout  m_rlayout_Background = null;
    private Bitmap          m_bmBgBlur = null;

    private ScrollView m_scroollview = null;
    private FrameLayout m_flayout_Home_page = null;
//    private FrameLayout m_flayout_Promotion = null;
    private FrameLayout m_flWebView1 = null;
    private FrameLayout m_flWebView2 = null;
    private FrameLayout m_flWebView3 = null;
    private FrameLayout m_flWebView4 = null;
    //
    private FrameLayout m_flayout_FlightInfo = null;
    //    track顯示
    private FrameLayout m_track_content = null;
    private CITimeTableTrack track = null;

    //捷徑列
    private View m_vShortcutBar = null;
    private ArrayList<ShortcutBtn> m_arShortcutBtnList = null;

    private CIMainNoTicketFragment m_MainNoTicketfragment = null;
    private CIMainSelectionFragment m_MainSelectionFragment = null;
    private CIMainCheckInFragment m_MainCheckInFragment = null;
    private CIFlightInfoFragment m_flightInfoFragment = null;
//    private CIPromotionFragment m_PromotionFragment = null;

    private CIPromotionCard[] m_WebViewFragment = new CIPromotionCard[4];
    private ArrayList<String> m_alLink = new ArrayList<>();

    private int m_iHomePage_Status = HomePage_Status.TYPE_UNKNOW;

    //Y軸可滾動的範圍大小,
    private int m_iScrollHeight = 0;
    //首頁的背景圖
    private CIHomeBgItem m_HomeBg = null;
    //旋轉的動畫
    private SwipeRefreshLayout m_SwipeRefreshLayout;

    //PNR
    private CIHomeStatusEntity m_HomeStatusEntity = null;

    //當前fragment是否為隱藏
    private boolean m_bHide = true;
    private boolean m_bFirst = true;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_ivBlurrBg = (ImageView) view.findViewById(R.id.ivBlurrBg);
        m_rlayout_Background = (RelativeLayout) view.findViewById(R.id.rlayout_bg);
        //首頁背景圖
        m_HomeBg = CIHomePageBgManager.getInstance().getBackground();
        m_rlayout_Background.setBackgroundResource(m_HomeBg.getMidImageId());
        //更新元件
        m_SwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);
        m_SwipeRefreshLayout.setOnRefreshListener(m_onRefreshListener);
        m_SwipeRefreshLayout.setColorSchemeResources(R.color.french_blue);
        m_SwipeRefreshLayout.setEnabled(false);

        m_scroollview = (ScrollView) view.findViewById(R.id.bg_scrollview);
        //主頁狀態
        m_flayout_Home_page = (FrameLayout) view.findViewById(R.id.flayout_main_page);
        //航班資訊牌卡
        m_flayout_FlightInfo = (FrameLayout) view.findViewById(R.id.flayout_flightInfo);

        //Promotion 牌卡
//        m_flayout_Promotion = (FrameLayout) view.findViewById(R.id.flayout_promotion);
        m_flWebView1 = (FrameLayout) view.findViewById(R.id.flayout_webview1);
        m_flWebView2 = (FrameLayout) view.findViewById(R.id.flayout_webview2);
        m_flWebView3 = (FrameLayout) view.findViewById(R.id.flayout_webview3);
        m_flWebView4 = (FrameLayout) view.findViewById(R.id.flayout_webview4);

        // track顯示卡
        m_track_content = (FrameLayout) view.findViewById(R.id.track_card_content);
        track = new CITimeTableTrack(getActivity());
        m_track_content.addView(track);

        //捷徑列按區域 initial
        m_vShortcutBar = view.findViewById(R.id.vShortcutBar);
        initialShortcutButton(m_vShortcutBar);

        //2016/04/07 初始化 Blur Image
        m_ivBlurrBg.setAlpha((float) 0);

        m_scroollview.getViewTreeObserver().addOnScrollChangedListener(m_onScroll);

        initialWebViewLink();
        initialWebView();

        m_bFirst = false;
    }

    private void initialWebViewLink(){
        if ( null == m_alLink )
            m_alLink = new ArrayList<>();

        if ( 0 != m_alLink.size() ){
            m_alLink.clear();
        }
        m_alLink.add(getString(R.string.home_page_promotion_link_1));
        m_alLink.add(getString(R.string.home_page_promotion_link_2));
        m_alLink.add(getString(R.string.home_page_promotion_link_3));
        m_alLink.add(getString(R.string.home_page_promotion_link_4));
    }

    private void initialWebView(){
        for (int i = 0; i < m_WebViewFragment.length; i ++ ){
            final String strUrl = m_alLink.get(i);
            m_onWebViewFragmentParameter[i] = new onWebViewFragmentParameter() {
                @Override
                public String GetUrl() {
                    return strUrl;
                }

                @Override
                public byte[] GetPostBody() {
                    return null;
                }

                @Override
                public String GetWebData() {
                    return null;
                }

                @Override
                public boolean IsShouldOverrideUrlLoading() {
                    if (strUrl.equals(getString(R.string.home_page_promotion_link_3))){
                        return false;
                    }else {
                        return true;
                    }
                }

                @Override
                public boolean IsOpenApp() {
                    if (strUrl.equals(getString(R.string.home_page_promotion_link_3))){
                        return true;
                    }else {
                        return false;
                    }
                }
            };

            if (m_WebViewFragment[i] == null)
                m_WebViewFragment[i] = new CIPromotionCard();

            m_WebViewFragment[i].uiSetParameterListener(
                    m_onWebViewFragmentParameter[i],
                    m_onWebViewFragmentListener);
        }
    }

    private void initialShortcutButton( View vShortcutBar ){

        if ( null == vShortcutBar ){
            return;
        }

        m_arShortcutBtnList = new ArrayList<>();
        m_arShortcutBtnList.clear();
        //Book Ticket
        ShortcutBtn shortcutLeftBtn = new ShortcutBtn();
        shortcutLeftBtn.rlayout_bg  = (RelativeLayout) vShortcutBar.findViewById(R.id.rlayout_left_button);
        shortcutLeftBtn.imgIcon     = (ImageView) vShortcutBar.findViewById(R.id.img_bookticket);
        shortcutLeftBtn.tvName      = (TextView) vShortcutBar.findViewById(R.id.tv_bookticket);
        shortcutLeftBtn.eType       = eShortcutType.BookTicket;
        shortcutLeftBtn.imgIcon.setImageResource(R.drawable.ic_book_flight);
        shortcutLeftBtn.tvName.setText(R.string.home_page_button_book_ticket);
        shortcutLeftBtn.rlayout_bg.setOnClickListener(m_onClickListener);
        m_arShortcutBtnList.add(shortcutLeftBtn);

        //Flight Status
        ShortcutBtn shortcutCenterBtn = new ShortcutBtn();
        shortcutCenterBtn.rlayout_bg = (RelativeLayout) vShortcutBar.findViewById(R.id.rlayout_center_button);
        shortcutCenterBtn.imgIcon = (ImageView) vShortcutBar.findViewById(R.id.img_flightstatus);
        shortcutCenterBtn.tvName = (TextView) vShortcutBar.findViewById(R.id.tv_flight_status);
        shortcutCenterBtn.eType  = eShortcutType.FlightStatus;
        shortcutCenterBtn.imgIcon.setImageResource(R.drawable.ic_flight_status);
        shortcutCenterBtn.tvName.setText(R.string.home_page_button_flight_status);
        shortcutCenterBtn.rlayout_bg.setOnClickListener(m_onClickListener);
        m_arShortcutBtnList.add(shortcutCenterBtn);

        //Timetable or Extra Service
        ShortcutBtn shortcutRightBtn = new ShortcutBtn();
        shortcutRightBtn.rlayout_bg = (RelativeLayout) vShortcutBar.findViewById(R.id.rlayout_right_button);
        shortcutRightBtn.imgIcon = (ImageView) vShortcutBar.findViewById(R.id.img_timetable);
        shortcutRightBtn.tvName = (TextView) vShortcutBar.findViewById(R.id.tv_timetable);
        shortcutRightBtn.eType  = eShortcutType.TimeTable;
        shortcutRightBtn.imgIcon.setImageResource(R.drawable.ic_timetablet);
        shortcutRightBtn.tvName.setText(R.string.home_page_timetable);
        shortcutRightBtn.rlayout_bg.setOnClickListener(m_onClickListener);
        m_arShortcutBtnList.add(shortcutRightBtn);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        //捷徑列按區域 initial
        m_vShortcutBar.getLayoutParams().height = vScaleDef.getLayoutHeight(60);
        for (ShortcutBtn btn : m_arShortcutBtnList) {

            ((RelativeLayout.LayoutParams) btn.imgIcon.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(7);
            ((RelativeLayout.LayoutParams) btn.tvName.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(4);

            //由於icon 需要設定為正方形, 所以使用最小單位避免畫面變形
            btn.imgIcon.getLayoutParams().height = vScaleDef.getLayoutMinUnit(24);
            btn.imgIcon.getLayoutParams().width = vScaleDef.getLayoutMinUnit(24);

            vScaleDef.setTextSize(13, btn.tvName);
        }

        //廣告區 暫時先將每個廣告固定高度230
        vScaleDef.setMargins(m_flWebView1, 0, 10, 0, 0);
        m_flWebView1.getLayoutParams().height = vScaleDef.getLayoutWidth(230);
        vScaleDef.setMargins(m_flWebView2, 0, 10, 0, 0);
        m_flWebView2.getLayoutParams().height = vScaleDef.getLayoutWidth(230);
        vScaleDef.setMargins(m_flWebView3, 0, 10, 0, 0);
        m_flWebView3.getLayoutParams().height = vScaleDef.getLayoutWidth(230);
        vScaleDef.setMargins(m_flWebView4, 0, 10, 0, 0);
        m_flWebView4.getLayoutParams().height = vScaleDef.getLayoutWidth(230);

        View vDiv = view.findViewById(R.id.vDiv);
        vDiv.getLayoutParams().height = vScaleDef.getLayoutHeight(10);
        View vDiv2 = view.findViewById(R.id.vDiv2);
        vDiv2.getLayoutParams().height = vScaleDef.getLayoutHeight(10);
    }

    private void UpdateShortcutListByStatus(  int iHomePage_Statusntity ){

        if (iHomePage_Statusntity == HomePage_Status.TYPE_A_NO_TICKET) {

            m_arShortcutBtnList.get(2).eType    = eShortcutType.TimeTable;
            m_arShortcutBtnList.get(2).imgIcon.setImageResource(R.drawable.ic_timetablet);
            m_arShortcutBtnList.get(2).tvName.setText(R.string.timetable_title);

        } else {
            m_arShortcutBtnList.get(2).eType    = eShortcutType.ExtraService;
            m_arShortcutBtnList.get(2).imgIcon.setImageResource(R.drawable.ic_extra_services);
            m_arShortcutBtnList.get(2).tvName.setText(R.string.home_page_button_extra_services);
        }
    }

    private void UpdateShortcutListText(){

        for ( ShortcutBtn btn : m_arShortcutBtnList ){

            if ( eShortcutType.TimeTable == btn.eType ){
                btn.tvName.setText(R.string.timetable_title);
            } else if ( eShortcutType.FlightStatus == btn.eType ){
                btn.tvName.setText(R.string.home_page_button_flight_status);
            } else if ( eShortcutType.BookTicket == btn.eType ){
                btn.tvName.setText(R.string.home_page_button_book_ticket);
            } else if ( eShortcutType.ExtraService == btn.eType ){
                btn.tvName.setText(R.string.home_page_button_extra_services);
            }
        }
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        //m_iHomePage_Status = AppInfo.getInstance(getContext()).GetHoemPageStatus();
        //初始化, 設為-1 方便更新
        m_HomeStatusEntity = null;
        m_handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //讀取PNR資料//更新PNR
                RefreshHomePagePNRStatus();
            }
        },2000);
        //更新畫面
        HomePageStatusUpdate(fragmentManager, HomePage_Status.TYPE_A_NO_TICKET);
        UpdateShortcutListByStatus(m_iHomePage_Status);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (null == m_flightInfoFragment) {
            m_flightInfoFragment = new CIFlightInfoFragment();
        }
        m_flightInfoFragment.uiSetParameterListener(m_OnFlightInfoListener);
        m_flightInfoFragment.setHomePageStatus(m_HomeStatusEntity);
        fragmentTransaction.replace(m_flayout_FlightInfo.getId(), m_flightInfoFragment, m_flightInfoFragment.toString());

        fragmentTransaction.replace(m_flWebView1.getId(), m_WebViewFragment[0], m_WebViewFragment[0].toString());
        fragmentTransaction.replace(m_flWebView2.getId(), m_WebViewFragment[1], m_WebViewFragment[1].toString());
        fragmentTransaction.replace(m_flWebView3.getId(), m_WebViewFragment[2], m_WebViewFragment[2].toString());
        fragmentTransaction.replace(m_flWebView4.getId(), m_WebViewFragment[3], m_WebViewFragment[3].toString());
        fragmentTransaction.commitAllowingStateLoss();

        createBlurImage();
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    private void UpdateHomePageInfo( CIHomeStatusEntity StatusEntity, int iStatusCode ){
        UpdateShortcutListByStatus(iStatusCode);
        HomePageStatusUpdate(getChildFragmentManager(), iStatusCode);
        if (null == m_flightInfoFragment) {
            m_flightInfoFragment = new CIFlightInfoFragment();
        }
        m_flightInfoFragment.uiSetParameterListener(m_OnFlightInfoListener);
        m_flightInfoFragment.setHomePageStatus(StatusEntity);
    }

    public void HomePageStatusUpdate(FragmentManager fragmentManager, int iStatusCode ) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (iStatusCode) {
            case HomePage_Status.TYPE_A_NO_TICKET:
            case HomePage_Status.TYPE_B_HAVE_TICKET: {
                if (null == m_MainNoTicketfragment) {
                    m_MainNoTicketfragment = new CIMainNoTicketFragment();
                }
                m_MainNoTicketfragment.uiSetParameterListener(m_OnMainNoTicketFragmentListener);
                m_MainNoTicketfragment.setHomePageStatus(m_HomeStatusEntity);
                //避免上一次已經執行過Replace, 重複執行會出現錯誤
                if ( HomePage_Status.TYPE_A_NO_TICKET != m_iHomePage_Status &&
                        HomePage_Status.TYPE_B_HAVE_TICKET != m_iHomePage_Status ){
                    fragmentTransaction.replace(m_flayout_Home_page.getId(), m_MainNoTicketfragment);
                    //背景不顯示文字,使用一般的圖
                    //確認有異動再換圖
                    m_rlayout_Background.setBackgroundResource(m_HomeBg.getMidImageId());
                }
                m_flayout_FlightInfo.setVisibility(View.GONE);
            }
            break;
            case HomePage_Status.TYPE_C_SEAT_MEAL_14D_24H:
            case HomePage_Status.TYPE_C_SELECT_SEAT_180D_14D: {
                if (null == m_MainSelectionFragment) {
                    m_MainSelectionFragment = new CIMainSelectionFragment();
                }
                //m_MainSelectionFragment.uiSetParameterListener(m_OnMainSelectionFragmentParameter);
                m_MainSelectionFragment.setHomePageStatus(m_HomeStatusEntity);
                //避免上一次已經執行過Replace, 重複執行會出現錯誤
                if ( HomePage_Status.TYPE_C_SEAT_MEAL_14D_24H != m_iHomePage_Status &&
                        HomePage_Status.TYPE_C_SELECT_SEAT_180D_14D != m_iHomePage_Status ){
                    fragmentTransaction.replace(m_flayout_Home_page.getId(), m_MainSelectionFragment);
                    //確認有異動再換圖
                    //背景不顯示文字,使用一般的圖
                    m_rlayout_Background.setBackgroundResource(m_HomeBg.getMidImageId());
                }
                m_flayout_FlightInfo.setVisibility(View.VISIBLE);
            }
            break;
            case HomePage_Status.TYPE_D_ONLINE_CHECKIN:
            case HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH:
            case HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_AT_AIRPORT_1H5:
            case HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_BOARDING_PASS_NOT_AVAILABLE:  //新增一個 預辦登機完成, 但該場站不可以列印電子登機證
            case HomePage_Status.TYPE_E_DESK_CHECKIN:
            case HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5:
            case HomePage_Status.TYPE_F_IN_FLIGHT:
            case HomePage_Status.TYPE_G_TRANSITION:
            case HomePage_Status.TYPE_H_ARRIVAL: {
                if (null == m_MainCheckInFragment) {
                    m_MainCheckInFragment = new CIMainCheckInFragment();
                }
                m_MainCheckInFragment.setHomePageStatus(m_HomeStatusEntity);
                //避免上一次已經執行過Replace, 重複執行會出現錯誤
                if ( HomePage_Status.TYPE_D_ONLINE_CHECKIN != m_iHomePage_Status &&
                        HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH != m_iHomePage_Status &&
                        HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_AT_AIRPORT_1H5 != m_iHomePage_Status &&
                        HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_BOARDING_PASS_NOT_AVAILABLE != m_iHomePage_Status &&
                        HomePage_Status.TYPE_E_DESK_CHECKIN != m_iHomePage_Status &&
                        HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5 != m_iHomePage_Status &&
                        HomePage_Status.TYPE_F_IN_FLIGHT != m_iHomePage_Status &&
                        HomePage_Status.TYPE_G_TRANSITION != m_iHomePage_Status &&
                        HomePage_Status.TYPE_H_ARRIVAL != m_iHomePage_Status ) {
                    fragmentTransaction.replace(m_flayout_Home_page.getId(), m_MainCheckInFragment);
                    //背景有顯示文字, 所以須調整為使用下半部有Blur的圖
                    m_rlayout_Background.setBackgroundResource(m_HomeBg.getMidBlurImageId());
                }
                m_flayout_FlightInfo.setVisibility(View.VISIBLE);
            }
            break;
            default:
                break;
        }
        try{
            fragmentTransaction.commitAllowingStateLoss();
        }catch (Exception e){
            Crashlytics.logException(e);
        }


        m_iHomePage_Status = iStatusCode;
    }

    private void createBlurImage() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Bitmap bmBg = BitmapFactory.decodeResource(getResources(), m_HomeBg.getMidBlurImageId());

                //Bitmap bmBg = m_rlayout_Background.getDrawingCache();

                m_bmBgBlur = ImageHandle.BlurBuilder(getActivity(), bmBg, 15, 0.15f);

                m_handler.obtainMessage(MESSAGE_SET_BLUR_IMAGE).sendToTarget();
            }
        }).start();
    }

    protected Handler m_handler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == MESSAGE_SET_BLUR_IMAGE) {
                //Bitmap bmImg = (Bitmap) msg.obj;

                if ( null != m_bmBgBlur ) {
                    m_ivBlurrBg.setImageBitmap(m_bmBgBlur);
                   SLog.d("[CAL]", "[CAL] blurr image is set");
                    //m_scroollview.scrollTo(0, 0);
                }
            }
        }
    };

    ViewTreeObserver.OnScrollChangedListener m_onScroll = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {

            int iCnt = m_scroollview.getChildCount();
            if ( iCnt <= 0 || null == m_bmBgBlur ) {
                return;
            }

            //if ( m_iScrollHeight == 0 ){
            m_iScrollHeight = m_scroollview.getChildAt(0).getHeight() - m_scroollview.getHeight();
            //}

            //利用百分比決定 blurr 效果
            //減少分母, 提高Blur的效果
            float fAlpha = (m_scroollview.getScrollY() / (float) (m_iScrollHeight / 4));

            // Apply a ceil
            if (fAlpha > 1) {
                fAlpha = 1;
            }

            m_ivBlurrBg.setAlpha(fAlpha);

            //String strtag = String.format("[H= %d][getScrollY= %d][Alpha=%f]", m_iScrollHeight, m_scroollview.getScrollY(), fAlpha);
            //Log.d("[CAL]", "[CAL] " + strtag);

            //
            int scrollY = m_scroollview.getScrollY();
            if( scrollY <= 0 )
                m_SwipeRefreshLayout.setEnabled(true);
            else
                m_SwipeRefreshLayout.setEnabled(false);
        }
    };

    View.OnClickListener m_onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (null == m_Listener) {
                return;
            }

            switch (v.getId()) {
                case R.id.rlayout_left_button:
                    m_Listener.OnBookTicketClick();
                    break;
                case R.id.rlayout_center_button:
                    m_Listener.OnFightStatusClick();
                    break;
                case R.id.rlayout_right_button:
                    //String strName = m_arShortcutBtnList.get(2).tvName.getText().toString();
                    eShortcutType eType = m_arShortcutBtnList.get(2).eType;
                    if ( eShortcutType.TimeTable == eType ) {
                        m_Listener.OnTimeTableClick();
                    } else {
                        m_Listener.OnExtraServicesClick();
                    }
                    break;

            }
        }
    };

    SwipeRefreshLayout.OnRefreshListener m_onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            RefreshHomePagePNRStatus();
            refreshTimeTableTrack();
        }
    };

    public void RefreshHomePagePNRStatus(){
       SLog.d("[CAL]", "[HomePage][RefreshHomePagePNRStatus]");
        m_SwipeRefreshLayout.setRefreshing(true);
        CIPNRStatusManager.getInstance(m_HomeStatusListener).RefreshHomePageStatus();
    }

    public void cancelRefreshHomePagePNRStatus(){
       SLog.d("[CAL]", "[HomePage][cancelRefreshHomePagePNRStatus]");
        m_SwipeRefreshLayout.setRefreshing(false);
        CIPNRStatusManager.getInstance(m_HomeStatusListener).cancelRefresh();
    }

    public OnHomeFragmentInterface uiSetParameterListener(OnHomeFragmentListener onListener) {
        m_Listener = onListener;
        return m_Interface;
    }

    public void onHomePageNoTicket() {
       SLog.d("[CAL]", "[refreshHomePage][onHomePageNoTicket]");
        //給畫面Noticket的狀態
        m_HomeStatusEntity = null;
        UpdateHomePageInfo(m_HomeStatusEntity, HomePage_Status.TYPE_A_NO_TICKET);
    }

    CIPNRStatusManager.CIHomeStatusListener m_HomeStatusListener = new CIPNRStatusManager.CIHomeStatusListener() {

        @Override
        public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {
           SLog.d("[CAL]","[HomePage][onRefreshPNRSuccess][StatusCode= "+ HomeStatusEntity.iStatus_Code + "]");
            //
            m_HomeStatusEntity = HomeStatusEntity;
            UpdateHomePageInfo(m_HomeStatusEntity, m_HomeStatusEntity.iStatus_Code);
        }

        @Override
        public void onNonUpdatePNRInfo() {
           SLog.d("[CAL]","[HomePage][onNonPNRInfo]");
            //
            //m_HomeStatusEntity = null;
            //UpdateHomePageInfo(m_HomeStatusEntity, HomePage_Status.TYPE_C_SEAT_MEAL_14D_24H);
        }

        @Override
        public void onHomePageNoTicket() {
           SLog.d("[CAL]","[HomePage][onHomePageNoTicket]");
            //給畫面Noticket的狀態
            m_HomeStatusEntity = null;
            UpdateHomePageInfo(m_HomeStatusEntity, HomePage_Status.TYPE_A_NO_TICKET);
        }

        @Override
        public void onInquiryError(String rt_code, String rt_msg) {
           SLog.d("[CAL]","[HomePage][onInquiryError] " + rt_msg);
            //
            m_HomeStatusEntity = null;
            showDialog(getString(R.string.warning), rt_msg);
        }

        @Override
        public void onDismissRefresh() {
           SLog.d("[CAL]","[HomePage][onDismissRefresh]");
            m_SwipeRefreshLayout.setRefreshing(false);
        }

    };

    /**
     * onResume()被呼叫，且Fragment狀態為 show，則呼叫此介面
     */
    protected void onFragmentResume() {
        track.removeAllViews();
        track.setTrackData();

        //2016-08-22 ryan for TripDetail 變更行程後, 須更新首頁行程
        if ( CIPNRStatusManager.getInstance(m_HomeStatusListener).IsHomePageTripChange() ){
            RefreshHomePagePNRStatus();
        }


        if ( false == m_bHide ){
            for (int i = 0; i < m_WebViewFragment.length; i ++){
                m_WebViewFragment[i].loadUrl(m_alLink.get(i));
            }
        }
    }

    /**
     *  onPause()被呼叫時，呼叫此介面
     */
    protected void onFragmentPause() {
       SLog.d("[CAL]","[HomePage][onFragmentPause]");
        m_SwipeRefreshLayout.setRefreshing(false);

        if ( false == m_bHide ){
            for (int i = 0; i < m_WebViewFragment.length; i ++){
                m_WebViewFragment[i].stopLoading();
            }
        }
    }

    private void refreshTimeTableTrack(){
        if ( null != getActivity() ){
            track = new CITimeTableTrack(getActivity());
            m_track_content.removeAllViews();
            m_track_content.addView(track);
        }
    }

    /**
     *  Fragment 狀態由 hide 轉 show 時，
     *  呼叫此介面
     */
    public void onFragmentShow() {
        m_bHide = false;

        if( m_scroollview != null ) {
            m_scroollview.fullScroll(View.FOCUS_UP);
        }

        refreshTimeTableTrack();

        //2016-08-10 ryan for TripDetail 變更行程後, 須更新首頁行程
        if ( CIPNRStatusManager.getInstance(m_HomeStatusListener).IsHomePageTripChange() ){
            RefreshHomePagePNRStatus();
        }

        //
        UpdateShortcutListText();

        //語系若變更, 網址需重撈
        if ( false == m_bFirst ){
            initialWebViewLink();
            initialWebView();

            for (int i = 0; i < m_WebViewFragment.length; i ++){
                m_WebViewFragment[i].loadUrl(m_alLink.get(i));
            }
        }
    }

    /**
     *  Fragment 狀態由 show 轉 hide 時，
     *  呼叫此介面
     */
    public void onFragmentHide() {
        m_bHide = true;

        track.removeAllViews();
        m_SwipeRefreshLayout.setRefreshing(false);
         /* 畫面轉換到其他的Fragment時，要停止刷新首頁狀態*/
        cancelRefreshHomePagePNRStatus();
       SLog.d("[CAL]", "[HomePage][onFragmentHide]");

        if ( false == m_bFirst ){
            for (int i = 0; i < m_WebViewFragment.length; i ++){
                m_WebViewFragment[i].stopLoading();
            }
        }
    }
}
