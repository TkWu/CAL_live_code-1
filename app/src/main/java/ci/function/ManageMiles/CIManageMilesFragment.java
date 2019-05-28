package ci.function.ManageMiles;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.ui.define.ViewScaleDef;
import ci.ws.Models.entities.CIExpiringMileageResp;
import ci.ws.Models.entities.CIExpiringMileage_Info;
import ci.ws.Models.entities.CIInquiryAwardRecordRespList;
import ci.ws.Models.entities.CIMileageRecordResp;
import ci.ws.Models.entities.CIMileageResp;
import ci.ws.Models.entities.CIRedeemRecordResp;
import ci.ws.Presenter.CIInquiryMileagePresenter;
import ci.ws.Presenter.Listener.CIInquiryMileageListener;

/**
 * 里程管理
 * 只有正式會員才可進入此頁面
 * 顯示近六個月的里程數
 */
public class CIManageMilesFragment extends BaseFragment implements View.OnClickListener{

    CIInquiryMileageListener m_MileageListener = new CIInquiryMileageListener() {
        @Override
        public void onInquiryMileageSuccess(String rt_code, String rt_msg, CIMileageResp MileageResp) {
            //總里程數有更動時才更新畫面並將資料存入記憶體
            if (!m_strMile.equals(MileageResp.mileage)){

                CIApplication.getLoginInfo().SetMiles(MileageResp.mileage);
                m_strMile = MileageResp.mileage;

                //設定累積里程數文字
                setTextSpan();
            }
        }

        @Override
        public void onInquiryMileageError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryExpiringMileageSuccess(String rt_code, String rt_msg, CIExpiringMileageResp expiringMileageResp) {
            m_alData = expiringMileageResp;

            setExpiringMileageCard(getString(R.string.manage_miles_not_find));
        }

        @Override
        public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {

            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void onInquiryMileageRecordSuccess(String rt_code, String rt_msg, CIMileageRecordResp mileageRecordResp) {}

        @Override
        public void onInquiryMileageRecordError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryRedeemRecordSuccess(String rt_code, String rt_msg, CIRedeemRecordResp redeemRecordResp) {}

        @Override
        public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryAwardRecordSuccess(String rt_code, String rt_msg, CIInquiryAwardRecordRespList awardRecordResps) {}

        @Override
        public void onInquiryAwardRecordError(String rt_code, String rt_msg) {}

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }

        @Override
        public void onAuthorizationFailedError(String rt_code, String rt_msg) {
            isProcessWSErrCode(rt_code, rt_msg);
        }
    };

    public static class ViewHolder{
        RelativeLayout  rlRoot;
        TextView        tvDateData;
        TextView        tvMileData;
        View            vLine;
    }

    private SpannableString         m_spannableString       = null;
    private SpannableStringBuilder  m_spannableStringBuilder= null;

    private ScrollView              m_sv                    = null;
    private TextView                m_tvMiles               = null;
//    private TextView                m_tvUpdate              = null;
    private Button                  m_btnMilesActivity      = null;
    private Button                  m_btnReclaimMiles       = null;

    private FrameLayout             m_flData                = null;
    private LinearLayout            m_llListData            = null;

    private LinearLayout            m_llRedeem              = null;
    private FrameLayout             m_flRedeem              = null;

    private String                  m_strMile               = "0";
    private ArrayList<CIExpiringMileage_Info> m_alData      = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_manage_miles;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_sv            = (ScrollView) view.findViewById(R.id.root);

        m_tvMiles = (TextView) view.findViewById(R.id.tv_miles);

//        m_tvUpdate = (TextView) view.findViewById(R.id.tv_update);
//        m_tvUpdate.setText(String.format(getString(R.string.update_xxx_ago), "1"));

        m_btnMilesActivity = (Button) view.findViewById(R.id.btn_miles_activity);
        m_btnMilesActivity.setOnClickListener(this);
        m_btnReclaimMiles = (Button) view.findViewById(R.id.btn_reclaim_miles);
        m_btnReclaimMiles.setOnClickListener(this);

        m_flData = (FrameLayout) view.findViewById(R.id.fl_card);

