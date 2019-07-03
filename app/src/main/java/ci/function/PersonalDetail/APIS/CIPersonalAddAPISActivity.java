package ci.function.PersonalDetail.APIS;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIApisDocmuntTextFieldFragment;
import ci.ui.TextField.CIApisNationalTextFieldFragment;
import ci.ui.TextField.CIApisStateTextFieldFragment;
import ci.ui.TextField.CICustomTextFieldFragment;
import ci.ui.TextField.CIDateOfBirthdayTextFieldFragment;
import ci.ui.TextField.CIDateOfExpiryTextFieldFragment;
import ci.ui.TextField.CIOnlyEnglishTextFieldFragment;
import ci.ui.TextField.CIPassportNumberFieldText;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.view.NavigationBar;
import ci.ui.view.TwoItemSelectBar;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Models.entities.CIApisEntity;
import ci.ws.Models.entities.CIApisNationalEntity;
import ci.ws.Models.entities.CIApisQryRespEntity;
import ci.ws.Models.entities.CICompanionApisEntity;
import ci.ws.Presenter.CIAPISPresenter;
import ci.ws.Presenter.Listener.CIInquiryApisListListener;
import ci.ws.cores.object.GsonTool;

import static ci.ui.TextField.Base.CIBaseTextFieldFragment.afterTextChangedListener;
import static ci.ui.TextField.Base.CITextFieldFragment.TypeMode.NORMAL;
import static ci.ui.view.NavigationBar.onNavigationbarListener;
import static ci.ui.view.NavigationBar.onNavigationbarParameter;

/**
 * Created by jlchen on 2016/3/22.
 */
