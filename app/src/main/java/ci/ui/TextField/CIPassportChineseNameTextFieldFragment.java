package ci.ui.TextField;

import android.os.Bundle;

import ci.ui.TextField.Base.CITextFieldFragment;


/**
 * Created by kevincheng on 2016/2/5.
 */
public class CIPassportChineseNameTextFieldFragment extends CITextFieldFragment {

    public static CIPassportChineseNameTextFieldFragment newInstance(String hint){
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入中文字
         * 2016/12/02 為了因應各種不同的中文輸入法app 取消過濾文字及長度限制
         * */
//        bundle.putString(FILTER_MODE, EFilterMode.CHINESE.name());
        CIPassportChineseNameTextFieldFragment fragment = new CIPassportChineseNameTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
