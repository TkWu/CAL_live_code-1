package ci.ui.TextField;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import ci.ui.TextField.Base.CITextFieldFragment;

import static ci.ui.TextField.Base.CIRegex.REGEX_NUMBER;

/**
 * Created by flowmahuang on 2016/3/31.
 */
public class CIFlightNumberTextFieldFragment extends CITextFieldFragment {

    public static CIFlightNumberTextFieldFragment newInstance(String hint, TypeMode mode) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, mode.name());
        /**限制輸入為數字 字數限制 4 */
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_NUMBER);
        bundle.putInt(TEXT_MAX_LENGHT, 4);
        CIFlightNumberTextFieldFragment fragment = new CIFlightNumberTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setInputType(InputType.TYPE_CLASS_NUMBER);
        setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

}
