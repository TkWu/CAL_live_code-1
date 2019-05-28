package ci.ui.APIS;


import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import ci.function.Base.BaseFragment;
import ci.function.Checkin.CIAPISDef;
import ci.function.Core.CIApplication;
import ci.ui.TextField.Base.CIBaseTextFieldFragment;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIApisDocmuntTextFieldFragment;
import ci.ui.TextField.CIApisNationalTextFieldFragment;
import ci.ui.TextField.CIApisStateTextFieldFragment;
import ci.ui.TextField.CICustomTextFieldFragment;
import ci.ui.TextField.CIDateOfBirthdayTextFieldFragment;
import ci.ui.TextField.CIDateOfExpiryTextFieldFragment;
import ci.ui.TextField.CIPassportNumberFieldText;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CILanguageInfo;
import ci.ui.view.TwoItemSelectBar;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Models.entities.CIApisEntity;
import ci.ws.Models.entities.CIApisNationalEntity;
import ci.ws.Models.entities.CIApisResp;
import ci.ws.Models.entities.CIApisStateEntity;
import ci.ws.Models.entities.CICheckInApisEntity;
import ci.ws.Models.entities.CICheckInDocaEntity;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICompanionApisEntity;
import ci.ws.Presenter.CIAPISPresenter;
import ci.ws.Presenter.Listener.CIInquiryApisListListener;

import static ci.ui.TextField.Base.CITextFieldFragment.TypeMode.NORMAL;

/**
 * Created by Ryan on 16/3/18.
 */
public class CIAPISFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener{

    public static final String BUNDLE_PARA_FIRSTNAME        = "FirstName";
    public static final String BUNDLE_PARA_LASTNAME         = "LastName";
    public static final String BUNDLE_PARA_APIS             = "Apis";
    public static final String BUNDLE_PARA_DOCA             = "Doca";
    public static final String BUNDLE_PARA_ITINERARY_INFO   = "CICheckInPax_ItineraryInfoEntity";


    private LinearLayout            m_llayout_root  = null;
    private TextView                m_tvName        = null;

    private TwoItemSelectBar        m_vGender   = null;
    private CITextFieldFragment     //m_APISDocumentFragment            = null,
                                    m_DateOfBirthdayfragment            = null,
                                    m_ResidentCountryFragment           = null,
                                    m_Nationalityfragment               = null,
                                    m_DocumentTypefragment              = null,
                                    m_DocumentNoFragment                = null,
                                    m_IssueCountryFragment              = null,
                                    m_DocExpiryDatefragment             = null;

    private RelativeLayout          m_rlayout_other_doc_checkbox        = null;
    private ImageView               m_ivOtherDocCheckBox                = null;
    private TextView                m_tvOtherDocNotice                  = null;

    private LinearLayout            m_llayout_second_doc                = null;
    private FrameLayout             m_flayout_issue_Country             = null;
    private FrameLayout             m_flayout_second_issue_Country      = null;
    private FrameLayout             m_flayout_second_DocExpiryDate      = null;

    private CITextFieldFragment     m_SecondDocumentTypefragment        = null,
                                    m_SecondDocumentNoFragment          = null,
                                    m_SecondIssueCountryFragment        = null,
                                    m_SecondDocExpiryDateFragment       = null;

    private LinearLayout            m_llayout_Address_Info              = null;
    private CITextFieldFragment     m_AddressNationalityfragment        = null,
                                    m_CityStatfragment                  = null,
                                    m_CityCountyDistrictfragment        = null,
                                    m_Streetfragment                    = null,
                                    m_ZipCodeFragment                   = null;

    private RelativeLayout  m_rlayout_CheckBox  = null;
    private ImageView       m_ivCheckBox        = null;
    private TextView        m_tvCheckText       = null;

    private String          m_strName           = "";
    private String          m_strFirstName      = "";
    private String          m_strLastName       = "";
    private ArrayList<CICheckInPax_ItineraryInfoEntity> m_arItinerary_InfoList = null;

    private ArrayList<ApisInputData> m_arApis       = null;
    private ApisInputData            m_tempApisData = null;

    private int             m_iCurrentDocType       = 0;
    private boolean         m_bIsMyApis             = false;
    private Date            m_dExpiryDate           = null;
    private String          m_strErrorMsg           = "";

    //航班狀態
    private CIAPISDef.CIRouteType m_enRouteType = CIAPISDef.CIRouteType.normal;

    private boolean m_bHaveSeconDoc = false;

    private CIApisDocmuntTypeEntity m_docPassportEntity = null;

    /** 確認有無從WS拿到合法的APIS*/
    private boolean         m_bGetApisFromWs      = false;
    private boolean         m_bSetApisInfoFromWS  = false;

    private static HashMap<String, CIApisNationalEntity> m_ResidentMap  = null;
    private static HashMap<String, CIApisNationalEntity> m_IssueMap     = null;
    private static HashMap<String, CIApisStateEntity>    m_StateMap     = null;
    //private static HashMap<String, CIApisDocmuntTypeEntity> m_DocumentTypeMap = null;
    //2018-09-01 新的結構，依國籍區分證件種類，因同一個證件類別在不同國籍也可能代表不同證件
    private static ArrayList<CIApisDocmuntTypeEntity> m_arDocumentTypeList = null;

    public class ApisInputData {
        public TwoItemSelectBar.ESelectSMode gender = TwoItemSelectBar.ESelectSMode.LEFT;

        public String birthday = "";

        public String residentCountryCd = "";
        public String residentCountryName = "";

        public String nationalityCd = "";
        public String nationalityName = "";

        public String docType = "";
        public String docTypeName = "";
//        public int    docTypeSelectPosition = -1;
        public String docNo = "";

        public String issueCountryCd = "";
        public String issueCountryName = "";

        public String docExpiryDate = "";

        public CICheckInDocaEntity Doca;

        public DocInfo secondDoc = new DocInfo();

        public boolean bSelectedGreenCard = false;

        public class DocInfo {
            public String docType = "";
            public String docTypeName = "";

            public String docNo = "";

            public String issueCountryCd = "";
            public String issueCountryName = "";

            public String docExpiryDate = "";
        }
    }

    private enum ApisDataType {
        DocList,TempData
    }

    public CIAPISFragment(){

    }

    private void addApis(CICheckInApisEntity Apis, CICheckInDocaEntity Doca ) {
        if( null == m_arApis ) {
            m_arApis = new ArrayList<>();
        }

        if( null == Apis ) {
            return;
        }

        if( null == m_ResidentMap ) {
            m_ResidentMap = CIAPISPresenter.getInstance().fetchApisNationalResidentMap();
        }

        if( null == m_IssueMap ) {
            m_IssueMap = CIAPISPresenter.getInstance().fetchApisNationalIssueMap();
        }

        ApisInputData inputData = new ApisInputData();

        inputData.gender = ( CIApisEntity.SEX_FEMALE.equals(Apis.Pax_Sex) )? TwoItemSelectBar.ESelectSMode.RIGHT : TwoItemSelectBar.ESelectSMode.LEFT;

        inputData.birthday = Apis.Pax_Birth;

        CIApisNationalEntity residentEntity = m_ResidentMap.get(Apis.Resident_Country);
        if( null != residentEntity) {
            inputData.residentCountryCd = Apis.Resident_Country;
            inputData.residentCountryName = residentEntity.getCountryName(CIApplication.getLanguageInfo().getLanguage_Locale());
        }

        CIApisNationalEntity nationalityEntity = m_IssueMap.get(Apis.Nationality);
        if( null != nationalityEntity ) {
            inputData.nationalityCd = Apis.Nationality;
            inputData.nationalityName = nationalityEntity.getCountryName(CIApplication.getLanguageInfo().getLanguage_Locale());
        }


        if( TextUtils.isEmpty(Apis.Document_Type) ) {
            //若無 docType資料則當作非法APIS
            return;

        } else {

            if ( null == m_arDocumentTypeList ){
                m_arDocumentTypeList = CIAPISPresenter.getInstance().fetchAllApisList();
            }

            CIApisDocmuntTypeEntity docEntity = getDocumentTypeInfo( Apis.Document_Type, Apis.Issue_Country);
            if (null != docEntity) {
                inputData.docType = Apis.Document_Type;
                inputData.docTypeName = docEntity.getName(CIApplication.getLanguageInfo().getLanguage_Locale());
            } else {
                //若無 docType資料則當作非法APIS
                return;
            }
        }

        inputData.docNo = Apis.Document_No;

        CIApisNationalEntity issueEntity = m_IssueMap.get(Apis.Issue_Country);
        if( null != issueEntity ) {
            inputData.issueCountryCd = Apis.Issue_Country;
            inputData.issueCountryName = issueEntity.getCountryName(CIApplication.getLanguageInfo().getLanguage_Locale());
        }

        inputData.docExpiryDate = Apis.Docuemnt_Eff_Date;

        inputData.Doca = Doca;

        //確認有從CheckInAllPaxByPNR or CheckInAllPaxByTicket拿到合法的Apis
        m_bGetApisFromWs = true;

        m_arApis.add(inputData);
    }


    public enum EAPISMode {
        USE_SAVE,FILL_NEW,NEW
    }