public class CIPersonalAddAPISActivity extends BaseActivity implements
        View.OnClickListener,
        View.OnTouchListener{

    private onNavigationbarParameter m_onNavigationParameter = new onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            switch (m_type){
                case ADD_MY_APIS:
                case ADD_COMPANAIONS_APIS:
                    return m_Context.getString(R.string.add_apis);
                case EDIT_MY_APIS:
                case EDIT_COMPANAIONS_APIS:
                    return m_Context.getString(R.string.edit) +" "+ m_strAPISName;
                default:
                    return m_Context.getString(R.string.add_apis);
            }
        }
    };

    private onNavigationbarListener m_onNavigationbarListener = new onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            CIPersonalAddAPISActivity.this.finish();
        }

        @Override
        public void onDeleteClick() {
            CIAlertDialog dialog = new CIAlertDialog(m_Context, new CIAlertDialog.OnAlertMsgDialogListener() {
                @Override
                public void onAlertMsgDialog_Confirm() {

//                    if( CIPersonalAddAPISType.EDIT_MY_APIS == m_type ) {
//                        CIAPISPresenter.getInstance().DeleteApisFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo(), m_apisEntity.doc_type, m_onInquiryApisListListener);
//
//                    } else if( CIPersonalAddAPISType.EDIT_COMPANAIONS_APIS == m_type ) {
//
//                        CICompanionApisEntity entity = new CICompanionApisEntity();
//
//                        entity.doc_type         = m_apisEntity.doc_type;
//                        entity.doc_no           = m_apisEntity.doc_no;
//                        entity.nationality      = m_apisEntity.nationality;
//                        entity.doc_expired_date = m_apisEntity.doc_expired_date;
//                        entity.issue_country    = m_apisEntity.issue_country;
//                        entity.resident_city    = m_apisEntity.resident_city;
//                        entity.last_name        = m_apisEntity.last_name;
//                        entity.first_name       = m_apisEntity.first_name;
//                        entity.birthday         = m_apisEntity.birthday;
//                        entity.sex              = m_apisEntity.sex;
//                        entity.addr_street      = m_apisEntity.addr_street;
//                        entity.addr_city        = m_apisEntity.addr_city;
//                        entity.addr_state       = m_apisEntity.addr_state;
//                        entity.addr_country     = m_apisEntity.addr_country;
//                        entity.addr_zipcode     = m_apisEntity.addr_zipcode;
//                        entity.card_no          = CIApplication.getLoginInfo().GetUserMemberCardNo();
//                        entity.full_name        = (TextUtils.isEmpty(entity.first_name)? "" : entity.first_name.toUpperCase()) + (TextUtils.isEmpty(entity.last_name)? "" : entity.last_name.toUpperCase() );
//                        entity.setId(entity.full_name, entity.card_no, entity.doc_type);
//
//                        CIAPISPresenter.getInstance().deleteCompanionApis(entity);
//
//                        setResult(RESULT_OK);
//                        CIPersonalAddAPISActivity.this.finish();
//
//                    }
                }

                @Override
                public void onAlertMsgDialogg_Cancel() {
                    //取消
                }
            });
            dialog.uiSetTitleText(getString(R.string.delete_apis_title));
            dialog.uiSetContentText(getString(R.string.delete_apis_msg));
            dialog.uiSetConfirmText(getString(R.string.delete_apis_delete));
            dialog.uiSetCancelText(getString(R.string.delete_apis_cancel));

            dialog.show();
        }

        @Override
        public void onDemoModeClick() {}
    };

    private NavigationBar.onNavigationbarInterface m_onNavigationbarInterface = null;

    afterTextChangedListener m_nationalityListener = new afterTextChangedListener() {
        @Override
        public void afterTextChangedListener(Editable editable) {

            /**如果選擇 usa permanent resident card 則要顯示地址欄位*/
            if (editable.toString().equals(getString(R.string.usa_permanent_resident_card))) {
                m_llayout_Address_Info.setVisibility(View.VISIBLE);
            } else {
                m_llayout_Address_Info.setVisibility(View.GONE);
            }
        }
    };

    private CIInquiryApisListListener m_onInquiryApisListListener = new CIInquiryApisListListener() {
        @Override
        public void InquiryApisSuccess(String rt_code, String rt_msg, CIApisQryRespEntity apis) { }

        @Override
        public void InquiryApisError(String rt_code, String rt_msg) { }

        @Override
        public void InsertApidSuccess(String rt_code, String rt_msg) {

            CIAPISPresenter.getInstance().saveMyApis(m_newApisEntity);

            setResult(RESULT_OK);
            CIPersonalAddAPISActivity.this.finish();
        }

        @Override
        public void InsertApisError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void UpdateApisSuccess(String rt_code, String rt_msg) {

            CIAPISPresenter.getInstance().saveMyApis(m_newApisEntity);

            setResult(RESULT_OK);
            CIPersonalAddAPISActivity.this.finish();
        }

        @Override
        public void UpdateApisError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void InsertUpdateApisSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void InsertUpdateApisError(String rt_code, String rt_msg) {

        }

        @Override
        public void DeleteApisSuccess(String rt_code, String rt_msg) {

            CIAPISPresenter.getInstance().deleteMyApis(m_apisEntity);
            //刪除
            setResult(RESULT_OK);

            CIPersonalAddAPISActivity.this.finish();
        }

        @Override
        public void DeleteApisError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }

        @Override
        public void onAuthorizationFailedError(String rt_code, String rt_msg) {
            isProcessWSErrorCodeByOtherActivity(rt_code, rt_msg);
        }
    };

    public enum CIPersonalAddAPISType {
        ADD_MY_APIS, EDIT_MY_APIS, ADD_COMPANAIONS_APIS, EDIT_COMPANAIONS_APIS;
    }

    private static final int RESIDENT_CD    = 1;
    private static final int ISSUE_CD       = 2;
//    private static final int COUNTRY_CD     = 0;

    private CIPersonalAddAPISType m_type = CIPersonalAddAPISType.ADD_MY_APIS;
    private CIApisDocmuntTextFieldFragment.EType m_apisType = null;
    private String m_strAPISName = "";
    private String m_strUserName = "";
//    private String[] m_strData;

    public NavigationBar        m_Navigationbar                     = null;
    public FrameLayout          m_flayout_Content                   = null;

