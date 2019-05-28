package ci.function.Core;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.define.ViewScaleDef;

/**
 * Created by kevincheng on 2016/3/9.
 */
public class CIConstructionFragment extends BaseFragment {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_construction;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

    }

    @Override
    protected void setOnParameterAndListener(View view) {

    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {

    }

    @Override
    public void onLanguageChangeUpdateUI() {

    }
}
