package ci.function.ManageMiles;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import ci.function.Core.SLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chinaairlines.mobile30.R;
import java.util.ArrayList;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;
import ci.ui.view.ShadowBar.ShadowBarScrollview;
import ci.ws.Models.entities.CIReclaimMileageReq;
import ci.ws.Presenter.CIReclaimMileagePresenter;
import ci.ws.Presenter.Listener.CIReclaimMileageListener;
import ci.ws.cores.object.GsonTool;

/**
 * Created by kevincheng on 2016/3/11.
 * 主要是作為里程補登輸入詳細飛航資訊的頁面
 */
public class CIReclaimMilesFlightDetialInputActivity extends BaseActivity
    implements View.OnClickListener,
                CIFlightDetialInputFragment.onFragmentDeletedListener{
    private             NavigationBar            m_Navigationbar          = null;
    public  final       int                      DEF_RESET_NO             = 1;
    private             int                      m_iMax                   = 2;
    private             int                      m_iFragmentCount         = 0;
    private             LinearLayout             m_llAdd                  = null;
    private             LinearLayout             m_contentView            = null;
    private             ShadowBarScrollview      m_scrollView             = null;
    private             ArrayList<Integer>       m_alViewId               = null;
    private             ArrayList<CIFlightDetialInputFragment> m_alFragment = null;
    private             CIReclaimMileageReq      m_reqData                = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if(null != bundle){
            String jsonData = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA);
            m_reqData = GsonTool.toObject(jsonData, CIReclaimMileageReq.class);
        }
        super.onCreate(savedInstanceState);
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.reclaim_miles);
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
        public void onDemoModeClick() {}
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_recliam_miles_flight_detial_input;
    }

    @Override
    protected void initialLayoutComponent() {
        m_alViewId      = new ArrayList<>();
        m_alFragment    = new ArrayList<>();
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_scrollView    = (ShadowBarScrollview) findViewById(R.id.scrollview);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.layout_reclaim_miles_flight_detail_input, m_scrollView.getContentView());
        m_contentView   = (LinearLayout) findViewById(R.id.ll_content);
        m_llAdd         = (LinearLayout) findViewById(R.id.ll_add_flight);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_add), 24, 24);
        ((TextView)findViewById(R.id.tv_notice_content_1)).setLineSpacing(vScaleDef.getLayoutHeight(6.7), 1.0f);
        ((TextView)findViewById(R.id.tv_notice_content_2)).setLineSpacing(vScaleDef.getLayoutHeight(6.7), 1.0f);
        m_scrollView.setShadowBarHeight(vScaleDef.getLayoutHeight(16));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        findViewById(R.id.btn_finish).setOnClickListener(this);
        findViewById(R.id.ll_add_flight).setOnClickListener(this);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        addFlightData();
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
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_finish:
                if(true == isFillCompleteAndCorrect()){
                        sendReqDataToWS();
                }
                break;
            case R.id.ll_add_flight:
                addFlightData();
                break;
        }
    }

    private boolean isFillCompleteAndCorrect(){
        for(CIFlightDetialInputFragment fragment : m_alFragment){
            CIFlightDetialInputFragment.Entity data = fragment.getData();


            if(TextUtils.isEmpty(data.departureAirport) || TextUtils.isEmpty(data.arrivalAirport)
                    || TextUtils.isEmpty(data.departureDate)  || TextUtils.isEmpty(data.company)
                    || TextUtils.isEmpty(data.flightNumber)   || TextUtils.isEmpty(data.tickerNumber)){
                showDialog(getString(R.string.warning),
                        getString(R.string.please_fill_all_text_field_that_must_to_fill));
                return false;
            }

            if(data.tickerNumber.length() != 13){
                String msg = String.format(getString(R.string.textfield_placeholder_invalidformatmesseage),
                        getString(R.string.ticket_number));
                showDialog(getString(R.string.warning),
                        msg,
                        getString(R.string.member_login_input_correvt_format_msg));
                return false;
            }
        }
        return true;
    }

    private void sendReqDataToWS(){
        int count = 1;
        CIReclaimMileageReq reqData = (CIReclaimMileageReq)m_reqData.clone();
        for(CIFlightDetialInputFragment fragment : m_alFragment){
            CIFlightDetialInputFragment.Entity data = fragment.getData();
            if(1 == count){
                reqData.dep_city1     = data.departureAirport;
                reqData.arr_city1     = data.arrivalAirport;
                reqData.dep_date1     = data.departureDate;
                reqData.cdc1          = data.company;
                reqData.fno1          = String.format("%1$04d",Integer.valueOf(data.flightNumber));
                reqData.ticket_no1    = data.tickerNumber;

            } else if(2 == count){
                reqData.dep_city2     = data.departureAirport;
                reqData.arr_city2     = data.arrivalAirport;
                reqData.dep_date2     = data.departureDate;
                reqData.cdc2          = data.company;
                reqData.fno2          = String.format("%1$04d",Integer.valueOf(data.flightNumber));
                reqData.ticket_no2    = data.tickerNumber;

            }
            count++;
        }

        CIReclaimMileagePresenter.getInstance(m_listener).ReclaimMileageFromWS(reqData);
    }


    /**
     * 轉換Activity
     * @param clazz     目標activity名稱
     */
    private void changeActivityForResult(Class clazz ,Intent data){
        data.setClass(this, clazz);
        startActivityForResult(data, UiMessageDef.REQUEST_CODE_LOGIN);
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }





    private void addFlightData(){
        //增加數量
        m_iFragmentCount++;
        int iId =  getViewId();
        //初始化frameLayout
        final FrameLayout layout = new FrameLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setId(iId);

        final CIFlightDetialInputFragment fragment = new CIFlightDetialInputFragment();
        fragment.setOnFragmentDeletedListener(this);
        m_alFragment.add(fragment);
        //加入framelayout就轉入fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        m_contentView.addView(layout);
        transaction.replace(iId,fragment, String.valueOf(iId)).commit();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                m_scrollView.getScrollView().smoothScrollTo(0, layout.getHeight() * (m_iFragmentCount - 1));
                setFragmentConfig(m_iFragmentCount,fragment);
                setTextFeildActionDone();
            }
        }, 200);
        if(m_iMax <= m_iFragmentCount){
            m_llAdd.setVisibility(View.GONE);
        }
    }

    private void setFragmentConfig(int count, CIFlightDetialInputFragment fragment){
        fragment.setNumber(count);
        if(1 < count){
            fragment.setGarbageIconVisible(View.VISIBLE);
        } else {
            fragment.setGarbageIconVisible(View.GONE);
        }

    }


    /**
     * 對View Id 做資源管理，避免使用到整數的極限值造成異常
     * @return view id
     */
    private int getViewId(){
        int iViewId = 11;
        while(true){
            if(!m_alViewId.contains(iViewId)){
                break;
            }
            iViewId++;
        }
        m_alViewId.add(iViewId);
       SLog.d("viewId", iViewId + "");
        return iViewId;
    }

    /**
     * 釋放加入管理的view id，可再次被利用
     * @return view id
     */
    private void releaseViewId(Integer viewId){
        m_alViewId.remove(viewId);
    }

    @Override
    public void onFragmentDeleted(Fragment deletedfragment) {
        getSupportFragmentManager().beginTransaction().remove(deletedfragment).commit();
        releaseViewId(deletedfragment.getId());
        m_contentView.removeView(m_contentView.findViewById(deletedfragment.getId()));
        m_alFragment.remove(deletedfragment);
        int count = DEF_RESET_NO;
        for (CIFlightDetialInputFragment fragment : m_alFragment) {
            fragment.setNumber(count);
            count++;
        }
        setTextFeildActionDone();
        //減少一個Fragment數量
        m_iFragmentCount--;
        //判斷是否顯示新增按鈕文字
        if(m_iMax > m_iFragmentCount){
            m_llAdd.setVisibility(View.VISIBLE);
        }
    }

    private void setTextFeildActionDone(){
        int size = m_alFragment.size();
        int count = 1;
        for (CIFlightDetialInputFragment fragment : m_alFragment) {
            if (size == count) {
                fragment.setImeOption(EditorInfo.IME_ACTION_DONE);
            } else {
                fragment.setImeOption(EditorInfo.IME_ACTION_NEXT);
            }
            count++;
        }
    }

    CIReclaimMileageListener m_listener = new CIReclaimMileageListener() {
        @Override
        public void onReclaimMileageSuccess(String rt_code, String rt_msg) {
            Intent data = new Intent();
            changeActivityForResult(CIReclaimMilesSuccessActivity.class,data);
        }

        @Override
        public void onReclaimMileageError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),rt_msg);
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
}
