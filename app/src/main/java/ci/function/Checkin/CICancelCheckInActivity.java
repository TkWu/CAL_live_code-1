package ci.function.Checkin;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Login.CIForgetSuccessFragment;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CICancelCheckInReq;
import ci.ws.Models.entities.CICancelCheckInReq_ItineraryInfo;
import ci.ws.Models.entities.CICancelCheckInReq_PaxInfo;
import ci.ws.Models.entities.CICancelCheckInResp;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Models.entities.CIPassengerListResp;
import ci.ws.Presenter.CICancelCheckInPresenter;
import ci.ws.Presenter.Listener.CICancelCheckInListener;

import static ci.function.Checkin.CICancelCheckInFragment.onCancelCheckInListener;
import static ci.function.Checkin.CICancelCheckInFragment.onCancelCheckInParameter;
import static ci.function.Login.CIForgetSuccessFragment.onForgetSuccessParameter;
import static ci.ui.view.NavigationBar.onNavigationbarInterface;
import static ci.ui.view.NavigationBar.onNavigationbarListener;
import static ci.ui.view.NavigationBar.onNavigationbarParameter;

/** 取消報到
 * zeplin: 11.8-2 / 3.5-2
 * wireframe: p.82
 * Created by jlchen on 2016/3/31.
 */
public class CICancelCheckInActivity extends BaseActivity {

    private onNavigationbarInterface m_onNavigationbarInterface = null;

