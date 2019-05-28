package ci.ui.TextField;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.ui.TextField.Base.CITextFieldFragment;

import static ci.ui.TextField.Base.CIRegex.REGEX_ENGLISH_NUMBER;
import static ci.ui.TextField.Base.CIRegex.REGEX_ENGLISH_AND_NUMBER;


/**
 * Created by kevincheng on 2016/2/5.
 */
public class CIPasswordTextFieldFragment extends CITextFieldFragment {

    public static CIPasswordTextFieldFragment newInstance(String hint){
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為字母及數字*/
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_ENGLISH_NUMBER);
        //2017-01-19 Modifly by ryan for 華航再次修正決議ui 檔長度限制, 10碼
        //使用限制輸入的方式去擋
        bundle.putInt(TEXT_MAX_LENGHT, 10);
        //bundle.putInt(TEXT_MAX_LENGHT, 8);
        CIPasswordTextFieldFragment fragment = new CIPasswordTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void afterTextChanged(Editable editable) {

        setErrorMsg(getString(R.string.member_login_input_correvt_format_msg));

        //2016-09-12 華航又忽然不鎖密碼長度,
        /**長度如果小於6或大於8就是不正確的格式*/
//        if (6 > editable.length() || 8 < editable.length()) {
//            setIsFormatCorrect(false);
//        } else {
            /**必須同時符合擁有子母及數字才是正確格式*/
            if(editable.toString().matches(REGEX_ENGLISH_AND_NUMBER)){
                setIsFormatCorrect(false);
            } else {
                setIsFormatCorrect(true);
            }
//        }

        /**長度如果等於零就直接設為正確格式*/
        if(0 == editable.length()){
            setIsFormatCorrect(true);
        }

        /**最後才會判斷另設監聽（特殊）*/
        super.afterTextChanged(editable);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ){
            setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            m_editText.setTransformationMethod(new PasswordTransformationMethod());
        }
    }
}
