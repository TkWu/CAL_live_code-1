package ci.ui.TextField;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.TimeTable.CISelectDepartureAirpotActivity;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.define.ViewScaleDef;

/**
 * Created by flowmahuang on 2016/3/15.
 */
public class CIChooseAirportTextFieldFragment extends CITextFieldFragment {

    public interface OnCIChooseAirportTextFragmentClick{
        boolean isOpenSelectPage();
        String getFromIAIT();
    }

    private OnCIChooseAirportTextFragmentClick onCIChooseAirportTextFragmentClick;

    private static final int    FROM_TIME_TABLE = 100;

    //自定義hint所需參數的tag
    private static final String A_TEXT_HINT     = "A_TEXT_HINT",
                                A_IMG_HINT      = "A_IMG_HINT",
                                A_MIN_IMG_HINT  = "A_MIN_IMG_HINT";

    private int                 m_iAHint        = -1;
    private int                 m_iAHintS       = -1;
    private String              m_strAHint      = "";

    //自定義hint是否已縮小過, true表示已縮小 false表示已放大
    private boolean             m_bAnim         = false;

    //自定義hint
    private RelativeLayout      m_rlHint        = null;
    private ImageView           m_ivHint        = null;
    private TextView            m_tvHint        = null;

    private ViewScaleDef        m_vScaleDef     = null;

    private String iait = "";

    //自定義hint, 需帶入參數 hint文字, icon大圖res, icon小圖res
    public static CIChooseAirportTextFieldFragment newInstance(String strHint,
                                                               int iHint,
                                                               int iHintS,
                                                               boolean  bIsToField,
                                                               int  iSource) {
        Bundle bundle = new Bundle();
        bundle.putString(A_TEXT_HINT, strHint);
        bundle.putInt(A_IMG_HINT, iHint);
        bundle.putInt(A_MIN_IMG_HINT, iHintS);
        bundle.putInt(CISelectDepartureAirpotActivity.ESOURCE, iSource);
        bundle.putBoolean(CISelectDepartureAirpotActivity.IS_TO_FRAGMENT, bIsToField);
        bundle.putString(TYPE_MODE, TypeMode.MENU_FULL_PAGE.name());
        CIChooseAirportTextFieldFragment fragment = new CIChooseAirportTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CIChooseAirportTextFieldFragment newInstance(String strHint) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, strHint);
        bundle.putString(TYPE_MODE, TypeMode.MENU_FULL_PAGE.name());
        CIChooseAirportTextFieldFragment fragment = new CIChooseAirportTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (null != bundle) {
            //hint文字前的icon(大)
            m_iAHint = bundle.getInt(A_IMG_HINT, -1);
            //hint文字前的icon(小)
            m_iAHintS = bundle.getInt(A_MIN_IMG_HINT, -1);
            //hint文字
            m_strAHint = bundle.getString(A_TEXT_HINT, null);
        }

