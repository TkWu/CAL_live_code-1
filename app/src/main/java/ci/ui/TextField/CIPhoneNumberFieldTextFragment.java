package ci.ui.TextField;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.ui.TextField.Base.CITextFieldFragment;

import static ci.ui.TextField.Base.CIRegex.REGEX_NUMBER;

/**
 * Created by kevincheng on 2016/2/23.
 */
public class CIPhoneNumberFieldTextFragment extends CITextFieldFragment {

    public static CIPhoneNumberFieldTextFragment newInstance(String hint){
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為數字 字數限制 12*/
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_NUMBER);
        bundle.putInt(TEXT_MAX_LENGHT, 12);
        CIPhoneNumberFieldTextFragment fragment = new CIPhoneNumberFieldTextFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void afterTextChanged(Editable editable) {

        super.afterTextChanged(editable);

        setErrorMsg(getString(R.string.member_login_input_correvt_format_msg));


        /**長度如果等於零就不再判斷格式正確性*/
        if(0 == editable.length()){
            setIsFormatCorrect(true);
            return;
        }

        /**如果都是數字就是正確的格式*/
        if (editable.toString().matches("[0-9]+")) {
            setIsFormatCorrect(true);
        } else {
            setIsFormatCorrect(false);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setInputType(InputType.TYPE_CLASS_PHONE);
    }
}
