package ci.function.Checkin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.Iterator;

import ci.function.Main.BaseActivity;
import ci.function.MyTrips.CIFindMyBookingNotesActivity;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIBookingRefTicketTextFieldFragment;
import ci.ui.TextField.CIOnlyEnglishTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CICheckInAllPaxResp;
import ci.ws.Presenter.CIInquiryCheckInPresenter;
import ci.ws.Presenter.Listener.CIInquiryCheckInListener;
import ci.ws.cores.object.GsonTool;


/**
 * Created by Ryan on 16/3/31.
 */
public class CIAddPassengerSingleActivity extends BaseActivity{

    public static final String BUNDLE_PARA_ACCOUNT  = "account";
    public static final String BUNDLE_PARA_PASSWORD = "password";
    public static final String BUNDLE_PNR_ID        = "PnrId";
    public static final String BUNDLE_SEGMENT_NO    = "SegmentNo";
    public static final String BUNDLE_UCI_LIST      = "UciList";

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {
        @Override
        public Boolean GetToolbarType() {
            return false;
        }
        @Override
        public String GetTitle() {
            return getString(R.string.add_passenager_title);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {
        @Override
        public void onRightMenuClick() {}
        @Override
        public void onLeftMenuClick() {}
        @Override
        public void onBackClick() { onBackPressed(); }
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

    private NavigationBar   m_Navigationbar = null;
    private RelativeLayout  m_rlayout_root  = null;
    private Button          m_btnAdd        = null;
    private String          m_errorMsg      = null;
    private String          m_strPnrId      = null;
    private ArrayList<String> m_SegmentNoList = null;
    private ArrayList<String> m_arUciList     = null;

    private CITextFieldFragment m_retrieveBookingFragment,
            m_firstNameFragment,
            m_lastNameFragment;

    //2016-07-15 ryan 調整Check-in Presenter 使用方式
    private CIInquiryCheckInPresenter m_InquiryCheckInPresenter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String strPnrId = getIntent().getStringExtra(BUNDLE_PNR_ID);
        if ( !TextUtils.isEmpty(strPnrId) ) {
            m_strPnrId = strPnrId;
        }

        ArrayList<String> arSegmentNo = getIntent().getStringArrayListExtra(BUNDLE_SEGMENT_NO);
        if ( null != arSegmentNo ) {
            m_SegmentNoList = arSegmentNo;
        }

        ArrayList<String> arUciList = getIntent().getStringArrayListExtra(BUNDLE_UCI_LIST);
        if( null != arUciList ) {
            m_arUciList = arUciList;
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_check_in_add_passenger;
    }

    @Override
    protected void initialLayoutComponent() {

        m_rlayout_root = (RelativeLayout)findViewById(R.id.root);
        m_rlayout_root.setOnTouchListener(m_onBackgroundTouchListener);

        m_Navigationbar = (NavigationBar)findViewById(R.id.toolbar);
        m_btnAdd        = (Button)findViewById(R.id.btn_add);
        m_btnAdd.setOnClickListener(m_onClick);

        findViewById(R.id.rl_notice_content).setOnClickListener(m_onClick);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_rlayout_root);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);

        m_InquiryCheckInPresenter = new CIInquiryCheckInPresenter(m_InquiryCheckInListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        m_retrieveBookingFragment = CIBookingRefTicketTextFieldFragment.newInstance(m_Context);
        m_firstNameFragment = CIOnlyEnglishTextFieldFragment.newInstance(getString(R.string.inquiry_input_box_first_name_hint));
        m_lastNameFragment = CIOnlyEnglishTextFieldFragment.newInstance(getString(R.string.inquiry_input_box_last_name_hint));
        transaction
                .replace(R.id.fragment1, m_retrieveBookingFragment)
                //2016-11-14 Modifly By Ryan, 調整名字與姓氏的順序
                .replace(R.id.fragment2, m_lastNameFragment)
                .replace(R.id.fragment3, m_firstNameFragment)
                //.replace(R.id.fragment2, m_firstNameFragment)
                //.replace(R.id.fragment3, m_lastNameFragment)
                .commitAllowingStateLoss();
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
    protected void onResume() {
        super.onResume();

        if( null != m_lastNameFragment ) {
            //2016-11-14 Modifly By Ryan, 因應調整名字與姓氏的順序
            m_firstNameFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
            //m_lastNameFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }

    View.OnClickListener m_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if ( v.getId() == m_btnAdd.getId() ){

                if( false == isFillCompleteAndCorrect() ) {

                    showDialog(getString(R.string.warning),
                            m_errorMsg);
                    return;
                } else {
                    CIBookingRefTicketTextFieldFragment.Type type = ((CIBookingRefTicketTextFieldFragment) m_retrieveBookingFragment).getTextFeildType();
                    String strRetriebeId = m_retrieveBookingFragment.getText();
                    String strFirstName = m_firstNameFragment.getText();
                    String strLastName = m_lastNameFragment.getText();

                    if(CIBookingRefTicketTextFieldFragment.Type.BOOKING_REF == type ) {
                        m_InquiryCheckInPresenter.InquiryCheckInAllPaxByPNRFromWS(m_strPnrId, m_SegmentNoList, strRetriebeId, strFirstName, strLastName);
                    } else if(CIBookingRefTicketTextFieldFragment.Type.TICKET == type ) {
                        m_InquiryCheckInPresenter.InquiryCheckInAllPaxByTicketFromWS(m_strPnrId, m_SegmentNoList, strRetriebeId, strFirstName, strLastName);
                    }
                }

            } else if( v.getId() == R.id.rl_notice_content ) {
                changeActivity(CIFindMyBookingNotesActivity.class, UiMessageDef.BUNDLE_CHKIN_FINDMYBOOKING_NOTES, "1");
            }
        }
    };

    /**
     * 轉換Activity
     * @param clazz 目標activity名稱
     */
//    private void changeActivity(Class clazz){
//        Intent intent = new Intent();
//        intent.setClass(this, clazz);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//
//        //測試切換activity滑入滑出動畫
//        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
//    }

    private void changeActivity(Class clazz,String key,String extra){
        Intent intent = new Intent();
        intent.putExtra(key, extra);
        intent.setClass(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private boolean isFillCompleteAndCorrect() {

        /**
         * 初始化錯誤訊息
         */
        m_errorMsg = getString(R.string.please_fill_all_text_field_that_must_to_fill);

        String strRetrieveBooking = m_retrieveBookingFragment.getText();

        if( TextUtils.isEmpty(strRetrieveBooking) ) {
            return false;
        } else if( !m_retrieveBookingFragment.getIsFormatCorrect() ) {
            return false;
        }

        String strFirstName = m_firstNameFragment.getText();
        if( TextUtils.isEmpty(strFirstName) ) {
            return false;
        }

        String strLastName = m_lastNameFragment.getText();
        if( TextUtils.isEmpty(strLastName) ) {
            return false;
        }

        return true;
    }

    private void filterPaxInfo( CICheckInAllPaxResp CheckInList ) {

        for( Iterator<CICheckInPax_InfoEntity> iterator = CheckInList.iterator() ; iterator.hasNext(); ) {
            CICheckInPax_InfoEntity entity = iterator.next();

            if( m_strPnrId.equals(entity.Pnr_Id) || null == entity.m_Itinerary_InfoList) {
                iterator.remove();
                continue;
            }

            boolean bNeedRemove = true;
            for (CICheckInPax_ItineraryInfoEntity itEntity : entity.m_Itinerary_InfoList ) {

                if( false == itEntity.Is_Check_In && false == itEntity.Is_Black && true == itEntity.Is_Do_Check_In ) {
                    bNeedRemove = false;
                    break;
                }
            }

            if( true == bNeedRemove ) {
                iterator.remove();
                continue;
            }

            //若無Uci則不需檢查是否重複新增旅客
            if( null == m_arUciList ) {
                continue;
            }

            //檢查是否有相同Uci的旅客
            for( String strUci : m_arUciList ) {
                if( strUci.equals( entity.Uci ) ) {
                    iterator.remove();
                    break;
                }
            }

        }
    }

    private CIInquiryCheckInListener m_InquiryCheckInListener = new CIInquiryCheckInListener() {
        @Override
        public void onInquiryCheckInSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {}

        @Override
        public void onInquiryCheckInError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryCheckInAllPaxSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {
            //檢查是否能CheckIn，若旅客人數超過九人，則需臨櫃CheckIn
            if( !isGroupTickets(CheckInList)) {
                filterPaxInfo(CheckInList);

                if (0 < CheckInList.size()) {

                    Intent intent = new Intent();
                    intent.putExtra(BUNDLE_PARA_ACCOUNT, GsonTool.toJson(CheckInList));
                    intent.putExtra(BUNDLE_PARA_PASSWORD, m_firstNameFragment.getText() + " " + m_lastNameFragment.getText());
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
                } else {
                    showDialog(getString(R.string.warning),
                            getString(R.string.can_not_find_any_passenger),
                            getString(R.string.confirm));
                }
            } else {
                showDialog(getString(R.string.warning),
                        getString(R.string.check_in_pnr_passenger_over_limit),
                        getString(R.string.confirm));
            }
        }

        @Override
        public void onInquiryCheckInAllPaxError(String rt_code, String rt_msg) {
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

    /**
     * 檢查是否為超過九人之團體票，若超過則需要臨櫃CheckIn
     * @param CheckInList
     * @return
     */
    private boolean isGroupTickets(CICheckInAllPaxResp CheckInList) {

        if( 9 < CheckInList.size() ) {
            return true;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //CIInquiryCheckInPresenter.getInstance(null);
        m_InquiryCheckInPresenter.setCallBack(null);
    }
}
