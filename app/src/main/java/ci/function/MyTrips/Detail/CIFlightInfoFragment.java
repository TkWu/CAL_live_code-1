package ci.function.MyTrips.Detail;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.view.ImageHandle;

/**
 * Created by kevincheng on 2016/3/4.
 */
public class CIFlightInfoFragment extends BaseFragment
    implements View.OnClickListener{

    private RelativeLayout m_rlFlightStatus = null,
                           m_rlShare        = null;
    private ImageView      m_ivFlightStatic = null;
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_flight_tab_flight_status;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_rlFlightStatus = (RelativeLayout)view.findViewById(R.id.rl_flight_status_click);
        m_rlShare        = (RelativeLayout)view.findViewById(R.id.rl_share_click);
        m_ivFlightStatic = (ImageView)     view.findViewById(R.id.iv_flight_status);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_ic_share_b),24,24);
        m_ivFlightStatic
                .setImageBitmap(ImageHandle.getRoundedCornerBitmap(getActivity(),
                                                                   R.drawable.sample_flight_status,
                                                                   vScaleDef.getLayoutMinUnit(3)));
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_rlFlightStatus.setOnClickListener(this);
        m_rlShare.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.rl_share_click:
                shareMessage("這是一則透過華航App分享的訊息，請多多支持，謝謝！");
                break;
        }
    }

    private void shareMessage(String msg){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
