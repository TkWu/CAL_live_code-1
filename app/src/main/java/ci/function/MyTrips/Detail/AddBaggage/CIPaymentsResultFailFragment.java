package ci.function.MyTrips.Detail.AddBaggage;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;

/**
 * Created by kevincheng on 2017/9/22.
 * 付款結果失敗頁
 */

public class CIPaymentsResultFailFragment extends BaseFragment{

    public static CIPaymentsResultFailFragment newInstance(String msg) {

        Bundle args = new Bundle();
        args.putSerializable(UiMessageDef.BUNDLE_FRAGMENT_DATA, msg);
        CIPaymentsResultFailFragment fragment = new CIPaymentsResultFailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_payments_fail_result_card;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        Bundle bundle = getArguments();
        String msg = "";
        if(null != bundle) {
            msg = bundle.getString(UiMessageDef.BUNDLE_FRAGMENT_DATA);
        }
        TextView tvMsg = (TextView)view.findViewById(R.id.tv_msg);
        tvMsg.setText(msg);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_fail), 48, 48);
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
