package ci.ui.TextField;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.PersonalDetail.APIS.CIApisStateSelectMenuActivity;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.define.UiMessageDef;

/**
 * Created by joannyang on 16/6/1.
 */
public class CIApisStateTextFieldFragment extends CITextFieldFragment {

    public static final int    ACTIVITY_ID = 1;
    public String              m_strStateCd = null;

    public static CIApisStateTextFieldFragment newInstance() {
        Bundle bundle = new Bundle();
        String hint   = "*"+ CIApplication.getContext().getString(R.string.sign_up_nationality);
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, TypeMode.MENU_FULL_PAGE.name());
        CIApisStateTextFieldFragment fragment = new CIApisStateTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CIApisStateTextFieldFragment newInstance(String hint) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, TypeMode.MENU_FULL_PAGE.name());
        CIApisStateTextFieldFragment fragment = new CIApisStateTextFieldFragment();
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
        changeActivity(CIApisStateSelectMenuActivity.class);
    }

    private void changeActivity(Class clazz) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_DATA_HINT, getArguments().get(TEXT_HINT).toString());
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_MODE, CIApisStateSelectMenuActivity.EMode.Base.name());
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
            m_strStateCd = data.getStringExtra(CIApisStateSelectMenuActivity.CODE);
            m_editText.setText(data.getStringExtra(CIApisStateSelectMenuActivity.VALUE));
        }
    }

    /**
     * 取得國家英文縮寫
     * @return
     */
    public String getStateCode() {
        return m_strStateCd;
    }

    /**
     * 設定國家英文縮寫
     */
    public void setStateCode(String StateCd) {
        this.m_strStateCd = StateCd;
    }
}
