package ci.function.FlightStatus;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.function.TimeTable.CISelectDepartureAirpotActivity;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIChooseAirportTextFieldFragment;
import ci.ui.define.ViewScaleDef;

/**
 * FlightStatus的ByRoute頁面，
 * Created by flowmahuang on 2016/3/28.
 */
public class CIFlightStatusByRouteFragment extends BaseFragment implements View.OnClickListener {
    private CITextFieldFragment m_FromFragment;
    private CITextFieldFragment m_ToFragment;

    private RelativeLayout m_rlRoot;
    private ImageButton m_ChangeButton;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_flight_status_by_route;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        setupViewComponents();

        m_rlRoot = (RelativeLayout) view.findViewById(R.id.rl_route_root);
        m_ChangeButton = (ImageButton) view.findViewById(R.id.btn_change_airport);
    }

    private void setupViewComponents() {
        FragmentManager manager = getChildFragmentManager();
        m_FromFragment = CIChooseAirportTextFieldFragment.newInstance(
                getString(R.string.from),
                R.drawable.ic_departure_2,
                R.drawable.ic_departure_4,
                false,
                CISelectDepartureAirpotActivity.FLIGHT_STATUS);
        m_ToFragment = CIChooseAirportTextFieldFragment.newInstance(
                getString(R.string.to),
                R.drawable.ic_arrival_2,
                R.drawable.ic_arrival_4,
                true,
                CISelectDepartureAirpotActivity.FLIGHT_STATUS);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment1, m_FromFragment)
                .replace(R.id.fragment2, m_ToFragment)
                .commitAllowingStateLoss();

        /**
         * 出發地監聽，主要是點擊欄位時會被觸發，固定都會開啟選擇機場的視窗，所以isOpenSelectPage()會回傳true
         * 出發地欄位開啟選單時不會去調用getFromIAIT()
         */
        ((CIChooseAirportTextFieldFragment)m_FromFragment).setOnCIChooseAirportTextFragmentClick(
                new CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick() {
                    @Override
                    public boolean isOpenSelectPage() {
                        if (!m_ToFragment.getText().equals("")) {
                            ((CIChooseAirportTextFieldFragment)m_ToFragment).setEditText("");
                        }
                        return true;
                    }

                    @Override
                    public String getFromIAIT() {
                        return ((CIChooseAirportTextFieldFragment) m_FromFragment).getIAIT();
                    }
                });

        ((CIChooseAirportTextFieldFragment)m_ToFragment).setOnCIChooseAirportTextFragmentClick(
                onCIChooseAirportTextFragmentClick);

    }

    /**
     * 目的地監聽，主要是點擊欄位時會被觸發，如果判斷出發地(m_fromFragment)尚未被選擇，
     * 則會跳出提示視窗要求選擇，則isOpenSelectPage()會回傳false，反之亦然；如果已經選
     * 擇出發地了，則會將跳出選單，另外目的地欄位開啟時會呼叫getFromIAIT()去取得出發地的IAIT
     */
    CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick onCIChooseAirportTextFragmentClick = new CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick() {
        @Override
        public boolean isOpenSelectPage() {
            if (((CIChooseAirportTextFieldFragment) m_FromFragment).getIAIT().equals("")) {

                showDialog(getString(R.string.warning)
                        ,String.format(getString(R.string.please_input_field), getString(R.string.from))
                        //,getResources().getString(R.string.select_error_msg) + " " + getString(R.string.from)
                        ,getString(R.string.confirm));

                return false;
            } else {
                return true;
            }
        }

        @Override
        public String getFromIAIT() {
            return ((CIChooseAirportTextFieldFragment) m_FromFragment).getIAIT();
        }
    };

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
//        vScaleDef.selfAdjustAllView(m_rlRoot);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)m_ChangeButton.getLayoutParams();
        layoutParams.width = vScaleDef.getLayoutWidth(32);
        layoutParams.height = vScaleDef.getLayoutWidth(32);
        layoutParams.rightMargin = vScaleDef.getLayoutWidth(20);
        layoutParams.topMargin = vScaleDef.getLayoutHeight(71.3);

        layoutParams = (RelativeLayout.LayoutParams)view.findViewById(R.id.fragment1).getLayoutParams();
        layoutParams.topMargin = vScaleDef.getLayoutHeight(26);
        layoutParams.leftMargin = vScaleDef.getLayoutWidth(20);
        vScaleDef.setPadding(view.findViewById(R.id.fragment1), 0, 0, 72, 0);

        layoutParams = (RelativeLayout.LayoutParams)view.findViewById(R.id.fragment2).getLayoutParams();
        layoutParams.topMargin = vScaleDef.getLayoutHeight(10.3);
        layoutParams.leftMargin = vScaleDef.getLayoutWidth(20);
        layoutParams.rightMargin = vScaleDef.getLayoutWidth(72);

        m_FromFragment.setWidth(268);
        m_ToFragment.setWidth(268);
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_ChangeButton.setOnClickListener(this);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View v) {
        String from = m_FromFragment.getText();
        String to = m_ToFragment.getText();

        String fromIAIT = ((CIChooseAirportTextFieldFragment)m_FromFragment).getIAIT();
        String toIAIT = ((CIChooseAirportTextFieldFragment)m_ToFragment).getIAIT();

        m_FromFragment.setText(to);
        m_ToFragment.setText(from);
        ((CIChooseAirportTextFieldFragment)m_FromFragment).setIAIT(toIAIT);
        ((CIChooseAirportTextFieldFragment)m_ToFragment).setIAIT(fromIAIT);
    }

    public String getFromFragmentIAIT(){
        return ((CIChooseAirportTextFieldFragment)m_FromFragment).getIAIT();
    }

    public String getToFragmentIAIT(){
        return ((CIChooseAirportTextFieldFragment)m_ToFragment).getIAIT();
    }

    public void setFromFragmentIAIT(String iata){
        ((CIChooseAirportTextFieldFragment)m_FromFragment).setIAIT(iata);
    }

    public void setFromFragmentText(String text){
        m_FromFragment.setText(text);
    }

    public void setToFragmentIAIT(String iata){
        ((CIChooseAirportTextFieldFragment)m_ToFragment).setIAIT(iata);
    }

    public void setToFragmentText(String text){
        m_ToFragment.setText(text);
    }

    public boolean checkFromToInput() {
        boolean checkFromTo = true;
        if (m_FromFragment.getText().isEmpty()) {
            checkFromTo = false;
        }
        if (m_ToFragment.getText().isEmpty()){
            checkFromTo = false;
        }
        return checkFromTo;
    }
}