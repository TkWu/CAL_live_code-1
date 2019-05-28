package ci.function.MyTrips;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CICustomTextFieldFragment;
import ci.ui.define.ViewScaleDef;

/**
 * Created by kevincheng on 2016/4/20.
 */
public class CIAddPassengerInputFragment extends BaseFragment
    implements CITextFieldFragment.dropDownListener,
               View.OnClickListener{

    private CICustomTextFieldFragment m_firstNameFragment     = null;
    private CICustomTextFieldFragment m_lastNameFragment      = null;
    private TextView m_tvHead = null;
    private ImageView                           m_ivGarbage             = null;
    private onFragmentDeletedListener           m_listener              = null;
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_add_passenger_input;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_tvHead                = (TextView) view.findViewById(R.id.tv_add_head);
        m_ivGarbage             = (ImageView) view.findViewById(R.id.iv_garbage);
        m_firstNameFragment     = CICustomTextFieldFragment.newInstance("*" + getString(R.string.sign_up_first_name), CITextFieldFragment.TypeMode.NORMAL);
        m_lastNameFragment      = CICustomTextFieldFragment.newInstance("*" + getString(R.string.sign_up_last_name), CITextFieldFragment.TypeMode.NORMAL);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_first_name, m_firstNameFragment);
        transaction.replace(R.id.fl_last_name, m_lastNameFragment);
        transaction.commit();
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.setViewSize(view.findViewById(R.id.rl_title), ViewGroup.LayoutParams.MATCH_PARENT,32);
        vScaleDef.setViewSize(m_tvHead, ViewGroup.LayoutParams.MATCH_PARENT,22.7);
        vScaleDef.setTextSize(16, m_tvHead);
        vScaleDef.setMargins(m_tvHead, 30, 0, 30, 0);
        vScaleDef.setMargins(view.findViewById(R.id.fl_first_name), 0, 20, 0, 0);
        vScaleDef.setMargins(view.findViewById(R.id.fl_last_name), 0, 6, 0, 0);
        vScaleDef.selfAdjustSameScaleView(m_ivGarbage, 24, 24);
        vScaleDef.setMargins(m_ivGarbage, 0, 0, 19, 0);
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_ivGarbage.setOnClickListener(this);
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

    public void setNumber(int number){
        String str = String.format(getString(R.string.add_passenager_passenager_x), number);
        m_tvHead.setText(str);
    }

    public void setGarbageIconVisible(int isVisible){
        m_ivGarbage.setVisibility(isVisible);
    }

    @Override
    public void onDropDown(CITextFieldFragment.TypeMode mode, View v, String tag) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_garbage:
                if(null != m_listener){
                    m_listener.onFragmentDeleted(this);
                }
                break;
        }
    }

    public interface onFragmentDeletedListener{
        void onFragmentDeleted(Fragment fragment);
    }

    public void setOnFragmentDeletedListener(onFragmentDeletedListener listener){
        m_listener = listener;
    }

    public String getFirstName(){
        return m_firstNameFragment.getText();
    }

    public String getLastName(){
        return m_lastNameFragment.getText();
    }

    public void setImeOption(int imeOption){
        m_lastNameFragment.setImeOptions(imeOption);
    }
}
