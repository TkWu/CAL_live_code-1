package ci.ui.TextField;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import com.chinaairlines.mobile30.R;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.TransformationMethod.AllCapTransformationMethod;

import static ci.ui.TextField.Base.CIRegex.REGEX_ENGLISH_NUMBER;


/**
 * Created by kevincheng on 2016/2/5.
 */
public class CIIdentityCardNoTextFieldFragment extends CITextFieldFragment {

    public static CIIdentityCardNoTextFieldFragment newInstance(String hint){
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為字母及數字  字數限制 10*/
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_ENGLISH_NUMBER);
        bundle.putInt(TEXT_MAX_LENGHT, 10);
        CIIdentityCardNoTextFieldFragment fragment = new CIIdentityCardNoTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void afterTextChanged(Editable editable) {

        setErrorMsg(getString(R.string.member_login_input_correvt_format_msg));

        /**長度如果不等於10和大於0就是不正確的格式*/
        if (10 != editable.length() && 0 < editable.length() ) {
            setIsFormatCorrect(false);
        } else if(10 == editable.length()){
            /**必須符合身分證格式*/
            if(VerifyID(editable.toString())){
                setIsFormatCorrect(true);
            } else {
                setIsFormatCorrect(false);
            }
        } else if(0 == editable.length()) {
            /**長度如果等於零就直接設為正確格式*/
            setIsFormatCorrect(true);
        }


        /**最後才會判斷另設監聽（特殊）*/
        super.afterTextChanged(editable);

    }

    /**
     * 驗證身分證字號
     * @param id 身分證字號
     * @return
     */
    public boolean VerifyID(String id){
        int[] num=new int[10];
        int[] rdd={10,11,12,13,14,15,16,17,34,18,19,20,21,22,35,23,24,25,26,27,28,29,32,30,31,33};
        id=id.toUpperCase();
        if(id.charAt(0)<'A'||id.charAt(0)>'Z'){
            System.out.println("第一個字錯誤!!");
            return false;
        }
        if(id.charAt(1)!='1' && id.charAt(1)!='2'){
            System.out.println("第二個字錯誤!!");
            return false;
        }
        for(int i=1;i<10;i++){
            if(id.charAt(i)<'0'||id.charAt(i)>'9'){
                System.out.println("輸入錯誤!!");
                return false;
            }
        }
        for(int i=1;i<10;i++){
            num[i]=(id.charAt(i)-'0');
        }
        num[0]=rdd[id.charAt(0)-'A'];
        int sum=((int)num[0]/10+(num[0]%10)*9);
        //2016-10-18 修正邏輯錯誤 modifly by ryan
        //正解：將所有數字依照規則加總
        for(int i = 0 ; i <= 8 ; i++){
            int iN = 8-i;
            if ( iN == 0 ){
                iN = 1;
            }
            sum += num[i+1]*iN;
        }

        //2016-10-18 修正邏輯錯誤 modifly by ryan
        //正解：總和的值可以被10整除代表該身分證字號是可用的
        if ( (sum % 10) == 0 ){
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        m_editText.setTransformationMethod(new AllCapTransformationMethod());
    }

    @Override
    public String getText() {
        //帳號欄位輸出的英文字母必需全部為大寫
        return m_editText.getText().toString().trim().toUpperCase();
    }
}