        m_vScaleDef = ViewScaleDef.getInstance(getActivity());
    }

    /**設定View元件*/
    @Override
    protected void setupViewComponents(View rootview) {
        super.setupViewComponents(rootview);

        m_rlHint = (RelativeLayout) rootview.findViewById(R.id.rl_hint);
        m_ivHint = (ImageView) rootview.findViewById(R.id.iv);
        m_tvHint = (TextView) rootview.findViewById(R.id.tv);

        if ( -1 != m_iAHint ){
            m_rlHint.setVisibility(View.VISIBLE);
            m_ivHint.setImageResource(m_iAHint);
            m_tvHint.setText(m_strAHint);
        }else {
            m_rlHint.setVisibility(View.GONE);
        }
    }

    /**設定自適應*/
    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        super.setTextSizeAndLayoutParams(view, vScaleDef);

        vScaleDef.setMargins(m_rlHint, 10, 0, 10, 0);
        m_rlHint.getLayoutParams().width = vScaleDef.getLayoutWidth(224);

        vScaleDef.setMargins(m_ivHint, 0, 18.7, 0, 0);
        vScaleDef.selfAdjustSameScaleView(m_ivHint, 24, 24);

        vScaleDef.setMargins(m_tvHint, 10, 20.3, 0, 0);
        m_tvHint.getLayoutParams().height = vScaleDef.getLayoutHeight(21.7);
        vScaleDef.setTextSize(18, m_tvHint);
    }

    @Override
    public void onResume() {
        super.onResume();
        setDropDownListener(listener);
    }

    dropDownListener listener = new dropDownListener() {
        @Override
        public void onDropDown(TypeMode mode, View v, String tag) {
            if (onCIChooseAirportTextFragmentClick != null) {

                //給實作的人傳遞數值
                boolean isChangActivity = onCIChooseAirportTextFragmentClick.isOpenSelectPage();
                getArguments().putString(CISelectDepartureAirpotActivity.IAIT, onCIChooseAirportTextFragmentClick.getFromIAIT());
                //實作的人決定是否換畫面
                if (isChangActivity){
                    fullPageMenu();
                }
            }else{
                //沒有實作
                fullPageMenu();
            }
        }
    };

    public void fullPageMenu() {
        changeActivity(CISelectDepartureAirpotActivity.class);
    }

    private void changeActivity(Class clazz) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);

        Bundle bundle = this.getArguments();

        intent.putExtras(bundle);

        startActivityForResult(intent, FROM_TIME_TABLE);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);

    }

    @Override
    public void setText(String text) {
        if ( 0 == text.length() ){
            if ( true == m_bAnim ){
                //當前自定義hint為縮小狀態, 需執行放大動畫
                RunAnimation(m_bAnim);
            }
        }else {
            if ( false == m_bAnim ){
                //當前自定義hint為放大狀態, 需執行縮小動畫
                RunAnimation(m_bAnim);
            }
        }
        super.setText(text);
    }

    private void RunAnimation(final boolean bAnim){

        if ( -1 != m_iAHint ){
            m_ivHint.setImageResource(m_iAHint);
        }

        new CountDownTimer(0, 100){

            @Override
            public void onTick(long millisUntilFinished) {

                if ( false == bAnim ){
                    //縮小動畫
                    RunScaleAnimation(1.0f, 0.67f, 1.0f, 0.67f);
                }else {
                    //往下移動的動畫
                    RunTranslateAnimation(0, 0, -m_vScaleDef.getLayoutHeight(20), 0);
                }
            }

            @Override
            public void onFinish() {

                if ( false == bAnim ){
                    //縮小自適應
                    m_vScaleDef.selfAdjustSameScaleView(m_ivHint, 16, 16);
                    RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) m_tvHint.getLayoutParams();
                    rp.leftMargin   = m_vScaleDef.getLayoutWidth(4);
                    rp.height       = m_vScaleDef.getLayoutHeight(16);
                    m_vScaleDef.setTextSize(13, m_tvHint);

                    //改放小圖icon
                    if ( -1 != m_iAHintS ){
                        m_ivHint.setImageResource(m_iAHintS);
                    }

                    //往上移動的動畫
                    RunTranslateAnimation(0, 0, 0, -m_vScaleDef.getLayoutHeight(20));

                    m_bAnim = true;
                }else {
                    //放大動畫
                    RunScaleAnimation(0.67f, 1.0f, 0.67f, 1.0f);

                    //放大後的自適應
                    m_vScaleDef.setMargins(m_ivHint, 0, 18.7, 0, 0);
                    m_vScaleDef.selfAdjustSameScaleView(m_ivHint, 24, 24);
                    RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) m_tvHint.getLayoutParams();
                    rp.topMargin    = m_vScaleDef.getLayoutHeight(20.3);
                    rp.leftMargin   = m_vScaleDef.getLayoutWidth(10);
                    rp.height       = m_vScaleDef.getLayoutHeight(21.7);
                    m_vScaleDef.setTextSize(18, m_tvHint);

                    m_bAnim = false;
                }
            }
        }.start();
    }

    //放大縮小動畫
    private void RunScaleAnimation(float fromX, float toX, float fromY, float toY){
        //fromX 原尺寸倍率寬 ex: 1.0f
        //toX   新尺寸倍率寬 ex: 0.67f
        //fromY 原尺寸倍率高 ex: 1.0f
        //toY   新尺寸倍率高 ex: 0.67f
        //帶入以上參數 結果會縮小成原視圖的67%大小
        Animation amS = new ScaleAnimation(fromX, toX, fromY, toY);
        amS.setDuration(100);
        amS.setFillAfter(true);
        m_rlHint.startAnimation(amS);
    }

    //位移動畫
    private void RunTranslateAnimation(float fromX, float toX, float fromY, float toY){
        //fromX 原相對X座標位置 ex: 0
        //toX   新相對X座標位置 ex: 20
        //fromY 原相對Y座標位置 ex: 0
        //toY   新相對Y座標位置 ex: 20
        //帶入以上參數 結果會往右下移動
        Animation amT = new TranslateAnimation(fromX, toX, fromY, toY);
        amT.setDuration(100);
        amT.setFillAfter(true);
        m_rlHint.startAnimation(amT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FROM_TIME_TABLE && resultCode == getActivity().RESULT_OK) {
            if ( false == m_bAnim ){
                //當前自定義hint為放大狀態, 需執行縮小動畫
                RunAnimation(m_bAnim);
            }

            iait = data.getStringExtra(CISelectDepartureAirpotActivity.IAIT);

            m_editText.setText(data.getStringExtra(CISelectDepartureAirpotActivity.LOCALIZATION_NAME) + "(" + iait + ")");
        }
    }

    public String getIAIT(){
        return iait;
    }

    public void setEditText(String string){
        this.setText(string);
    }

    public void setIAIT(String iata){
        this.iait = iata;
    }

    public void setOnCIChooseAirportTextFragmentClick(OnCIChooseAirportTextFragmentClick onCIChooseAirportTextFragmentClick) {
        this.onCIChooseAirportTextFragmentClick = onCIChooseAirportTextFragmentClick;
    }
}
