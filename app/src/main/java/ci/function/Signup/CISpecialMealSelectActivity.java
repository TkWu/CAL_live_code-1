package ci.function.Signup;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

import static ci.ui.view.NavigationBar.*;

/**
 * Created by kevincheng on 2016/2/19.
 */
public class CISpecialMealSelectActivity extends BaseActivity
        implements View.OnClickListener{

    private   NavigationBar     m_Navigationbar             = null;
    public final static String  STRING_MEAL                 = "STRING_MEAL";
    private   String            m_strMealValue              = null;

    private onNavigationbarParameter m_onNavigationParameter = new onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.special_meal_preference);
        }
    };

    private onNavigationbarListener m_onNavigationbarListener = new onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    @Override
    public void onBackPressed() {
        if(null != m_strMealValue){
            Intent intent = new Intent();
            intent.putExtra(STRING_MEAL, m_strMealValue);
            setResult(RESULT_OK, intent);
        }
        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_sign_up_special_meal_select;
    }

    @Override
    protected void initialLayoutComponent() {
        m_Navigationbar = (NavigationBar)findViewById(R.id.toolbar);
        initImageView();
    }

    @Override
    public void onClick(View v) {
        initImageView();
        adjustAllViewVisible(v);
        m_strMealValue = ((TextView)((ViewGroup)v).getChildAt(1)).getText().toString();
    }

    private void adjustAllViewVisible(View v){
        if(true == isNormalViewGroup(v)){
            for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                adjustAllViewVisible(((ViewGroup) v).getChildAt(i));
            }
        }
        v.setVisibility(View.VISIBLE);
    }

    private boolean isNormalViewGroup(View rootView){
        if (rootView instanceof ViewGroup&& false == rootView instanceof ListView && false == rootView instanceof Spinner && false == rootView instanceof RecyclerView) {
            return true;
        }
        return false;
    }

    private void initImageView(){
        findViewById(R.id.iv_normal_meal).setVisibility(View.INVISIBLE);
        findViewById(R.id.iv_Vegetarian_Oriental_Meal).setVisibility(View.INVISIBLE);
        findViewById(R.id.iv_Vegetarian_Oriental_Meal2).setVisibility(View.INVISIBLE);
        findViewById(R.id.iv_Vegetarian_Lacto_Ovo_Meal).setVisibility(View.INVISIBLE);
        findViewById(R.id.iv_Vegetarian_Vegan_Meal).setVisibility(View.INVISIBLE);

        findViewById(R.id.v_nomal_meal).setVisibility(View.INVISIBLE);
        findViewById(R.id.v_Vegetarian_Oriental_Meal).setVisibility(View.INVISIBLE);
        findViewById(R.id.v_Vegetarian_Oriental_Meal2).setVisibility(View.INVISIBLE);
        findViewById(R.id.v_Vegetarian_Lacto_Ovo_Meal).setVisibility(View.INVISIBLE);
        findViewById(R.id.v_Vegetarian_Vegan_Meal).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        float iLineSpacingExtra = vScaleDef.getLayoutHeight(2);
        ((TextView)findViewById(R.id.tv_Vegetarian_Oriental_Meal_mutil_line)).setLineSpacing(iLineSpacingExtra,1);
        ((TextView)findViewById(R.id.tv_Vegetarian_Oriental_Meal2_mutil_line)).setLineSpacing(iLineSpacingExtra,1);
        ((TextView)findViewById(R.id.tv_Vegetarian_Lacto_Ovo_Meal_mutil_line)).setLineSpacing(iLineSpacingExtra,1);
        ((TextView)findViewById(R.id.tv_Vegetarian_Vegan_Meal_mutil_line)).setLineSpacing(iLineSpacingExtra,1);
    }

    @Override
    protected void setOnParameterAndListener() {
        findViewById(R.id.rl_nomal_meal).setOnClickListener(this);
        findViewById(R.id.rl_Vegetarian_Oriental_Meal).setOnClickListener(this);
        findViewById(R.id.rl_Vegetarian_Oriental_Meal2).setOnClickListener(this);
        findViewById(R.id.rl_Vegetarian_Lacto_Ovo_Meal).setOnClickListener(this);
        findViewById(R.id.rl_Vegetarian_Vegan_Meal).setOnClickListener(this);
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    protected void onLanguageChangeUpdateUI() {}

}