    private onNavigationbarParameter m_onNavigationParameter = new onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_strTitle;
        }
    };

    private onNavigationbarListener m_onNavigationbarListener = new onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    private onCancelCheckInParameter m_onCancelCheckInParameter = new onCancelCheckInParameter() {
        @Override
        public CICancelCheckInReq getCancelCheckInData() {
            return m_CancelCheckInReq;
        }
    };

    private onCancelCheckInListener m_onCancelCheckInListener = new onCancelCheckInListener() {
        @Override
        public void onDoneClick(ArrayList<Integer> alSelectedPosition) {
            CICancelCheckInReq req = new CICancelCheckInReq();

            for (int i = 0; i < alSelectedPosition.size(); i ++ ){
                req.Pax_Info.add(m_CancelCheckInReq.Pax_Info.get(alSelectedPosition.get(i)));
            }

            CICancelCheckInPresenter.getInstance(m_CancelCheckInWSListener).CancelCheckInFromWS(req);
        }
    };

    private onForgetSuccessParameter m_onForgetSuccessParameter = new onForgetSuccessParameter() {

        @Override
        public String GetForgetSuccessMsg() {
            return getString(R.string.CancelCheckIn_SuccessMsg);
        }

        @Override
        public String GetButtonText() {
            return getString(R.string.done);
        }
    };

    CICancelCheckInListener m_CancelCheckInWSListener = new CICancelCheckInListener() {
        @Override
        public void onSuccess(String rt_code, String rt_msg, CICancelCheckInResp resp) {

            //取消行程後要通知主頁更新
            CIPNRStatusManager.getInstance(null).setHomePageTripIsChange(true);
            ChangeFragment(m_ForgetSuccessFragment);
            m_onNavigationbarInterface.hideBackButton();
        }

        @Override
        public void onError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning), rt_msg, getString(R.string.confirm));
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

    private CIForgetSuccessFragment m_ForgetSuccessFragment = null;

    private Bitmap                  m_bitmap                = null;
    private RelativeLayout          m_rlBg                  = null;
    private NavigationBar           m_Navigationbar         = null;
    private FrameLayout             m_flContent             = null;

    private String                  m_strTitle              = null;
    private CICancelCheckInReq      m_CancelCheckInReq      = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initialLayoutComponent() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            //行程資料
            CITripListResp_Itinerary tripData = (CITripListResp_Itinerary) bundle.getSerializable(
                    UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);

            //title文字為 出發地 -> 目的地
            m_strTitle = String.format(
                    getString(R.string.cancel_check_in_flight),
                    tripData.Departure_Station,
                    tripData.Arrival_Station);

            //乘客資料
            CIPassengerListResp passengerData = (CIPassengerListResp)
                    bundle.getSerializable(UiMessageDef.BUNDLE_PASSENGER_INFO);

            //CPR資料
            ArrayList<CICheckInPax_InfoEntity> cpr = (ArrayList<CICheckInPax_InfoEntity>)
                    bundle.getSerializable(UiMessageDef.BUNDLE_CANCEL_CHECK_IN_DATA);

            m_CancelCheckInReq = new CICancelCheckInReq();

            for( int i = 0; i < cpr.size(); i ++ ){
                //判斷此乘客是否可以取消check-in
                boolean bPaxCanCancelCheckIn = false;

                CICancelCheckInReq_PaxInfo paxInfo = new CICancelCheckInReq_PaxInfo();
                paxInfo.Pnr_Id      = cpr.get(i).Pnr_Id;
                paxInfo.First_Name  = cpr.get(i).First_Name;
                paxInfo.Last_Name   = cpr.get(i).Last_Name;
                paxInfo.Uci         = cpr.get(i).Uci;

                for ( int j = 0; j < cpr.get(i).m_Itinerary_InfoList.size(); j ++ ){

                    //航段Segment_Number與trip一致, 且已進行過check-in
                    if ( tripData.Segment_Number.equals(cpr.get(i).m_Itinerary_InfoList.get(j).Segment_Number)
                            && true == cpr.get(i).m_Itinerary_InfoList.get(j).Is_Check_In ){

                        //2016-08-10 取消Check-in 須額外判斷CPR的Flag 來決定該行段該乘客是否可以取消
                        if ( cpr.get(i).m_Itinerary_InfoList.get(j).Is_Do_Cancel_Check_In ){
                            bPaxCanCancelCheckIn = true;
                        } else {
                            bPaxCanCancelCheckIn = false;
                        }

                        CICancelCheckInReq_ItineraryInfo itineraryInfo = new CICancelCheckInReq_ItineraryInfo();
                        itineraryInfo.Did               = cpr.get(i).m_Itinerary_InfoList.get(j).Did;
                        itineraryInfo.Departure_Station = cpr.get(i).m_Itinerary_InfoList.get(j).Departure_Station;
                        itineraryInfo.Arrival_Station   = cpr.get(i).m_Itinerary_InfoList.get(j).Arrival_Station;

                        paxInfo.Itinerary_Info.add(itineraryInfo);
                    }
                }

                if ( true == bPaxCanCancelCheckIn ){
                    m_CancelCheckInReq.Pax_Info.add(paxInfo);
                }
            }
        }

        m_rlBg              = (RelativeLayout) findViewById(R.id.rl_bg);
        m_bitmap            = ImageHandle.getLocalBitmap(m_Context, R.drawable.bg_login, 1);
        Drawable drawable   = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        m_rlBg.setBackground(drawable);

        m_Navigationbar		= (NavigationBar) findViewById(R.id.toolbar);
        m_flContent         = (FrameLayout) findViewById(R.id.container);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {}

    @Override
    protected void setOnParameterAndListener() {
        m_onNavigationbarInterface = m_Navigationbar.uiSetParameterListener(
                m_onNavigationParameter, m_onNavigationbarListener);
        m_ForgetSuccessFragment = new CIForgetSuccessFragment();
        m_ForgetSuccessFragment.uiSetParameterListener(m_onForgetSuccessParameter);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        CICancelCheckInFragment cancelCheckInFragment = new CICancelCheckInFragment();
        cancelCheckInFragment.uiSetParameterListener(m_onCancelCheckInParameter, m_onCancelCheckInListener);
        ChangeFragment(cancelCheckInFragment);
    }

    /** 顯示送出結果畫面*/
    protected void ChangeFragment(Fragment fragment) {

        // 開始Fragment的事務Transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (fragment == m_ForgetSuccessFragment){
            // 設置轉換效果
            fragmentTransaction.setCustomAnimations(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        // 替换容器(container)原来的Fragment
        fragmentTransaction.replace(m_flContent.getId(),
                fragment,
                fragment.getTag());

        // 提交事務
        fragmentTransaction.commitAllowingStateLoss();

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
    public void onPause() {
        super.onPause();

        CICancelCheckInPresenter.getInstance(m_CancelCheckInWSListener).interrupt();
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
}
