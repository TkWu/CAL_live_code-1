package ci.ui.TextField;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.function.BaggageTrack.CIScannerBaggageTagActivity;
import ci.function.Core.CIApplication;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.TransformationMethod.AllCapTransformationMethod;
import ci.ui.define.UiMessageDef;
import ci.ui.toast.CIToastView;

/**
 * 可掃描行李追蹤條碼以及手動輸入*/
public class CICustomTextWithScanBagNumFieldFragment extends CITextFieldFragment {

    public final static int  PERMISSIONS_REQUEST_CODE = 1;
    public static final int    ACTIVITY_ID = 1;
    private Type m_type = Type.NONE;

    public enum Type{
        /**NONE = 0,BAGGAGE_TAG = 1 */
        NONE, BAGGAGE_TAG,
    }

    public static CICustomTextWithScanBagNumFieldFragment newInstance(String hint) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, TypeMode.EDITTEXT_BUTTON.name());
        CICustomTextWithScanBagNumFieldFragment fragment = new CICustomTextWithScanBagNumFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        m_editText.setTransformationMethod(new AllCapTransformationMethod());
    }

    @Override
    public void afterTextChanged(Editable editable) {
        super.afterTextChanged(editable);

        /**長度如果等於零就不再判斷格式正確性*/
        if(0 == editable.length()){
            setIsFormatCorrect(true);
            setTextFeildType(Type.NONE);
            return;
        }
        setErrorMsg(getString(R.string.member_login_input_correvt_format_msg));
        setTextFeildType(Type.NONE);
        setIsFormatCorrect(true);

        if ( CIApplication.getBaggageInfo().checkBaggageTagFormat(editable.toString()) ) {
            setTextFeildType(Type.BAGGAGE_TAG);
        } else {
            if (0 < editable.length()) {
                setIsFormatCorrect(false);
            }
            setTextFeildType(Type.NONE);
        }
    }

    public Type getTextFeildType(){
        return m_type;
    }

    private void setTextFeildType( Type type){
        m_type = type;
    }


    @Override
    public void onResume() {
        super.onResume();
        setItemClickListener(m_onListener);
    }

    onItemClickListener m_onListener = new onItemClickListener() {
        @Override
        public void onItemClick(TypeMode mode, View v, String tag) {
            fullPageMenu();
        }
    };

    private void fullPageMenu() {

        //需要相機授權
        if ( checkSelfPermission(Manifest.permission.CAMERA) ){
            changeActivity(CIScannerBaggageTagActivity.class);
        } else {
            CIToastView.makeText(getContext(),getContext().getString(R.string.bagtag_camera_permissions_msg)).show();
        }
    }

    private void changeActivity(Class clazz) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(getActivity(), clazz);
        startActivityForResult(intent, ACTIVITY_ID);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_ID && getActivity().RESULT_OK == resultCode){
            m_editText.setText(data.getStringExtra(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_NUMBER));
        }
    }

    public boolean checkSelfPermission( String strPermission ){
        if (ActivityCompat.checkSelfPermission( getContext(), strPermission) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions((Activity)getContext(),
                    new String[]{strPermission}, PERMISSIONS_REQUEST_CODE);

            return false;
        }
        return true;
    }

    @Override
    public String getText() {
        //帳號欄位輸出的英文字母必需全部為大寫
        return m_editText.getText().toString().trim().toUpperCase();
    }


}
