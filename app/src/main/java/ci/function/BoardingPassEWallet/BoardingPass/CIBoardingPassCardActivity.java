package ci.function.BoardingPassEWallet.BoardingPass;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;
import com.jakewharton.rxbinding.view.RxView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ci.function.BaggageTrack.CIBaggageInfoContentActivity;
import ci.function.Base.BaseNoToolbarActivity;
import ci.function.BoardingPassEWallet.Adapter.CICardViewPagerAdapter;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIBaggageInfoContentResp;
import ci.ws.Models.entities.CIBaggageInfoResp;
import ci.ws.Models.entities.CIBoardPassResp_Itinerary;
import ci.ws.Models.entities.CIBoardPassResp_PaxInfo;
import ci.ws.Presenter.CIInquiryBaggageInfoPresenter;
import ci.ws.Presenter.Listener.CIBaggageInfoListener;
import rx.functions.Action1;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * 登機證
 * zeplin: 14.1-3 / 14.1-5
 * wireframe: p.90
 * Created by jlchen on 2016/3/24.
 */
public class CIBoardingPassCardActivity extends BaseNoToolbarActivity {

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

    //644336 2019 2月3月 截圖
    private ImageButton     m_ibtnScreenshot     = null;

    private ImageView       m_ivFlip        = null;

    private int             m_iCurrentPageType = 0;

    private CIBoardPassResp_Itinerary m_Data = null;

    private ArrayList<CIBoardPassResp_Itinerary> m_arData = null;

    private static final int FOREGROUND_PAGE = 0;
    private static final int BACKGROUND_PAGE = 1;

    private boolean          m_bIsFirstShow  = true;

    /**被點擊的行李編號
     * 顯示用*/
    private String m_Baggage_ShowNumber = "";