        //2016.04.15 目前暫無資料源, 酬賓獎項兌換 暫時移除-Ling
        m_llRedeem = (LinearLayout) view.findViewById(R.id.ll_redeem_miles);
        m_llRedeem.setVisibility(View.GONE);
        m_flRedeem = (FrameLayout) view.findViewById(R.id.fl_redeem_miles);
        m_flRedeem.setVisibility(View.GONE);

    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        //自適應
        vScaleDef.selfAdjustAllView(m_sv);
    }

    private void setTextSpan(){
        m_spannableString = new SpannableString(
                String.format(getString(R.string.menu_miles), m_strMile));
        m_spannableStringBuilder = new SpannableStringBuilder(m_spannableString);

        int index = m_spannableStringBuilder.toString().indexOf(" ", 0);

//        m_spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
//                0,
//                index,
//                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        m_spannableStringBuilder.setSpan(new AbsoluteSizeSpan(
                ViewScaleDef.getInstance(getActivity()).getTextSize(40)),
                0,
                index,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        m_spannableStringBuilder.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                index,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        m_tvMiles.setText(m_spannableStringBuilder);
    }

    private void setExpiringMileageCard(String strMsg){
        if ( null == m_flData )
            return;

        m_flData.removeAllViews();

        View vCard = m_layoutInflater.inflate(R.layout.layout_view_manage_miles_card, null);
        LinearLayout llMilesOverdue = (LinearLayout) vCard.findViewById(R.id.ll_miles_overdue);
        TextView tvMilesOverdue = (TextView) vCard.findViewById(R.id.tv_miles_overdue);
        m_llListData = (LinearLayout) vCard.findViewById(R.id.ll_list_data);
        TextView tvNotFind = (TextView) vCard.findViewById(R.id.tv_not_find);
        tvNotFind.setText(strMsg);

        for (int i = 0; i < m_alData.size(); i++){

            View vItem = m_layoutInflater.inflate(R.layout.layout_view_manage_miles_item, null);
            ViewHolder holder = new ViewHolder();

            holder.rlRoot      = (RelativeLayout) vItem.findViewById(R.id.list_root);
            holder.tvDateData  = (TextView) vItem.findViewById(R.id.tv_date_data);
            holder.tvMileData  = (TextView) vItem.findViewById(R.id.tv_miles_data);
            holder.vLine       = (View) vItem.findViewById(R.id.v_line);

            //統一調整顯示格式 by ryan , 2016-08-16
            holder.tvDateData.setText(m_alData.get(i).expire_date);
//            holder.tvDateData.setText(
//                    AppInfo.getInstance(getActivity()).
//                            ConvertDateFormatToyyyyMMddEEE(m_alData.get(i).expire_date));

            DecimalFormat df = new DecimalFormat("#,##0");
            holder.tvMileData.setText(df.format(Integer.valueOf(m_alData.get(i).mileage)));

            m_llListData.addView(vItem);
        }

        if ( 0 == m_alData.size() ){
            tvNotFind.setVisibility(View.VISIBLE);
        }else {
            tvNotFind.setVisibility(View.GONE);
        }

        m_flData.addView(vCard);

        //自適應
        ViewScaleDef.getInstance(getActivity()).selfAdjustAllView(vCard);
        llMilesOverdue.setMinimumHeight(ViewScaleDef.getInstance(getActivity()).getLayoutHeight(64));
        tvMilesOverdue.setMinimumHeight(ViewScaleDef.getInstance(getActivity()).getLayoutHeight(24));

        m_alData.clear();
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(final FragmentManager fragmentManager) {
        //2016.04.15 目前暫無資料源, 酬賓獎項兌換 暫時移除-Ling
//        getActivity().runOnUiThread(new Runnable() {
//            public void run() {
//
//                CIRedeemMileFragment fragment = new CIRedeemMileFragment();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.replace(m_flRedeem.getId(), fragment, CIRedeemMileFragment.class.getSimpleName());
//                transaction.commitAllowingStateLoss();
//            }
//        });
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch ( v.getId() ){
            case R.id.btn_miles_activity:
                changeActivity(intent, CIMilesActivityActivity.class);
                break;
            case R.id.btn_reclaim_miles:
                changeActivity(intent, CIReclaimMilesMemberInputActivity.class);
                break;
        }
    }

    private void changeActivity(Intent intent, Class clazz){
        intent.setClass(getActivity(), clazz);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void onFragmentShow() {
        super.onFragmentShow();

        if( m_sv != null ) {
            m_sv.fullScroll(View.FOCUS_UP);
        }

        //顯示登入時取到的累積里程
        if ( 0 < CIApplication.getLoginInfo().GetMiles().length() ){
            m_strMile = CIApplication.getLoginInfo().GetMiles();
        }else {
            m_strMile = "0";
        }
        setTextSpan();

        m_btnMilesActivity.setText(getString(R.string.miles_activity));
        m_btnReclaimMiles.setText(getString(R.string.reclaim_miles));

        m_FragmentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //向ws撈六個月內到期的里程資料
                CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryExpiringMileageFromWS();
                CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryMileageFromWS();
            }
        }, 500);

    }

    @Override
    public void onFragmentHide() {
        super.onFragmentHide();

        setExpiringMileageCard("");

        //如果ws還在撈資料 要取消請求
        CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryExpiringMileageCancel();
        CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryMileageCancel();

//        System.gc();
    }

    @Override
    public void onResume() {
        super.onResume();
        if ( null != m_sv ){
            m_sv.setBackgroundResource(R.drawable.bg_login);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if ( null != m_sv ){
            if ( m_sv.getBackground() instanceof BitmapDrawable){
                m_sv.setBackground(null);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CIInquiryMileagePresenter.getInstance(null);
//        System.gc();
    }
}
