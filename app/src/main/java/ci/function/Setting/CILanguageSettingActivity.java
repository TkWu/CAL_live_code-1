package ci.function.Setting;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.object.item.CIHomeStatusEntity;
import ci.ui.object.item.LocaleItem;
import ci.ui.view.NavigationBar;

public class CILanguageSettingActivity extends BaseActivity {

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.language_setting);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            CILanguageSettingActivity.this.onBackPressed();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    private NavigationBar.onNavigationbarInterface m_onNavigationbarInterface = null;

    private ListView        m_listview         = null;
    private LanguageAdpter  m_LanguageAdpter   = null;

    private int             m_iSelect          = 0;
    private ArrayList<LocaleItem> m_arLanguageList = null;

    private Locale          m_OrgLocal          = null;

    public NavigationBar m_Navigationbar = null;

    @Override
    protected int getLayoutResourceId() { return R.layout.activity_languagesetting; }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);

        m_listview = (ListView)findViewById(R.id.listView);
        m_arLanguageList = CIApplication.getLanguageInfo().getLanguageList();
        m_LanguageAdpter = new LanguageAdpter( this );
        m_listview.setAdapter(m_LanguageAdpter);
        m_listview.setOnItemClickListener(m_onItemClick);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        LocaleItem localeItem = CIApplication.getLanguageInfo().getLanguage();
        int iSize = m_arLanguageList.size();
        for( int iIdx =0; iIdx < iSize; iIdx++ ){
            if ( m_arLanguageList.get(iIdx).strTag.equals(localeItem.strTag) ){
                m_iSelect = iIdx;
                break;
            }
        }

        m_OrgLocal = localeItem.locale;
        m_LanguageAdpter.notifyDataSetChanged();
    }

    @Override
    protected void setOnParameterAndListener() {
        m_onNavigationbarInterface = m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
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

    @Override
    public void onBackPressed() {

        LocaleItem localeItem = CIApplication.getLanguageInfo().getLanguage();
        if ( m_OrgLocal.equals( localeItem.locale ) ){
            setResult(RESULT_OK);

            CILanguageSettingActivity.this.finish();
            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
        } else {

            setResult(UiMessageDef.RESULT_CODE_LANGUAGE_CHANGE);

            showProgressDialog();

            //語系變更後, 要重送推播主機需要的資料
            CIHomeStatusEntity homeStatusEntity = CIPNRStatusManager.getInstanceWithoutSetListener().getHomeStatusFromMemory();
            if ( null == homeStatusEntity ) {
                CIApplication.getFCMManager().UpdateDeviceToCIServer(null);
            } else {
                CIApplication.getFCMManager().UpdateDeviceToCIServer(homeStatusEntity.AllItineraryInfo);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideProgressDialog();
                    CILanguageSettingActivity.this.finish();
                    overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
                }
            },1000);
        }

    }


    AdapterView.OnItemClickListener m_onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if ( m_iSelect < 0 && m_iSelect > m_arLanguageList.size() ){
                return;
            }

            m_iSelect = position;

            LocaleItem localeItem = m_arLanguageList.get(m_iSelect);
            CIApplication.getLanguageInfo().setLanguage(localeItem.locale);

            m_LanguageAdpter.notifyDataSetChanged();

            //重新設定語系
            m_onNavigationbarInterface.changeLanguageText(getString(R.string.language_setting));
        }
    };

    class ItemHolder{

        RelativeLayout  rlayout_bg;
        TextView        tvText;
        ImageView       imgDone;
    };

    class LanguageAdpter extends BaseAdapter{

        private Context  m_context      = null;

        LanguageAdpter( Context context){
            m_context     = context;
        }

        @Override
        public int getCount() {
            if ( null != m_arLanguageList ){
                return m_arLanguageList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if ( null != m_arLanguageList ){
                return m_arLanguageList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ItemHolder itemHolder = null;
            if ( null == convertView ){
                itemHolder = new ItemHolder();
                convertView = LayoutInflater.from(m_context).inflate( R.layout.layout_language_listview_child_view, null);
                itemHolder.rlayout_bg   = (RelativeLayout)convertView.findViewById(R.id.rlayout_bg);
                itemHolder.imgDone      = (ImageView)convertView.findViewById(R.id.img_done);
                itemHolder.tvText       = (TextView)convertView.findViewById(R.id.tvText);

                ViewScaleDef.getInstance(m_context).setTextSize(16, itemHolder.tvText);
                int ileftMargin = ViewScaleDef.getInstance(m_context).getLayoutWidth(30);
                ((RelativeLayout.LayoutParams)itemHolder.tvText.getLayoutParams()).leftMargin = ileftMargin;

                int iWidth = ViewScaleDef.getInstance(m_context).getLayoutWidth(24);
                itemHolder.imgDone.getLayoutParams().height = iWidth;
                itemHolder.imgDone.getLayoutParams().width = iWidth;
                int irightMargin = ViewScaleDef.getInstance(m_context).getLayoutWidth(20);
                ((RelativeLayout.LayoutParams)itemHolder.imgDone.getLayoutParams()).rightMargin = irightMargin;

                int iHeight = ViewScaleDef.getInstance(m_context).getLayoutWidth(60);
                itemHolder.rlayout_bg.getLayoutParams().height = iHeight;

                convertView.setBackgroundResource(R.drawable.bg_transparent_press_black20);

                convertView.setTag(itemHolder);
            } else {
                itemHolder = (ItemHolder)convertView.getTag();
            }

            itemHolder.tvText.setText(m_arLanguageList.get(position).strDisplayName);

            if ( m_iSelect == position ){
                itemHolder.imgDone.setVisibility(View.VISIBLE);
                itemHolder.rlayout_bg.setSelected(true);
            } else {
                itemHolder.imgDone.setVisibility(View.INVISIBLE);
                itemHolder.rlayout_bg.setSelected(false);
            }

            return convertView;
        }
    };
}