    //644336 2019 2月3月 截圖
    private static final int REQUEST_PERMISSION_PHOTO_WRITE = 8001;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_boarding_pass_card;
    }

    @Override
    protected void initialLayoutComponent() {
        Bundle bundle   = getIntent().getExtras();
        //是否已使用登機證的tag
        m_bIsUsed       = bundle.getBoolean(UiMessageDef.BUNDLE_BOARDING_PASS_IS_EXPIRED_TAG);
        //當前要顯示第幾頁
        m_iCurrentPage  = bundle.getInt(UiMessageDef.BUNDLE_BOARDING_PASS_INDEX);
        //背景
        m_bitmap        = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
        //登機證資料
        m_Data          = (CIBoardPassResp_Itinerary)bundle.getSerializable(
                UiMessageDef.BUNDLE_BOARDING_PASS_DATA);

        //登機證資料
        m_arData          = (ArrayList<CIBoardPassResp_Itinerary>)bundle.getSerializable(
                UiMessageDef.BUNDLE_BOARDING_PASS_DATAS);

        m_rlPageControl = (RelativeLayout)  findViewById(R.id.rlayout_pagecontrol);
        m_llDots        = (LinearLayout)    findViewById(R.id.llayout_dots);
        m_ibtnClose     = (ImageButton)     findViewById(R.id.ibtn_close);

        //644336 2019 2月3月 截圖
        m_ibtnScreenshot= (ImageButton)     findViewById(R.id.ibtn_screenshot);

        m_viewPager     = (ViewPager)       findViewById(R.id.viewPager);
        m_ivFlip        = (ImageView)       findViewById(R.id.iv_flip);

        m_ibtnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CIBoardingPassCardActivity.this.finish();
            }
        });

        RxView.clicks(m_ivFlip)
              .throttleFirst(1, TimeUnit.SECONDS)
              .subscribe(new Action1<Void>() {
                  @Override
                  public void call(Void aVoid) {
                      if(m_iCurrentPageType == FOREGROUND_PAGE) {
                          //如果目前在前景就指定背景去切換
                          m_iCurrentPageType = BACKGROUND_PAGE;
                      } else {
                          m_iCurrentPageType = FOREGROUND_PAGE;
                      }

                      displayViewPageAnim(R.animator.anim_flip_hide , new Animator.AnimatorListener() {
                          @Override
                          public void onAnimationStart(Animator animation) {}

                          @Override
                          public void onAnimationEnd(Animator animation) {
                              initialViewPager(m_iCurrentPageType);
                          }

                          @Override
                          public void onAnimationCancel(Animator animation) {}

                          @Override
                          public void onAnimationRepeat(Animator animation) {}
                      });
                  }
              });

        RelativeLayout rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
        BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        rl_bg.setBackground(drawable);

        initialViewPager(m_iCurrentPageType);

        //644336 2019 2月3月 AI/行事曆/截圖/注意事項
        m_ibtnScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //CIBoardingPassCardActivity.this.finish();
                if (checkPermissionForPhoto() == false) {
                    return;
                }
                Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
                new SavePicToFileTask().execute(bitmap);
            }
        });
    }

    private void displayViewPageAnim(int anim, Animator.AnimatorListener animatorListener){
        AnimatorSet inAnimatorSet = (AnimatorSet) AnimatorInflater
                .loadAnimator(CIBoardingPassCardActivity.this, anim);
        inAnimatorSet.setTarget(m_viewPager);

        if(false == m_bIsFirstShow) {
            inAnimatorSet.start();
        } else {
            m_bIsFirstShow = false;
        }

        if(null != animatorListener) {
            inAnimatorSet.addListener(animatorListener);
        }
    }

    //初始化ViewPager各頁的Fragment
    private void initialViewPager(int type) {
        m_ListFragments.clear();

        if ( null != m_arData ){

            for( int iPos = 0 ; iPos < m_arData.size() ; iPos++ ) {
                CIBoardPassResp_Itinerary itinerary = m_arData.get(iPos);
                for ( int i = 0 ; i < itinerary.Pax_Info.size() ; i ++ ){
                    CIBoardPassResp_PaxInfo paxInfo = itinerary.Pax_Info.get(i);
                    initContent(type, itinerary, paxInfo);
                }
            }
        }

        if ( null != m_Data ){
            for ( int i = 0 ; i < m_Data.Pax_Info.size() ; i ++ ){
                CIBoardPassResp_PaxInfo paxInfo = m_Data.Pax_Info.get(i);
                initContent(type, m_Data, paxInfo);
            }
        }

        m_adapter = new CICardViewPagerAdapter( getSupportFragmentManager(),
                m_ListFragments );

        m_viewPager.setAdapter(m_adapter);
        m_viewPager.addOnPageChangeListener(onPageViewChangeListener);

        //指定停留頁
        m_viewPager.setCurrentItem(m_iCurrentPage);

        //頁面總數
        m_iPageCount = m_adapter.getCount();

        displayViewPageAnim(R.animator.anim_flip_show, null);
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

    private void initContent(int type ,CIBoardPassResp_Itinerary itinerary,  CIBoardPassResp_PaxInfo paxInfo){
        if (type == FOREGROUND_PAGE) {
            m_ListFragments.add(CIBoardingPassInfoForegroundCardFragment.newInstance(itinerary, paxInfo, m_bIsUsed));
            //644336 2019 2月3月 截圖
            m_ibtnScreenshot.setVisibility(View.VISIBLE);
        } else if(type == BACKGROUND_PAGE) {
            m_ListFragments.add(CIBoardingPassInfoBackgroundCardFragment.newInstance(itinerary, paxInfo, m_onBgCardFragmentListener));
            //644336 2019 2月3月 截圖
            m_ibtnScreenshot.setVisibility(View.GONE);
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

        rp = (RelativeLayout.LayoutParams) m_ivFlip.getLayoutParams();
        vScaleDef.selfAdjustSameScaleView(m_ivFlip, 26.7, 26.7);
        rp.rightMargin = vScaleDef.getLayoutMinUnit(18);
        rp.topMargin = vScaleDef.getLayoutMinUnit(9.3);

        //644336 2019 2月3月 截圖
        rp = (RelativeLayout.LayoutParams) m_ibtnScreenshot.getLayoutParams();
        vScaleDef.selfAdjustSameScaleView(m_ibtnScreenshot, 26.7, 26.7);
        rp.rightMargin = vScaleDef.getLayoutMinUnit(10);
        rp.bottomMargin = vScaleDef.getLayoutMinUnit(23);

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

    CIBoardingPassInfoBackgroundCardFragment.OnBackgroundCardFragmentListener m_onBgCardFragmentListener = new CIBoardingPassInfoBackgroundCardFragment.OnBackgroundCardFragmentListener(){


        @Override
        public void onBaggageInfoClick(String strBaggageNumber, String strShowNum ) {
            m_Baggage_ShowNumber = strShowNum;
            String strDeparture_Station = "";
            String strDeparture_Date = "";
            if ( null == m_Data ){
                strDeparture_Station = m_arData.get(0).Departure_Station;
                strDeparture_Date = m_arData.get(0).Departure_Date;
            } else {
                strDeparture_Station = m_Data.Departure_Station;
                strDeparture_Date = m_Data.Departure_Date;
            }
            CIInquiryBaggageInfoPresenter.getInstance(m_BaggageInfoListener).InquiryBaggageInfoByBGNumFromWS( strBaggageNumber, strDeparture_Station, strDeparture_Date);
        }
    };

    CIBaggageInfoListener m_BaggageInfoListener = new CIBaggageInfoListener() {

        @Override
        public void onBaggageInfoByPNRAndBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoResp> arDataList) {}

        @Override
        public void onBaggageInfoByPNRAndBGNumError(String rt_code, String rt_msg) {}

        @Override
        public void onBaggageInfoByBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoContentResp> arDataList ) {

            Activity activity = (Activity) m_Context;
            Bitmap bitmap = ImageHandle.getScreenShot(activity);
            Bitmap blur   = ImageHandle.BlurBuilder(m_Context, bitmap, 13.5f, 0.15f);

            String strDeparture_Station = "";
            String strDeparture_Date = "";
            String strArrival_Station = "";
            if ( null == m_Data ){
                strDeparture_Station = m_arData.get(0).Departure_Station;
                strDeparture_Date = m_arData.get(0).Departure_Date;
                strArrival_Station = m_arData.get(0).Arrival_Station;
            } else {
                strDeparture_Station = m_Data.Departure_Station;
                strDeparture_Date = m_Data.Departure_Date;
                strArrival_Station = m_Data.Arrival_Station;
            }

            Bundle bundle = new Bundle();
            bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG,     blur);
            bundle.putSerializable(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_RESP,    arDataList);
            bundle.putString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_NUMBER, m_Baggage_ShowNumber);
            bundle.putString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_DEPARTUREDATE,     strDeparture_Date);
            bundle.putString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_DEPARTURESTATION,  strDeparture_Station);
            bundle.putString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_ARRIVALSTATION,    strArrival_Station);

            Intent intent = new Intent();
            intent.putExtras(bundle);

            intent.setClass(m_Context, CIBaggageInfoContentActivity.class);
            activity.startActivity(intent);

            activity.overridePendingTransition(R.anim.anim_alpha_in, 0);

            bitmap.recycle();

        }

        @Override
        public void onBaggageInfoByBGNumError(String rt_code, String rt_msg) {
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

    //644336 2019 2月3月 截圖
    private class SavePicToFileTask extends AsyncTask<Bitmap,Void,String> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            showProgressDialog();
            //showProgressDlg(getString(R.string.indicator_loading));
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            String fileName = "";

            try {
                fileName = print();
            } catch (Exception e) {
                e.printStackTrace();

                //CIToastView.makeText(CIBoardingPassCardActivity.this, "error").show();
            }

            return fileName;
        }

        @Override
        protected void onPostExecute(String result) {
            //closeProgressDlg();
            hideProgressDialog();

            if (TextUtils.isEmpty(result) == true) {
                showDialog("",
                        getString(R.string.braoding_pass_screenshots_fail),
                        getString(R.string.confirm));

                return;
            }
            showDialog(
                    "",
                    getString(R.string.braoding_pass_screenshots_success),
                    getString(R.string.confirm));
        }
    }

    private String print() {
        View fragmentView = m_adapter.getItem(m_iCurrentPage).getView();

        Bitmap bitmap = getBitmapFromView(m_adapter.getItem(m_iCurrentPage).getView(), m_adapter.getItem(m_iCurrentPage).getView().getHeight(), m_adapter.getItem(m_iCurrentPage).getView().getWidth());

        try {
            File fileDefault = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)+"/Screenshots");
            if (fileDefault.exists() == false) {
                fileDefault.mkdirs();
            }

            String strName = String.format(Locale.getDefault(),
                    getString(R.string.braoding_pass_screenshots_name),
                    ((TextView)fragmentView.findViewById(R.id.tv_boarding_date)).getText().toString().substring(0,10),
                    ((TextView)fragmentView.findViewById(R.id.tv_aircraft_no)).getText(),
                    ((TextView)fragmentView.findViewById(R.id.tv_name)).getText()
            );

            String strFileName = strName + ".jpg";

            File file = new File(fileDefault, strFileName);

            if (file.exists() == true) {
                file.delete();
                file = new File(fileDefault, strFileName);
            }

            FileOutputStream output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();

            Uri uri = Uri.fromFile(file);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();

            return "";
        }
    }

    //create bitmap from the view
    private Bitmap getBitmapFromView(View view, int iHeight, int iWidth) {
        Bitmap bitmap = Bitmap.createBitmap(iWidth, iHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable drawableBg = view.getBackground();

        if (drawableBg!=null) {
            drawableBg.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);

        return bitmap;
    }

    private boolean checkPermissionForPhoto() {
        boolean bPermissionWrite = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (bPermissionWrite == false) {
            requestPermission(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_PHOTO_WRITE);

            return false;
        }

        return true;
    }

    private boolean checkPermission(String strPermission) {
        if (ActivityCompat.checkSelfPermission(this, strPermission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }
    private void requestPermission(String[] strPermission, int iRequestCode) {
        ActivityCompat.requestPermissions(this, strPermission, iRequestCode);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_PHOTO_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
                new SavePicToFileTask().execute(bitmap);
            } else {
                showDialog(getString(R.string.warning),
                        getString(R.string.permissions_error),
                        getString(R.string.confirm));
            }
        }
    }
}
