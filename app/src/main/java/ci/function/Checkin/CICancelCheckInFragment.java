package ci.function.Checkin;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.ui.define.ViewScaleDef;
import ci.ws.Models.entities.CICancelCheckInReq;

import static ci.function.Checkin.CISelectPassengersFragment.SelectPassengersType;

/**
 * 勾選要取消報到的乘客頁面
 * Created by jlchen on 2016/4/1.
 */
public class CICancelCheckInFragment extends BaseFragment{

    public interface onCancelCheckInParameter{
        //取得乘客資料
        CICancelCheckInReq getCancelCheckInData();
    }

    public interface onCancelCheckInListener{
        //按下完成按鈕
        void onDoneClick(ArrayList<Integer> alSelectedPosition);
    }

    private onCancelCheckInParameter m_Parameter;
    private onCancelCheckInListener m_Listner;

    private FrameLayout             m_flContent = null;
    private TextView                m_tvTitle   = null;
    private Button                  m_btnDone   = null;
    private CISelectPassengersFragment m_SelectPassengersFragment = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_cancel_check_in;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        CICancelCheckInReq req = null;
        if ( null != m_Parameter )
            req = m_Parameter.getCancelCheckInData();

        m_SelectPassengersFragment =
                CISelectPassengersFragment.newInstance(SelectPassengersType.CANCEL_CHECK_IN, req);

        m_flContent = (FrameLayout) view.findViewById(R.id.fl_list);
        m_tvTitle   = (TextView) view.findViewById(R.id.tv_title);
        m_btnDone   = (Button) view.findViewById(R.id.btn_done);
        m_btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( null != m_SelectPassengersFragment ){
                    if ( true == m_SelectPassengersFragment.isPassengerSelected() ){
                        if ( null != m_Listner ){
                            m_Listner.onDoneClick(m_SelectPassengersFragment.getSelectCancelCheckInList());
                        }
                    }else {
                        showDialog(getString(R.string.warning),
                                getString(R.string.please_select_passenger),
                                getString(R.string.confirm));
                    }
                }
            }
        });
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        vScaleDef.setMargins(m_tvTitle, 30, 30, 30, 20);
        vScaleDef.setTextSize(20, m_tvTitle);

        //按鈕置底 下間隔30
        RelativeLayout.LayoutParams rParams =
                (RelativeLayout.LayoutParams) view.findViewById(R.id.rl_done).getLayoutParams();
        rParams.bottomMargin = vScaleDef.getLayoutHeight(30);

        //按鈕大小 寬320, 高40
        //按鈕文字 大小16
        rParams = (RelativeLayout.LayoutParams) m_btnDone.getLayoutParams();
        rParams.width = vScaleDef.getLayoutWidth(320);
        rParams.height = vScaleDef.getLayoutHeight(40);
        vScaleDef.setTextSize( vScaleDef.DEF_TEXT_SIZE_16, m_btnDone);
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction .replace(m_flContent.getId(), m_SelectPassengersFragment)
                    .commitAllowingStateLoss();
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    public void uiSetParameterListener(onCancelCheckInParameter onParameter,
                                       onCancelCheckInListener onListenr) {
        m_Parameter = onParameter;
        m_Listner = onListenr;
    }
}
