package ci.ui.TextField;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.Signup.CISpecialMealListSelectMenuActivity;
import ci.ui.TextField.Base.CITextFieldFragment;

/**
 * Created by kevin on 2016/2/21.
 */
public class CISpecialMealSelectTextFieldFragment extends CITextFieldFragment {

    public static final int    ACTIVITY_ID  = 27502;
    public String m_code                    = "";
    public static CISpecialMealSelectTextFieldFragment newInstance(){
        Bundle bundle = new Bundle();
        String hint = CIApplication.getContext().getString(R.string.special_meal_preference);
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, TypeMode.MENU_FULL_PAGE.name());
        CISpecialMealSelectTextFieldFragment fragment = new CISpecialMealSelectTextFieldFragment();
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
        changeActivity(CISpecialMealListSelectMenuActivity.class);
    }

    private void changeActivity(Class clazz) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, ACTIVITY_ID);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(ACTIVITY_ID == requestCode && getActivity().RESULT_OK == resultCode){
            m_editText.setText(data.getStringExtra(CISpecialMealListSelectMenuActivity.VALUE));
            setCode(data.getStringExtra(CISpecialMealListSelectMenuActivity.CODE));
        }
    }

    /**
     * 取得餐點代碼
     * @return
     */
    public String getCode() {
        return m_code;
    }

    /**
     * 設定餐點代碼
     * @param code
     */
    public void setCode(String code) {
        this.m_code = code;
    }
}
