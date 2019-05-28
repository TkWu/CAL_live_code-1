package ci.ui.TextField.Base;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ci.ui.define.ViewScaleDef;

/**
 * Created by kevincheng on 2016/2/3.
 */
public abstract class CIBaseTextFieldFragment extends Fragment
    implements TextWatcher{
    protected View     m_rootView = null;
    protected afterTextChangedListener m_listener = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_rootView = inflater.inflate(getLayoutResourceId(),container,false);
        setupViewComponents(m_rootView);
        return m_rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTextSizeAndLayoutParams(view, ViewScaleDef.getInstance(getActivity()));
    }

    /**
     * BaseActivity在{@link CIBaseTextFieldFragment#onCreate(Bundle) onCreateView()}時 設定
     *
     * @return 此畫面的 Layout Resource Id
     */
    protected abstract int getLayoutResourceId();

    /**
     * 設定字型大小及版面大小
     * @param view
     * @param vScaleDef 請參閱{@link ViewScaleDef}
     */
    protected abstract void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef);

    protected abstract void setupViewComponents(View view);

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //do some thing
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //do some thing
        if(null != m_listener){
            m_listener.afterTextChangedListener(editable);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //do some thing
    }



    public interface afterTextChangedListener{
        void afterTextChangedListener(Editable editable);
    }

    public void setAfterTextChangedListener(afterTextChangedListener listener){
        this.m_listener = listener;
    }

}
