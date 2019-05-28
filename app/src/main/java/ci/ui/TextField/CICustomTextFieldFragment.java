package ci.ui.TextField;

import android.os.Bundle;

import ci.ui.TextField.Base.CITextFieldFragment;

/**
 * Created by kevincheng on 2016/2/18.
 */
public class CICustomTextFieldFragment extends CITextFieldFragment {

    public static CICustomTextFieldFragment newInstance(String hint, TypeMode mode) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, mode.name());
        CICustomTextFieldFragment fragment = new CICustomTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
