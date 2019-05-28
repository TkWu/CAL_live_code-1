package ci.ui.object;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.ui.dialog.CIAlertDialog;

/**
 * Created by kevincheng on 2016/7/12.
 */
public class CIDialPhoneNumberManager {
    private CIAlertDialog                         m_dialog = null;
    private Context                               m_context = null;
    public void showAlertDialogForDialPhoneNumber(final Context context, final String phoneNumber){
        m_context = context;
        if(null != m_dialog && true == m_dialog.isShowing()){
            m_dialog.dismiss();
            return;
        }
        m_dialog = new CIAlertDialog(m_context, new CIAlertDialog.OnAlertMsgDialogListener() {
            @Override
            public void onAlertMsgDialog_Confirm() {
                dialPhoneNumber(phoneNumber);
            }

            @Override
            public void onAlertMsgDialogg_Cancel() {

            }
        });
        m_dialog.uiSetTitleText(m_context.getString(R.string.international_call));
        m_dialog.uiSetContentText(m_context.getString(R.string.international_call_msg) + phoneNumber);
        m_dialog.uiSetConfirmText(m_context.getString(R.string.call));
        m_dialog.uiSetCancelText(m_context.getString(R.string.cancel));
        m_dialog.show();
    }

    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(m_context.getPackageManager()) != null) {
            m_context.startActivity(intent);
        }
    }
}
