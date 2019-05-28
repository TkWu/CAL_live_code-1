package ci.function.PersonalDetail;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.AppInfo;
import ci.ws.Models.entities.CIProfileEntity;

/**
 * Created by kevincheng on 2016/3/21.
 */
public class CIPersonalProfileFragment extends BaseFragment
    implements View.OnClickListener{

//    private final String WS_FORMAT = "yyyy-MM-dd";
//    private final String UI_FORMAT = "MMM dd, yyyy";

    private CIAlertDialog m_dialog = null;

    private onPersonalProfileCardViewListener m_listener = null;

    public interface  onPersonalProfileCardViewListener {
        public void onEditPersonalProfileClick();
    }

    private TextView m_tvPassportChinessName = null;
    private TextView m_tvIdentityCardNo      = null;
    private TextView m_tvDateOfBirth         = null;
    private TextView m_tvPassportNo          = null;
    private TextView m_tvEmail               = null;
    private TextView m_tvMobilePhone         = null;

    private final int REQUEST_CODE           = 27503;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_personal_profile;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_tvPassportChinessName = (TextView) view.findViewById(R.id.tv_passport_chinese_name_value);
        m_tvIdentityCardNo      = (TextView) view.findViewById(R.id.tv_identity_card_no_value);
        m_tvDateOfBirth         = (TextView) view.findViewById(R.id.tv_date_of_birth_value);
        m_tvPassportNo          = (TextView) view.findViewById(R.id.tv_passport_no_value);
        m_tvEmail               = (TextView) view.findViewById(R.id.tv_email_value);
        m_tvMobilePhone         = (TextView) view.findViewById(R.id.tv_mobile_phone_value);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.ibtn_edit), 24, 24);
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        view.findViewById(R.id.ll_edit).setOnClickListener(this);

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
    public void onLanguageChangeUpdateUI() {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ll_edit:

                if( null != m_listener ) {
                    m_listener.onEditPersonalProfileClick();
                }
                break;
        }
    }

    public void setPersonalProfileCardViewListener(onPersonalProfileCardViewListener listener) {
        m_listener = listener;
    }

    public void updateProfileInfo(CIProfileEntity profile) {

        if( null != getView() ) {
            getView().setVisibility(View.VISIBLE);
        }


        if( null != profile ) {
            final String strPhoneNum = "+" + profile.cell_city + " " + (profile.cell_num.startsWith("0") ? profile.cell_num.substring(1) : profile.cell_num);

            m_tvPassportChinessName.setText(profile.chin_name);
            m_tvIdentityCardNo.setText(profile.id_num);
            m_tvDateOfBirth.setText( formatDate(profile.birth_date) );
            m_tvPassportNo.setText(profile.Passport);
            m_tvEmail.setText(profile.email);
            m_tvMobilePhone.setText(strPhoneNum);
        } else {
            m_tvPassportChinessName.setText("");
            m_tvIdentityCardNo.setText("");
            m_tvDateOfBirth.setText("");
            m_tvPassportNo.setText("");
            m_tvEmail.setText("");
            m_tvMobilePhone.setText("");
        }
    }

    private String formatDate(String strDate) {

        if( TextUtils.isEmpty(strDate)
                || !strDate.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$") ) {
            return "";
        }

        //2016-08-04 調整時間顯示格式
        return AppInfo.getInstance(getContext()).ConvertDateFormat(strDate);
//        try {
//            // 定義時間格式
//            SimpleDateFormat sdfA = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
//            sdfA.applyPattern(WS_FORMAT);
//
//            Date date = sdfA.parse(strDate);
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(UI_FORMAT);
//            return simpleDateFormat.format(date.getTime());
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return "";
//        }
    }

    private void showDialog(String strTitle, String strMsg, String strConfirm,CIAlertDialog.OnAlertMsgDialogListener listner) {
        if(null == m_dialog) {
            m_dialog = new CIAlertDialog(getContext(), listner);
        } else if (true == m_dialog.isShowing()){
            m_dialog.dismiss();
            m_dialog = new CIAlertDialog(getContext(), listner);
        }
        m_dialog.uiSetTitleText(strTitle);
        m_dialog.uiSetContentText(strMsg);
        m_dialog.uiSetConfirmText(strConfirm);
        m_dialog.show();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();

    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();



    }

    @Override
    public void onFragmentShow() {
        super.onFragmentShow();

        UpdateProfileText();

        updateProfile(true);
    }

    private void UpdateProfileText() {
        ((TextView) getView().findViewById(R.id.tv_title)).setText(R.string.profile);
        ((TextView) getView().findViewById(R.id.tv_passport_chinese_name_title)).setText(R.string.passport_chinese_name);
        ((TextView) getView().findViewById(R.id.tv_identity_card_no_title)).setText(R.string.identity_card_no);
        ((TextView) getView().findViewById(R.id.tv_date_of_birth_title)).setText(R.string.date_of_birth);
        ((TextView) getView().findViewById(R.id.tv_passport_no_title)).setText(R.string.passport_no);
        ((TextView) getView().findViewById(R.id.tv_email_title)).setText(R.string.personal_email);
        ((TextView) getView().findViewById(R.id.tv_mobile_phone_title)).setText(R.string.mobile_phone);
    }

    public void updateProfile(boolean bResetProfile) {

        if( null != getView() ) {
            getView().setVisibility(View.GONE);
        }

        if( true == bResetProfile ) {
            m_tvPassportChinessName.setText("");
            m_tvIdentityCardNo.setText("");
            m_tvDateOfBirth.setText("");
            m_tvPassportNo.setText("");
            m_tvEmail.setText("");
            m_tvMobilePhone.setText("");
        }

    }

}
