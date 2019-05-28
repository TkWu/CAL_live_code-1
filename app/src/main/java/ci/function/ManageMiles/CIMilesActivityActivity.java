package ci.function.ManageMiles;

import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.function.ManageMiles.TabFragment.CIAccumulationTabFragment;
import ci.function.ManageMiles.TabFragment.CIAwardTransferTabFragment;
import ci.function.ManageMiles.TabFragment.CIRedeemTabFragment;
import ci.function.MyTrips.Adapter.CIMyDetialFragmentAdapter;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIProgressDialog;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIExpiringMileageResp;
import ci.ws.Models.entities.CIInquiryAwardRecordResp;
import ci.ws.Models.entities.CIInquiryAwardRecordRespList;
import ci.ws.Models.entities.CIMileageRecordResp;
import ci.ws.Models.entities.CIMileageRecord_Info;
import ci.ws.Models.entities.CIMileageResp;
import ci.ws.Models.entities.CIRedeemRecordResp;
import ci.ws.Models.entities.CIRedeemRecord_Info;
import ci.ws.Presenter.CIInquiryMileagePresenter;
import ci.ws.Presenter.Listener.CIInquiryMileageListener;

/** 里程活動(里程明細)
 * Created by jlchen on 2016/3/9.
 */
public class CIMilesActivityActivity extends BaseActivity{

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.miles_activity);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() { }

        @Override
        public void onBackClick() {
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    CIInquiryMileageListener m_MileageListener = new CIInquiryMileageListener() {
        @Override
        public void onInquiryMileageSuccess(String rt_code, String rt_msg, CIMileageResp MileageResp) {}

        @Override
        public void onInquiryMileageError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryExpiringMileageSuccess(String rt_code, String rt_msg, CIExpiringMileageResp expiringMileageResp) {}

        @Override
        public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryMileageRecordSuccess(String rt_code, String rt_msg, CIMileageRecordResp mileageRecordResp) {
            m_alMileageRecordData = mileageRecordResp;

            m_AccumulationTabFragment.ResetDataList(m_alMileageRecordData);
        }

        @Override
        public void onInquiryMileageRecordError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void onInquiryRedeemRecordSuccess(String rt_code, String rt_msg, CIRedeemRecordResp redeemRecordResp) {
            m_alRedeemRecordData = redeemRecordResp;

            m_RedeemTabFragment.ResetDataList(m_alRedeemRecordData);
        }

        @Override
        public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void onInquiryAwardRecordSuccess(String rt_code, String rt_msg, CIInquiryAwardRecordRespList awardRecordResps) {
            m_alAwardRecordData = awardRecordResps;

            m_AwardTransferTabFragment.ResetDataList(m_alAwardRecordData);
        }

        @Override
        public void onInquiryAwardRecordError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void showProgress() {
            showProgressDialog(new CIProgressDialog.CIProgressDlgListener() {
                @Override
                public void onBackPressed() {
                    CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryMileageRecordCancel();
                    CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryRedeemRecordCancel();
                    CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryAwardRecordCancel();
                }
            });
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }

        @Override
        public void onAuthorizationFailedError(String rt_code, String rt_msg) {
            isProcessWSErrorCodeByOtherActivity(rt_code, rt_msg);
        }
    };

    private CIAccumulationTabFragment   m_AccumulationTabFragment;
    private CIRedeemTabFragment         m_RedeemTabFragment;
    private CIAwardTransferTabFragment  m_AwardTransferTabFragment;

    private SpannableString             m_spannableString           = null;
    private SpannableStringBuilder      m_spannableStringBuilder    = null;

    private NavigationBar               m_Navigationbar             = null;
    private FrameLayout                 m_flayout_Content           = null;

    private CoordinatorLayout           m_clBg                      = null;

    private TextView                    m_tvMiles                   = null;
    private TabLayout                   m_tabLayout                 = null;
    private ViewPager                   m_viewPager                 = null;
//
    private ArrayList<String>           m_strLists                  = null;
    private List<Fragment>              m_ListFragments             = null;
    private CIMyDetialFragmentAdapter   m_adapter                   = null;

    private String                      m_strMile                   = null;
    private int                         m_iTabIndex                 = 0;

    public ArrayList<CIMileageRecord_Info>  m_alMileageRecordData   = null;
    public ArrayList<CIRedeemRecord_Info>   m_alRedeemRecordData    = null;
    public ArrayList<CIInquiryAwardRecordResp> m_alAwardRecordData  = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initialLayoutComponent() {

        //顯示登入時取到的累積里程
        if ( 0 < CIApplication.getLoginInfo().GetMiles().length() ){
            m_strMile = CIApplication.getLoginInfo().GetMiles();
        }else {
            m_strMile = "0";
        }

        m_AccumulationTabFragment   = new CIAccumulationTabFragment();
        m_RedeemTabFragment         = new CIRedeemTabFragment();
        m_AwardTransferTabFragment  = new CIAwardTransferTabFragment();

        m_Navigationbar	            = (NavigationBar) findViewById(R.id.toolbar);
        m_flayout_Content 	        = (FrameLayout) findViewById(R.id.container);

        View content                = View.inflate(this, R.layout.fragment_miles_activity, null);
        m_flayout_Content.addView(content);

        m_clBg                      = (CoordinatorLayout) content.findViewById(R.id.cl_bg);

        m_tvMiles                   = (TextView) content.findViewById(R.id.tv_miles);
        m_tabLayout                 = (TabLayout) content.findViewById(R.id.tabLayout);
        m_viewPager                 = (ViewPager) content.findViewById(R.id.viewPager);

        m_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                m_iTabIndex = position;
                setTabTextSpan();
                getDataFromWs();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        initViewPager();
        initTabHost();
        selfAdjustForTabLayout(ViewScaleDef.getInstance(m_Context));

        m_viewPager.setCurrentItem(m_iTabIndex);
        setTabTextSpan();
    }

    private void initTabHost() {
        m_tabLayout.setupWithViewPager(m_viewPager);
        m_tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void initViewPager() {
        m_ListFragments = new ArrayList<>();
        m_ListFragments.add(m_AccumulationTabFragment);
        m_ListFragments.add(m_RedeemTabFragment);
        m_ListFragments.add(m_AwardTransferTabFragment);

        m_strLists = new ArrayList<>();
        m_strLists.add(getString(R.string.managemiles_tab_accumulation));
        m_strLists.add(getString(R.string.managemiles_tab_redeem));
        m_strLists.add(getString(R.string.managemiles_tab_award_transfer));

        m_adapter = new CIMyDetialFragmentAdapter(
                        getSupportFragmentManager(), m_ListFragments, m_strLists);
        m_viewPager.setAdapter(m_adapter);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        //自適應
        vScaleDef.selfAdjustAllView(m_flayout_Content);

        //里程數數字要變大
        if(null == m_spannableString){
            m_spannableString = new SpannableString(
                    String.format(getString(R.string.menu_miles), m_strMile));
            m_spannableStringBuilder = new SpannableStringBuilder(m_spannableString);
        }

        int index = m_spannableStringBuilder.toString().indexOf(" ", 0);

        m_spannableStringBuilder.setSpan(new AbsoluteSizeSpan(vScaleDef.getTextSize(40)),
                0,
                index,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        m_spannableStringBuilder.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                index,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        m_tvMiles.setText(m_spannableStringBuilder);
    }

    private void selfAdjustForTabLayout(ViewScaleDef vScaleDef) {

        ArrayList<View> viewLists = new ArrayList<>();

        for (int loop = 0; loop < m_strLists.size(); loop++) {
            m_tabLayout.findViewsWithText(
                    viewLists,
                    m_strLists.get(loop),
                    ViewGroup.FIND_VIEWS_WITH_TEXT);
        }

        try {
            Field field = TabLayout.class.getDeclaredField("mTabTextSize");
            Field field2 = TabLayout.class.getDeclaredField("mTabTextMultiLineSize");
            field.setAccessible(true);
            field2.setAccessible(true);
            field.setInt(m_tabLayout, vScaleDef.getTextSize(16));
            field2.setInt(m_tabLayout, vScaleDef.getTextSize(16));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    protected void onLanguageChangeUpdateUI() {}

    private void setTabTextSpan(){
        //將未被選取的tab恢復原本的字型
        for (int i = 0; i < m_tabLayout.getTabCount(); i++){
            if ( i != m_iTabIndex ){
                String str = m_tabLayout.getTabAt(i).getText().toString();
                SpannableString spannableString = new SpannableString(str);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableString);
                spannableStringBuilder.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(m_Context, R.color.white_70)),
                        0,
                        str.length(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                //
                TypefaceSpan typefaceSpan = new TypefaceSpan("sans-serif-light");
                spannableStringBuilder.setSpan(
                        typefaceSpan,
                        0,
                        str.length(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                m_tabLayout.getTabAt(i).setText(spannableStringBuilder);
            }
        }

        //將被選取的tab字體加粗
        String strSelect = m_tabLayout.getTabAt(m_iTabIndex).getText().toString();
        SpannableString spannableStringSelect = new SpannableString(strSelect);
        SpannableStringBuilder spannableStringBuilderSelect = new SpannableStringBuilder(spannableStringSelect);
        TypefaceSpan typefaceSpan = new TypefaceSpan("sans-serif");
        spannableStringBuilderSelect.setSpan(
                typefaceSpan,
                0,
                strSelect.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilderSelect.setSpan(
                new StyleSpan(Typeface.NORMAL),
                0,
                strSelect.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        m_tabLayout.getTabAt(m_iTabIndex).setText(spannableStringBuilderSelect);
    }

    private void getDataFromWs(){
        switch (m_iTabIndex){
            case 0:
                if ( null == m_alMileageRecordData ){
                    CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryMileageRecordFromWS();
                }
                break;
            case 1:
                if ( null == m_alRedeemRecordData ){
                    CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryRedeemRecordFromWS();
                }
                break;
            case 2:
                if ( null == m_alAwardRecordData ){
                    CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryAwardRecordFromWS();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        m_BaseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataFromWs();
            }
        },500);
    }

    @Override
    public void onPause() {
        super.onPause();

        CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryMileageRecordCancel();
        CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryRedeemRecordCancel();
        CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryAwardRecordCancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CIInquiryMileagePresenter.getInstance(null);

        if (null != m_clBg) {
            if ( m_clBg.getBackground() instanceof BitmapDrawable){
                m_clBg.setBackground(null);
            }
        }
        System.gc();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }
}
