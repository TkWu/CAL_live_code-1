package ci.function.MealSelection;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

/**
 * Created by isa on 2016/5/16.
 */
public class CIMealSelectionNoticeActivity extends BaseActivity {
    private TextView content = null;
    private NavigationBar toorBar = null;
    private String strMealteams;

    public  final static  String MEALTERM = "strMealteams";
    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.meal_selection_notice);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {
        }

        @Override
        public void onLeftMenuClick() {
        }

        @Override
        public void onBackClick() {
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {

        }

        @Override
        public void onDemoModeClick() {
        }
    };

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_meal_selection_notice;
    }

    @Override
    protected void initialLayoutComponent() {
        content = (TextView) findViewById(R.id.notice_content);
        toorBar = (NavigationBar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        strMealteams = intent.getStringExtra(MEALTERM);
        if (strMealteams != null) {
            content.setText(strMealteams);
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        toorBar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
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
    protected void onLanguageChangeUpdateUI() {

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }
}
