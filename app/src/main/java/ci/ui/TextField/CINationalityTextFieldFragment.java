package ci.ui.TextField;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.Signup.CINationalitySelectMenuActivity;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.define.UiMessageDef;

/**
 * Created by kevin on 2016/2/20.
 */
public class CINationalityTextFieldFragment extends CITextFieldFragment {

    public static final int    ACTIVITY_ID = 1;
    public String              m_strCountryCd = null;

    public static CINationalityTextFieldFragment newInstance() {
        Bundle bundle = new Bundle();
        String hint   = "*"+ CIApplication.getContext().getString(R.string.sign_up_nationality);
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, TypeMode.MENU_FULL_PAGE.name());
        CINationalityTextFieldFragment fragment = new CINationalityTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CINationalityTextFieldFragment newInstance(String hint) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, TypeMode.MENU_FULL_PAGE.name());
        CINationalityTextFieldFragment fragment = new CINationalityTextFieldFragment();
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
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_MODE, CINationalitySelectMenuActivity.EMode.Base.name());
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(getActivity(), clazz);
        startActivityForResult(intent, ACTIVITY_ID);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_ID && getActivity().RESULT_OK == resultCode){
            m_strCountryCd = data.getStringExtra(CINationalitySelectMenuActivity.COUNTRY_CD);
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
}
