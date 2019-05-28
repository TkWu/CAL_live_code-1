package ci.function.BaggageTrack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.BaggageTrack.Adapter.CIBaggageTrackingRecyclerViewAdapter;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIBaggageInfoContentResp;
import ci.ws.Models.entities.CIBaggageInfoResp;
import ci.ws.Presenter.CIInquiryBaggageInfoPresenter;
import ci.ws.Presenter.Listener.CIBaggageInfoListener;


public class CIBaggageTrackingListActivity extends BaseActivity  {

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.menu_title_baggage_tracking);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

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

    View.OnTouchListener m_onBackgroundTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            HidekeyBoard();
            return false;
        }
    };


    private NavigationBar.onNavigationbarInterface m_onNavigationbarInterface = null;

    private NavigationBar   m_Navigationbar     = null;
    private RecyclerView    m_recyclerView      = null;

    private CIBaggageTrackingRecyclerViewAdapter m_adapter = null;

    //點擊的牌卡資訊
    private String m_Departure_Date;
    private String m_Departure_Station;
    private String m_Arrival_Station;
    private String m_Baggage_ShowNumber;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_baggage_tracking_list;
    }

    @Override
    protected void initialLayoutComponent() {

        RelativeLayout rlayout_Bg = (RelativeLayout)findViewById(R.id.root);
        rlayout_Bg.setOnTouchListener(m_onBackgroundTouchListener);

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        m_recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        m_recyclerView.setLayoutManager(linearLayoutManager);

        Bundle bundle   = getIntent().getExtras();
        ArrayList<CIBaggageInfoResp> m_arItemList = (ArrayList<CIBaggageInfoResp>) bundle.getSerializable(UiMessageDef.BUNDLE_BAGGAGE_INFO_RESP);

        m_adapter = new CIBaggageTrackingRecyclerViewAdapter(this, m_arItemList, m_onRecyclerViewAdapterListener);
        m_recyclerView.setAdapter(m_adapter);
        m_adapter.notifyDataSetChanged();
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_onNavigationbarInterface = m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if ( null != intent ){
            Bundle bundle   = intent.getExtras();
            if ( null != bundle ){
                ArrayList<CIBaggageInfoResp> m_arItemList = (ArrayList<CIBaggageInfoResp>) bundle.getSerializable(UiMessageDef.BUNDLE_BAGGAGE_INFO_RESP);

                if ( null != m_adapter ) {
                    m_adapter.setDataList(m_arItemList);
                } else {
                    m_adapter = new CIBaggageTrackingRecyclerViewAdapter(this, m_arItemList, m_onRecyclerViewAdapterListener);
                    m_recyclerView.setAdapter(m_adapter);
                }
                m_adapter.notifyDataSetChanged();
            }
        }
    }


    private CIBaggageTrackingRecyclerViewAdapter.onRecyclerViewAdapterListener m_onRecyclerViewAdapterListener = new CIBaggageTrackingRecyclerViewAdapter.onRecyclerViewAdapterListener() {
        @Override
        public void onClick_GotoAdd() {

            Activity activity = (Activity) m_Context;
            Intent intent = new Intent();

            intent.setClass(m_Context, CIFindMyBaggageActivity.class);
            activity.startActivity(intent);

            activity.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        @Override
        public void onBaggageInfoClick(String strDeparture_Date, String strDeparture_Station, String strArrival_Station, String strBaggage_Number, String strBaggageShowNum) {
            m_Baggage_ShowNumber = strBaggageShowNum;
            m_Departure_Date        = strDeparture_Date;
            m_Departure_Station     = strDeparture_Station;
            m_Arrival_Station       = strArrival_Station;
            CIInquiryBaggageInfoPresenter.getInstance(m_BaggageInfoListener).InquiryBaggageInfoByBGNumFromWS(strBaggage_Number, m_Departure_Station, m_Departure_Date);
        }
    };


    CIBaggageInfoListener m_BaggageInfoListener = new CIBaggageInfoListener() {

        @Override
        public void onBaggageInfoByPNRAndBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoResp> arBaggageInfoListResp  ) {}

        @Override
        public void onBaggageInfoByPNRAndBGNumError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void onBaggageInfoByBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoContentResp> arDataList ) {

            Activity activity = (Activity) m_Context;
            Bitmap bitmap = ImageHandle.getScreenShot(activity);
            Bitmap blur   = ImageHandle.BlurBuilder(m_Context, bitmap, 13.5f, 0.15f);

            Bundle bundle = new Bundle();
            bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG,     blur);
            bundle.putSerializable(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_RESP,    arDataList);
            bundle.putString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_NUMBER, m_Baggage_ShowNumber);
            bundle.putString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_DEPARTUREDATE, m_Departure_Date);
            bundle.putString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_DEPARTURESTATION, m_Departure_Station);
            bundle.putString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_ARRIVALSTATION, m_Arrival_Station);

            Intent intent = new Intent();
            intent.putExtras(bundle);

            intent.setClass(m_Context, CIBaggageInfoContentActivity.class);
            activity.startActivity(intent);

            activity.overridePendingTransition(R.anim.anim_alpha_in_1000, R.anim.anim_do_nothing);

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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }
}
