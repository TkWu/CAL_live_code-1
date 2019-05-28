package ci.function.BaggageTrack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Signup.CITermsAndConditionsActivity;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CICustomTextWithScanBagNumFieldFragment;
import ci.ui.TextField.CIOnlyEnglishTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CILoginInfo;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIBaggageInfoContentResp;
import ci.ws.Models.entities.CIBaggageInfoReq;
import ci.ws.Models.entities.CIBaggageInfoResp;
import ci.ws.Presenter.CIInquiryBaggageInfoPresenter;
import ci.ws.Presenter.Listener.CIBaggageInfoListener;
import ci.ws.cores.object.GsonTool;
import static ci.ui.TextField.CICustomTextWithScanBagNumFieldFragment.Type;


/**使用行李號碼查詢行李追蹤列表*/
public class CIFindMyBaggageFragment extends BaseFragment
        implements View.OnClickListener{

    CIBaggageInfoListener m_CIBaggageInfoListener = new CIBaggageInfoListener(){

        @Override
        public void onBaggageInfoByPNRAndBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoResp> arBaggageInfoListResp ) {

            //儲存資料
            CIApplication.getBaggageInfo().setBaggageList(arBaggageInfoListResp);

            Bitmap bitmap = ImageHandle.getScreenShot(getActivity());
            Bitmap blur   = ImageHandle.BlurBuilder(getActivity(), bitmap, 13.5f, 0.15f);

            Bundle bundle = new Bundle();
            bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG,  blur);
            bundle.putSerializable(UiMessageDef.BUNDLE_BAGGAGE_INFO_RESP,    arBaggageInfoListResp);

            Intent intent = new Intent();
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setClass(getActivity(), CIBaggageTrackingListActivity.class);
            getActivity().startActivity(intent);

            getActivity().overridePendingTransition(R.anim.anim_alpha_in, 0);

            bitmap.recycle();
            getActivity().finish();
        }

        @Override
        public void onBaggageInfoByPNRAndBGNumError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void onBaggageInfoByBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoContentResp> arDataList) {

        }

        @Override
        public void onBaggageInfoByBGNumError(String rt_code, String rt_msg) {

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



    public static CIFindMyBaggageFragment newInstance() {
        CIFindMyBaggageFragment fragment = new CIFindMyBaggageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private CITextFieldFragment m_BaggageTagFragment,
                                m_firstNameFragment,
                                m_lastNameFragment;

    private String              m_strFirstName  = null;
    private String              m_strLastName   = null;

    //644336 20190322 行李追蹤注意事項更改顯示方式
    /**注意事項*/
    //private CITermsAndConditionsActivity.ContentList   m_TermsAndConditionsContentList = null;
    //644336 20190322 行李追蹤注意事項更改顯示方式

    /**
     * BaseFragment在
     * {@link BaseFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
     * onCreateView()}時 設定{@link LayoutInflater#inflate(int, ViewGroup, boolean)
     * inflate()}用
     *
     * @return 此畫面的 Layout Resource Id
     */
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_find_my_booking_for_baggage;
    }

    /**
     * 元件初始化，靜態取得元件實體、動態建製元件實體…等
     *
     * @param inflater
     * @param view
     */
    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        //644336 20190322 行李追蹤注意事項更改顯示方式
//        m_TermsAndConditionsContentList = new CITermsAndConditionsActivity.ContentList();
//        CITermsAndConditionsActivity.Content data1 = new CITermsAndConditionsActivity.Content();
//        data1.itemTitle = getString(R.string.find_my_booking_baggage_notes);
//        data1.itemContent = getString(R.string.find_my_booking_baggage_notes_content);
//        m_TermsAndConditionsContentList.add(data1);
//        //
//        CITermsAndConditionsActivity.Content data2 = new CITermsAndConditionsActivity.Content();
//        data2.itemTitle = getString(R.string.find_my_booking_baggage_notes_2);
//        data2.itemContent = getString(R.string.find_my_booking_baggage_notes_content_2);
//        m_TermsAndConditionsContentList.add(data2);
        //644336 20190322 行李追蹤注意事項更改顯示方式
    }

    /**
     * 設定字型大小及版面大小
     *
     * @param view
     * @param vScaleDef 請參閱{@link ViewScaleDef}
     */
    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_arrow), 17, 17);
    }

    /**
     * 通知父BaseActivity對子BaseFragment設定客製的「OnParameter(參數讀取)」跟「OnListener(
     * 動線通知)」介面
     *
     * @param view
     */
    @Override
    protected void setOnParameterAndListener(View view) {
        //644336 20190322 行李追蹤注意事項更改顯示方式
        //view.findViewById(R.id.iv_arrow).setOnClickListener(this);
        //view.findViewById(R.id.tv_can_find_the_trip).setOnClickListener(this);
        //644336 20190322 行李追蹤注意事項更改顯示方式
        view.findViewById(R.id.btn_findmybooking_retrieve).setOnClickListener(this);

    }

    /**
     * 依需求調用以下函式
     *
     * @param fragmentManager
     * @see FragmentTransaction FragmentTransaction相關操作
     */
    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        m_BaggageTagFragment = CICustomTextWithScanBagNumFieldFragment.newInstance(getString(R.string.baggage_tag_ref_hint));
        m_firstNameFragment = CIOnlyEnglishTextFieldFragment
                .newInstance(getString(R.string.inquiry_input_box_first_name_hint));
        m_lastNameFragment = CIOnlyEnglishTextFieldFragment
                .newInstance(getString(R.string.inquiry_input_box_last_name_hint));
        transaction
                .replace(R.id.fragment1, m_BaggageTagFragment)
                .replace(R.id.fragment2, m_lastNameFragment)
                .replace(R.id.fragment3, m_firstNameFragment)
                .commitAllowingStateLoss();
        if(null != getActivity()){
            getActivity().getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    m_firstNameFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
                }
            });
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                CILoginInfo info = CIApplication.getLoginInfo();
                if(info.isDynastyFlyerMember()) {
                    String firstName = info.GetUserFirstName();
                    String lastName = info.GetUserLastName();
                    if(!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)){
                        m_firstNameFragment.setText(firstName);
                        m_lastNameFragment.setText(lastName);
                        m_firstNameFragment.setLock(true);
                        m_lastNameFragment.setLock(true);
                    }
                }
            }
        });
    }

    /**
     * 若收到Handle Message且BaseActivity不認得時，
     * 視為子class自訂Message，可經由此Function接收通知。
     *
     * @param msg
     * @return true：代表此Message已處理<p> false：代表此Message連子class也不認得<p>
     */
    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    /**
     * 若子class有自訂Message，請經由此Function清空Message。
     */
    @Override
    protected void removeOtherHandleMessage() {}

    /**
     * 當App語言變更後, 會呼叫此介面，藉此更新畫面UI,需要重新呼叫setText
     */
    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btn_findmybooking_retrieve:

                //尚未輸入訂位代號或機票號
                String strBooking = m_BaggageTagFragment.getText().toString();
                if ( 0 >= strBooking.length() ){

                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field),
                                    m_BaggageTagFragment.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                //尚未輸入名
                m_strFirstName = m_firstNameFragment.getText().toString().trim();
                if ( 0 >= m_strFirstName.length() ){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field),
                                    m_firstNameFragment.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                //尚未輸入姓
                m_strLastName  = m_lastNameFragment.getText().toString().trim();
                if ( 0 >= m_strLastName.length() ){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field),
                                    m_lastNameFragment.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                //格式不符
                Type type = ((CICustomTextWithScanBagNumFieldFragment)m_BaggageTagFragment).getTextFeildType();
                if ( type == Type.NONE ) {
                    showDialog(getString(R.string.warning),
                            getString(R.string.member_login_input_correvt_format_msg),
                            getString(R.string.confirm));
                } else {

                    ArrayList<CIBaggageInfoReq> arList = new ArrayList<CIBaggageInfoReq>();
                    CIBaggageInfoReq req = new CIBaggageInfoReq();
                    req.Baggage_Number  = strBooking;
                    req.First_Name      = m_strFirstName;
                    req.Last_Name       = m_strLastName;
                    arList.add(req);

                    CIInquiryBaggageInfoPresenter.getInstance(m_CIBaggageInfoListener).InquiryBaggageInfoByPNRAndBGNumFromWS(
                            "",
                            "",
                            new ArrayList<String>(),
                            arList);
                }

                break;
            //644336 20190322 行李追蹤注意事項更改顯示方式
//            case R.id.iv_arrow:
//            case R.id.tv_can_find_the_trip:
//                changeActivityToNote();
//                break;
            //644336 20190322 行李追蹤注意事項更改顯示方式
        }
    }
    //644336 20190322 行李追蹤注意事項更改顯示方式
//    /**
//     * 轉換Activity
//     * @param clazz 目標activity名稱
//     */
//    private void changeActivity(Class clazz, Intent data ){
//
//        Intent intent;
//        if ( null == data ){
//            intent = new Intent();
//        } else {
//            intent = data;
//        }
//        intent.setClass(getActivity(), clazz);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//
//        //測試切換activity滑入滑出動畫
//        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
//    }


//    private void changeActivityToNote(){
//        Intent data = new Intent();
//        String jsData = GsonTool.toJson(m_TermsAndConditionsContentList);
//        data.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA_TITLE,getString(R.string.find_my_booking_baggage_notes));
//        data.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA,jsData);
//        changeActivity(CITermsAndConditionsActivity.class, data);
//    }
    //644336 20190322 行李追蹤注意事項更改顯示方式


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CIInquiryBaggageInfoPresenter.getInstance(null);
    }
}