    private void initiaPara(){

        CICheckInApisEntity Apis = null;
        CICheckInDocaEntity Doca = null;

        Bundle bundle = getArguments();
        if ( null != bundle ){

            m_strFirstName = bundle.getString(BUNDLE_PARA_FIRSTNAME, "");
            m_strLastName = bundle.getString(BUNDLE_PARA_LASTNAME, "");
            Apis = (CICheckInApisEntity)bundle.getSerializable(BUNDLE_PARA_APIS);
            Doca = (CICheckInDocaEntity)bundle.getSerializable(BUNDLE_PARA_DOCA);
            m_arItinerary_InfoList = (ArrayList<CICheckInPax_ItineraryInfoEntity>)bundle.getSerializable(BUNDLE_PARA_ITINERARY_INFO);
        }

        //組合Section使用的文字
        m_strName = m_strFirstName + " " + m_strLastName;

        //檢查是否為User本人。若是，則從MyApis拿取List;若否，則從同行旅客拿取List
        if( CIApplication.getLoginInfo().GetUserFirstName().equals(m_strFirstName) && CIApplication.getLoginInfo().GetUserLastName().equals(m_strLastName) ) {
            m_bIsMyApis = true;
        } else {
            m_bIsMyApis = false;
        }

        addApis(Apis,Doca);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_apis_view;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_llayout_root  = (LinearLayout)view.findViewById(R.id.llayout_root);
        m_llayout_root.setOnTouchListener(this);
        m_tvName        = (TextView)view.findViewById(R.id.tv_name);
        m_vGender       = (TwoItemSelectBar)view.findViewById(R.id.v_gender);

        initiaPara();

        String[] docTypeList = new String[m_arApis.size()];
        for( int iPos = 0 ; iPos < m_arApis.size() ; iPos++ ) {
            docTypeList[iPos] = m_arApis.get(iPos).docTypeName;
        }

        m_DateOfBirthdayfragment    = CIDateOfBirthdayTextFieldFragment.newInstance("*"+getString(R.string.inquiry_input_box_date_of_birth_hint));
        m_ResidentCountryFragment   = CIApisNationalTextFieldFragment.newInstance("*"+getString(R.string.resident_country), CIApisNationalTextFieldFragment.EMode.ResidentNational);
        m_Nationalityfragment       = CIApisNationalTextFieldFragment.newInstance("*"+getString(R.string.sign_up_nationality), CIApisNationalTextFieldFragment.EMode.IssueNational);
        m_Nationalityfragment.setAfterTextChangedListener(m_NationalityListener);

        m_DocumentTypefragment      = CIApisDocmuntTextFieldFragment.newInstance("*"+getString(R.string.document_type),
                                                                                 CIApisDocmuntTextFieldFragment.EType.CheckIn);

        m_DocumentNoFragment        = CIPassportNumberFieldText.newInstance("*"+getString(R.string.document_number));
        m_IssueCountryFragment      = CIApisNationalTextFieldFragment.newInstance("*" + getString(R.string.country_of_issuance), CIApisNationalTextFieldFragment.EMode.IssueNational);
        m_flayout_issue_Country     = (FrameLayout)view.findViewById(R.id.flayout_issue_country);
        m_DocExpiryDatefragment     = CIDateOfExpiryTextFieldFragment.newInstance("*"+getString(R.string.date_of_expiry));

        //使否有其他證件（綠卡、身分證、台胞證、港澳證件
        m_rlayout_other_doc_checkbox = (RelativeLayout) view.findViewById(R.id.rlayout_other_doc);
        m_rlayout_other_doc_checkbox.setOnClickListener(this);
        m_ivOtherDocCheckBox = (ImageView) view.findViewById(R.id.iv_green_card_checkbox);
        m_tvOtherDocNotice = (TextView) view.findViewById(R.id.tv_other_doc_notice);

        //第二證件資料元件
        m_llayout_second_doc = (LinearLayout) view.findViewById(R.id.llayout_second_document);

        m_SecondDocumentTypefragment = CIApisDocmuntTextFieldFragment.newInstance("*"+getString(R.string.document_type),CIApisDocmuntTextFieldFragment.EType.CheckIn);
        m_SecondDocumentNoFragment   = CIPassportNumberFieldText.newInstance("*"+getString(R.string.document_number));
        m_SecondIssueCountryFragment = CIApisNationalTextFieldFragment.newInstance("*" + getString(R.string.country_of_issuance), CIApisNationalTextFieldFragment.EMode.IssueNational);
        m_SecondDocExpiryDateFragment= CIDateOfExpiryTextFieldFragment.newInstance("*"+getString(R.string.date_of_expiry));
        m_flayout_second_DocExpiryDate  = (FrameLayout)view.findViewById(R.id.flayout_second_expiry_date);
        m_flayout_second_issue_Country  = (FrameLayout)view.findViewById(R.id.flayout_second_issue_country);

        m_llayout_Address_Info = (LinearLayout)view.findViewById(R.id.llayout_Address);
        m_AddressNationalityfragment= CIApisNationalTextFieldFragment.newInstance("*"+getString(R.string.sign_up_nationality), CIApisNationalTextFieldFragment.EMode.IssueNational);
        m_CityStatfragment          = CIApisStateTextFieldFragment.newInstance("*"+getString(R.string.city_stat));
        m_CityCountyDistrictfragment= CICustomTextFieldFragment.newInstance("*"+getString(R.string.city_county_district),NORMAL);
        m_Streetfragment            = CICustomTextFieldFragment.newInstance("*"+getString(R.string.street),NORMAL);
        //m_Streetfragment            = CIPassportNumberFieldText.newInstance("*"+getString(R.string.street));
        m_ZipCodeFragment           = CIPassportNumberFieldText.newInstance("*"+getString(R.string.zip_code));


        m_rlayout_CheckBox  = (RelativeLayout)view.findViewById(R.id.rlayout_checkbox);
        m_rlayout_CheckBox.setOnClickListener(this);
        m_ivCheckBox        = (ImageView)view.findViewById(R.id.iv_checkbox);
        m_tvCheckText       = (TextView)view.findViewById(R.id.tv_save);
        m_ivCheckBox.setSelected(false);
        //2016-10-03 調整顯示邏輯, 如果該APIS 資料不是自己，則不顯示儲存變更的選項s
        if ( m_bIsMyApis ){
            m_rlayout_CheckBox.setVisibility(View.VISIBLE);
        } else {
            m_rlayout_CheckBox.setVisibility(View.GONE);
        }

        m_vGender.setSelectMode(TwoItemSelectBar.ESelectSMode.LEFT);
        m_tvName.setText(m_strName);

//        //Arrival_Station 有經過美國時，顯示Address輸入欄位
//        if( true == m_bArrivalUSA ){
//            m_llayout_Address_Info.setVisibility(View.VISIBLE);
//        } else {
//            m_llayout_Address_Info.setVisibility(View.INVISIBLE);
//        }

        //2018-09-05 證件效期要調整為 出發日+1天
        m_dExpiryDate = AdjustDocExpiryDate();
    }


    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_llayout_root);
        vScaleDef.selfAdjustSameScaleView(m_ivCheckBox, 24, 24);
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace( R.id.flayout_birthday,     m_DateOfBirthdayfragment,   m_DateOfBirthdayfragment.toString());
        fragmentTransaction.replace( R.id.flayout_country,      m_ResidentCountryFragment,  m_ResidentCountryFragment.toString());
        fragmentTransaction.replace( R.id.flayout_nationality,  m_Nationalityfragment,      m_Nationalityfragment.toString());
        fragmentTransaction.replace( R.id.flayout_doc_type,     m_DocumentTypefragment,     m_DocumentTypefragment.toString());
        fragmentTransaction.replace( R.id.flayout_doc_no,       m_DocumentNoFragment,       m_DocumentNoFragment.toString());
        fragmentTransaction.replace( R.id.flayout_issue_country,m_IssueCountryFragment,     m_IssueCountryFragment.toString());
        fragmentTransaction.replace( R.id.flayout_date,         m_DocExpiryDatefragment,    m_DocExpiryDatefragment.toString());
        //address
        fragmentTransaction.replace( R.id.flayout_address_Nationality,  m_AddressNationalityfragment,   m_AddressNationalityfragment.toString());
        fragmentTransaction.replace( R.id.flayout_address_city_stat, m_CityStatfragment, m_CityStatfragment.toString());
        fragmentTransaction.replace( R.id.flayout_address_county, m_CityCountyDistrictfragment, m_CityCountyDistrictfragment.toString());
        fragmentTransaction.replace( R.id.flayout_address_street, m_Streetfragment, m_Streetfragment.toString());
        fragmentTransaction.replace( R.id.flayout_address_zipcode, m_ZipCodeFragment, m_ZipCodeFragment.toString());
        fragmentTransaction.commitAllowingStateLoss();

        //APISModeCheck();

        fragmentTransaction.replace( R.id.flayout_second_doc_type, m_SecondDocumentTypefragment ,m_SecondDocumentTypefragment.toString());
        fragmentTransaction.replace( R.id.flayout_second_doc_no, m_SecondDocumentNoFragment, m_SecondDocumentNoFragment.toString());
        fragmentTransaction.replace( R.id.flayout_second_issue_country, m_SecondIssueCountryFragment, m_SecondIssueCountryFragment.toString());
        fragmentTransaction.replace( R.id.flayout_second_expiry_date , m_SecondDocExpiryDateFragment, m_SecondDocExpiryDateFragment.toString());


        m_llayout_root.post(new Runnable() {
                @Override
                public void run() {
                    //2018-10-10 Ryan, for 取消下一步連動
                    m_DocumentNoFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    m_SecondDocumentNoFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    m_ZipCodeFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
                }
            });
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

    @Override
    public void onResume() {
        super.onResume();

        //是否已將WS給的APIS資料放進畫面
        if(m_bSetApisInfoFromWS) {

            return;
        }

        m_bSetApisInfoFromWS = true;

        //設定郵遞區號長度
        if( null != m_ZipCodeFragment ) {
            m_ZipCodeFragment.setMaxLenght(5);
        }

        //檢查有無APIS
//        if( null == m_arApis || 0 == m_arApis.size() ) {
//            m_SelectBar.getView().setVisibility(View.GONE);
//            //若無APIS資料，則隱藏SelectBar，設定為Fill_New mode
//            m_eAPISMode = EAPISMode.FILL_NEW;
//        }

        if(!CIApplication.getLoginInfo().GetLoginStatus()) {
            m_rlayout_CheckBox.setVisibility(View.GONE);
        }

        //美國地址，地區國家一定是美國
        if( null != m_AddressNationalityfragment ) {
            m_AddressNationalityfragment.setLock(true);
            ((CIApisNationalTextFieldFragment)m_AddressNationalityfragment).setCountryCd("USA");
            m_AddressNationalityfragment.setText(m_IssueMap.get("USA").getCountryName(CIApplication.getLanguageInfo().getLanguage_Locale()));
        }

        APISModeCheck();

    }



    private void APISModeCheck() {
        //預設填入 arApis 第一筆資料
        if( null == m_tempApisData ) {
            setStoreSaveInputData(ApisDataType.DocList);
        } else {
            setStoreSaveInputData(ApisDataType.TempData);
        }
    }


    private void resetAllInput() {

        //性別預設為男性(左邊選項)
        m_vGender.setSelectMode(TwoItemSelectBar.ESelectSMode.LEFT);


        //清空證件類別
//        ((CIDropDownMenuTextFieldFragment) m_APISDocumentFragment).setPosition(-1);
//        m_APISDocumentFragment.setText("");

        //若沒有從WS拿到Apis，則預設New_form_apis的docType為「護照」。
        if (!m_bGetApisFromWs) { //&& m_eAPISMode == EAPISMode.FILL_NEW ){

            if ( null == m_arDocumentTypeList ){
                m_arDocumentTypeList = CIAPISPresenter.getInstance().fetchAllApisList();
            }
            if( null == m_docPassportEntity ) {
                m_docPassportEntity = getDocumentTypeInfo("P", "");
            }
            ((CIApisDocmuntTextFieldFragment) m_DocumentTypefragment).setDocmuntType("P");
            m_DocumentTypefragment.setText(m_docPassportEntity.getName(CIApplication.getLanguageInfo().getLanguage_Locale()));
        } else {
            ((CIApisDocmuntTextFieldFragment) m_DocumentTypefragment).setDocmuntType(null);
            m_DocumentTypefragment.setText("");
        }

        //清空出生日期
        m_DateOfBirthdayfragment.setText("");

        //若沒有從WS拿到Apis，則預設New_form_apis的居住地為「台灣」。
        if (!m_bGetApisFromWs) {
            if( null == m_ResidentMap ) {
                m_ResidentMap = CIAPISPresenter.getInstance().fetchApisNationalResidentMap();
            }
            CIApisNationalEntity residentEntity = m_ResidentMap.get(CIAPISDef.NATIONAL_TWN);
            m_ResidentCountryFragment.setText(residentEntity.getCountryName(CIApplication.getLanguageInfo().getLanguage_Locale()));
            ((CIApisNationalTextFieldFragment) m_ResidentCountryFragment).setCountryCd(CIAPISDef.NATIONAL_TWN);
        } else {
            //清空居住國家
            m_ResidentCountryFragment.setText("");
            ((CIApisNationalTextFieldFragment) m_ResidentCountryFragment).setCountryCd(null);
        }

        //若沒有從WS拿到Apis，則預設New_form_apis的國籍為「台灣」。
        if (!m_bGetApisFromWs) {//
            if( null == m_IssueMap ) {
                m_IssueMap = CIAPISPresenter.getInstance().fetchApisNationalIssueMap();
            }
            CIApisNationalEntity nationalityEntity = m_IssueMap.get(CIAPISDef.NATIONAL_TWN);

            m_Nationalityfragment.setText(nationalityEntity.getCountryName(CIApplication.getLanguageInfo().getLanguage_Locale()));
            ((CIApisNationalTextFieldFragment) m_Nationalityfragment).setCountryCd(CIAPISDef.NATIONAL_TWN);
        } else {
            //清空國籍
            m_Nationalityfragment.setText("");
            ((CIApisNationalTextFieldFragment) m_Nationalityfragment).setCountryCd(null);
        }

        //清空證件號碼
        m_DocumentNoFragment.setText("");

        //清空發證國家
        m_IssueCountryFragment.setText("");
        ((CIApisNationalTextFieldFragment)m_IssueCountryFragment).setCountryCd(null);

        //清空效期截止日期
        m_DocExpiryDatefragment.setText("");

//        //清空國家
//        m_AddressNationalityfragment.setText("");
//        ((CIApisNationalTextFieldFragment)m_AddressNationalityfragment).setCountryCd(null);

        //清空州別
        m_CityStatfragment.setText("");
        ((CIApisStateTextFieldFragment)m_CityStatfragment).setStateCode(null);

        //清空鄉鎮市區
        m_CityCountyDistrictfragment.setText("");

        //清空門牌號碼
        m_Streetfragment.setText("");

        //清空郵遞區號
        m_ZipCodeFragment.setText("");

        //取消勾選「是否有綠卡」
        m_ivOtherDocCheckBox.setSelected(false);
        m_rlayout_other_doc_checkbox.setVisibility(View.VISIBLE);

        //隱藏第二證件區塊
        m_llayout_second_doc.setVisibility(View.GONE);

        //清空第二證件資料
        setSecondDocInfo("", "", false);
    }

    /**
     * 新增旅行證件切換至Save mode，將暫存的Input data填寫回輸入欄位
     */
    private void setStoreSaveInputData(ApisDataType type) {

        if( m_iCurrentDocType >= m_arApis.size() ) {
            resetAllInput();

            //更新航線狀態
            checkRouteType();

            checkDocInfo();
            return;
        }


        ApisInputData m_apisSaveData = null;

        if( ApisDataType.DocList == type ) {
            m_apisSaveData = m_arApis.get(m_iCurrentDocType);
        } else {
            m_apisSaveData = m_tempApisData;
        }


        if(TwoItemSelectBar.ESelectSMode.LEFT == m_apisSaveData.gender ) {
            m_vGender.setSelectMode(TwoItemSelectBar.ESelectSMode.LEFT);
        } else {
            m_vGender.setSelectMode(TwoItemSelectBar.ESelectSMode.RIGHT);
        }

//        ((CIDropDownMenuTextFieldFragment)m_APISDocumentFragment).setPosition(m_iCurrentDocType);
//        m_APISDocumentFragment.setText(m_apisSaveData.docTypeName);

        //設定證件類別
        ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntType(m_apisSaveData.docType);
        m_DocumentTypefragment.setText(m_apisSaveData.docTypeName);


        if( !TextUtils.isEmpty(m_apisSaveData.birthday) ) {
            ((CIDateOfBirthdayTextFieldFragment) m_DateOfBirthdayfragment).setFormatedDate(m_apisSaveData.birthday);
            m_DateOfBirthdayfragment.setText(m_apisSaveData.birthday);
        } else {
            m_DateOfBirthdayfragment.setText(null);
        }

        ((CIApisNationalTextFieldFragment)m_ResidentCountryFragment).setCountryCd(m_apisSaveData.residentCountryCd);
        m_ResidentCountryFragment.setText(m_apisSaveData.residentCountryName);

        ((CIApisNationalTextFieldFragment)m_Nationalityfragment).setCountryCd(m_apisSaveData.nationalityCd);
        m_Nationalityfragment.setText(m_apisSaveData.nationalityName);

        m_DocumentNoFragment.setText(m_apisSaveData.docNo);

        ((CIApisNationalTextFieldFragment)m_IssueCountryFragment).setCountryCd(m_apisSaveData.issueCountryCd);
        m_IssueCountryFragment.setText(m_apisSaveData.issueCountryName);

        String strDocExpiryDate = checkExpiryDate(m_apisSaveData.docExpiryDate);

        //m_DocExpiryDatefragment.setText(m_apisSaveData.docExpiryDate);
        if( !TextUtils.isEmpty(strDocExpiryDate) ) {
            ((CIDateOfExpiryTextFieldFragment) m_DocExpiryDatefragment).setFormatedDate(strDocExpiryDate);
            m_DocExpiryDatefragment.setText(strDocExpiryDate);
        } else {
            m_DocExpiryDatefragment.setText(null);
        }

        if( null == m_apisSaveData.Doca ) {

            ((CIApisStateTextFieldFragment)m_CityStatfragment).setStateCode("");
            m_CityStatfragment.setText("");

            m_CityCountyDistrictfragment.setText("");

            m_Streetfragment.setText("");

            m_ZipCodeFragment.setText("");
            return;
        }

        ((CIApisStateTextFieldFragment)m_CityStatfragment).setStateCode(m_apisSaveData.Doca.Country_Sub_Entity);

        if( null == m_StateMap) {
            m_StateMap = CIAPISPresenter.getInstance().fetchApisStateMap();
        }

        CIApisStateEntity stateEntity = m_StateMap.get(m_apisSaveData.Doca.Country_Sub_Entity);
        if( null != stateEntity ) {
            m_CityStatfragment.setText(stateEntity.name);
        } else {
            m_CityStatfragment.setText("");
        }


        m_CityCountyDistrictfragment.setText(m_apisSaveData.Doca.Traveler_City);

        m_Streetfragment.setText(m_apisSaveData.Doca.Traveler_Address);

        m_ZipCodeFragment.setText(m_apisSaveData.Doca.Traveler_Postcode);

        //是否有綠卡
        m_ivOtherDocCheckBox.setSelected(m_apisSaveData.bSelectedGreenCard);

        //檢查航段
        checkRouteType();

        checkDocInfo();

        //第二證件
        m_SecondDocumentNoFragment.setText( m_apisSaveData.secondDoc.docNo);
        if( !TextUtils.isEmpty(m_apisSaveData.secondDoc.docExpiryDate) ) {
            ((CIDateOfExpiryTextFieldFragment) m_SecondDocExpiryDateFragment).setFormatedDate(m_apisSaveData.secondDoc.docExpiryDate);
            m_SecondDocExpiryDateFragment.setText(m_apisSaveData.secondDoc.docExpiryDate);
        } else {
            m_SecondDocExpiryDateFragment.setText(null);
        }

    }

    @Override
    public void onClick(View v) {
        if ( v.getId() == m_rlayout_CheckBox.getId() ){
            if ( m_ivCheckBox.isSelected() ){
                m_ivCheckBox.setSelected(false);
            } else {
                m_ivCheckBox.setSelected(true);
            }
        } else if( v.getId() == m_rlayout_other_doc_checkbox.getId() ) {

            m_ivOtherDocCheckBox.setSelected( !m_ivOtherDocCheckBox.isSelected() );
            checkDocInfo();
        }
    }

    /**可取得目前CheckBox 是否有被選擇*/
    public boolean getCheckBoxSelectType(){
        return m_ivCheckBox.isSelected();
    }

    public boolean isFillCompleteAndCorrect() {

        m_strErrorMsg = getString(R.string.please_fill_all_text_field_that_must_to_fill);

        //出生日期必填
        String strBirthday = ((CIDateOfBirthdayTextFieldFragment)m_DateOfBirthdayfragment).getFormatedDate();
        if( TextUtils.isEmpty(strBirthday) || TextUtils.isEmpty(m_DateOfBirthdayfragment.getText())) {
            return false;
        }

        //居住國家必選
        String strCountryCd = ((CIApisNationalTextFieldFragment)m_ResidentCountryFragment).getCountryCd();
        if( TextUtils.isEmpty(strCountryCd) ) {
            return false;
        }

        //國籍必選
        String strNationality = ((CIApisNationalTextFieldFragment)m_Nationalityfragment).getCountryCd();
        if( TextUtils.isEmpty(strNationality) ) {
            return false;
        }

        // 證件類別必選
        String strDocumentType = ((CIApisDocmuntTextFieldFragment) m_DocumentTypefragment).getDocmuntType();
        if (TextUtils.isEmpty(strDocumentType)) {
            return false;
        }


        //證件號碼必填
        String strDocumentNo = m_DocumentNoFragment.getText();
        if( TextUtils.isEmpty(strDocumentNo) ) {
            return false;
        }

        //發證國家必選
        String strIssueCountryCd = ((CIApisNationalTextFieldFragment)m_IssueCountryFragment).getCountryCd();
        if( TextUtils.isEmpty(strIssueCountryCd) ) {
            return false;
        }

        //效期截止日期必選
        String strExpiryDate = ((CIDateOfExpiryTextFieldFragment)m_DocExpiryDatefragment).getFormatedDate();
        if( TextUtils.isEmpty(strExpiryDate) || TextUtils.isEmpty(m_DocExpiryDatefragment.getText())) {
            return false;
        }

        if( m_bHaveSeconDoc ) {

            //第二證件類別必選
            String strSecDocumentType = ((CIApisDocmuntTextFieldFragment) m_SecondDocumentTypefragment).getDocmuntType();
            if (TextUtils.isEmpty(strSecDocumentType)) {
                return false;
            }

            //第二證件號碼必填
            String strSecDocumentNo = m_SecondDocumentNoFragment.getText();
            if( TextUtils.isEmpty(strSecDocumentNo)) {
                return false;
            } else if ( !TextUtils.isEmpty(((CIPassportNumberFieldText)m_SecondDocumentNoFragment).getErrorMsgforId()) ) {
                m_strErrorMsg = ((CIPassportNumberFieldText)m_SecondDocumentNoFragment).getErrorMsgforId();
                return false;
            }

            //第二證件發證國家必填
            String strSecIssueCountryCd = ((CIApisNationalTextFieldFragment)m_SecondIssueCountryFragment).getCountryCd();
            if( TextUtils.isEmpty(strSecIssueCountryCd) ) {
                return false;
            }

            //第二證件效期截止日期必選
            if ( !TextUtils.equals( "F", strSecDocumentType) ){
                String strSecExpiryDate = ((CIDateOfExpiryTextFieldFragment)m_SecondDocExpiryDateFragment).getFormatedDate();
                if( TextUtils.isEmpty(strSecExpiryDate) || TextUtils.isEmpty(m_SecondDocExpiryDateFragment.getText())) {
                    return false;
                }
            }
        }

        //如果有經過美國，且不需輸入第二證件，檢查APIS地址是否輸入完畢
        if( View.VISIBLE ==  m_llayout_Address_Info.getVisibility() && m_enRouteType == CIAPISDef.CIRouteType.arrivalUSA ) {
            String strAddrCountry = ((CIApisNationalTextFieldFragment) m_AddressNationalityfragment).getCountryCd();
            //經過美國，國家必選(美國)
            if( TextUtils.isEmpty(strAddrCountry) ) {
                return false;
            }


            String strAddrState = ((CIApisStateTextFieldFragment) m_CityStatfragment).getStateCode();
            //經過美國，州別必選
            if( TextUtils.isEmpty(strAddrState) ) {
                return false;
            }

            //經過美國，鄉鎮市區必填
            String strAddrCity = m_CityCountyDistrictfragment.getText();
            if( TextUtils.isEmpty(strAddrCity) ) {
                return false;
            }

            //經過美國，門牌號碼必填
            String strAddrStreet = m_Streetfragment.getText();
            if( TextUtils.isEmpty(strAddrStreet) ) {
                return false;
            }

            //經過美國，郵遞區號必填
            String strAddrZipcode = m_ZipCodeFragment.getText();
            if( TextUtils.isEmpty(strAddrZipcode) ) {
                return false;
            }
        }

        return true;

    }

    /**錯誤訊息*/
    public String getErrorMsg(){ return  m_strErrorMsg; }


    private ArrayList<CICheckInApisEntity> getApisEntity() {

        ArrayList<CICheckInApisEntity> ar_Apis = new ArrayList<>();

        //居住國家必選
        String strResidentCountryCd = ((CIApisNationalTextFieldFragment)m_ResidentCountryFragment).getCountryCd();

        //國籍必選
        String strNationality = ((CIApisNationalTextFieldFragment)m_Nationalityfragment).getCountryCd();

        //證件類別必選
        String strDocumentType = ((CIApisDocmuntTextFieldFragment) m_DocumentTypefragment).getDocmuntType();

        //證件號碼必填
        String strDocumentNo = m_DocumentNoFragment.getText();

        //發證國家必選
        String strIssueCountryCd = ((CIApisNationalTextFieldFragment)m_IssueCountryFragment).getCountryCd();

        //效期截止日期必選
        String strExpiryDate = ((CIDateOfExpiryTextFieldFragment)m_DocExpiryDatefragment).getFormatedDate();

        String strPaxBirth = ((CIDateOfBirthdayTextFieldFragment)m_DateOfBirthdayfragment).getFormatedDate();

        CICheckInApisEntity ciApisEntity = new CICheckInApisEntity();
        ciApisEntity.Document_Type = strDocumentType;
        ciApisEntity.Nationality = strNationality;
        ciApisEntity.Docuemnt_Eff_Date = strExpiryDate;
        ciApisEntity.Resident_Country = strResidentCountryCd;
        ciApisEntity.Issue_Country = strIssueCountryCd;
        ciApisEntity.Document_No = strDocumentNo;

//        ciApisEntity.first_name = m_strFirstName;
//        ciApisEntity.last_name = m_strLastName;

        if(TwoItemSelectBar.ESelectSMode.LEFT == m_vGender.getSelectModeParam() ) {
            ciApisEntity.Pax_Sex = CIApisEntity.SEX_MALE;
        } else {
            ciApisEntity.Pax_Sex = CIApisEntity.SEX_FEMALE;
        }

        ciApisEntity.Pax_Birth = strPaxBirth;

        ar_Apis.add(ciApisEntity);

        if( m_bHaveSeconDoc ) {

            String strSecDocType =
                    ((CIApisDocmuntTextFieldFragment)m_SecondDocumentTypefragment).getDocmuntType();
            String strSecDocNo = m_SecondDocumentNoFragment.getText();
            String strSecIssue = ((CIApisNationalTextFieldFragment)m_SecondIssueCountryFragment).getCountryCd();
            String strSecExpiryDate = ((CIDateOfExpiryTextFieldFragment)m_SecondDocExpiryDateFragment).getFormatedDate();

            CICheckInApisEntity second_Apis = new CICheckInApisEntity();
            second_Apis.Document_Type = strSecDocType;
            second_Apis.Nationality = strNationality;
            second_Apis.Docuemnt_Eff_Date = strSecExpiryDate;
            second_Apis.Resident_Country = strResidentCountryCd;
            second_Apis.Issue_Country = strSecIssue;
            second_Apis.Document_No = strSecDocNo;
            second_Apis.Pax_Birth = strPaxBirth;

            if(TwoItemSelectBar.ESelectSMode.LEFT == m_vGender.getSelectModeParam() ) {
                second_Apis.Pax_Sex = CIApisEntity.SEX_MALE;
            } else {
                second_Apis.Pax_Sex = CIApisEntity.SEX_FEMALE;
            }

            ar_Apis.add(second_Apis);
        }

        return ar_Apis;
    }

