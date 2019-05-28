package ci.function.Core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.FragmentManager;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.function.Start.CIStartActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;

/**
 * Created by kevincheng on 2016/7/6.
 */
public class CIExceptionActivity extends BaseActivity {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_exception;
    }

    @Override
    protected void initialLayoutComponent() {
        //使用Dialog來顯示異常信息
        CIAlertDialog dialog = new CIAlertDialog(this, new CIAlertDialog.OnAlertMsgDialogListener() {
            @Override
            public void onAlertMsgDialog_Confirm() {
                restartApp();
            }

            @Override
            public void onAlertMsgDialogg_Cancel() {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    finishAndRemoveTask();
                } else {
                    finish();
                }
                System.exit(1);
            }
        });
        dialog.uiSetTitleText(getString(R.string.warning));
        dialog.uiSetConfirmText(getString(R.string.confirm));
        dialog.uiSetCancelText(getString(R.string.cancel));
        dialog.uiSetContentText(getString(R.string.out_of_memory_error));
        dialog.show();
    }

    private void restartApp(){
        Intent intent = new Intent(getApplicationContext(), CIStartActivity.class);
        PendingIntent restartIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // 1秒鐘後重新啟用
        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                restartIntent);
        //退出程序
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            finishAndRemoveTask();
        } else {
            finish();
        }
        System.exit(1);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

    }

    @Override
    protected void setOnParameterAndListener() {

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
}
