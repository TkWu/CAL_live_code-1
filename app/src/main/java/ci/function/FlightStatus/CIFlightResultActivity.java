package ci.function.FlightStatus;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.TimeTable.CITimeTableFlightStatusDetailList;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;
import ci.ui.view.TwoItemNavigationBar;

/**
 * 與TimeTable共用，使用m_iViewType檢查由哪個Activity過來並由returnOrNot判斷TimeTable中是否為來回
 * 使用intent 放置departDate選擇時間，class放置由何處來，return放置是否為TimeTable之單程或來回
 * Created by flowmahuang on 2016/3/15.
 */
public class CIFlightResultActivity extends BaseActivity implements TwoItemNavigationBar.ItemClickListener {

    /**
     * BUNDEL_PARA_TIMETABLE_FROM_CODE  timetable 出發機場代號
     * BUNDEL_PARA_TIMETABLE_TO_CODE   timetable  抵達機場代號
     * BUNDEL_PARA_FLIGHT_FROM_CODE    flight status 出發機場代號
     * BUNDEL_PARA_FLIGHT_TO_CODE  flight status 抵達機場代號
     */
    public static final String BUNDEL_PARA_TIMETABLE_FROM_CODE  = "Timetable_From";
    public static final String BUNDEL_PARA_TIMETABLE_TO_CODE    = "Timetable_to";
    public static final String BUNDEL_PARA_FLIGHT_FROM_CODE     = "Flight_from";
    public static final String BUNDEL_PARA_FLIGHT_TO_CODE       = "Flight_to";
    /*
       * Timetable 的資料
       * BUNDEL_PARA_TIMETABLE_DEPARTURE_DATA   來回
       * BUNDEL_PARA_TIMETABLE_RETURN_DATA 單程
        */
    public static final String BUNDEL_PARA_TIMETABLE_DEPARTURE_DATA = "departureData";
    public static final String BUNDEL_PARA_TIMETABLE_RETURN_DATA    = "returnData";

    //    出發時間
    public static final String BUNDEL_PARA_DEPARTDATE       = "departDate";
    //    抵達時間
    public static final String BUNDEL_PARA_RETURNDATE       = "returnDate";
    //    從哪個畫面過來
    public static final String BUNDLE_PARA_FROM_VIEW        = "class";
    //    有無return
    public static final String BUNDEL_PARA_RETURN_OR_NOT    = "m_breturnOrNot";
    //    以出發日還是抵達日搜尋
    public static final String BUNDEL_PARA_SEARCHWAY        = "searchWay";
    //    Flight staus 的資料
    public static final String BUNDEL_PARA_FLIGHT_DATA      = "data";

    private static final int VIEW_FROM_TIME_TABLE = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_TIME_TABLE;
    private static final int VIEW_FROM_FLIGHT_STATUS = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_FIGHT_STATUS;

    private static int m_iViewType;

    private NavigationBar m_Navigationbar;
    private CIFlightResultDepartureFragment m_DepartFragment = null;
    private CIFlightResultReturnFragment m_ReturnFragment = null;
    private FrameLayout m_flSelect = null;
    private FrameLayout m_flContent = null;
    private CISearchNoMatchingPopupindow m_Popupindow = null;
    private Intent  m_getIntent = null;
    private boolean m_breturnOrNot;
    private String  m_strDepartTime;
    private String  m_strReturnTime;

    //讓popupWindow 顯示一次
    private boolean checkPopupWindowShow = true;

    private boolean m_bFragment = true;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_flight_status_result;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_flSelect = (FrameLayout) findViewById(R.id.fl_select);
        m_flContent = (FrameLayout) findViewById(R.id.fl_content);
        m_DepartFragment = new CIFlightResultDepartureFragment();
        m_ReturnFragment = new CIFlightResultReturnFragment();

        m_getIntent = this.getIntent();

        m_strDepartTime = m_getIntent.getStringExtra(BUNDEL_PARA_DEPARTDATE);
        m_iViewType = m_getIntent.getIntExtra(BUNDLE_PARA_FROM_VIEW, 0);
        if (m_iViewType == VIEW_FROM_TIME_TABLE) {

            m_breturnOrNot = m_getIntent.getBooleanExtra(BUNDEL_PARA_RETURN_OR_NOT, false);
            if (m_breturnOrNot)
                m_strReturnTime = m_getIntent.getStringExtra(BUNDEL_PARA_RETURNDATE);
            else
                m_flSelect.setVisibility(View.GONE);
            m_DepartFragment.setFromClass(VIEW_FROM_TIME_TABLE);
            m_DepartFragment.setContentt(getString(R.string.depart_on) + " " + m_strDepartTime);
        } else {

            m_flSelect.setVisibility(View.GONE);
            m_DepartFragment.setFromClass(VIEW_FROM_FLIGHT_STATUS);
            if (m_getIntent.getBooleanExtra(BUNDEL_PARA_SEARCHWAY, false)) {
                m_DepartFragment.setContentt(getString(R.string.depart_on) + " " + m_strDepartTime);
            } else {
                m_DepartFragment.setContentt(getString(R.string.arrival_date) + " " + m_strDepartTime);
            }
        }

//        m_iViewType = m_getIntent.getIntExtra("class", 0);
        m_breturnOrNot = m_getIntent.getBooleanExtra(BUNDEL_PARA_RETURN_OR_NOT, false);
        if (m_breturnOrNot)
            m_strReturnTime = m_getIntent.getStringExtra(BUNDEL_PARA_RETURNDATE);
        else
            m_flSelect.setVisibility(View.GONE);
        ChangeFragment(m_DepartFragment);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            String title = "";
            if (m_iViewType == VIEW_FROM_TIME_TABLE)
                title = m_Context.getString(R.string.timetable_title);
            else if (m_iViewType == VIEW_FROM_FLIGHT_STATUS)
                title = m_Context.getString(R.string.flight_status);