//    private Bitmap              m_bitmap                            = null;
//    private ScrollView          m_sv                                = null;

    private RelativeLayout      m_rlayout                           = null;

    private TextView            m_tvMsg                             = null;
    private TextView            m_tvName                            = null;

    private LinearLayout        m_llayout_name                      = null;
    private CITextFieldFragment m_FirstNamefragment                 = null,
                                m_LastNamefragment                  = null;

    private TwoItemSelectBar    m_vGender                           = null;
    private CITextFieldFragment m_DateOfBirthdayfragment            = null,
                                m_ResidentCountryFragment           = null,
                                m_Nationalityfragment               = null,
                                m_DocumentTypefragment              = null,
                                m_DocumentNoFragment                = null,
                                m_IssueCountryFragment              = null,
                                m_DocExpiryDatefragment             = null;

    private LinearLayout        m_llayout_Address_Info              = null;
    private CITextFieldFragment m_AddressNationalityfragment        = null,
                                m_CityStatfragment                  = null,
                                m_CityCountyDistrictfragment        = null,
                                m_Streetfragment                    = null,
                                m_ZipCodeFragment                   = null;

    private Button              m_btnSave                           = null;

    private String              m_errorMsg                          = null;

    private HashMap<String, CIApisDocmuntTypeEntity>    m_apisDocmuntType   = null;
    private ArrayList<CIApisDocmuntTypeEntity>          m_arApisDocmuntList = null;
    private CIApisEntity                                m_apisEntity        = null;
    private CIApisEntity                                m_newApisEntity     = null;
    private ArrayList<CIApisNationalEntity>             m_arApisNationList  = null;
    private boolean                                     m_bInitializedAPIS  = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String mode = getIntent().getStringExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE);
        m_apisType = (CIApisDocmuntTextFieldFragment.EType)getIntent()
                .getSerializableExtra(CIApisDocmuntTextFieldFragment.APIS_TYPE);
        if (null != mode) {
            m_type = CIPersonalAddAPISType.valueOf(mode);
        }

        String strAPISName = getIntent().getStringExtra(UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_TAG);
        if (null != strAPISName && 0 < strAPISName.length()) {
            m_strAPISName = strAPISName;
        }

        String strUserName = getIntent().getStringExtra(UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_USER_NAME_TAG);
        if (null != strUserName && 0 < strUserName.length()) {
            m_strUserName = strUserName;
        }else {
            m_strUserName = CIApplication.getLoginInfo().GetUserName();
        }

        String strData = getIntent().getStringExtra(UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_DATA_TAG);
        if (null != strData) {
//            m_strData = strData.split(";");
            m_apisEntity = GsonTool.toObject(strData,CIApisEntity.class);
        }

        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initialLayoutComponent() {

        if( m_arApisDocmuntList == null || m_arApisDocmuntList.size() == 0 ) {
            m_arApisDocmuntList = CIAPISPresenter.getInstance().fetchApisList();
        }

        String[] apisDocmuntList = new String[m_arApisDocmuntList.size()];
        for( int iPos = 0; iPos < m_arApisDocmuntList.size() ; iPos++ ) {
            String strName = m_arApisDocmuntList.get(iPos).getName(CIApplication.getLanguageInfo().getLanguage_Locale());
            apisDocmuntList[iPos] = strName;
        }
        if( m_apisDocmuntType == null ) {
            m_apisDocmuntType = CIAPISPresenter.getInstance().fetchApisDocmuntMap();
        }

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_flayout_Content = (FrameLayout) findViewById(R.id.container);

        View ViewContent = View.inflate(this, R.layout.fragment_personal_add_apis, null);
        m_flayout_Content.addView(ViewContent);

//        m_sv            = (ScrollView) ViewContent.findViewById(R.id.sv_root);
//        m_bitmap        = ImageHandle.getLocalBitmap(m_Context, R.drawable.bg_login, 1);
//        Drawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmap);
//        m_sv.setBackground(drawable);

        m_vGender       = (TwoItemSelectBar) ViewContent.findViewById(R.id.v_gender);
        m_vGender.setSelectMode(TwoItemSelectBar.ESelectSMode.LEFT);

        m_rlayout                   = (RelativeLayout) ViewContent.findViewById(R.id.rlayout);
        m_rlayout.setOnTouchListener(this);

        m_tvMsg                     = (TextView) ViewContent.findViewById(R.id.tv_msg);
        m_tvName                    = (TextView) ViewContent.findViewById(R.id.tv_name);
        if ( 0 < m_strUserName.length())
            m_tvName.setText(m_strUserName);

        m_llayout_name              = (LinearLayout)ViewContent.findViewById(R.id.ll_companions_name);
        m_FirstNamefragment         = CIOnlyEnglishTextFieldFragment.newInstance("*" + getString(R.string.sign_up_first_name));
        m_LastNamefragment          = CIOnlyEnglishTextFieldFragment.newInstance("*" + getString(R.string.sign_up_last_name));

        m_DateOfBirthdayfragment    = CIDateOfBirthdayTextFieldFragment.newInstance("*"+getString(R.string.inquiry_input_box_date_of_birth_hint));
        m_ResidentCountryFragment   = CIApisNationalTextFieldFragment.newInstance("*" + getString(R.string.resident_country),CIApisNationalTextFieldFragment.EMode.ResidentNational);
        m_Nationalityfragment       = CIApisNationalTextFieldFragment.newInstance("*" + getString(R.string.sign_up_nationality),CIApisNationalTextFieldFragment.EMode.IssueNational);

        m_DocumentTypefragment      = CIApisDocmuntTextFieldFragment.newInstance( "*" + getString(R.string.document_type), m_apisType);

        if( CIPersonalAddAPISType.ADD_MY_APIS == m_type || CIPersonalAddAPISType.EDIT_MY_APIS == m_type ) {
            HashSet<String> docTypeList = CIAPISPresenter.getInstance().getMyApisExistDocumentTypeList(CIApplication.getLoginInfo().GetUserMemberCardNo());
            if( CIPersonalAddAPISType.EDIT_MY_APIS == m_type && null != m_apisEntity ) {
                docTypeList.remove(m_apisEntity.doc_type);
            }
            ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeFilter(docTypeList);
        }

        m_DocumentNoFragment        = CIPassportNumberFieldText.newInstance("*" + getString(R.string.document_number));
        m_IssueCountryFragment      = CIApisNationalTextFieldFragment.newInstance("*" + getString(R.string.country_of_issuance), CIApisNationalTextFieldFragment.EMode.IssueNational);
        m_DocExpiryDatefragment     = CIDateOfExpiryTextFieldFragment.newInstance("*" + getString(R.string.date_of_expiry));

        m_llayout_Address_Info      = (LinearLayout)ViewContent.findViewById(R.id.llayout_Address);

        //顯示Address 欄位
        m_llayout_Address_Info.setVisibility(View.VISIBLE);


        m_AddressNationalityfragment= CIApisNationalTextFieldFragment.newInstance("*" + getString(R.string.apis_address_country),CIApisNationalTextFieldFragment.EMode.IssueNational);
        m_CityStatfragment          = CIApisStateTextFieldFragment.newInstance("*" + getString(R.string.city_stat));
        m_CityCountyDistrictfragment= CICustomTextFieldFragment.newInstance("*" + getString(R.string.city_county_district),NORMAL);
        m_Streetfragment            = CIPassportNumberFieldText.newInstance("*" + getString(R.string.street));
        m_ZipCodeFragment           = CIPassportNumberFieldText.newInstance("*" + getString(R.string.zip_code));

        m_btnSave                   = (Button) ViewContent.findViewById(R.id.btn_finish);
        m_btnSave.setOnClickListener(this);
        m_btnSave.setText(getString(R.string.finish));

    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_flayout_Content);

        m_tvMsg.setMinHeight(vScaleDef.getLayoutHeight(113.3));

        switch (m_type){
            case ADD_MY_APIS:
                m_tvName.setVisibility(View.VISIBLE);
                m_llayout_name.setVisibility(View.GONE);
                m_tvMsg.setVisibility(View.VISIBLE);
                break;
            case EDIT_MY_APIS:
                m_tvName.setVisibility(View.VISIBLE);
                m_llayout_name.setVisibility(View.GONE);
                m_tvMsg.setVisibility(View.GONE);
                break;
            case ADD_COMPANAIONS_APIS:
                m_tvName.setVisibility(View.GONE);
                m_llayout_name.setVisibility(View.VISIBLE);
                m_tvMsg.setVisibility(View.VISIBLE);
                break;
            case EDIT_COMPANAIONS_APIS:
                m_tvName.setVisibility(View.VISIBLE);
                m_llayout_name.setVisibility(View.GONE);
                m_tvMsg.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void setOnParameterAndListener() {
        m_onNavigationbarInterface  = m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);

        switch (m_type){
            case EDIT_MY_APIS:
            case EDIT_COMPANAIONS_APIS:
                m_onNavigationbarInterface.showGarbageButton();
                break;
        }
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace( R.id.flayout_firstname,    m_FirstNamefragment,        m_FirstNamefragment.toString());
        fragmentTransaction.replace( R.id.flayout_lastname,     m_LastNamefragment,         m_LastNamefragment.toString());

        fragmentTransaction.replace( R.id.flayout_birthday,     m_DateOfBirthdayfragment,   m_DateOfBirthdayfragment.toString());
        fragmentTransaction.replace( R.id.flayout_country,      m_ResidentCountryFragment,  m_ResidentCountryFragment.toString());
        fragmentTransaction.replace( R.id.flayout_nationality,  m_Nationalityfragment,      m_Nationalityfragment.toString());
        fragmentTransaction.replace( R.id.flayout_doc_type,     m_DocumentTypefragment,     m_DocumentTypefragment.toString());
        fragmentTransaction.replace( R.id.flayout_doc_no,       m_DocumentNoFragment,       m_DocumentNoFragment.toString());
        fragmentTransaction.replace( R.id.flayout_issue_country,m_IssueCountryFragment,     m_IssueCountryFragment.toString());
        fragmentTransaction.replace( R.id.flayout_date,         m_DocExpiryDatefragment,    m_DocExpiryDatefragment.toString());
        //address
        fragmentTransaction.replace( R.id.flayout_address_Nationality,  m_AddressNationalityfragment,   m_AddressNationalityfragment.toString());
        fragmentTransaction.replace( R.id.flayout_address_city_stat,    m_CityStatfragment,             m_CityStatfragment.toString());
        fragmentTransaction.replace( R.id.flayout_address_county,       m_CityCountyDistrictfragment,   m_CityCountyDistrictfragment.toString());
        fragmentTransaction.replace( R.id.flayout_address_street,       m_Streetfragment,               m_Streetfragment.toString());
        fragmentTransaction.replace(R.id.flayout_address_zipcode, m_ZipCodeFragment, m_ZipCodeFragment.toString());
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {

    }

    @Override
    protected void onLanguageChangeUpdateUI() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if( false == m_bInitializedAPIS ) {
            m_bInitializedAPIS = true;
            setPersonalAPIS();
        }

    }

    private String getCountryName(String strCountryCd, int iCountryType) {

        if(TextUtils.isEmpty(strCountryCd)) {
            return "";
        }

//        if( RESIDENT_CD == iCountryType || ISSUE_CD == iCountryType ) {
        if (null == m_arApisNationList || 0 >= m_arApisNationList.size()) {
            m_arApisNationList = CIAPISPresenter.getInstance().fetchApisNationalList();
        }

        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();

        for (CIApisNationalEntity entity : m_arApisNationList) {
            if ((entity.resident_cd.equals(strCountryCd) && RESIDENT_CD == iCountryType)
                    || (entity.issue_cd.equals(strCountryCd) && ISSUE_CD == iCountryType) ) {
                return entity.getCountryName(locale);
            }
        }
//        }
//        else if( COUNTRY_CD == iCountryType ) {
//            return CIInquiryNationalPresenter.getInstance(null).getNationalMap().get(strCountryCd).country_name;
//        }

        return "";
    }

    private String getStateName(String strStateCode) {
        if(TextUtils.isEmpty(strStateCode)) {
            return "";
        }

        if( null != CIAPISPresenter.getInstance().fetchApisStateMap().get(strStateCode) ) {
            String name = CIAPISPresenter.getInstance().fetchApisStateMap().get(strStateCode).name;

            if( !TextUtils.isEmpty(name) ) {
                return name;
            }

        } else {
            return "";
        }

        return "";
    }

    private String getDocmuntName(String strDocType) {
        if( TextUtils.isEmpty(strDocType) ) {
            return "";
        }

        CIApisDocmuntTypeEntity entity = CIAPISPresenter.getInstance().fetchApisDocmuntMap().get(strDocType);

        if( null != entity ) {
            return entity.getName(CIApplication.getLanguageInfo().getLanguage_Locale());
        } else {
            return strDocType;
        }

    }

    /**
     * 設定APIS初始資料
     */
    private void setPersonalAPIS() {


        if( null != m_AddressNationalityfragment ) {
            m_AddressNationalityfragment.setLock(true);
            m_AddressNationalityfragment.setText( getCountryName("USA",ISSUE_CD));
            ((CIApisNationalTextFieldFragment)m_AddressNationalityfragment).setCountryCd("USA");
        }

        if( null != m_ZipCodeFragment ) {
            m_ZipCodeFragment.setMaxLenght(5);
        }

        if( CIPersonalAddAPISType.EDIT_MY_APIS == m_type || CIPersonalAddAPISType.EDIT_COMPANAIONS_APIS == m_type ) {
            setAPISData();
        }



    }

    private void setAPISData() {

        //Set Membership data
        if( CIPersonalAddAPISType.EDIT_MY_APIS == m_type
                || CIPersonalAddAPISType.EDIT_COMPANAIONS_APIS == m_type ) {

            final String strGender = m_apisEntity.sex ;
            if( CIApisEntity.SEX_FEMALE.equals(strGender) ) {
                m_vGender.setSelectMode(TwoItemSelectBar.ESelectSMode.RIGHT);
            } else {
                m_vGender.setSelectMode(TwoItemSelectBar.ESelectSMode.LEFT);
            }


            m_DateOfBirthdayfragment.setText(m_apisEntity.birthday);
            ((CIDateOfBirthdayTextFieldFragment)m_DateOfBirthdayfragment).setFormatedDate(m_apisEntity.birthday);

        }

        if( CIPersonalAddAPISType.EDIT_COMPANAIONS_APIS == m_type ) {
            m_FirstNamefragment.setText(m_apisEntity.first_name );
            m_FirstNamefragment.setLock(true);

            m_LastNamefragment.setText( m_apisEntity.last_name );
            m_LastNamefragment.setLock(true);
        }

        m_ResidentCountryFragment.setText(getCountryName(m_apisEntity.resident_city, RESIDENT_CD));
        ((CIApisNationalTextFieldFragment) m_ResidentCountryFragment).setCountryCd(m_apisEntity.resident_city);

        m_Nationalityfragment.setText(getCountryName(m_apisEntity.nationality, ISSUE_CD));
        ((CIApisNationalTextFieldFragment) m_Nationalityfragment).setCountryCd(m_apisEntity.nationality);

        m_DocumentTypefragment.setText(getDocmuntName(m_apisEntity.doc_type));
        ((CIApisDocmuntTextFieldFragment) m_DocumentTypefragment).setDocmuntType(m_apisEntity.doc_type);
        m_DocumentTypefragment.setLock(true);


        m_DocumentNoFragment.setText(m_apisEntity.doc_no);

        m_IssueCountryFragment.setText(getCountryName(m_apisEntity.issue_country, ISSUE_CD));
        ((CIApisNationalTextFieldFragment) m_IssueCountryFragment).setCountryCd(m_apisEntity.issue_country);


        ((CIDateOfExpiryTextFieldFragment)m_DocExpiryDatefragment).setFormatedDate(m_apisEntity.doc_expired_date);
        m_DocExpiryDatefragment.setText( m_apisEntity.doc_expired_date );

        //設定City/State
        if( !TextUtils.isEmpty(m_apisEntity.addr_state)) {
            ((CIApisStateTextFieldFragment) m_CityStatfragment).setStateCode(m_apisEntity.addr_state);
            m_CityStatfragment.setText(getStateName(m_apisEntity.addr_state));//getCountryName(m_apisEntity.addr_state,COUNTRY_CD) );
        }

        //設定City/Country/District
        if( !TextUtils.isEmpty(m_apisEntity.addr_city)) {
            m_CityCountyDistrictfragment.setText(m_apisEntity.addr_city);
        }

        //設定street
        if( !TextUtils.isEmpty(m_apisEntity.addr_street)) {
            m_Streetfragment.setText( m_apisEntity.addr_street);
        }

        //設定 zipcode
        if( !TextUtils.isEmpty(m_apisEntity.addr_zipcode)) {
            m_ZipCodeFragment.setText( m_apisEntity.addr_zipcode);
        }

    }

    @Override
    protected void onDestroy() {
//        if (null != m_sv) {
//            m_sv.setBackground(null);
//        }
//        if (null != m_bitmap) {
//            ImageHandle.recycleBitmap(m_bitmap);
//        }
        System.gc();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    public void onClick(View v) {
        if (R.id.btn_finish == v.getId()){

            if( false == isFillCompleteAndCorrect() ) {

                showDialog(getString(R.string.warning),
                        m_errorMsg);
                return;

            } else if( CIPersonalAddAPISType.ADD_MY_APIS == m_type ) {
                showDialog(getString(R.string.editapis_alert_title_save_and_upload_your_apis),
                        getString(R.string.editapis_alert_message_save_and_upload_your_apis),
                        getString(R.string.confirm), getString(R.string.cancel),
                        onAlertAddMyApisDialogListener);


            } else if( CIPersonalAddAPISType.EDIT_MY_APIS == m_type ) {
                sendUpdateApisFromWS();

            } else if( CIPersonalAddAPISType.ADD_COMPANAIONS_APIS == m_type || CIPersonalAddAPISType.EDIT_COMPANAIONS_APIS == m_type) {
                saveCompanionsApisFromDB( getApisEntity() );
            }

        }
    }

    private CIAlertDialog.OnAlertMsgDialogListener onAlertAddMyApisDialogListener = new CIAlertDialog.OnAlertMsgDialogListener() {
        @Override
        public void onAlertMsgDialog_Confirm() {
            sendInsertApisFromWS();
        }

        @Override
        public void onAlertMsgDialogg_Cancel() {
            showDialog(getString(R.string.editapis_alert_title_save_and_upload_your_apis),
                    getString(R.string.editapis_alert_message_do_not_save_and_upload_your_apis),
                    getString(R.string.confirm), getString(R.string.cancel),
                    onAlertSaveMyApisDialogListener);
        }
    };

    private CIAlertDialog.OnAlertMsgDialogListener onAlertSaveMyApisDialogListener = new CIAlertDialog.OnAlertMsgDialogListener() {
        @Override
        public void onAlertMsgDialog_Confirm() {
            //2016-07-22 第二次詢問只需要儲存, 不需要上傳
            saveCompanionsApisFromDB(getApisEntity());
            //sendInsertApisFromWS();
        }

        @Override
        public void onAlertMsgDialogg_Cancel() {
            CIPersonalAddAPISActivity.this.finish();
        }
    };

    private void saveCompanionsApisFromDB( CIApisEntity apisEntity) {
        CICompanionApisEntity entity = new CICompanionApisEntity();

        entity.doc_type         = apisEntity.doc_type;
        entity.doc_no           = apisEntity.doc_no;
        entity.nationality      = apisEntity.nationality;
        entity.doc_expired_date = apisEntity.doc_expired_date;
        entity.issue_country    = apisEntity.issue_country;
        entity.resident_city    = apisEntity.resident_city;
        entity.last_name        = apisEntity.last_name;
        entity.first_name       = apisEntity.first_name;
        entity.birthday         = apisEntity.birthday;
        entity.sex              = apisEntity.sex;
        entity.addr_street      = apisEntity.addr_street;
        entity.addr_city        = apisEntity.addr_city;
        entity.addr_state       = apisEntity.addr_state;
        entity.addr_country     = apisEntity.addr_country;
        entity.addr_zipcode     = apisEntity.addr_zipcode;
        entity.card_no          = CIApplication.getLoginInfo().GetUserMemberCardNo();
        entity.full_name        = (TextUtils.isEmpty(entity.first_name)? "" : entity.first_name.toUpperCase()) + (TextUtils.isEmpty(entity.last_name)? "" : entity.last_name.toUpperCase() );
        entity.setId(entity.full_name, entity.card_no, entity.doc_type);

        CIAPISPresenter.getInstance().saveCompanionApis(entity);

        setResult(RESULT_OK);
        CIPersonalAddAPISActivity.this.finish();
    }

    private void sendInsertApisFromWS() {

        CIApisEntity ciApisEntity = getApisEntity();

        setMyApisEntity(ciApisEntity);

        //CIAPISPresenter.getInstance().InsertApisFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo(), m_onInquiryApisListListener, ciApisEntity);

    }

    private void setMyApisEntity(CIApisEntity data) {

        if( m_newApisEntity != null ){
            m_newApisEntity = null;
        }

        m_newApisEntity = (CIApisEntity)data.clone();

        m_newApisEntity.card_no =  CIApplication.getLoginInfo().GetUserMemberCardNo();

        if( CIPersonalAddAPISType.ADD_MY_APIS == m_type) {
            m_newApisEntity.setId(m_newApisEntity.card_no, m_newApisEntity.doc_type);
        } else if( CIPersonalAddAPISType.EDIT_MY_APIS == m_type ){
            m_newApisEntity.setId(m_apisEntity.card_no, m_apisEntity.doc_type);
        }
    }

    private CIApisEntity getApisEntity() {
        /** 居住國家必選 */
        String strResidentCountryCd = ((CIApisNationalTextFieldFragment)m_ResidentCountryFragment).getCountryCd();

        /** 國籍必選 */
        String strNationality = ((CIApisNationalTextFieldFragment)m_Nationalityfragment).getCountryCd();

        /** 證件類別必選 */
        String strDocumentType = ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).getDocmuntType();

        /** 證件號碼必填 */
        String strDocumentNo = m_DocumentNoFragment.getText();

        /** 發證國家必選 */
        String strIssueCountryCd = ((CIApisNationalTextFieldFragment)m_IssueCountryFragment).getCountryCd();

        /** 效期截止日期必選 */
        String strExpiryDate = ((CIDateOfExpiryTextFieldFragment)m_DocExpiryDatefragment).getFormatedDate();


        CIApisEntity ciApisEntity = new CIApisEntity();
        ciApisEntity.doc_type = strDocumentType;
        ciApisEntity.nationality = strNationality;
        ciApisEntity.doc_expired_date = strExpiryDate;
        ciApisEntity.resident_city = strResidentCountryCd;
        ciApisEntity.issue_country = strIssueCountryCd;
        ciApisEntity.doc_no = strDocumentNo;

        if( CIPersonalAddAPISType.ADD_MY_APIS == m_type || CIPersonalAddAPISType.EDIT_MY_APIS == m_type ) {
            ciApisEntity.first_name = CIApplication.getLoginInfo().GetUserFirstName();//CIApplication.getLoginInfo().GetUserProfileFirstName();
            ciApisEntity.last_name = CIApplication.getLoginInfo().GetUserLastName();//CIApplication.getLoginInfo().GetUserProfileLastName();

        } else if( CIPersonalAddAPISType.ADD_COMPANAIONS_APIS == m_type || CIPersonalAddAPISType.EDIT_COMPANAIONS_APIS == m_type ) {
            ciApisEntity.first_name = m_FirstNamefragment.getText();
            ciApisEntity.last_name = m_LastNamefragment.getText();

        }

        if(TwoItemSelectBar.ESelectSMode.LEFT == m_vGender.getSelectModeParam() ) {
            ciApisEntity.sex = CIApisEntity.SEX_MALE;
        } else {
            ciApisEntity.sex = CIApisEntity.SEX_FEMALE;
        }

        ciApisEntity.birthday = ((CIDateOfBirthdayTextFieldFragment)m_DateOfBirthdayfragment).getFormatedDate();//CIApplication.getLoginInfo().GetBirthday();


        String strAddrCountry = ((CIApisNationalTextFieldFragment)m_AddressNationalityfragment).getCountryCd();
        if( TextUtils.isEmpty(strAddrCountry) ) {
            strAddrCountry = "";
        }
        ciApisEntity.addr_country = strAddrCountry;

        String strAddrState = ((CIApisStateTextFieldFragment)m_CityStatfragment).getStateCode();
        if( TextUtils.isEmpty(strAddrState) ) {
            strAddrState = "";
        }
        ciApisEntity.addr_state = strAddrState;

        String strAddrCity = m_CityCountyDistrictfragment.getText();
        ciApisEntity.addr_city = strAddrCity;

        String strAddrStreet = m_Streetfragment.getText();
        ciApisEntity.addr_street = strAddrStreet;

        String strAddrZipcode = m_ZipCodeFragment.getText();
        ciApisEntity.addr_zipcode = strAddrZipcode;

        return ciApisEntity;
    }


    private void sendUpdateApisFromWS() {

        CIApisEntity ciApisEntity = getApisEntity();

        setMyApisEntity(ciApisEntity);

        //CIAPISPresenter.getInstance().UpdateApisFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo(), m_onInquiryApisListListener, ciApisEntity);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(R.id.rlayout == v.getId()){
            HidekeyBoard();
        }
        return false;
    }

    private boolean isFillCompleteAndCorrect() {

        /**
         * 初始化錯誤訊息
         */
        m_errorMsg = getString(R.string.please_fill_all_text_field_that_must_to_fill);

        if( CIPersonalAddAPISType.ADD_COMPANAIONS_APIS == m_type || CIPersonalAddAPISType.EDIT_COMPANAIONS_APIS == m_type ) {
            String strFirstName = m_FirstNamefragment.getText();
            String strLastName  = m_LastNamefragment.getText();

            if( TextUtils.isEmpty(strFirstName) || TextUtils.isEmpty(strLastName) ) {
                return false;
            }

        }


        /** 出生日期必填*/
        String strBirthday = ((CIDateOfBirthdayTextFieldFragment)m_DateOfBirthdayfragment).getFormatedDate();
        if( TextUtils.isEmpty(strBirthday) ) {
            return false;
        }

        /** 居住國家必選 */
        String strCountryCd = ((CIApisNationalTextFieldFragment)m_ResidentCountryFragment).getCountryCd();
        if( TextUtils.isEmpty(strCountryCd) ) {
            return false;
        }

        /** 國籍必選 */
        String strNationality = ((CIApisNationalTextFieldFragment)m_Nationalityfragment).getCountryCd();
        if( TextUtils.isEmpty(strNationality) ) {
            return false;
        }

        /** 證件類別必選 */
        String strDocumentType = ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).getDocmuntType();
        if( TextUtils.isEmpty(strDocumentType) ) {
            return false;
        }

        /** 證件號碼必填 */
        String strDocumentNo = m_DocumentNoFragment.getText();
        if( TextUtils.isEmpty(strDocumentNo) ) {
            return false;
        }

        /** 發證國家必選 */
        String strIssueCountryCd = ((CIApisNationalTextFieldFragment)m_IssueCountryFragment).getCountryCd();
        if( TextUtils.isEmpty(strIssueCountryCd) ) {
            return false;
        }

        /** 效期截止日期必選 */
        String strExpiryDate = ((CIDateOfExpiryTextFieldFragment)m_DocExpiryDatefragment).getFormatedDate();
        if( TextUtils.isEmpty(strExpiryDate) ) {
            return false;
        }

        return true;

    }
}
