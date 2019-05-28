package ci.function.Setting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Start.CIStartActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.AppInfo;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.object.item.CIHomeStatusEntity;
import ci.ui.object.item.LocaleItem;

/**
 * Created by Ryan
 * 設定頁面
 */
public class CISettingFragment extends BaseFragment{

    private RelativeLayout  m_rlayout_Language  = null;
    private TextView        m_tvLanguageTitle   = null;
    private TextView        m_tvLanguage        = null;

    private RelativeLayout  m_rlayout_notification  = null;
    private Switch          m_swNotifly         = null;
    private TextView        m_tvSwitch_title    = null;
    private TextView        m_tvSwitch          = null;
    public TextView         m_tvVersion         = null;
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_rlayout_Language = (RelativeLayout)view.findViewById(R.id.rlayout_language);
        m_rlayout_Language.setOnClickListener(m_onclick);
        m_tvLanguageTitle = (TextView)view.findViewById(R.id.tv_language_title);
        m_tvLanguage = (TextView)view.findViewById(R.id.tv_language);
        m_tvVersion = (TextView)view.findViewById(R.id.tv_version);
        m_rlayout_notification = (RelativeLayout)view.findViewById(R.id.rlayout_notification);
        m_tvSwitch_title  = (TextView)view.findViewById(R.id.tv_notification_title);
        m_tvSwitch  = (TextView)view.findViewById(R.id.tv_notification);
        m_swNotifly = (Switch)view.findViewById(R.id.switch1);
        m_swNotifly.setOnCheckedChangeListener(m_onCheckChangeListener);
        m_tvVersion.setText(CIApplication.getVersionName());
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        initialUI();

        //設定UI
        m_rlayout_Language.getLayoutParams().height = vScaleDef.getLayoutHeight(70);
        m_rlayout_notification.getLayoutParams().height = vScaleDef.getLayoutHeight(70);

        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, m_tvLanguageTitle);
        vScaleDef.setTextSize(13, m_tvLanguage);
        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, m_tvSwitch_title);
        vScaleDef.setTextSize(13, m_tvSwitch);

        LinearLayout llayout_language = (LinearLayout)view.findViewById(R.id.llayout_notify);
        ((RelativeLayout.LayoutParams)llayout_language.getLayoutParams()).leftMargin = vScaleDef.getLayoutWidth(30);

        ImageView img_arrow = (ImageView)view.findViewById(R.id.img_arrow);
        ((RelativeLayout.LayoutParams)img_arrow.getLayoutParams()).rightMargin = vScaleDef.getLayoutWidth(20);

        LinearLayout llayout_notifly = (LinearLayout)view.findViewById(R.id.llayout_notify);
        ((RelativeLayout.LayoutParams)llayout_notifly.getLayoutParams()).leftMargin = vScaleDef.getLayoutWidth(30);

        ((RelativeLayout.LayoutParams)m_swNotifly.getLayoutParams()).rightMargin = vScaleDef.getLayoutWidth(16.3);
    }

    public void initialUI(){

        m_tvLanguageTitle.setText(R.string.language_setting);
        //取得App設定的語系
        LocaleItem localeItem = CIApplication.getLanguageInfo().getLanguage();
        m_tvLanguage.setText( localeItem.strDisplayName );

        m_tvSwitch_title.setText(R.string.flight_notification);
        //取的推播通知的設定
        Boolean bSwitch = AppInfo.getInstance(getActivity()).GetAppNotiflySwitch();
        if ( bSwitch ){
            m_tvSwitch.setText(R.string.on);
        } else {
            m_tvSwitch.setText(R.string.off);
        }
        m_swNotifly.setChecked(bSwitch);
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
    public void onLanguageChangeUpdateUI() {

        CIAlertDialog dialog = new CIAlertDialog(getActivity(),
                                                 new CIAlertDialog.OnAlertMsgDialogListener() {
                                                     @Override
                                                     public void onAlertMsgDialog_Confirm() {
                                                         restartApp();
                                                     }

                                                     @Override
                                                     public void onAlertMsgDialogg_Cancel() {

                                                     }
                                                 });
        dialog.uiSetTitleText(getString(R.string.warning));
        dialog.uiSetContentText("應用程式即將重新啟動\nApplication will restart automatically.");
        dialog.uiSetConfirmText(getString(R.string.confirm));
        dialog.show();

    }

    private void restartApp(){
        Intent intent = new Intent(getActivity(), CIStartActivity.class);
        PendingIntent restartIntent = PendingIntent.getActivity(
                getActivity(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        // 1秒鐘後重新啟用
        AlarmManager mgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis(),
                restartIntent);
        //退出程序
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getActivity().finishAndRemoveTask();
        } else {
            getActivity().finish();
        }
        System.exit(1);
    }

    View.OnClickListener m_onclick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            if ( v.getId() == m_rlayout_Language.getId() ){

                Intent intent = new Intent();
                intent.setClass( getActivity(), CILanguageSettingActivity.class );
                getActivity().startActivityForResult(intent, UiMessageDef.RESULT_CODE_LANGUAGE_SETTING);
                getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
            }
        }
    };

    CompoundButton.OnCheckedChangeListener m_onCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if ( buttonView.getId() == m_swNotifly.getId() ){

                if ( isChecked ){
                    m_tvSwitch.setText(R.string.on);
                } else {
                    m_tvSwitch.setText(R.string.off);
                }
                AppInfo.getInstance(getActivity()).SetAppNotiflySwitch(isChecked);

                //變更推播通知開關,要重送推播主機需要的資料
                CIHomeStatusEntity homeStatusEntity = CIPNRStatusManager.getInstanceWithoutSetListener().getHomeStatusFromMemory();
                if ( null == homeStatusEntity ) {
                    CIApplication.getFCMManager().UpdateDeviceToCIServer(null);
                } else {
                    CIApplication.getFCMManager().UpdateDeviceToCIServer(homeStatusEntity.AllItineraryInfo);
                }
            }
        }
    };
}