            return title;
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
//        if (m_Popupindow.isShowing())
//            m_Popupindow.dismiss();
        CIFlightResultActivity.this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        m_bFragment = true;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        TwoItemNavigationBar bar = TwoItemNavigationBar
                .newInstance(
                        getString(R.string.Departure),
                        getString(R.string.Return));
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


    //TwoItem onItemClick
    @Override
    public void onItemClick(View v) {
        if (m_iViewType == VIEW_FROM_TIME_TABLE) {
            if (m_breturnOrNot) {
                switch (v.getId()) {
                    case R.id.rl_left_bg:
                        if (true == m_bFragment)
                            return;
                        m_bFragment = true;
                        ChangeFragment(m_DepartFragment);
                        m_DepartFragment.setContentt(getString(R.string.depart_on) + " " + m_strDepartTime);
                        break;
                    case R.id.rl_right_bg:
                        if (false == m_bFragment)
                            return;
                        m_bFragment = false;
                        ChangeFragment(m_ReturnFragment);
                        m_ReturnFragment.setContentt(getString(R.string.return_date) + " " + m_strReturnTime);
                        break;
                }
            }
        }
    }

    private void ChangeFragment(Fragment fragment) {
        String data = null;
        Bundle bundle = new Bundle();
        if (m_iViewType == VIEW_FROM_TIME_TABLE) {
            if (m_bFragment) {
                data = m_getIntent.getStringExtra(BUNDEL_PARA_TIMETABLE_DEPARTURE_DATA);
                bundle.putString(CIFlightResultDepartureFragment.BUNDLE_DATA, data);
                bundle.putString(CIFlightResultDepartureFragment.BUNDLE_FROM_CODE, m_getIntent.getStringExtra(BUNDEL_PARA_TIMETABLE_FROM_CODE));
                bundle.putString(CIFlightResultDepartureFragment.BUNDLE_TO_CODE, m_getIntent.getStringExtra(BUNDEL_PARA_TIMETABLE_TO_CODE));
                //若無資料，顯示popupWindow
                setPopupWindowSetting(data);

            } else {

                data = m_getIntent.getStringExtra(BUNDEL_PARA_TIMETABLE_RETURN_DATA);
                bundle.putString(CIFlightResultReturnFragment.BUNDLE_DATA, data);
                bundle.putString(CIFlightResultReturnFragment.BUNDLE_FROM, m_getIntent.getStringExtra(BUNDEL_PARA_TIMETABLE_FROM_CODE));
                bundle.putString(CIFlightResultReturnFragment.BUNDLE_TO, m_getIntent.getStringExtra(BUNDEL_PARA_TIMETABLE_TO_CODE));


            }
        } else if (m_iViewType == VIEW_FROM_FLIGHT_STATUS) {
            data = m_getIntent.getStringExtra(BUNDEL_PARA_FLIGHT_DATA);
            bundle.putString(CIFlightResultDepartureFragment.BUNDLE_DATA, data);
            bundle.putString(CIFlightResultDepartureFragment.BUNDLE_FROM_CODE, m_getIntent.getStringExtra(BUNDEL_PARA_FLIGHT_FROM_CODE));
            bundle.putString(CIFlightResultDepartureFragment.BUNDLE_TO_CODE, m_getIntent.getStringExtra(BUNDEL_PARA_FLIGHT_TO_CODE));
            //若無資料，顯示popupWindow
            setPopupWindowSetting(data);

        }


        fragment.setArguments(bundle);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fl_content, fragment, fragment.toString());

        fragmentTransaction.commitAllowingStateLoss();
    }

    public static int getWhatClass() {
        return m_iViewType;
    }

    private int getStatusBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
    //設定popupWindow, 傳入資料data

    public void setPopupWindowSetting(String data) {
        if (data.equals("[]") && checkPopupWindowShow) {
            checkPopupWindowShow = false;
            m_Popupindow = new CISearchNoMatchingPopupindow(m_Context,
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            new View(m_Context).post(new Runnable() {
                @Override
                public void run() {
                    m_Popupindow.showAtLocation(findViewById(R.id.put_pop), Gravity.NO_GRAVITY,
                            0, m_Navigationbar.getHeight() + getStatusBarHeight());
                }
            });
            new View(m_Context).postDelayed(new Runnable() {
                @Override
                public void run() {
                    m_Popupindow.dismiss();
                }
            }, 3000);
        } else {
            if (m_Popupindow != null) {
                m_Popupindow.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m_Popupindow != null) {
            m_Popupindow.dismiss();
        }
    }
}
