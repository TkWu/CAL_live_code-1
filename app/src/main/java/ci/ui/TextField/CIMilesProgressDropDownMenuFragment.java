package ci.ui.TextField;

import android.os.Bundle;

import com.chinaairlines.mobile30.R;

/**
 * Created by kevincheng on 2016/3/17.
 */
public class CIMilesProgressDropDownMenuFragment extends CIDropDownMenuTextFieldFragment {

    public static CIMilesProgressDropDownMenuFragment newInstance(int itemArray ){
        Bundle bundle = new Bundle();
        bundle.putInt(ITEM_ARRAY, itemArray);
        bundle.putString(TYPE_MODE, TypeMode.MENU_PULL_DOWN.name());
        CIMilesProgressDropDownMenuFragment fragment = new CIMilesProgressDropDownMenuFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**設定佈局ID*/
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_ci_text_feild_miles_progress;
    }
}
