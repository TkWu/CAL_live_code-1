package ci.ui.TextField;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import ci.ui.TextField.Base.CITextFieldFragment;

import static ci.ui.TextField.Base.CIRegex.REGEX_NUMBER;

/**
 * Created by kevin on 2016/2/20.
 */
public class CIOnlyNumberTextFieldFragment extends CITextFieldFragment {
    public static CIOnlyNumberTextFieldFragment newInstance(String hint){
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為數字*/
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_NUMBER);
        CIOnlyNumberTextFieldFragment fragment = new CIOnlyNumberTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setInputType(InputType.TYPE_CLASS_NUMBER);
    }
}
