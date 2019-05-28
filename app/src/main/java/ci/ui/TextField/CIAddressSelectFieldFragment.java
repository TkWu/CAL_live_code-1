package ci.ui.TextField;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.function.Signup.CIAddressSelectMenuActivity;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.define.UiMessageDef;


public class CIAddressSelectFieldFragment extends CITextFieldFragment {


    private static final int    ACTIVITY_ID = 1;
    private String              m_strCode = null;
    boolean m_bClickLock = false;

    public static CIAddressSelectFieldFragment newInstance(String hint, CIAddressSelectMenuActivity.EMode mode ) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, TypeMode.MENU_FULL_PAGE.name());
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_MODE, mode.name());
        CIAddressSelectFieldFragment fragment = new CIAddressSelectFieldFragment();
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
            if ( !m_bClickLock ){
                fullPageMenu();
            }
        }
    };

    private void fullPageMenu() {
        changeActivity(CIAddressSelectMenuActivity.class);
    }

    private void changeActivity(Class clazz) {
        Intent intent = new Intent();

        Bundle bundle = new Bundle();
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_DATA_HINT, getArguments().get(TEXT_HINT).toString());
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_MODE, getArguments().get(UiMessageDef.BUNDLE_ACTIVITY_MODE).toString());
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
            m_strCode = data.getStringExtra(CIAddressSelectMenuActivity.COUNTRY_CD);
            m_editText.setText(data.getStringExtra(CIAddressSelectMenuActivity.VALUE));
        }
    }

    /**
     * 取得code
     * @return
     */
    public String getCode() {
        return m_strCode;
    }

    /**
     * 設定code
     */
    public void setCode(String code) {
        this.m_strCode = code;
    }

    public void setClickLock( boolean bClickLock ){
        m_bClickLock = bClickLock;
    }
}
