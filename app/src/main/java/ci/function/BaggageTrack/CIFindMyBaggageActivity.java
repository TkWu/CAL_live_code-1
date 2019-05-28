package ci.function.BaggageTrack;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.function.Main.BaseActivity;
import ci.function.Signup.CITermsAndConditionsActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.toast.CIToastView;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ui.view.TwoItemNavigationBar;
import ci.ws.cores.object.GsonTool;

import static ci.ui.view.NavigationBar.onNavigationbarInterface;
import static ci.ui.view.NavigationBar.onNavigationbarListener;
import static ci.ui.view.NavigationBar.onNavigationbarParameter;
import static ci.ui.view.TwoItemNavigationBar.EInitItem.LEFT;

;

public class CIFindMyBaggageActivity extends BaseActivity
        implements TwoItemNavigationBar.ItemClickListener {

    private onNavigationbarParameter m_onNavigationParameter = new onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.menu_title_baggage_tracking);
        }
    };
    //644336 20190322 行李追蹤注意事項更改顯示方式
//    private onNavigationbarListener m_onNavigationbarListener = new onNavigationbarListener() {
//
//        @Override
//        public void onRightMenuClick() {}
//
//        @Override
//        public void onLeftMenuClick() {}
//
//        @Override
//        public void onBackClick() {
//            CIFindMyBaggageActivity.this.finish();
//        }
//
//        @Override
//        public void onDeleteClick() {}
//
//        @Override
//        public void onDemoModeClick() {}
//    };

    private NavigationBar.AboutBtn m_onNavigationbarListener = new NavigationBar.AboutBtn() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            CIFindMyBaggageActivity.this.finish();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}

        @Override
        public void onAboutClick() {
            changeActivityToNote();
        }
    };
    //644336 20190322 行李追蹤注意事項更改顯示方式

    View.OnTouchListener m_onBackgroundTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            HidekeyBoard();
            return false;
        }
    };

    private CIFindMyBaggageFragment             m_findMyBaggageFragment = null;
    private CIFindMyBookingForBaggageFragment   m_findMyBookingBaggageFragment = null;

    private NavigationBar   m_Navigationbar = null;
    private FrameLayout     m_flContent = null;

    private Bitmap m_bitmap = null;
    private RelativeLayout m_rlBg = null;

    private onNavigationbarInterface m_onNavigationbarInterface = null;

    public final static int  PERMISSIONS_REQUEST_CODE = 1;

    //644336 20190322 行李追蹤注意事項更改顯示方式
    /**注意事項*/
    private CITermsAndConditionsActivity.ContentList   m_TermsAndConditionsContentList = null;
    //644336 20190322 行李追蹤注意事項更改顯示方式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initialLayoutComponent() {

        m_rlBg = (RelativeLayout) findViewById(R.id.relativelayout);
        m_bitmap = ImageHandle.getLocalBitmap(m_Context, R.drawable.bg_login, 1);
        Drawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        m_rlBg.setBackground(drawable);
        m_rlBg.setOnTouchListener(m_onBackgroundTouchListener);

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_flContent = (FrameLayout) findViewById(R.id.container);

        //644336 20190322 行李追蹤注意事項更改顯示方式
        m_TermsAndConditionsContentList = new CITermsAndConditionsActivity.ContentList();
        CITermsAndConditionsActivity.Content data1 = new CITermsAndConditionsActivity.Content();
        data1.itemTitle = getString(R.string.find_my_booking_baggage_notes);
        data1.itemContent = getString(R.string.find_my_booking_baggage_notes_content);
        m_TermsAndConditionsContentList.add(data1);
        //
        CITermsAndConditionsActivity.Content data2 = new CITermsAndConditionsActivity.Content();
        data2.itemTitle = getString(R.string.find_my_booking_baggage_notes_2);
        data2.itemContent = getString(R.string.find_my_booking_baggage_notes_content_2);
        m_TermsAndConditionsContentList.add(data2);

        //644336 20190322 行李追蹤注意事項更改顯示方式
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        vScaleDef.setMargins(findViewById(R.id.container), 0, 66, 0, 0);
        vScaleDef.setMargins(findViewById(R.id.two_item_navigation_bar), 0, 20, 0, 0);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_onNavigationbarInterface = m_Navigationbar.uiSetParameterListener(
                m_onNavigationParameter,
                m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        m_findMyBaggageFragment         = CIFindMyBaggageFragment.newInstance();
        m_findMyBookingBaggageFragment  = CIFindMyBookingForBaggageFragment.newInstance();

        // 開始Fragment的事務Transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // 設置轉換效果
        //fragmentTransaction.setCustomAnimations(R.anim.anim_right_in, R.anim.anim_left_out);

        String strLeftText = getString(R.string.find_my_baggage_twoitem_left_text);
        String strRightText = getString(R.string.find_my_baggage_twoitem_right_text);

        TwoItemNavigationBar bar = TwoItemNavigationBar.newInstance(
                strLeftText,
                strRightText, LEFT);
        bar.setListener(this);
        replaceFragment(fragmentTransaction, R.id.two_item_navigation_bar, bar);
        replaceFragment(fragmentTransaction, m_flContent.getId(), m_findMyBookingBaggageFragment);
        // 提交事務
        fragmentTransaction.commitAllowingStateLoss();

        //644336 20190322 行李追蹤注意事項更改顯示方式
        m_onNavigationbarInterface.showAboutButton();
        //644336 20190322 行李追蹤注意事項更改顯示方式
    }

    private void replaceFragment(FragmentTransaction transaction,
                                 int container,
                                 BaseFragment fragment) {
        transaction.replace(container,
                fragment,
                fragment.getTag());
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    protected void onLanguageChangeUpdateUI() {}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (null != m_rlBg) {
            m_rlBg.setBackground(null);
        }
        if (null != m_bitmap) {
            ImageHandle.recycleBitmap(m_bitmap);
        }
        System.gc();
        super.onDestroy();
    }


    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    public void onItemClick(View v) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.rl_left_bg:
                replaceFragment(fragmentTransaction, R.id.container, m_findMyBookingBaggageFragment);
                break;
            case R.id.rl_right_bg:
                replaceFragment(fragmentTransaction, R.id.container, m_findMyBaggageFragment);
                break;
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 當執行ActivityCompat.requestPermissions()後，會callbackonRequestPermissionsResult()
     * @param requestCode   requestPermissions()設定的requestCode
     * @param permissions   permission權限
     * @param grantResults  要求權限結果，如果等於[PackageManager.PERMISSION_GRANTED]就是同意
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    CIToastView.makeText(this,getString(R.string.bagtag_camera_permissions_msg)).show();
                }
                return;
            }

        }
    }

    //644336 20190322 行李追蹤注意事項更改顯示方式
    /**
     * 轉換Activity
     * @param clazz 目標activity名稱
     */
    private void changeActivity(Class clazz, Intent data ){

        Intent intent;
        if ( null == data ){
            intent = new Intent();
        } else {
            intent = data;
        }
        intent.setClass(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        //測試切換activity滑入滑出動畫
        this.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private void changeActivityToNote(){
        Intent data = new Intent();
        String jsData = GsonTool.toJson(m_TermsAndConditionsContentList);
        data.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA_TITLE,getString(R.string.menu_title_baggage_tracking));
        data.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA,jsData);
        changeActivity(CITermsAndConditionsActivity.class, data);
    }
    //644336 20190322 行李追蹤注意事項更改顯示方式
}
