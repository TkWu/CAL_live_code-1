package ci.function.MyTrips;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;

/**
 * Created by kevin on 2016/2/27.
 */
public class CIAskOtherPassengerFragment extends BaseFragment
    implements View.OnClickListener{

    private TextView m_tvUserName = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_add_passenger_ask_with_other_companions;
    }

    /**
     * 元件初始化，靜態取得元件實體、動態建製元件實體…等
     *
     * @param inflater
     * @param view
     */
    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_tvUserName = (TextView)view.findViewById(R.id.tv_user_name);
        String name = "Kevin Cheng" + ",";
        m_tvUserName.setText(name);
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
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_ask_with_companions_img),160,127);

    }

    /**
     * 通知父BaseActivity對子BaseFragment設定客製的「OnParameter(參數讀取)」跟「OnListener(
     * 動線通知)」介面
     *
     * @param view
     */
    @Override
    protected void setOnParameterAndListener(View view) {
        view.findViewById(R.id.btn_no).setOnClickListener(this);
        view.findViewById(R.id.btn_yes).setOnClickListener(this);
    }

    /**
     * 依需求調用以下函式
     *
     * @param fragmentManager
     * @see FragmentTransaction FragmentTransaction相關操作
     */
    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

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
    protected void removeOtherHandleMessage() {

    }

    /**
     * 當App語言變更後, 會呼叫此介面，藉此更新畫面UI,需要重新呼叫setText
     */
    @Override
    public void onLanguageChangeUpdateUI() {

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_yes:
                getActivity().setResult(UiMessageDef.RESULT_CODE_ADD_PASSENGER);
                getActivity().finish();
                break;
            case R.id.btn_no:
                getActivity().setResult(getActivity().RESULT_OK);
                getActivity().finish();
                break;

        }
    }

    /**
     * 轉換Activity
     * @param clazz 目標activity名稱
     * @param key   extra key
     * @param extra extra value
     *              
     */
    private void changeActivity(Class clazz,String key,String extra,boolean isFinish){
        Intent intent = new Intent();
        intent.putExtra(key, extra);
        intent.setClass(getActivity(), clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(true == isFinish){
            getActivity().finish();
        }
        startActivity(intent);

        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }
}
