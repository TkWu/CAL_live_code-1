package ci.function.MyTrips;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

/**
 * Created by Kevincheng on 2016/4/22.
 */
public class CIFindMyBookingNotesActivity extends BaseActivity implements View.OnClickListener{

    //start 644336 2019 2月3月 AI/行事曆/截圖/注意事項
    private String from_where; //1. 首頁查詢行程 2. 右側預辦登機
    //end 644336 2019 2月3月 AI/行事曆/截圖/注意事項

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            //start 644336 2019 2月3月 AI/行事曆/截圖/注意事項
            if (from_where.equals("1")) {
                return m_Context.getString(R.string.find_my_booking_notes);
            }
            else{
                return m_Context.getString(R.string.menu_title_check_in);
            }
            //end 644336 2019 2月3月 AI/行事曆/截圖/注意事項
        }

    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

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

        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);

    }

    public NavigationBar 	m_Navigationbar		= null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_find_my_booking_notes;
    }

    @Override
    protected void initialLayoutComponent() {
        m_Navigationbar		= (NavigationBar) findViewById(R.id.toolbar);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        int iLineSpacing = vScaleDef.getLayoutHeight(3);
        ((TextView)findViewById(R.id.tv_content)).setLineSpacing(iLineSpacing,1);

        //start 644336 2019 2月3月 AI/行事曆/截圖/注意事項
        if (from_where.equals("1")) {
            findViewById(R.id.rlayout_checkin_notes).setVisibility(View.GONE);
            findViewById(R.id.rlayout_home_notes).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.rlayout_checkin_notes).setVisibility(View.VISIBLE);
            findViewById(R.id.rlayout_home_notes).setVisibility(View.GONE );
        }
        //end 644336 2019 2月3月 AI/行事曆/截圖/注意事項
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    //start 644336 2019 2月3月 AI/行事曆/截圖/注意事項
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        from_where = getIntent().getStringExtra(UiMessageDef.BUNDLE_CHKIN_FINDMYBOOKING_NOTES);
        super.onCreate(savedInstanceState);
        //Log.e("from_where","from_where: "+from_where);
    }
    //end 644336 2019 2月3月 AI/行事曆/截圖/注意事項

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}


    @Override
    protected void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View v) {}

}
