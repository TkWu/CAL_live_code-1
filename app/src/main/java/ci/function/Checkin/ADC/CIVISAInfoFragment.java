package ci.function.Checkin.ADC;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ci.function.Base.BaseFragment;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIDateOfExpiryTextFieldFragment;
import ci.ui.TextField.CIPassportNumberFieldText;
import ci.ui.define.ViewScaleDef;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;

public class CIVISAInfoFragment extends BaseFragment {

    public static final String BUNDLE_PARA_TRIP_TEXT  = "Trip_text";
    public static final String BUNDLE_PARA_DEPARTURE_DATE = "Departure_Date";

    private TextView m_tvText = null;
    private CITextFieldFragment m_DocumentNoFragment = null,
                                m_DocExpiryDateFragment = null;

    public CIVISAInfoFragment(){}

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_check_in_visa;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_tvText = (TextView)view.findViewById(R.id.tv_text);

        m_DocumentNoFragment = CIPassportNumberFieldText.newInstance("*"+getString(R.string.check_in_visa_document_number));

        m_DocExpiryDateFragment = CIDateOfExpiryTextFieldFragment.newInstance("*"+getString(R.string.check_in_visa_date_of_expiry));

    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        LinearLayout llayoutRoot = view.findViewById(R.id.llayout_root);
        vScaleDef.selfAdjustAllView(llayoutRoot);
    }

    @Override
    protected void setOnParameterAndListener(View view) {

        Bundle bundle = getArguments();
        if ( null != bundle ){

            String strText= bundle.getString(BUNDLE_PARA_TRIP_TEXT);
            String strDepartureDate = bundle.getString(BUNDLE_PARA_DEPARTURE_DATE);
            m_tvText.setText(strText);

            AdjustDocExpiryDate(strDepartureDate);
        }

    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {


        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace( R.id.flayout_doc_no, m_DocumentNoFragment, m_DocumentNoFragment.toString());
        fragmentTransaction.replace( R.id.flayout_expiry_date , m_DocExpiryDateFragment, m_DocExpiryDateFragment.toString());

        fragmentTransaction.commitAllowingStateLoss();

        if(null != getActivity()){
            getActivity().getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    //2018-10-10 Ryan, for 取消下一步連動
                    m_DocumentNoFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
                }
            });
        }
        m_tvText.requestFocus();
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

    private void AdjustDocExpiryDate( String strDepartureDate ){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();

        try {

            if ( TextUtils.isEmpty(strDepartureDate) ){

                calendar.setTime(new Date());

            } else {

                Date dDeparture_Date = sdf.parse(strDepartureDate);
                calendar.setTime(dDeparture_Date);
                int iMinDay = calendar.get(Calendar.DAY_OF_MONTH) +1;
                calendar.set(Calendar.DAY_OF_MONTH, iMinDay);
            }

            if ( null != m_DocExpiryDateFragment ){
                ((CIDateOfExpiryTextFieldFragment)m_DocExpiryDateFragment).setMinDay(calendar.getTime().getTime());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**取得簽證號碼*/
    public String getDocumentNo(){
        if ( null != m_DocumentNoFragment ){
            return m_DocumentNoFragment.getText();
        } else {
            return "";
        }
    }

    /**取得簽證有效日期*/
    public String getDocExpiryDate(){
        if ( null != m_DocExpiryDateFragment ){
            return m_DocExpiryDateFragment.getText();
        } else {
            return "";
        }
    }

    /**檢查欄位是否都填妥*/
    public boolean isFillCompleteAndCorrect(){

        //證件號碼必填
        String strDocumentNo = m_DocumentNoFragment.getText();
        if( TextUtils.isEmpty(strDocumentNo) ) {
            return false;
        }

        //效期截止日期必選
        String strExpiryDate = ((CIDateOfExpiryTextFieldFragment)m_DocExpiryDateFragment).getFormatedDate();
        if( TextUtils.isEmpty(strExpiryDate) || TextUtils.isEmpty(m_DocExpiryDateFragment.getText())) {
            return false;
        }

        return true;
    }


}
