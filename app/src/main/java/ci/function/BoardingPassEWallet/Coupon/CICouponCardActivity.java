package ci.function.BoardingPassEWallet.Coupon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIInquiryCoupon_Info;

import static ci.function.BoardingPassEWallet.Coupon.CICouponInfoCardFragment.CouponInfoCardParameter;

/**
 * 優惠券
 * zeplin: 14.3-2
 * wireframe: p.92
 * Created by jlchen on 2016/3/24.
 */
public class CICouponCardActivity extends BaseNoToolbarActivity {

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

    private CouponInfoCardParameter m_onCouponInfoCardParameter = new CouponInfoCardParameter() {
        @Override
        public CIInquiryCoupon_Info getCouponInfo() {
            return m_Coupon;
        }
    };

    private static CIInquiryCoupon_Info m_Coupon   = null;
    //背景
    private Bitmap          m_bitmap        = null;

    private RelativeLayout  m_rlPageControl = null;
    private LinearLayout    m_llDots        = null;

    /** ViewPager的頁面指示 */
    private ImageView[]     m_ivDots        = null;

    private ViewPager       m_viewPager     = null;
    private List<Fragment>  m_ListFragments = new ArrayList<>();
    private CICardViewPagerAdapter m_adapter= null;

    /** ViewPage頁數 */
    private int             m_iPageCount    = 0;
    /** 當下ViewPage */
    private int             m_iCurrentPage  = 0;

    private ImageButton     m_ibtnClose     = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_boarding_pass_card;
    }

    @Override
    protected void initialLayoutComponent() {

        m_rlPageControl = (RelativeLayout)  findViewById(R.id.rlayout_pagecontrol);
        m_llDots        = (LinearLayout)    findViewById(R.id.llayout_dots);
        //暫時不會用到多頁切換
        m_llDots.setVisibility(View.INVISIBLE);

        m_ibtnClose     = (ImageButton)     findViewById(R.id.ibtn_close);
        m_viewPager     = (ViewPager)       findViewById(R.id.viewPager);

        initialViewPager();

        byte[] bytes = getIntent().getByteArrayExtra(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
        m_bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        Bundle bundle   = getIntent().getExtras();
//        m_bitmap        = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
//        m_Coupon        = (CIInquiryCoupon_Info) bundle.getSerializable(
//                UiMessageDef.BUNDLE_EWALLET_COUPON_DATA);

        RelativeLayout rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
        BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        rl_bg.setBackground(drawable);

        m_ibtnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CICouponCardActivity.this.finish();
            }
        });
    }

    //初始化ViewPager各頁的Fragment
    private void initialViewPager() {
        m_ListFragments.clear();

        CICouponInfoCardFragment fragment = new CICouponInfoCardFragment();
        fragment.uiSetParameterListener(m_onCouponInfoCardParameter);
        m_ListFragments.add(fragment);
//        m_ListFragments.add(new CICouponInfoCardFragment());
//        m_ListFragments.add(new CICouponInfoCardFragment());

        m_adapter = new CICardViewPagerAdapter(
                getSupportFragmentManager(),
                m_ListFragments);

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

        vScaleDef.selfAdjustAllView(m_rlPageControl);

        RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) m_ibtnClose.getLayoutParams();
        rp.height = vScaleDef.getLayoutMinUnit(40);
        rp.width = vScaleDef.getLayoutMinUnit(40);
        rp.bottomMargin = vScaleDef.getLayoutMinUnit(16);

        LinearLayout ll_card = (LinearLayout) findViewById(R.id.ll_vp);
        vScaleDef.selfAdjustAllView(ll_card);
        rp = (RelativeLayout.LayoutParams) ll_card.getLayoutParams();
        rp.topMargin = vScaleDef.getLayoutHeight(0);

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

    public static void setCouponInfo(CIInquiryCoupon_Info info){
        m_Coupon = info;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ImageHandle.recycleBitmap(m_bitmap);
        m_Coupon = null;

        System.gc();
    }
}
