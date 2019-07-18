package ci.ui.TextField;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.chinaairlines.mobile30.R;
import java.util.ArrayList;
import java.util.HashSet;

import ci.function.Checkin.CIAPISCheckInDocmuntTypeSelectMenuActivity;
import ci.function.Core.CIApplication;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Models.entities.CIApisQryRespEntity;

/**
 * Created by joannyang on 16/5/26.
 */
public class CIApisDocmuntTextFieldFragment extends CITextFieldFragment {

    public interface OnCIAPISDocChoosedFragmentClick{
        boolean getSelectResult();
    }

    private OnCIAPISDocChoosedFragmentClick onCIAPISDocChoosedFragmentClick;

    public static final int     ACTIVITY_ID = 1;
    public CIApisQryRespEntity.ApisRespDocObj               m_strDocmuntTypeObj = null;
    public String              m_strDocmuntType = null;

    private HashSet<String>     m_filter = null;
    public static final String  APIS_TYPE = "APIS_TYPE";

    //可選清單，會將總表濾掉，只留下可選擇清單
    private HashSet<CIApisDocmuntTypeEntity> m_SelectList = null;

    //新增已存清單
    private ArrayList<CIApisQryRespEntity.ApisRespDocObj> m_SavedList = null;

    public enum EType{
        Personal, CheckIn
    }

    public static CIApisDocmuntTextFieldFragment newInstance() {
        Bundle bundle = new Bundle();
        String hint   = "*"+ CIApplication.getContext().getString(R.string.document_type);
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, TypeMode.MENU_FULL_PAGE.name());
        bundle.putSerializable(APIS_TYPE, EType.Personal);
        CIApisDocmuntTextFieldFragment fragment = new CIApisDocmuntTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CIApisDocmuntTextFieldFragment newInstance(String hint,EType type) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, TypeMode.MENU_FULL_PAGE.name());
        bundle.putSerializable(APIS_TYPE, type);
        CIApisDocmuntTextFieldFragment fragment = new CIApisDocmuntTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**需要過濾掉的證件類別*/
    public void setDocmuntTypeFilter(HashSet<String> filterHash) {
        m_filter = filterHash;
    }

    /**輸入可提供選擇的清單*/
    public void setDocmuntTypeSelectList(HashSet<CIApisDocmuntTypeEntity> selectHash) {
        m_SelectList = selectHash;
    }

    /**已儲存的清單*/
    public void setSavedDocmuntSelectList(ArrayList<CIApisQryRespEntity.ApisRespDocObj> selectHash) {
        m_SavedList = selectHash;
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
        changeActivity(CIAPISCheckInDocmuntTypeSelectMenuActivity.class);
    }

    private void changeActivity(Class clazz) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_DATA_HINT, getArguments().get(TEXT_HINT).toString());
        bundle.putSerializable(CIAPISCheckInDocmuntTypeSelectMenuActivity.BUNDLE_ACTIVITY_DATA_FILTER_LIST, m_filter);
        bundle.putSerializable(CIAPISCheckInDocmuntTypeSelectMenuActivity.BUNDLE_ACTIVITY_DATA_SELECT_LIST, m_SelectList);
        bundle.putSerializable(CIAPISCheckInDocmuntTypeSelectMenuActivity.BUNDLE_ACTIVITY_DATA_SAVED_LIST, m_SavedList);

        bundle.putSerializable(APIS_TYPE, getArguments().getSerializable(APIS_TYPE));
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
            Bundle bundle = data.getExtras();
            if(null != bundle) {
                m_strDocmuntTypeObj = (CIApisQryRespEntity.ApisRespDocObj)bundle.getSerializable(CIAPISCheckInDocmuntTypeSelectMenuActivity.DOCUMUNT_TYPE);
            }
            onCIAPISDocChoosedFragmentClick.getSelectResult();
            m_editText.setText(m_strDocmuntTypeObj.documentName);
        }
    }

    /**
     * 取得護照類型
     * @return
     */
    public String getDocmuntType() {
        return m_strDocmuntType;
    }

    public CIApisQryRespEntity.ApisRespDocObj getDocmuntTypeObj() {
        return m_strDocmuntTypeObj;
    }

    /**
     * 設定護照類型
     */
    public void setDocmuntType(String strDocmuntType) {
        this.m_strDocmuntType = strDocmuntType;
    }

    public void setOnCIAPISDocChoosedFragmentClick(OnCIAPISDocChoosedFragmentClick onCIChooseAirportTextFragmentClick) {
        this.onCIAPISDocChoosedFragmentClick = onCIChooseAirportTextFragmentClick;
    }
}
