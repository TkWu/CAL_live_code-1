package ci.function.Checkin;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.List;

import ci.function.Base.BaseNoToolbarActivity;
import ci.function.BoardingPassEWallet.Adapter.CICardViewPagerAdapter;
import ci.function.BoardingPassEWallet.BoardingPass.CIBoardingPassInfoForegroundCardFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIBoardPassResp_Itinerary;
import ci.ws.Models.entities.CIBoardPassResp_PaxInfo;

/**
 * Created by joannyang on 16/6/21.
 */
@Deprecated
public class CICheckInCompleteCardActivity extends BaseNoToolbarActivity {
    /** Page切換的Listener */
    private ViewPager.OnPageChangeListener onPageViewChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {}

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}

        @Override
        public void onPageSelected(int arg0) {
            m_iCurrentPage = arg0;
            setCurrentDot(m_iCurrentPage);
        }
    };

    //背景
    private Bitmap m_bitmap        = null;

    private RelativeLayout m_rlPageControl = null;
    private LinearLayout m_llDots        = null;

    /** ViewPager的頁面指示 */
    private ImageView[]     m_ivDots        = null;

    private ViewPager       m_viewPager     = null;
    private List<Fragment> m_ListFragments = new ArrayList<>();
    private CICardViewPagerAdapter m_adapter= null;

    /** ViewPage頁數 */
    private int             m_iPageCount    = 0;
    /** 當下ViewPage */
    private int             m_iCurrentPage  = 0;
    /** 是否已使用 */
    private boolean         m_bIsUsed       = false;

    private ImageButton m_ibtnClose     = null;

    private ArrayList<CIBoardPassResp_Itinerary> m_arData = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_boarding_pass_card;
    }

    @Override
    protected void initialLayoutComponent() {
        Bundle bundle   = getIntent().getExtras();
        //是否已使用登機證的tag
        m_bIsUsed       = bundle.getBoolean(UiMessageDef.BUNDLE_BOARDING_PASS_IS_EXPIRED_TAG);
        //背景
        m_bitmap        = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
        //登機證資料
        m_arData          = (ArrayList<CIBoardPassResp_Itinerary>)bundle.getSerializable(
                UiMessageDef.BUNDLE_BOARDING_PASS_DATA);

        m_rlPageControl = (RelativeLayout)  findViewById(R.id.rlayout_pagecontrol);
        m_llDots        = (LinearLayout)    findViewById(R.id.llayout_dots);
        m_ibtnClose     = (ImageButton)     findViewById(R.id.ibtn_close);
        m_viewPager     = (ViewPager)       findViewById(R.id.viewPager);

        m_ibtnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CICheckInCompleteCardActivity.this.finish();
            }
        });

        RelativeLayout rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
        BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        rl_bg.setBackground(drawable);

        initialViewPager();
    }

    //初始化ViewPager各頁的Fragment
    private void initialViewPager() {
        m_ListFragments.clear();

        if ( null != m_arData ){
//            for ( int i = 0 ; i < m_Data.Pax_Info.size() ; i ++ ){
//                CIBoardPassResp_PaxInfo paxInfo = m_Data.Pax_Info.get(i);
//                m_ListFragments.add(CIBoardingPassInfoForegroundCardFragment.newInstance(m_Data, paxInfo, m_bIsUsed));
//            }

            for( int iPos = 0 ; iPos < m_arData.size() ; iPos++ ) {
                for ( int i = 0 ; i < m_arData.get(iPos).Pax_Info.size() ; i ++ ){
                    CIBoardPassResp_PaxInfo paxInfo = m_arData.get(iPos).Pax_Info.get(i);
                    m_ListFragments.add(CIBoardingPassInfoForegroundCardFragment.newInstance(m_arData.get(iPos), paxInfo, m_bIsUsed));
                }
            }
        }
//        else { //假資料
//            //CIBoardingPassInfoCardFragment傳入true表示有sky.
//            m_ListFragments.add(CIBoardingPassInfoForegroundCardFragment.newInstance(null, null, m_bIsUsed));
//            m_ListFragments.add(CIBoardingPassInfoForegroundCardFragment.newInstance(null, null, m_bIsUsed));
//            m_ListFragments.add(CIBoardingPassInfoForegroundCardFragment.newInstance(null, null, m_bIsUsed));
//        }

        m_adapter = new CICardViewPagerAdapter( getSupportFragmentManager(),
                m_ListFragments );

        m_viewPager.setAdapter(m_adapter);
        m_viewPager.addOnPageChangeListener(onPageViewChangeListener);

        //指定停留頁
        m_viewPager.setCurrentItem(m_iCurrentPage);

        //頁面總數
        m_iPageCount = m_adapter.getCount();
    }

    //初始化指示ViewPager頁數的view
    private void initDots() {
        m_llDots.removeAllViews();

        //有幾頁就初始化幾個imageView
        m_ivDots = new ImageView[m_iPageCount];

        for (int i = 0; i < m_iPageCount; i++) {
            m_ivDots[i] = new ImageView(m_Context);

            m_ivDots[i].setTag(i);
            m_ivDots[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_viewPager.setCurrentItem(Integer.valueOf(v.getTag().toString()));
                }
            });

            m_ivDots[i].setVisibility(View.VISIBLE);

            //表示當前頁面的imageView為白色, 其他都是半透明白
            if (i == m_iCurrentPage) {
                m_ivDots[i].setImageResource(R.drawable.shape_pagecontrol);
            } else {
                m_ivDots[i].setImageResource(R.drawable.shape_pagecontrol_unselect);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i == 0) {
                params.leftMargin = 0;
            } else {
                params.leftMargin = ViewScaleDef.getInstance(m_Context).getLayoutWidth(6);
            }
            m_llDots.addView(m_ivDots[i], params);
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

//        vScaleDef.selfAdjustAllView(m_rlPageControl);
        RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams)m_rlPageControl.getLayoutParams();
        rp.height = vScaleDef.getLayoutHeight(6);
        rp.topMargin = vScaleDef.getLayoutHeight(24);

        LinearLayout ll_card = (LinearLayout) findViewById(R.id.ll_vp);
//        vScaleDef.selfAdjustAllView(ll_card);
        rp = (RelativeLayout.LayoutParams) ll_card.getLayoutParams();
        rp.topMargin = vScaleDef.getLayoutHeight(40);

        rp = (RelativeLayout.LayoutParams) m_ibtnClose.getLayoutParams();
        rp.height = vScaleDef.getLayoutMinUnit(40);
        rp.width = vScaleDef.getLayoutMinUnit(40);
        rp.bottomMargin = vScaleDef.getLayoutMinUnit(16);

        //放allview自適應後面 pagecontrol自身的自適應才不會跑掉
        initDots();
    }

    //當換頁時,需重新設定當前頁imageView
    private void setCurrentDot(int position) {

        if (m_ivDots == null) {
            return;
        }

        int nLength = m_ivDots.length;
        for (int i = 0; i < nLength; i++) {
            if (position == i) {
                m_ivDots[i].setImageResource(R.drawable.shape_pagecontrol);
            } else {
                m_ivDots[i].setImageResource(R.drawable.shape_pagecontrol_unselect);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ImageHandle.recycleBitmap(m_bitmap);
        System.gc();
    }
}
