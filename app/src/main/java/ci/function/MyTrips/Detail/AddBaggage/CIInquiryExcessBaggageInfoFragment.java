package ci.function.MyTrips.Detail.AddBaggage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.MyTrips.Detail.common.CIReadmeActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.view.ShadowBar.ShadowBarScrollview;
import ci.ui.view.TextHandle;
import ci.ws.Models.CIInquiryAllPassengerByPNRModel;
import ci.ws.Models.entities.CIInquiryExcessBaggageInfoResp;

import static com.chinaairlines.mobile30.R.id.tv_ebamount;

/**
 * Created by kevincheng on 2017/9/18.
 */

public class CIInquiryExcessBaggageInfoFragment extends BaseFragment implements View.OnClickListener{
    private ShadowBarScrollview            m_shadowScrollView    = null;
    private LinearLayout                   m_llContent           = null;
    private CIInquiryExcessBaggageInfoResp m_inquiryBagInfo      = null;
    private ArrayList<View>                m_arViews             = null;
    private boolean                        m_bTermsAndConditions = false;
    private ImageButton                    m_ivTermsAndCond      = null;
    private ICallBack                      m_callback            = null;
    public interface ICallBack {
        void onClickTermsAndConditionsBtn(boolean bEnable);
    }

    public static CIInquiryExcessBaggageInfoFragment newInstance(CIInquiryExcessBaggageInfoResp datas) {

        Bundle args = new Bundle();
        args.putSerializable(UiMessageDef.BUNDLE_INQUIRY_EXCESS_BAGGAGE_INFO_RESP, datas);
        CIInquiryExcessBaggageInfoFragment fragment = new CIInquiryExcessBaggageInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_inquiry_excess_baggage_info_card;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        Bundle bundle = getArguments();
        if(null != bundle) {
            m_inquiryBagInfo = (CIInquiryExcessBaggageInfoResp)bundle.getSerializable(UiMessageDef.BUNDLE_INQUIRY_EXCESS_BAGGAGE_INFO_RESP);
        }
        m_shadowScrollView  = (ShadowBarScrollview) view.findViewById(R.id.shadowlayout);
        m_llContent         = m_shadowScrollView.getContentView();

        int iPaxInfo = 0;

        if(null != m_inquiryBagInfo && null != m_inquiryBagInfo.Pax_info) {
            iPaxInfo = m_inquiryBagInfo.Pax_info.size();
            m_arViews = new ArrayList<>();
        } else {
            return;
        }
        AppInfo appInfo = AppInfo.getInstance(CIApplication.getContext());

        //抬頭總計
        View ViewTitle = m_layoutInflater.inflate( R.layout.layout_view_passenger_inquiry_excess_baggage_info_total_card, null);
        TextView tvEbamout = (TextView)ViewTitle.findViewById(R.id.tv_ebamount);
        tvEbamout.setText(m_inquiryBagInfo.ttl_currency + " " + appInfo.valueFormat(m_inquiryBagInfo.ttl_amount));
        m_llContent.addView(ViewTitle);

        //個乘客購賣數量及價格
        for(int loop = 0; loop < iPaxInfo; loop ++) {
            View           ViewContent = m_layoutInflater.inflate( R.layout.layout_view_passenger_inquiry_excess_baggage_info_card, null);

            final TextView      name       = (TextView) ViewContent.findViewById(R.id.name);
            final TextView      ssrAmount  = (TextView)ViewContent.findViewById(R.id.tv_value);
            final TextView      ebAmount   = (TextView)ViewContent.findViewById(tv_ebamount);

            final CIInquiryExcessBaggageInfoResp.PaxInfo paxInfo = m_inquiryBagInfo.Pax_info.get(loop);

            ssrAmount.setText(paxInfo.ssrAmount + CIInquiryAllPassengerByPNRModel.getBagUnit(paxInfo.ssrType));
            ebAmount.setText(paxInfo.ebCurrency + " " + appInfo.valueFormat(paxInfo.ebAmount));
            //收集購買資料
            ViewContent.setTag(paxInfo.pax_num);
            m_arViews.add(ViewContent);

            name.setText(paxInfo.first_name + " " + paxInfo.last_name);

            m_llContent.addView(ViewContent);
        }

        //我已詳細閱讀行李加購各項須知，並同意遵守區塊
        View        viewFooter     = m_layoutInflater.inflate( R.layout.layout_view_passenger_inquiry_excess_baggage_info_term_and_conditions_card, null);
        TextView    tvTermsAndCond = (TextView)viewFooter.findViewById(R.id.tv_terms_and_conditions);
        m_ivTermsAndCond = (ImageButton)viewFooter.findViewById(R.id.iv_terms_and_conditions);

        TextHandle.initTermsAndConditionsTextFormat(tvTermsAndCond,
                                                    getString(R.string.trip_detail_passenger_bag_term_and_conditions),
                                                    new TextHandle.ICallBack() {
                                                        @Override
                                                        public void onClick() {
                                                            changeActivityToTermsAndConditions(getString(R.string.trip_detail_passenger_bag_pay_add_excess_notes), getString(R.string.trip_detail_passenger_bag_pay_add_excess_notes_content));
                                                        }
                                                    });

        m_llContent.addView(viewFooter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_terms_and_conditions:
            m_bTermsAndConditions =
                    setViewIconSwitch(m_ivTermsAndCond,
                                      m_bTermsAndConditions);
                m_callback.onClickTermsAndConditionsBtn(m_bTermsAndConditions);
            break;
        }
    }

    private boolean setViewIconSwitch(View v, boolean isOn) {
        if (true == isOn) {
            ((ImageView) v).setImageResource(R.drawable.btn_checkbox_off);
            return false;
        } else {
            ((ImageView) v).setImageResource(R.drawable.btn_checkbox_on);
            return true;
        }
    }

    private void changeActivityToTermsAndConditions(String title,String msg){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_DATA_TITLE, title);
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_DATA, msg);
        intent.putExtras(bundle);
        intent.setClass(getActivity(), CIReadmeActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private void changeActivity(Class clazz,Intent intent){
        if(null == intent){
            intent = new Intent();
        }
        intent.setClass(getActivity(), clazz);
        startActivity(intent);

        //測試切換activity滑入滑出動畫
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_llContent);
        if(null == m_arViews) {
            return;
        }
            vScaleDef.selfAdjustSameScaleView(m_ivTermsAndCond, 24, 24);
    }

    public boolean isAgreeTermsAndConditions(){
        return m_bTermsAndConditions;
    }

    public void setListener(ICallBack callBack){
        m_callback = callBack;
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_ivTermsAndCond.setOnClickListener(this);
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
    public void onLanguageChangeUpdateUI() {

    }
}
