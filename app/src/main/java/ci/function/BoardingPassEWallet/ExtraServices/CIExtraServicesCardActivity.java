package ci.function.BoardingPassEWallet.ExtraServices;

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
import ci.function.BoardingPassEWallet.Adapter.CIExtraServiceRecyclerViewAdapter;
import ci.function.BoardingPassEWallet.item.CIExtraServiceItem;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIEWallet_ExtraService_Info;

/**
 * 其他服務明細卡
 * zeplin: 14.2-2 / 14.2-4 / 14.2-5
 * wireframe: p.91
 * Created by jlchen on 2016/3/24.
 */
public class CIExtraServicesCardActivity extends BaseNoToolbarActivity {

    private CIExtraServiceRecyclerViewAdapter.EServiceType m_type;

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
    /** 是否已使用 */
    private boolean         m_bIsUsed       = false;

    private ImageButton     m_ibtnClose     = null;

    private CIEWallet_ExtraService_Info m_ExtraServiceData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String mode = getIntent().getStringExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE);
        if (null != mode) {
            m_type = CIExtraServiceRecyclerViewAdapter.EServiceType.valueOf(mode);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_boarding_pass_card;
    }

    @Override
    protected void initialLayoutComponent() {
        Bundle bundle   = getIntent().getExtras();
        m_bIsUsed       = bundle.getBoolean(CIExtraServiceItem.DEF_IS_EXPIRED_TAG);
        m_bitmap        = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
        m_ExtraServiceData = (CIEWallet_ExtraService_Info)
                bundle.getSerializable(UiMessageDef.BUNDLE_EWALLET_EXTRA_SERVICES_DATA);

        m_rlPageControl = (RelativeLayout)  findViewById(R.id.rlayout_pagecontrol);
        m_llDots        = (LinearLayout)    findViewById(R.id.llayout_dots);
        m_ibtnClose     = (ImageButton)     findViewById(R.id.ibtn_close);
        m_viewPager     = (ViewPager)       findViewById(R.id.viewPager);

        initialViewPager();

        RelativeLayout rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
        BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        rl_bg.setBackground(drawable);

        m_ibtnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CIExtraServicesCardActivity.this.finish();
            }
        });
    }

    //初始化ViewPager各頁的Fragment
    private void initialViewPager() {
        m_ListFragments.clear();

        //依照不同的服務項目, 會顯示不同的view, 需將type傳給fragment
        m_ListFragments.add(
                CIExtraServicesInfoCardFragment.newInstance(m_ExtraServiceData));
//        m_ListFragments.add(CIExtraServicesInfoCardFragment.newInstance(m_type,m_bIsUsed));
//        m_ListFragments.add(CIExtraServicesInfoCardFragment.newInstance(m_type,m_bIsUsed));

        m_adapter = new CICardViewPagerAdapter(
                getSupportFragmentManager(),
                m_ListFragments);

        m_viewPager.setAdapter(m_adapter);
        m_viewPager.addOnPageChangeListener(onPageViewChangeListener);

        //指定停留頁
        m_viewPager.setCurrentItem(m_iCurrentPage);

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

        LinearLayout ll_card = (LinearLayout) findViewById(R.id.ll_vp);
        vScaleDef.selfAdjustAllView(ll_card);

        RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) m_ibtnClose.getLayoutParams();
        rp.height = vScaleDef.getLayoutMinUnit(40);
        rp.width = vScaleDef.getLayoutMinUnit(40);
        rp.bottomMargin = vScaleDef.getLayoutMinUnit(16);

        rp = (RelativeLayout.LayoutParams) ll_card.getLayoutParams();
        rp.topMargin = vScaleDef.getLayoutHeight(0);

//        //依據type不同 要設定的card margin也不同
//        //三行115, 四行89.7, 四行帶qrcode 66.7, 五行64.4
//        rp = (RelativeLayout.LayoutParams) ll_card.getLayoutParams();
//        switch (m_type){
//            case ExtraBaggage:
//                rp.topMargin = vScaleDef.getLayoutHeight(89.7);
//                break;
//            case WIFI:
//                rp.topMargin = vScaleDef.getLayoutHeight(89.7);
//                break;
//            case VIP:
//                rp.topMargin = vScaleDef.getLayoutHeight(64.4);
//                break;
//            case THSR:
//                rp.topMargin = vScaleDef.getLayoutHeight(64.4);
//                break;
//            case FamilyCouch:
//                rp.topMargin = vScaleDef.getLayoutHeight(89.7);
//                break;
//        }

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