//    private boolean isNeedInputSecondDocument() {
//
//        final String strCountryCd =
//                ((CIApisNationalTextFieldFragment)m_Nationalityfragment).getCountryCd();
//
//        if( CIAPISDef.bIsUSAStation(m_strArrivalStation) && !"USA".equals(strCountryCd) && m_ivOtherDocCheckBox.isSelected()) {
//
//            return true;
//        } else if( CIAPISDef.bIsCNStation(m_strArrivalStation) && CIAPISDef.NATIONAL_TWN.equals(strCountryCd)) {
//
//            return true;
//        }
//
//        return false;
//    }

    /**
     * 拿取Doca
     * 若不需Doca資料，則回傳 null
     */
    @Deprecated
    private ArrayList<CICheckInDocaEntity> getDoca() {

        String strNationality = ((CIApisNationalTextFieldFragment)m_Nationalityfragment).getCountryCd();
        ArrayList<CICheckInDocaEntity> ar_Doca = null;
        if( View.VISIBLE ==  m_llayout_Address_Info.getVisibility() && m_enRouteType == CIAPISDef.CIRouteType.arrivalUSA ) {
//            String strAddrCountry = ((CIApisNationalTextFieldFragment) m_AddressNationalityfragment).getCountryCd();

            CICheckInDocaEntity doca = new CICheckInDocaEntity();

            String strAddrState = ((CIApisStateTextFieldFragment) m_CityStatfragment).getStateCode();

            String strAddrCity = m_CityCountyDistrictfragment.getText();

            String strAddrStreet = m_Streetfragment.getText();

            String strAddrZipcode = m_ZipCodeFragment.getText();

            doca.Traveler_Address = strAddrStreet;
            doca.Traveler_City = strAddrCity;
            doca.Traveler_Postcode = strAddrZipcode;
            doca.Country_Sub_Entity = strAddrState;

            ar_Doca = new ArrayList<>();
            ar_Doca.add(doca);
        }

        return  ar_Doca;
    }

    /**
     * 拿取Doca
     * 若不需Doca資料，則回傳 null
     */
    private CICheckInDocaEntity getSingleDoca() {

        //String strNationality = ((CIApisNationalTextFieldFragment)m_Nationalityfragment).getCountryCd();
        CICheckInDocaEntity doca = null;
        if( View.VISIBLE ==  m_llayout_Address_Info.getVisibility() && m_enRouteType == CIAPISDef.CIRouteType.arrivalUSA ) {

            doca = new CICheckInDocaEntity();

            String strAddrState = ((CIApisStateTextFieldFragment) m_CityStatfragment).getStateCode();

            String strAddrCity = m_CityCountyDistrictfragment.getText();

            String strAddrStreet = m_Streetfragment.getText();

            String strAddrZipcode = m_ZipCodeFragment.getText();

            doca.Traveler_Address = strAddrStreet;
            doca.Traveler_City = strAddrCity;
            doca.Traveler_Postcode = strAddrZipcode;
            doca.Country_Sub_Entity = strAddrState;
        }

        return doca;
    }

    /**地址資訊修正為單一筆*/
    private CIApisEntity convertApisFormat(CICheckInApisEntity apis, CICheckInDocaEntity doca ) {

        CIApisEntity ciApisEntity = new CIApisEntity();
        ciApisEntity.doc_type = apis.Document_Type;
        ciApisEntity.nationality = apis.Nationality;
        ciApisEntity.doc_expired_date = apis.Docuemnt_Eff_Date;
        ciApisEntity.resident_city = apis.Resident_Country;
        ciApisEntity.issue_country = apis.Issue_Country;
        ciApisEntity.doc_no = apis.Document_No;

        ciApisEntity.first_name = m_strFirstName;
        ciApisEntity.last_name = m_strLastName;

        ciApisEntity.sex = apis.Pax_Sex;

        ciApisEntity.birthday = apis.Pax_Birth;

        if( null != doca ) {

            ciApisEntity.addr_country = ((CIApisNationalTextFieldFragment) m_AddressNationalityfragment).getCountryCd();

            ciApisEntity.addr_state = doca.Country_Sub_Entity;

            ciApisEntity.addr_city = doca.Traveler_City;

            ciApisEntity.addr_street = doca.Traveler_Address;

            ciApisEntity.addr_zipcode = doca.Traveler_Postcode;
        }


        return ciApisEntity;
    }

    public HashMap<String,Object> getInputAPIS() {

        ArrayList<CICheckInApisEntity> ar_apisEntity = getApisEntity();

        CICheckInDocaEntity DocaEntity = getSingleDoca();

        CICheckInApisEntity apisEntity = ar_apisEntity.get(0);
        //若有勾選 儲存/新增選項，則儲存APIS資料至DB
        //只存護照
        if (m_ivCheckBox.isSelected() && TextUtils.equals("P", apisEntity.Document_No) ) {

            CIApisEntity apis = convertApisFormat( ar_apisEntity.get(0), DocaEntity );
            if(!m_bIsMyApis) {
                saveCompanionsApisFromDB( apis);
            } else {
                saveMyApis( apis );
            }
        }

        HashMap<String,Object> apisMap = new HashMap<>();

        if( null != DocaEntity ) {

            apisMap.put( "Doca", DocaEntity );
        }

        apisMap.put("Apis",ar_apisEntity);

        //TODO:待確認邏輯
        //若第二證件為台胞證，外層ItineraryInfoEntity的國籍需改為CHN
        if( 2 == ar_apisEntity.size() && "CT".equals(ar_apisEntity.get(1).Document_Type) ) {
            apisMap.put("Nationality", CIAPISDef.NATIONAL_CHN);
        } else {
            apisMap.put("Nationality", ar_apisEntity.get(0).Nationality);
        }

        return apisMap;
    }


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

        entity.addr_street = apisEntity.addr_street;
        entity.addr_city = apisEntity.addr_city;
        entity.addr_state = apisEntity.addr_state;
        entity.addr_country = apisEntity.addr_country;
        entity.addr_zipcode = apisEntity.addr_zipcode;

        entity.card_no          = CIApplication.getLoginInfo().GetUserMemberCardNo();
        entity.full_name        = (TextUtils.isEmpty(entity.first_name)? "" : entity.first_name.toUpperCase()) + (TextUtils.isEmpty(entity.last_name)? "" : entity.last_name.toUpperCase() );
        entity.setId(entity.full_name, entity.card_no, entity.doc_type);

        CIAPISPresenter.getInstance().saveCompanionApis(entity);
    }

    private void saveMyApis(CIApisEntity apisEntity) {

        CIAPISPresenter.getInstance().InsertUpdateApisFromWS( CIApplication.getLoginInfo().GetUserMemberCardNo(),m_listener, apisEntity);
    }

    private CIInquiryApisListListener m_listener = new CIInquiryApisListListener() {
        @Override
        public void InquiryApisSuccess(String rt_code, String rt_msg, CIApisResp apis) {

        }

        @Override
        public void InquiryApisError(String rt_code, String rt_msg) {

        }

        @Override
        public void InsertApidSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void InsertApisError(String rt_code, String rt_msg) {

        }

        @Override
        public void UpdateApisSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void UpdateApisError(String rt_code, String rt_msg) {

        }

        @Override
        public void InsertUpdateApisSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void InsertUpdateApisError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void DeleteApisSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void DeleteApisError(String rt_code, String rt_msg) {

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
        }
    };


    CITextFieldFragment.afterTextChangedListener m_NationalityListener = new CIBaseTextFieldFragment.afterTextChangedListener() {
        @Override
        public void afterTextChangedListener(Editable editable) {

            //Reset 第二證件別
            m_ivOtherDocCheckBox.setSelected(false);

            //更新航線狀態
            checkRouteType();

            //判斷證件別
            checkDocInfo();
        }
    };


    /**使用航段來歸類航線*/
    private void checkRouteType(){

        if ( m_arItinerary_InfoList.size() <= 0 ){
            return;
        }


        int iSize = m_arItinerary_InfoList.size();
        int iLast = iSize - 1;

        final String strCountryCd = ((CIApisNationalTextFieldFragment)m_Nationalityfragment).getCountryCd();

        //調整邏輯，判斷所有狀態，當有兩種以上時，則為預設類型
        ArrayList<CIAPISDef.CIRouteType> arRouteType = new ArrayList<>();
        arRouteType.clear();
        m_enRouteType = CIAPISDef.CIRouteType.normal;


        //出發香港
        if ( CIAPISDef.bIsHKGStation(m_arItinerary_InfoList.get(0).Departure_Station) ){

            //抵達台灣
            //特殊邏輯 香港-台灣 當天來回屬於出發香港抵達台灣。
            //Size=2 代表兩張牌卡。
            if (    iSize == 2 &&
                    CIAPISDef.bIsHKGStation(m_arItinerary_InfoList.get(0).Departure_Station)  &&
                    CIAPISDef.bIsTWNStation(m_arItinerary_InfoList.get(0).Arrival_Station)    &&
                    CIAPISDef.bIsTWNStation(m_arItinerary_InfoList.get(1).Departure_Station)  &&
                    CIAPISDef.bIsHKGStation(m_arItinerary_InfoList.get(1).Arrival_Station)    ){

                arRouteType.add(CIAPISDef.CIRouteType.departureHKGArrivalTWN);

            } else if ( CIAPISDef.bIsTWNStation(m_arItinerary_InfoList.get(iLast).Arrival_Station) ) {
                //抵達台灣
                arRouteType.add(CIAPISDef.CIRouteType.departureHKGArrivalTWN);

            } else {

                //檢查是否為中轉航班中轉台灣
                for( int iIdx = 0; iIdx < iLast; iIdx++ ) {
                    if( CIAPISDef.bIsTWNStation(m_arItinerary_InfoList.get(iIdx).Arrival_Station) ) {
                        //中轉航班
                        arRouteType.add(CIAPISDef.CIRouteType.departureHKGTransitTWN);

                        //國籍：港澳，中轉超過24hr 算抵達
                        if ( TextUtils.equals(CIAPISDef.NATIONAL_HKG, strCountryCd) || TextUtils.equals(CIAPISDef.NATIONAL_MAC, strCountryCd) ){
                            String strArrival = m_arItinerary_InfoList.get(iIdx).Display_Arrival_Date_Gmt + " " + m_arItinerary_InfoList.get(iIdx).Display_Arrival_Time_Gmt;
                            String strDeparture = m_arItinerary_InfoList.get(iIdx+1).Display_Departure_Date_Gmt + " " + m_arItinerary_InfoList.get(iIdx+1).Display_Departure_Time_Gmt;
                            if ( IsOver24hour(strArrival, strDeparture) ){
                                arRouteType.add(CIAPISDef.CIRouteType.departureHKGArrivalTWN);
                            }
                        }
                    }
                }
            }

        }


        //抵達香港
        if ( CIAPISDef.bIsHKGStation(m_arItinerary_InfoList.get(iLast).Arrival_Station ) ) {

            //特殊邏輯
            //台灣-香港 當天來回屬於抵達香港。
            //Size=2 代表兩張牌卡。
            if (    iSize == 2 &&
                    CIAPISDef.bIsTWNStation(m_arItinerary_InfoList.get(0).Departure_Station)  &&
                    CIAPISDef.bIsHKGStation(m_arItinerary_InfoList.get(0).Arrival_Station)    &&
                    CIAPISDef.bIsHKGStation(m_arItinerary_InfoList.get(1).Departure_Station)  &&
                    CIAPISDef.bIsTWNStation(m_arItinerary_InfoList.get(1).Arrival_Station)    ){

                arRouteType.add(CIAPISDef.CIRouteType.arrivalHKG);

            } else if ( TextUtils.equals(CIAPISDef.NATIONAL_CHN, strCountryCd) ){
                boolean bTWN = false;
                //抵達香港，大陸國籍需額外判斷是否經台灣中轉
                for( int iIdx = 0; iIdx < iLast; iIdx++ ) {
                    if (CIAPISDef.bIsTWNStation(m_arItinerary_InfoList.get(iIdx).Arrival_Station)) {
                        bTWN = true;
                        break;
                    }
                }
                if ( bTWN ){
                    arRouteType.add(CIAPISDef.CIRouteType.arrivalHKGTransitTWN);
                } else {
                    arRouteType.add(CIAPISDef.CIRouteType.arrivalHKG);
                }
            } else {
                arRouteType.add(CIAPISDef.CIRouteType.arrivalHKG);
            }
        }

        //抵達大陸
        if ( CIAPISDef.bIsCNStation(m_arItinerary_InfoList.get(iLast).Arrival_Station) ){

            if (    TextUtils.equals(CIAPISDef.NATIONAL_CHN, strCountryCd) ||
                    TextUtils.equals(CIAPISDef.NATIONAL_HKG, strCountryCd) || TextUtils.equals(CIAPISDef.NATIONAL_MAC, strCountryCd)){

                //判斷是否為兩岸航線：台灣出發，中轉站必須是台灣或中國
                if ( CIAPISDef.bIsTWNStation(m_arItinerary_InfoList.get(0).Departure_Station) ){
                    boolean bTWN_CHN = true;
                    for( int iIdx = 0; iIdx < iLast; iIdx++ ) {
                        if (    !CIAPISDef.bIsTWNStation(m_arItinerary_InfoList.get(iIdx).Arrival_Station)  &&
                                !CIAPISDef.bIsCNStation(m_arItinerary_InfoList.get(iIdx).Arrival_Station)   ) {
                            bTWN_CHN = false;
                            break;
                        }
                    }
                    if ( bTWN_CHN ){
                        arRouteType.add(CIAPISDef.CIRouteType.arrivalCHNCrossstraitOnly);
                    } else {
                        //抵達大陸，非兩岸航線，就是純抵達大陸
                        arRouteType.add(CIAPISDef.CIRouteType.arrivalCHN);
                    }
                } else {
                    //抵達大陸，非兩岸航線，就是純抵達大陸
                    arRouteType.add(CIAPISDef.CIRouteType.arrivalCHN);
                }
            } else {
                arRouteType.add(CIAPISDef.CIRouteType.arrivalCHN);
            }
        }

        //抵達美國
        if ( CIAPISDef.bIsUSAStation(m_arItinerary_InfoList.get(iLast).Arrival_Station)   ){
            arRouteType.add(CIAPISDef.CIRouteType.arrivalUSA);
        }

        //抵達加拿大
        if ( CIAPISDef.bIsCANStation(m_arItinerary_InfoList.get(iLast).Arrival_Station) ) {
            arRouteType.add(CIAPISDef.CIRouteType.arrivalCAN);
        }

        //出發大陸、抵達台灣
        if (    CIAPISDef.bIsCNStation(m_arItinerary_InfoList.get(0).Departure_Station) &&
                CIAPISDef.bIsTWNStation(m_arItinerary_InfoList.get(iLast).Arrival_Station) ) {

            arRouteType.add(CIAPISDef.CIRouteType.departureCHNArrivalTWN);
        }

        if ( CIAPISDef.bIsCNStation(m_arItinerary_InfoList.get(0).Departure_Station) ){
            //出發大陸、中轉台灣
            for( int iIdx = 0; iIdx < iLast; iIdx++ ) {
                if( CIAPISDef.bIsTWNStation(m_arItinerary_InfoList.get(iIdx).Arrival_Station) ) {

                    arRouteType.add(CIAPISDef.CIRouteType.departureCHNTransitTWN);

                    //國籍：港澳，中轉超過24hr 算抵達
                    if ( TextUtils.equals(CIAPISDef.NATIONAL_HKG, strCountryCd) || TextUtils.equals(CIAPISDef.NATIONAL_MAC, strCountryCd) ){
                        String strArrival = m_arItinerary_InfoList.get(iIdx).Display_Arrival_Date_Gmt + " " + m_arItinerary_InfoList.get(iIdx).Display_Arrival_Time_Gmt;
                        String strDeparture = m_arItinerary_InfoList.get(iIdx+1).Display_Departure_Date_Gmt + " " + m_arItinerary_InfoList.get(iIdx+1).Display_Departure_Time_Gmt;
                        if ( IsOver24hour(strArrival, strDeparture) ){
                            arRouteType.add(CIAPISDef.CIRouteType.departureCHNArrivalTWN);
                        }
                    }
                }
            }

        }

        //符合多種航線條件，則改回預設狀態。
        if ( arRouteType.size() == 1 ) {
            m_enRouteType = arRouteType.get(0);
        } else if ( arRouteType.size() <= 0  ){
            m_enRouteType = CIAPISDef.CIRouteType.normal;
        }

    }

    /**使用國籍以及航段來判斷該顯示的證件*/
    private void checkDocInfo() {

        final String strCountryCd = ((CIApisNationalTextFieldFragment)m_Nationalityfragment).getCountryCd();

        m_rlayout_other_doc_checkbox.setVisibility(View.GONE);
        m_llayout_second_doc.setVisibility(View.GONE);
        m_llayout_Address_Info.setVisibility(View.GONE);
        m_bHaveSeconDoc = false;

        switch (m_enRouteType) {

            case arrivalCHN: {
                if ( TextUtils.equals( CIAPISDef.NATIONAL_TWN, strCountryCd) ){
                    //國籍-台灣
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);

                    //第二證件 是否“未持有台胞證”
                    if ( m_ivOtherDocCheckBox.isSelected() ){
                        //身分證
                        setSecondDocInfo("F", CIAPISDef.NATIONAL_TWN, false);
                        HashSet<CIApisDocmuntTypeEntity> docTypeList_ii = new HashSet<>();
                        docTypeList_ii.add(new CIApisDocmuntTypeEntity("F", CIAPISDef.NATIONAL_TWN));
                        ((CIApisDocmuntTextFieldFragment)m_SecondDocumentTypefragment).setDocmuntTypeSelectList(docTypeList_ii);
                    } else {
                        //台胞證
                        setSecondDocInfo("CT", CIAPISDef.NATIONAL_CHN, false);
                        HashSet<CIApisDocmuntTypeEntity> docTypeList_ii = new HashSet<>();
                        docTypeList_ii.add(new CIApisDocmuntTypeEntity("CT", CIAPISDef.NATIONAL_CHN));
                        ((CIApisDocmuntTextFieldFragment)m_SecondDocumentTypefragment).setDocmuntTypeSelectList(docTypeList_ii);
                    }
                    m_rlayout_other_doc_checkbox.setVisibility(View.VISIBLE);
                    m_llayout_second_doc.setVisibility(View.VISIBLE);
                    m_llayout_second_doc.requestFocus();
                    m_tvOtherDocNotice.setText(getString(R.string.check_in_apis_non_ct));
                    m_bHaveSeconDoc = true;

                }
                else if ( TextUtils.equals( CIAPISDef.NATIONAL_CHN, strCountryCd) ){
                    //國籍-大陸
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                    setSecondDocInfo("", "", false);

//                    //ｘ
//                    ////大陸居民往來台灣通行證(CD) /中華人民共和國出入境通行證(PL) /中華人民共和國旅行證(PT)
//                    setDocumentInfo("CD", CIAPISDef.NATIONAL_CHN, false );
//                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
//                    docTypeList.add(new CIApisDocmuntTypeEntity("CD", CIAPISDef.NATIONAL_CHN));
//                    docTypeList.add(new CIApisDocmuntTypeEntity("PL", CIAPISDef.NATIONAL_CHN));
//                    docTypeList.add(new CIApisDocmuntTypeEntity("PT", CIAPISDef.NATIONAL_CHN));
//                    //海員證
//                    docTypeList.add(new CIApisDocmuntTypeEntity("S", CIAPISDef.NATIONAL_CHN));
//                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
//

                } else if (  TextUtils.equals( CIAPISDef.NATIONAL_HKG, strCountryCd) ||  TextUtils.equals( CIAPISDef.NATIONAL_MAC, strCountryCd)  ){
                    //國籍-港、澳
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                    setSecondDocInfo("", "", false);

//                    //兩岸航線- 護照
//                    setDocumentInfo("P", "", false );
//                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
//                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
//                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
//
//                    //第二證件-港澳同胞回鄉證
//                    setSecondDocInfo("CC", CIAPISDef.NATIONAL_CHN, false);
//                    HashSet<CIApisDocmuntTypeEntity> seconddocTypeList = new HashSet<>();
//                    seconddocTypeList.add(new CIApisDocmuntTypeEntity("CC", CIAPISDef.NATIONAL_CHN));
//                    ((CIApisDocmuntTextFieldFragment)m_SecondDocumentTypefragment).setDocmuntTypeSelectList(seconddocTypeList);
//                    m_llayout_second_doc.setVisibility(View.VISIBLE);
//                    m_llayout_second_doc.requestFocus();
//                    m_bHaveSeconDoc = true;


                }
                else {
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                    setSecondDocInfo("", "", false);
                }
            }
            break;
            case arrivalCHNCrossstraitOnly: {
                if ( TextUtils.equals( CIAPISDef.NATIONAL_CHN, strCountryCd) ) {
                    //國籍-大陸
                    //兩岸航線
                    ////大陸居民往來台灣通行證(CD) /中華人民共和國出入境通行證(PL) /中華人民共和國旅行證(PT)
                    setDocumentInfo("CD", CIAPISDef.NATIONAL_CHN, false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("CD", CIAPISDef.NATIONAL_CHN));
                    docTypeList.add(new CIApisDocmuntTypeEntity("PL", CIAPISDef.NATIONAL_CHN));
                    docTypeList.add(new CIApisDocmuntTypeEntity("PT", CIAPISDef.NATIONAL_CHN));
                    //海員證
                    docTypeList.add(new CIApisDocmuntTypeEntity("S", CIAPISDef.NATIONAL_CHN));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);

                    //第二證件-無
                    setSecondDocInfo("", "", false);

                } else if (  TextUtils.equals( CIAPISDef.NATIONAL_HKG, strCountryCd) ||  TextUtils.equals( CIAPISDef.NATIONAL_MAC, strCountryCd)  ){
                    //國籍-港、澳
                    //兩岸航線- 護照
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);

                    //第二證件-港澳同胞回鄉證
                    setSecondDocInfo("CC", CIAPISDef.NATIONAL_CHN, false);
                    HashSet<CIApisDocmuntTypeEntity> seconddocTypeList = new HashSet<>();
                    seconddocTypeList.add(new CIApisDocmuntTypeEntity("CC", CIAPISDef.NATIONAL_CHN));
                    ((CIApisDocmuntTextFieldFragment)m_SecondDocumentTypefragment).setDocmuntTypeSelectList(seconddocTypeList);
                    m_llayout_second_doc.setVisibility(View.VISIBLE);
                    m_llayout_second_doc.requestFocus();
                    m_bHaveSeconDoc = true;

                } else {
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                    setSecondDocInfo("", "", false);
                }
                break;
            }
            case arrivalCHNTransitTWN: {
                if (    TextUtils.equals( CIAPISDef.NATIONAL_CHN, strCountryCd)||
                        TextUtils.equals( CIAPISDef.NATIONAL_HKG, strCountryCd) ||  TextUtils.equals( CIAPISDef.NATIONAL_MAC, strCountryCd)) {
                    //國籍-大陸
                    //國籍-港、澳
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                    setSecondDocInfo("", "", false);

                } else {
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                    setSecondDocInfo("", "", false);
                }
                break;
            }
            case departureCHNArrivalTWN:
            case departureCHNTransitTWN:
            case departureHKGArrivalTWN:
            case departureHKGTransitTWN: {

                if ( TextUtils.equals( CIAPISDef.NATIONAL_TWN, strCountryCd) ){
                    //國籍-台灣
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);

                    //第二證件-無
                    setSecondDocInfo("", "", false);

                } else if ( TextUtils.equals( CIAPISDef.NATIONAL_CHN, strCountryCd) ){
                    //國籍-大陸
                    if (    m_enRouteType == CIAPISDef.CIRouteType.departureCHNArrivalTWN ||
                            m_enRouteType == CIAPISDef.CIRouteType.departureHKGArrivalTWN ){
                        //抵達台灣
                        //大陸居民往來台灣通行證(CD) /中華人民共和國出入境通行證(PL) /中華人民共和國旅行證(PT)
                        setDocumentInfo("CD", CIAPISDef.NATIONAL_CHN, false );
                        HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                        docTypeList.add(new CIApisDocmuntTypeEntity("CD", CIAPISDef.NATIONAL_CHN));
                        docTypeList.add(new CIApisDocmuntTypeEntity("PL", CIAPISDef.NATIONAL_CHN));
                        docTypeList.add(new CIApisDocmuntTypeEntity("PT", CIAPISDef.NATIONAL_CHN));
                        if ( m_enRouteType == CIAPISDef.CIRouteType.departureCHNArrivalTWN ){
                            //海員證
                            docTypeList.add(new CIApisDocmuntTypeEntity("S", CIAPISDef.NATIONAL_CHN));
                        }
                        ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);

                        //第二證件-無
                        setSecondDocInfo("", "", false);

                    } else {
                        //中轉台灣
                        //護照
                        setDocumentInfo("P", "", false );
                        HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                        docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                        ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);

                        //2018-09-14 需求變更
                        //第二證件-無
                        setSecondDocInfo("", "", false);

                    }

                } else if (  TextUtils.equals( CIAPISDef.NATIONAL_HKG, strCountryCd) ||  TextUtils.equals( CIAPISDef.NATIONAL_MAC, strCountryCd)  ){
                    //國籍-港、澳
                    if (    m_enRouteType == CIAPISDef.CIRouteType.departureHKGArrivalTWN ||
                            m_enRouteType == CIAPISDef.CIRouteType.departureCHNArrivalTWN ){
                        //護照
                        setDocumentInfo("P", "", false );
                        HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                        docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                        ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                        //第二證件-入台證
                        setSecondDocInfo("CN", "", false);
                        HashSet<CIApisDocmuntTypeEntity> seconddocTypeList = new HashSet<>();
                        seconddocTypeList.add(new CIApisDocmuntTypeEntity("CN", CIAPISDef.NATIONAL_TWN));
                        ((CIApisDocmuntTextFieldFragment)m_SecondDocumentTypefragment).setDocmuntTypeSelectList(seconddocTypeList);
                        m_llayout_second_doc.setVisibility(View.VISIBLE);
                        m_llayout_second_doc.requestFocus();
                        m_bHaveSeconDoc = true;

                    } else {
                        //護照
                        setDocumentInfo("P", "", false );
                        HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                        docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                        ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                        //第二證件-無
                        setSecondDocInfo("", "", false);

                    }
                } else {
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                    //第二證件-無
                    setSecondDocInfo("", "", false);
                }
            }
            break;
            case arrivalHKG: {

                if ( TextUtils.equals( CIAPISDef.NATIONAL_TWN, strCountryCd) ){
                    //國籍-台灣
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                    m_rlayout_other_doc_checkbox.setVisibility(View.VISIBLE);
                    m_tvOtherDocNotice.setText(getString(R.string.check_in_apis_have_ct));

                    //第二證件 是否持有台胞證”
                    if ( m_ivOtherDocCheckBox.isSelected() ){
                        //台胞證
                        setSecondDocInfo("CT", "", false);
                        HashSet<CIApisDocmuntTypeEntity> seconddocTypeList = new HashSet<>();
                        seconddocTypeList.add(new CIApisDocmuntTypeEntity("CT", CIAPISDef.NATIONAL_CHN));
                        ((CIApisDocmuntTextFieldFragment)m_SecondDocumentTypefragment).setDocmuntTypeSelectList(seconddocTypeList);
                        m_llayout_second_doc.setVisibility(View.VISIBLE);
                        m_llayout_second_doc.requestFocus();
                        m_tvOtherDocNotice.setText(getString(R.string.check_in_apis_have_ct));
                        m_bHaveSeconDoc = true;

                    } else {
                        //第二證件-無
                        setSecondDocInfo("", "", false);
                        m_llayout_second_doc.setVisibility(View.GONE);
                        m_bHaveSeconDoc = false;
                    }

                } else if ( TextUtils.equals( CIAPISDef.NATIONAL_CHN, strCountryCd) ){
                    //國籍-大陸
                    // 前往 大陸居民往來台灣通行證(CD)/ 港澳通行證(Q) /因公往來香港澳門特別行政區通行證(CA)
                    // /往來港澳通行證(W) /中華人民共和國出入境通行證(PL) /中華人民共和國旅行證(PT)
                    setDocumentInfo("CD", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("CD", CIAPISDef.NATIONAL_CHN));
                    docTypeList.add(new CIApisDocmuntTypeEntity("Q", CIAPISDef.NATIONAL_CHN));
                    docTypeList.add(new CIApisDocmuntTypeEntity("CA", CIAPISDef.NATIONAL_CHN));
                    docTypeList.add(new CIApisDocmuntTypeEntity("W", CIAPISDef.NATIONAL_CHN));
                    docTypeList.add(new CIApisDocmuntTypeEntity("PL", CIAPISDef.NATIONAL_CHN));
                    docTypeList.add(new CIApisDocmuntTypeEntity("PT", CIAPISDef.NATIONAL_CHN));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);

                    //第二證件-無
                    setSecondDocInfo("", "", false);

                    //多段經台灣中轉
                    boolean bTrue = false;
                    if ( bTrue ){
                        setDocumentInfo("P", "", false );
                        HashSet<CIApisDocmuntTypeEntity> hsdocTypeList = new HashSet<>();
                        hsdocTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                        ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(hsdocTypeList);
                        //第二證件-無
                        setSecondDocInfo("", "", false);
                    }

                } else if (  TextUtils.equals( CIAPISDef.NATIONAL_HKG, strCountryCd) ||  TextUtils.equals( CIAPISDef.NATIONAL_MAC, strCountryCd)  ){
                    //國籍-港、澳
                    //護照
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                    //第二證件-無
                    setSecondDocInfo("", "", false);

                } else {
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                    //第二證件-無
                    setSecondDocInfo("", "", false);
                }
            }
            break;
            case arrivalHKGTransitTWN: {
                if (    TextUtils.equals( CIAPISDef.NATIONAL_CHN, strCountryCd) ) {
                    //國籍-大陸
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                    setSecondDocInfo("", "", false);

                } else {
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                    setSecondDocInfo("", "", false);
                }
                break;
            }
            case arrivalCAN: {
                if ( TextUtils.equals(CIAPISDef.NATIONAL_CAN, strCountryCd) ){
                    //國籍-加拿大
                    //護照
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);

                    //第二證件-無
                    setSecondDocInfo("", "", false);

                } else {
                    //國籍-非加拿大籍
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);

                    m_rlayout_other_doc_checkbox.setVisibility(View.VISIBLE);
                    m_tvOtherDocNotice.setText(getString(R.string.check_in_apis_have_can_c));

                    //第二證件 是否“持有楓葉卡
                    if ( m_ivOtherDocCheckBox.isSelected() ){
                        //楓葉卡
                        setSecondDocInfo("C", CIAPISDef.NATIONAL_CAN, false);
                        HashSet<CIApisDocmuntTypeEntity> seconddocTypeList = new HashSet<>();
                        seconddocTypeList.add(new CIApisDocmuntTypeEntity("C", CIAPISDef.NATIONAL_CAN));
                        ((CIApisDocmuntTextFieldFragment)m_SecondDocumentTypefragment).setDocmuntTypeSelectList(seconddocTypeList);
                        m_llayout_second_doc.setVisibility(View.VISIBLE);
                        m_llayout_second_doc.requestFocus();
                        m_bHaveSeconDoc = true;
                    } else {

                        setSecondDocInfo("", "", false);
                        m_llayout_second_doc.setVisibility(View.GONE);
                        m_bHaveSeconDoc = false;
                    }
                }
            }
            break;
            case arrivalUSA: {
                if ( TextUtils.equals(CIAPISDef.NATIONAL_USA, strCountryCd) ){
                    //國籍-美國
                    //護照
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);

                    //第二證件-無
                    setSecondDocInfo("", "", false);

                } else {
                    //國籍-非美國籍
                    setDocumentInfo("P", "", false );
                    HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                    docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                    ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);

                    m_rlayout_other_doc_checkbox.setVisibility(View.VISIBLE);
                    m_tvOtherDocNotice.setText(getString(R.string.is_there_a_green_card));

                    //第二證件 是否“有綠卡
                    if ( m_ivOtherDocCheckBox.isSelected() ){
                        //綠卡
                        setSecondDocInfo("C", CIAPISDef.NATIONAL_USA, false);
                        HashSet<CIApisDocmuntTypeEntity> seconddocTypeList = new HashSet<>();
                        seconddocTypeList.add(new CIApisDocmuntTypeEntity("C", CIAPISDef.NATIONAL_USA));
                        ((CIApisDocmuntTextFieldFragment)m_SecondDocumentTypefragment).setDocmuntTypeSelectList(seconddocTypeList);
                        m_llayout_second_doc.setVisibility(View.VISIBLE);
                        m_llayout_second_doc.requestFocus();
                        m_llayout_Address_Info.setVisibility(View.GONE);
                        m_bHaveSeconDoc = true;
                    } else {

                        setSecondDocInfo("", "", true);
                        m_llayout_second_doc.setVisibility(View.GONE);
                        m_llayout_Address_Info.setVisibility(View.VISIBLE);
                        m_bHaveSeconDoc = false;
                    }
                }
            }
            break;

            case normal:
            default: {
                setDocumentInfo("P", "", false );
                HashSet<CIApisDocmuntTypeEntity> docTypeList = new HashSet<>();
                docTypeList.add(new CIApisDocmuntTypeEntity("P", ""));
                ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntTypeSelectList(docTypeList);
                //第二證件-無
                setSecondDocInfo("", "", false);
                m_bHaveSeconDoc = false;
            }
        }
    }

    private void setDocumentInfo( String strDocType, String strIssueCountry, boolean bIsLock ){

        CIApisDocmuntTypeEntity apisDocType = getDocumentTypeInfo(strDocType, strIssueCountry);

        CILanguageInfo languageInfo = CIApplication.getLanguageInfo();

        ((CIApisDocmuntTextFieldFragment)m_DocumentTypefragment).setDocmuntType(strDocType);
        m_DocumentTypefragment.setText(apisDocType.getName(languageInfo.getLanguage_Locale()));
        m_DocumentTypefragment.setLock(bIsLock);

        if ( null == apisDocType.issued_country ){
            apisDocType.issued_country = "";
        }

        //沒有指定發證國，則抓取對應表內的發證國家資料
        if ( TextUtils.isEmpty(strIssueCountry) ){
            strIssueCountry = apisDocType.issued_country;
        }

        if ( !TextUtils.isEmpty(strIssueCountry) ){
            ((CIApisNationalTextFieldFragment)m_IssueCountryFragment).setCountryCd(strIssueCountry);
            m_IssueCountryFragment.setText(m_IssueMap.get(strIssueCountry).getCountryName(languageInfo.getLanguage_Locale()));
            m_IssueCountryFragment.setLock(bIsLock);
        }

        //除了護照以外的證件都不顯示發照國
        if ( TextUtils.equals("P", strDocType ) ){
            m_flayout_issue_Country.setVisibility(View.VISIBLE);
        } else {
            m_flayout_issue_Country.setVisibility(View.GONE);
        }
    }

    /**
     * 重置第二證件區塊的資料
     */
    private void setSecondDocInfo(String strDocType, String strIssued_country, boolean bIsLock ) {

        ((CIApisDocmuntTextFieldFragment)m_SecondDocumentTypefragment).setDocmuntType("");
        m_SecondDocumentTypefragment.setText("");
        m_SecondDocumentTypefragment.setLock(false);

        m_SecondDocumentNoFragment.setText("");

        ((CIApisNationalTextFieldFragment)m_SecondIssueCountryFragment).setCountryCd("");
        m_SecondIssueCountryFragment.setText("");
        m_SecondIssueCountryFragment.setLock(false);

        m_SecondDocExpiryDateFragment.setText("");

        //除了護照以外的證件都不顯示發照國
        if ( TextUtils.equals("P",strDocType ) ){
            m_flayout_second_issue_Country.setVisibility(View.VISIBLE);
        } else {
            m_flayout_second_issue_Country.setVisibility(View.GONE);
        }

        if ( TextUtils.isEmpty(strDocType) ){
            return;
        }

        //身分證字號不需要顯示效期
        if ( TextUtils.equals("F",strDocType) ){
            m_flayout_second_DocExpiryDate.setVisibility(View.GONE);
            m_SecondDocExpiryDateFragment.setText("");
        } else {
            m_flayout_second_DocExpiryDate.setVisibility(View.VISIBLE);
        }

        CIApisDocmuntTypeEntity apisDocType = getDocumentTypeInfo(strDocType, strIssued_country);
        if ( null == apisDocType.issued_country ){
            apisDocType.issued_country = "";
        }

        //沒有指定發證國，則抓取對應表內的發證國家資料
        if ( TextUtils.isEmpty(strIssued_country) ){
            strIssued_country = apisDocType.issued_country;
        }


        CILanguageInfo languageInfo = CIApplication.getLanguageInfo();
        //填入指定的證件資料

        ((CIApisDocmuntTextFieldFragment)m_SecondDocumentTypefragment).setDocmuntType(strDocType);
        m_SecondDocumentTypefragment.setText(apisDocType.getName(languageInfo.getLanguage_Locale()));
        m_SecondDocumentTypefragment.setLock(bIsLock);
        ((CIPassportNumberFieldText)m_SecondDocumentNoFragment).setDocmuntType(strDocType);

        if ( !TextUtils.isEmpty(strIssued_country) ){
            ((CIApisNationalTextFieldFragment)m_SecondIssueCountryFragment).setCountryCd(strIssued_country);
            m_SecondIssueCountryFragment.setText(m_IssueMap.get(strIssued_country).getCountryName(languageInfo.getLanguage_Locale()));
            m_SecondIssueCountryFragment.setLock(bIsLock);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(R.id.llayout_root == v.getId()){
            HidekeyBoard();
        }
        return false;
    }

    /**檢查停留時間是否超過24hr*/
    private boolean IsOver24hour( String strArrival, String strDeparture ){

        boolean bResult = false;

        long l24hr = 24 * 3600 * 1000;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {

            Date dArrival = sdf.parse(strArrival);
            Date dDeparture = sdf.parse(strDeparture);
            long lTimediff = dDeparture.getTime() - dArrival.getTime();

            if ( lTimediff >= l24hr ){
                bResult = true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return bResult;
    }

    private CIApisDocmuntTypeEntity getDocumentTypeInfo( String strDocType, String strIssued_country ){

        if ( null == m_arDocumentTypeList ){
            m_arDocumentTypeList = CIAPISPresenter.getInstance().fetchAllApisList();
        }

        for ( CIApisDocmuntTypeEntity DocType : m_arDocumentTypeList ){

            if ( TextUtils.equals(DocType.code_1A, strDocType) ){
                if ( TextUtils.isEmpty(strIssued_country) || TextUtils.equals("P",strDocType) ){
                    return DocType;
                }
                if ( TextUtils.equals(DocType.issued_country, strIssued_country) ){
                    return DocType;
                }
            }
        }

        return m_arDocumentTypeList.get(0);
    }

    private Date AdjustDocExpiryDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        if ( null == m_arItinerary_InfoList ){

            int iMinDay = calendar.get(Calendar.DAY_OF_MONTH) +1;
            calendar.set(Calendar.DAY_OF_MONTH, iMinDay);

            return calendar.getTime();
        }

        CICheckInPax_ItineraryInfoEntity infoEntity = m_arItinerary_InfoList.get(0);

        try {

            Date dDeparture_Date = sdf.parse(infoEntity.Departure_Date);
            calendar.setTime(dDeparture_Date);
            int iMinDay = calendar.get(Calendar.DAY_OF_MONTH) +1;
            calendar.set(Calendar.DAY_OF_MONTH, iMinDay);

            if ( null != m_DocExpiryDatefragment ){
                ((CIDateOfExpiryTextFieldFragment)m_DocExpiryDatefragment).setMinDay(calendar.getTime().getTime());
            }

            if ( null != m_SecondDocExpiryDateFragment ){
                ((CIDateOfExpiryTextFieldFragment)m_SecondDocExpiryDateFragment).setMinDay(calendar.getTime().getTime());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar.getTime();
    }

    private String checkExpiryDate( String strApisDocExpiryDate ){

        if ( TextUtils.isEmpty(strApisDocExpiryDate) ){
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dExpiryDate = null;

        try {
            dExpiryDate = sdf.parse(strApisDocExpiryDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if ( m_dExpiryDate.getTime() > dExpiryDate.getTime() ){
            return sdf.format(m_dExpiryDate);
        } else {
            return sdf.format(dExpiryDate);
        }

    }
}
