package ci.function.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.function.Main.CIMainActivity;
import ci.ui.define.ViewScaleDef;

/**忘記卡號.忘記密碼.申請密碼及取消報到頁面 按下送出後所顯示的成功畫面
 * zeplin: 3.5-2
 * wireframe: p.24
 * Created by jlchen on 2016/2/19.
 */
public class CIForgetSuccessFragment extends BaseFragment
        implements View.OnClickListener {

    public interface onForgetSuccessParameter {
        /**要顯示的成功訊息*/
        String GetForgetSuccessMsg();
        /**按鈕的text*/
        String GetButtonText();
    }

    private onForgetSuccessParameter    m_onParameter   = null;

    private ImageView                   m_ivSuccess     = null;

    private LinearLayout                m_llTextMsg     = null;
    private TextView                    m_tvMsg         = null;

    private RelativeLayout              m_rlOk          = null;
    private Button                      m_btnOk         = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_forget_success;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_ivSuccess = (ImageView) view.findViewById(R.id.iv_success);

        m_llTextMsg = (LinearLayout) view.findViewById(R.id.ll_textview);
        m_tvMsg     = (TextView) view.findViewById(R.id.tv_msg);

        m_rlOk      = (RelativeLayout) view.findViewById(R.id.rl_ok);
        m_btnOk     = (Button) view.findViewById(R.id.btn_ok);
        m_btnOk.setOnClickListener(this);

        if ( null != m_onParameter ){
            if ( null != m_onParameter.GetForgetSuccessMsg() )
                m_tvMsg.setText(m_onParameter.GetForgetSuccessMsg());

            if ( null != m_onParameter.GetButtonText() ){
                m_btnOk.setTag(m_onParameter.GetButtonText());
                m_btnOk.setText(m_onParameter.GetButtonText());
            } else {
                m_btnOk.setTag("Go Home!");
            }
        }
    }


    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        //圖片 上間隔51.3, 寬高82.7
        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) m_ivSuccess.getLayoutParams();
        rParams.topMargin   = vScaleDef.getLayoutHeight(51.3);
        rParams.width       = vScaleDef.getLayoutMinUnit(82.7);
        rParams.height      = vScaleDef.getLayoutMinUnit(82.7);

        //說明文字欄 上間隔30, 左右間隔30
        rParams = (RelativeLayout.LayoutParams) m_llTextMsg.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(30);
        rParams.leftMargin = vScaleDef.getLayoutWidth(30);
        rParams.rightMargin = vScaleDef.getLayoutWidth(30);

        //說明文字 字體大小16, 行高 22.7(扣掉文字大小為6.7)
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_tvMsg);
        m_tvMsg.setLineSpacing(vScaleDef.getLayoutHeight(6.7), 1.0f);

        //按鈕置底 下間隔30
        rParams = (RelativeLayout.LayoutParams) m_rlOk.getLayoutParams();
        rParams.bottomMargin = vScaleDef.getLayoutHeight(30);

        //按鈕大小 寬320, 高40
        //按鈕文字 大小16
        rParams = (RelativeLayout.LayoutParams) m_btnOk.getLayoutParams();
        rParams.width = vScaleDef.getLayoutWidth(320);
        rParams.height = vScaleDef.getLayoutHeight(40);
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_btnOk);
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                if (m_btnOk.getTag().equals(getString(R.string.back_to_trip_detail))){
                    //回到行程資訊頁面 需告知上一頁結果為ok 才能刷新乘客資訊
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }else {
                    //回到首頁
                    Intent intent = new Intent(getActivity(), CIMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                    getActivity().finish();
                }
                break;
        }
    }

    public void uiSetParameterListener(onForgetSuccessParameter onParameter) {
        m_onParameter = onParameter;
    }
}
