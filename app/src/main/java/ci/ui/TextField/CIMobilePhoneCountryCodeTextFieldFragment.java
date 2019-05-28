package ci.ui.TextField;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.function.Signup.CINationalitySelectMenuActivity;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.define.UiMessageDef;

/**
 * Created by kevin on 2016/2/20.
 */
public class CIMobilePhoneCountryCodeTextFieldFragment extends CITextFieldFragment {

    public static final int    ACTIVITY_ID = 3;
    public String               m_strCountryCd  = "";
    public String               m_strPhoneCd    = "";

    public static CIMobilePhoneCountryCodeTextFieldFragment newInstance(String strHint) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, strHint);
        bundle.putString(TYPE_MODE, TypeMode.MENU_FULL_PAGE.name());
        CIMobilePhoneCountryCodeTextFieldFragment fragment = new CIMobilePhoneCountryCodeTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        setDropDownListener(listener);
    }

    dropDownListener listener = new dropDownListener() {
        @Override
        public void onDropDown(TypeMode mode, View v, String tag) {
            fullPageMenu();
        }
    };

    private void fullPageMenu() {
        changeActivity(CINationalitySelectMenuActivity.class);
    }

    private void changeActivity(Class clazz) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_DATA_HINT, getArguments().get(TEXT_HINT).toString());
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_MODE, CINationalitySelectMenuActivity.EMode.PhoneCode.name());
        intent.putExtras(bundle);
        intent.setClass(getActivity(), clazz);
        startActivityForResult(intent, ACTIVITY_ID);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_ID && getActivity().RESULT_OK == resultCode){
            m_strCountryCd = data.getStringExtra(CINationalitySelectMenuActivity.COUNTRY_CD);
            m_strPhoneCd = data.getStringExtra(CINationalitySelectMenuActivity.PHONE_CD);
            m_editText.setText(data.getStringExtra(CINationalitySelectMenuActivity.VALUE));
        }
    }

    /**
     * 取得國家英文縮寫
     * @return
     */
    public String getCountryCd() {
        return m_strCountryCd;
    }

    /**
     * 設定國家英文縮寫
     */
    public void setCountryCd(String CountryCd) {
        this.m_strCountryCd = CountryCd;
    }

    /**
     * 取得國家電話國碼
     * @return
     */
    public String getPhoneCd() {
        return m_strPhoneCd;
    }

    /**
     * 設定國家電話國碼
     */
    public void setPhoneCd(String countryCode) {
        this.m_strPhoneCd = countryCode;
    }
}
