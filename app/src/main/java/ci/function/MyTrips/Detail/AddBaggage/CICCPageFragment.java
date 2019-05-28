package ci.function.MyTrips.Detail.AddBaggage;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.WebView.CIWebView;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.view.ShadowBar.ShadowBarScrollview;
import ci.ws.Models.entities.CICCPageResp;
import ci.ws.Models.entities.CIInquiryExcessBaggageInfoResp;

/**
 * Created by kevincheng on 2017/9/18.
 * 加購信用卡付款頁面
 */

public class CICCPageFragment extends BaseFragment{
    private ShadowBarScrollview            m_shadowScrollView = null;
    private LinearLayout                   m_llContent        = null;
    private CICCPageResp                   m_ccpageResp       = null;
    private CIInquiryExcessBaggageInfoResp m_resp             = null;

    public static CICCPageFragment newInstance(CICCPageResp datas,CIInquiryExcessBaggageInfoResp resp) {

        Bundle args = new Bundle();
        args.putSerializable(UiMessageDef.BUNDLE_INQUIRY_CCPAGE_RESP, datas);
        args.putSerializable(UiMessageDef.BUNDLE_INQUIRY_EXCESS_BAGGAGE_INFO_RESP, resp);
        CICCPageFragment fragment = new CICCPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_ccpage_card;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        Bundle bundle = getArguments();
        if(null != bundle) {
            m_ccpageResp = (CICCPageResp)bundle.getSerializable(UiMessageDef.BUNDLE_INQUIRY_CCPAGE_RESP);
            m_resp = (CIInquiryExcessBaggageInfoResp)bundle.getSerializable(UiMessageDef.BUNDLE_INQUIRY_EXCESS_BAGGAGE_INFO_RESP);
        }

        AppInfo   appInfo    = new AppInfo();
        TextView  tvEbAmount = (TextView)view.findViewById(R.id.tv_ebamount);
        CIWebView ciWebView = (CIWebView)view.findViewById(R.id.ciwebview);
        if(null != m_ccpageResp && !TextUtils.isEmpty(m_ccpageResp.url)) {
            ciWebView.loadUrl(m_ccpageResp.url);
        }
        tvEbAmount.setText(m_resp.ttl_currency + " " + appInfo.valueFormat(m_resp.ttl_amount));
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.ib_cc), 24, 24);
    }

    @Override
    protected void setOnParameterAndListener(View view) {
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
