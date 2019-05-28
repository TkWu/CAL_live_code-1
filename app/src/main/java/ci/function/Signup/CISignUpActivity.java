package ci.function.Signup;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.function.PersonalDetail.CIPersonalProfileChangePasswordActivity;
import ci.ui.TextField.Base.CIBaseTextFieldFragment;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIAddressSelectFieldFragment;
import ci.ui.TextField.CICustomTextFieldFragment;
import ci.ui.TextField.CIDateOfBirthdayTextFieldFragment;
import ci.ui.TextField.CIDropDownMenuTextFieldFragment;
import ci.ui.TextField.CIEmailTextFieldFragment;
import ci.ui.TextField.CIIdentityCardNoTextFieldFragment;
import ci.ui.TextField.CIMemberNoTextFieldFragment;
import ci.ui.TextField.CIMobilePhoneCountryCodeTextFieldFragment;
import ci.ui.TextField.CINationalityTextFieldFragment;
import ci.ui.TextField.CIOnlyChineseTextFieldFragmnet;
import ci.ui.TextField.CIOnlyEnglishTextFieldFragment;
import ci.ui.TextField.CIOnlyNumberTextFieldFragment;
import ci.ui.TextField.CIPassportNumberFieldText;
import ci.ui.TextField.CIPasswordTextFieldFragment;
import ci.ui.TextField.CIPhoneNumberFieldTextFragment;
import ci.ui.TextField.CISpecialMealSelectTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CILoginInfo;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIAddressEntity;
import ci.ws.Models.entities.CICodeNameEntity;
import ci.ws.Models.entities.CIMealEntity;
import ci.ws.Models.entities.CINationalEntity;
import ci.ws.Models.entities.CIProfileEntity;
import ci.ws.Models.entities.CISignUpReq;
import ci.ws.Models.entities.CISignUpResp;
import ci.ws.Models.entities.CIUpdateProfileEntity;
import ci.ws.Presenter.CIInquiryAddressPresenter;
import ci.ws.Presenter.CIInquiryMealListPresenter;
import ci.ws.Presenter.CIInquiryNationalPresenter;
import ci.ws.Presenter.CIProfilePresenter;
import ci.ws.Presenter.CISignUpWSPresenter;
import ci.ws.Presenter.Listener.CIInquiryAddressListner;
import ci.ws.Presenter.Listener.CIInquiryNationalListner;
import ci.ws.Presenter.Listener.CIInquiryProfileListener;
import ci.ws.Presenter.Listener.CISignUpWSListener;
import ci.ws.cores.object.GsonTool;


public class CISignUpActivity extends BaseActivity
        implements View.OnClickListener,
                   View.OnTouchListener{

    public enum AddMode{
        TW,CN,OTHER
    }

    public enum EMode{
        BASE,EDIT
    }

    private NavigationBar       m_Navigationbar                     = null;
    private CITextFieldFragment m_FirstNamefragment                 = null,
                                m_LastNamefragment                  = null,
                                m_PassportNumberfragment            = null,
                                m_Emailfragment                     = null,
                                m_MobilePhoneNumberfragment         = null,
                                m_passwordfragment                  = null,
                                m_confirmPasswordfragment           = null,
                                m_DEFFirstNameName                  = null,
                                m_DEFLastNameName                   = null;
    private CIMemberNoTextFieldFragment                 m_DEFMembershipCardNumber           = null;
    private CIIdentityCardNoTextFieldFragment           m_IdentityCardNoFragment            = null;
//    private CIPassportChineseNameTextFieldFragment      m_PassportChineseNameFragment       = null;
    private CIOnlyChineseTextFieldFragmnet              m_PassportChineseNameFragment       = null;
    private CISpecialMealSelectTextFieldFragment        m_SpecialMealSelectFragment         = null;
    private CIDateOfBirthdayTextFieldFragment           m_CIDateOfBirthdayFragment          = null;
    private CIDateOfBirthdayTextFieldFragment           m_DEFDateOfBirthdayFragment         = null;
    private CIDropDownMenuTextFieldFragment             m_SpecialSeatSelectFragment         = null;
    private CIDropDownMenuTextFieldFragment             m_SalutationFragment                = null;
    private CIMobilePhoneCountryCodeTextFieldFragment   m_MobilePhoneCountryCodefragment    = null;
    private CINationalityTextFieldFragment              m_NationalityTextFieldFragment      = null;
    //2019-01-24 新增郵寄地址
    private CIDropDownMenuTextFieldFragment             m_AddressTypeFragment           = null;
    private CIAddressSelectFieldFragment                m_AddressCountryFragment        = null;
    private CIAddressSelectFieldFragment                m_AddressCityFragment           = null;
    private CIAddressSelectFieldFragment                m_AddressCountyFragment         = null;
    private CIAddressSelectFieldFragment                m_AddressStreetFragment         = null;
    private CITextFieldFragment                         m_AddressStreetNumFragment      = null;
    private CITextFieldFragment                         m_AddressInfoFragment           = null;
    private CIDropDownMenuTextFieldFragment             m_AddressZipCodeFragment        = null;
    private CIAddressSelectFieldFragment                m_AddressNearestAreaFragment    = null;

    private CITextFieldFragment                         m_AddressCountyCNFragment       = null;
    private CITextFieldFragment                         m_AddressStreetCNFragment       = null;

    private CITextFieldFragment                         m_AddressStreet1Fragment        = null;
    private CITextFieldFragment                         m_AddressStreet2Fragment        = null;
    private CITextFieldFragment                         m_AddressStreet3Fragment        = null;
    private CITextFieldFragment                         m_AddressCityStateTextFragment  = null;
    private CITextFieldFragment                         m_AddressZipCodeTextFragment    = null;
    //2019-01-24 新增郵寄地址

    private ScrollView          m_scrollView                        = null;
    private Button              m_btnSignUp                         = null;
    private Button              m_btnSave                           = null;
    private View                m_vGradient                         = null;
    private EMode               m_mode                              = EMode.BASE;
    private String              m_errorMsg                          = "";
    private ArrayList<CINationalEntity>              m_NationalList = null;
    private HashMap<String, CINationalEntity>        m_NationalMap  = null;
    private HashMap<String, CIMealEntity>              m_MealMap     = null;
    private CITermsAndConditionsActivity.ContentList   m_TermsAndConditionsContentList = null;
    private boolean             m_bReceivePromotionEmail            = false;
    private boolean             m_bReceiveSmsFromChinaAirlines      = false;
    private boolean m_bTermsAndConditions         = false;
    private boolean m_bIsPasswordSame             = false;
    private boolean m_bIsCheckPassportChinessName = false;
    private boolean m_bIsCheckIdentityCardNo      = false;
    private boolean m_bIsCheckGuardianDetail      = false;
    private boolean m_bIsChangeToSocialEmail      = false;
    private boolean m_bIsSocialCombine            = false;
    private int     m_iViewId                     = 0;
    private int     m_legalAdultAge               = 2;
    private int     m_iLegalAdultAgeMillis        = 0;
    private int     m_iTwoYearOldMillis           = 0;
    private int     m_iAgeMillis                  = 0;

    private AddMode m_enumAddressMode             = AddMode.TW;
    private CIProfileEntity m_profile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent data = getIntent();
        if (null != data.getExtras()) {
            String name = data.getStringExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE);
            if(!TextUtils.isEmpty(name)){
                m_mode = EMode.valueOf(name);
            }
            m_bIsSocialCombine  = data.getBooleanExtra(UiMessageDef.BUNDLE_IS_SOCIAL_COMBINE_TAG, false);
            m_iViewId           = data.getIntExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, 0);

            //
            if ( EMode.EDIT == m_mode ){
                m_profile = (CIProfileEntity)data.getSerializableExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA);
            }
        }
        super.onCreate(savedInstanceState);
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            switch (m_mode) {
                case BASE:
                    return m_Context.getString(R.string.sign_up_title);
                case EDIT:
                    return m_Context.getString(R.string.profile);
            }
            return null;
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() { }

        @Override
        public void onBackClick() {
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    @Override
    public void onBackPressed() {
        CISignUpActivity.this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_scrollView    = (ScrollView) findViewById(R.id.sign_up_scrollview);
        m_vGradient     = findViewById(R.id.vGradient);
        m_btnSignUp     = (Button) findViewById(R.id.btn_signup);
        m_btnSave       = (Button) findViewById(R.id.btn_save);

        m_TermsAndConditionsContentList = new CITermsAndConditionsActivity.ContentList();
        CITermsAndConditionsActivity.Content data1 = new CITermsAndConditionsActivity.Content();
        data1.itemTitle = getString(R.string.Qualifications_title);
        data1.itemContent = getString(R.string.Qualifications_content);
        CITermsAndConditionsActivity.Content data2 = new CITermsAndConditionsActivity.Content();
        data2.itemTitle = getString(R.string.Conditions_Membership_Privileges_and_Obligations_title);
        data2.itemContent = getString(R.string.Conditions_Membership_Privileges_and_Obligations_content);
        //2017-01-20 add by Ryan for 新增中華航空保護隱私及資料保安政策
        CITermsAndConditionsActivity.Content data3 = new CITermsAndConditionsActivity.Content();
        data3.itemTitle = getString(R.string.Conditions_Membership_Data_Privacy_Security_Statement_title);
        data3.itemContent = getString(R.string.Conditions_Membership_Data_Privacy_Security_Statement_content);
        //
        m_TermsAndConditionsContentList.add(data1);
        m_TermsAndConditionsContentList.add(data2);
        m_TermsAndConditionsContentList.add(data3);

        if (EMode.EDIT == m_mode) {
            //隱藏會員資格權利義務的項目
            findViewById(R.id.rl_Terms_and_Conditions).setVisibility(View.GONE);
            //隱藏密碼欄位項目標題
            findViewById(R.id.rl_password_title).setVisibility(View.GONE);
            //隱藏輸入密碼欄位
            findViewById(R.id.fragment10).setVisibility(View.GONE);
            findViewById(R.id.fragment11).setVisibility(View.GONE);
            findViewById(R.id.rl_password).setVisibility(View.GONE);
            //隱藏加入會員按鈕
            findViewById(R.id.btn_signup).setVisibility(View.GONE);
            //顯示儲存按鈕
            findViewById(R.id.btn_save).setVisibility(View.VISIBLE);
            //顯示改變密碼標題
            findViewById(R.id.change_password).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_change_password).setVisibility(View.VISIBLE);
            //漸層陰影效果
            findViewById(R.id.vGradient).setVisibility(View.VISIBLE);
        }


        FragmentManager manager = getSupportFragmentManager();

        m_SalutationFragment                = CIDropDownMenuTextFieldFragment.newInstance(this, R.string.sign_up_salutation, R.array.sign_up_salutation_list);
        m_NationalityTextFieldFragment      = CINationalityTextFieldFragment.newInstance();
//        m_PassportChineseNameFragment       = CIPassportChineseNameTextFieldFragment.newInstance("*" + getString(R.string.sign_up_passport_chinese_name));
        m_PassportChineseNameFragment       = CIOnlyChineseTextFieldFragmnet.newInstance("*" + getString(R.string.sign_up_passport_chinese_name));
        m_IdentityCardNoFragment            = CIIdentityCardNoTextFieldFragment.newInstance("*" + getString(R.string.sign_up_identity_card_no));
        m_DEFMembershipCardNumber           = CIMemberNoTextFieldFragment.newInstance("*" + getString(R.string.member_no));
        m_DEFFirstNameName                  = CIOnlyEnglishTextFieldFragment.newInstance("*" + getString(R.string.inquiry_input_box_first_name_hint));
        m_DEFLastNameName                   = CIOnlyEnglishTextFieldFragment.newInstance("*" + getString(R.string.inquiry_input_box_last_name_hint));
        m_DEFDateOfBirthdayFragment         = CIDateOfBirthdayTextFieldFragment.newInstance("*"+getString(R.string.sign_up_date_of_birth_hint));
        m_FirstNamefragment                 = CIOnlyEnglishTextFieldFragment.newInstance("*" + getString(R.string.inquiry_input_box_first_name_hint));
        m_LastNamefragment                  = CIOnlyEnglishTextFieldFragment.newInstance("*"+getString(R.string.inquiry_input_box_last_name_hint));
        m_CIDateOfBirthdayFragment          = CIDateOfBirthdayTextFieldFragment.newInstance("*"+getString(R.string.sign_up_date_of_birth_hint));

        //CR 2017-10-24 調整護照為非必填
        String text = getString(R.string.sign_up_passport_NO);
        m_PassportNumberfragment            = CIPassportNumberFieldText.newInstance(text.replace("*",""));

        m_Emailfragment                     = CIEmailTextFieldFragment.newInstance("*"+getString(R.string.sign_up_email_address));
        m_MobilePhoneCountryCodefragment    = CIMobilePhoneCountryCodeTextFieldFragment.newInstance("*"+getString(R.string.sign_up_mobile_num_country_code));
        m_MobilePhoneNumberfragment         = CIPhoneNumberFieldTextFragment.newInstance("*"+getString(R.string.sign_up_mobile_number));
        m_passwordfragment                  = CIPasswordTextFieldFragment.newInstance("*"+getString(R.string.sign_up_password));
        m_confirmPasswordfragment           = CIPasswordTextFieldFragment.newInstance("*"+getString(R.string.sign_up_confirm_password));
        m_SpecialMealSelectFragment         = CISpecialMealSelectTextFieldFragment.newInstance();
        m_SpecialSeatSelectFragment         = CIDropDownMenuTextFieldFragment.newInstance(getString(R.string.special_seat_preference), R.array.sign_up_special_seat_list);
        //-----2019-01-24 新增郵寄地址-----
        m_AddressTypeFragment           = CIDropDownMenuTextFieldFragment.newInstance("*"+getString(R.string.sign_up_address_type), R.array.sign_up_address_type_list);
        m_AddressCountryFragment        = CIAddressSelectFieldFragment.newInstance("*"+getString(R.string.sign_up_country), CIAddressSelectMenuActivity.EMode.National);
        m_AddressCityFragment           = CIAddressSelectFieldFragment.newInstance("*"+getString(R.string.sign_up_city), CIAddressSelectMenuActivity.EMode.City);
        m_AddressCountyFragment         = CIAddressSelectFieldFragment.newInstance("*"+getString(R.string.sign_up_county), CIAddressSelectMenuActivity.EMode.County);
        m_AddressStreetFragment         = CIAddressSelectFieldFragment.newInstance("*"+getString(R.string.sign_up_street), CIAddressSelectMenuActivity.EMode.Street);
        m_AddressStreetNumFragment      = CICustomTextFieldFragment.newInstance(getString(R.string.sign_up_street_number), CITextFieldFragment.TypeMode.NORMAL);
        m_AddressStreetNumFragment.setMaxLenght(75);
        m_AddressInfoFragment           = CICustomTextFieldFragment.newInstance(getString(R.string.sign_up_address_info), CITextFieldFragment.TypeMode.NORMAL);
        m_AddressInfoFragment.setMaxLenght(75);
        m_AddressZipCodeFragment        = CIDropDownMenuTextFieldFragment.newInstance("*"+getString(R.string.sign_up_zip_code), new String[0]);
        m_AddressNearestAreaFragment    = CIAddressSelectFieldFragment.newInstance("*"+getString(R.string.sign_up_nearest_area), CIAddressSelectMenuActivity.EMode.CurrArea);

        //中國大陸
        m_AddressCountyCNFragment       = CICustomTextFieldFragment.newInstance("*"+getString(R.string.sign_up_county), CITextFieldFragment.TypeMode.NORMAL);
        m_AddressCountyCNFragment.setMaxLenght(18);
        m_AddressStreetCNFragment       = CICustomTextFieldFragment.newInstance("*"+getString(R.string.sign_up_street), CITextFieldFragment.TypeMode.NORMAL);
        m_AddressStreetCNFragment.setMaxLenght(75);
        //中台以外
        m_AddressStreet1Fragment        = CICustomTextFieldFragment.newInstance("*"+getString(R.string.sign_up_street1), CITextFieldFragment.TypeMode.NORMAL);
        m_AddressStreet1Fragment.setMaxLenght(75);
        m_AddressStreet2Fragment        = CICustomTextFieldFragment.newInstance(getString(R.string.sign_up_street2), CITextFieldFragment.TypeMode.NORMAL);
        m_AddressStreet2Fragment.setMaxLenght(75);
        m_AddressStreet3Fragment        = CICustomTextFieldFragment.newInstance(getString(R.string.sign_up_street3), CITextFieldFragment.TypeMode.NORMAL);
        m_AddressStreet3Fragment.setMaxLenght(75);
        m_AddressCityStateTextFragment  = CICustomTextFieldFragment.newInstance("*"+getString(R.string.sign_up_city_state), CITextFieldFragment.TypeMode.NORMAL);
        m_AddressCityStateTextFragment.setMaxLenght(25);
        m_AddressZipCodeTextFragment    = CIOnlyNumberTextFieldFragment.newInstance("*"+getString(R.string.sign_up_zip_code_en));
        m_AddressZipCodeTextFragment.setMaxLenght(9);
        //-----2019-01-24 新增郵寄地址-----

        FragmentTransaction transaction = manager.beginTransaction();
        transaction
                .replace(R.id.fragment1, m_SalutationFragment)
                .replace(R.id.fragment2, m_NationalityTextFieldFragment)
                .replace(R.id.fragment2_1, m_PassportChineseNameFragment)
                .replace(R.id.fragment2_2, m_IdentityCardNoFragment)
                //2016-11-14 Modifly By Ryan, 因應調整名字與姓氏的順序
                .replace(R.id.fragment3, m_LastNamefragment)
                .replace(R.id.fragment4, m_FirstNamefragment)
                //.replace(R.id.fragment4, m_LastNamefragment)
                .replace(R.id.fragment5, m_CIDateOfBirthdayFragment)
                .replace(R.id.fragment5_1, m_DEFMembershipCardNumber)
                //2016-11-14 Modifly By Ryan, 因應調整名字與姓氏的順序
                .replace(R.id.fragment5_2, m_DEFLastNameName)
                .replace(R.id.fragment5_3, m_DEFFirstNameName)
                //.replace(R.id.fragment5_3, m_DEFLastNameName)
                .replace(R.id.fragment5_4, m_DEFDateOfBirthdayFragment)
                .replace(R.id.fragment6, m_PassportNumberfragment)
                .replace(R.id.fragment7, m_Emailfragment)
                .replace(R.id.fragment8, m_MobilePhoneCountryCodefragment)
                .replace(R.id.fragment9, m_MobilePhoneNumberfragment)
                .replace(R.id.fragment10, m_passwordfragment)
                .replace(R.id.fragment11, m_confirmPasswordfragment)
                .replace(R.id.fragment12, m_SpecialMealSelectFragment)
                .replace(R.id.fragment13, m_SpecialSeatSelectFragment)
                //-----2019-01-24 新增郵寄地址-----
                .replace(R.id.fragment_address_type,    m_AddressTypeFragment)
                .replace(R.id.fragment_address_country, m_AddressCountryFragment)
                .replace(R.id.fragment_address1_tw,     m_AddressCityFragment)
                .replace(R.id.fragment_address2_tw,     m_AddressCountyFragment)
                .replace(R.id.fragment_address3_tw,     m_AddressStreetFragment)
                .replace(R.id.fragment_address4_tw,     m_AddressStreetNumFragment)
                .replace(R.id.fragment_address5_tw,     m_AddressInfoFragment)
                .replace(R.id.fragment_address6_tw,     m_AddressZipCodeFragment)
                //CN
                .replace(R.id.fragment_address2_cn,     m_AddressCountyCNFragment)
                .replace(R.id.fragment_address3_cn,     m_AddressStreetCNFragment)
                //Other
                .replace(R.id.fragment_address1,        m_AddressStreet1Fragment)
                .replace(R.id.fragment_address2,        m_AddressStreet2Fragment)
                .replace(R.id.fragment_address3,        m_AddressStreet3Fragment)
                .replace(R.id.fragment_address5,        m_AddressCityStateTextFragment)
                .replace(R.id.fragment_address6,        m_AddressZipCodeTextFragment)
                .replace(R.id.fragment_Nearest_Area,    m_AddressNearestAreaFragment)
                //-----2019-01-24 新增郵寄地址-----
                .commitAllowingStateLoss();

        initTermsAndConditionsTextFormat((TextView) findViewById(R.id.tv_Terms_and_Conditions), getString(R.string.sign_up_trem_and_conditions));


        if( EMode.BASE == m_mode ) {
            findViewById(R.id.root).post(runInitDataForBase);
        }

//        if(EMode.EDIT == m_mode){
//            findViewById(R.id.root).post(runInitDataForEdit);
//        } else {
//            findViewById(R.id.root).post(runInitDataForBase);
//        }

        /**
         * 取得國籍資料，如果為null，Model layer 會再次向ws要資料
         * */
        m_NationalList = CIInquiryNationalPresenter.getInstance(m_InquiryNationalListener).getNationalList();
        if(null != m_NationalList){
            m_NationalMap  = CIInquiryNationalPresenter.getInstance(m_InquiryNationalListener).getNationalMap();
            if(EMode.EDIT == m_mode){
                findViewById(R.id.root).post(runInitDataForEdit);
            }
        }
        m_MealMap = CIInquiryMealListPresenter.getInstance(null).fetchMealMap();

        //取得郵寄地址所需的國家清單
        CIInquiryAddressPresenter.getInstance(m_InquiryAddressListner).getNationalList();
    }

    /**編輯會員資訊*/
    Runnable runInitDataForEdit = new Runnable() {
        @Override
        public void run() {

            CILoginInfo loginInfo = CIApplication.getLoginInfo();
            CISignUpReq data = new CISignUpReq();
            data.surname            = loginInfo.GetSurName();
            data.nation_code        = loginInfo.GetNationCode();
            data.chin_name          = loginInfo.GetChinName();
            data.id_num             = loginInfo.GetIDNum();
            data.first_name         = loginInfo.GetUserProfileFirstName();
            data.last_name          = loginInfo.GetUserProfileLastName();
            data.birth_date         = loginInfo.GetBirthday();
            data.passport           = loginInfo.GetPassport();
            data.guard_card_no      = loginInfo.GetGuardCardNo();
            data.guard_first_name   = loginInfo.GetGuardFirstName();
            data.guard_last_name    = loginInfo.GetGuardLastName();
            data.guard_birth_date   = loginInfo.GetGuardBirthDate();
            data.email              = loginInfo.GetUserEmail();
            data.rcv_email          = loginInfo.GetRcvEmail();
            data.cell_city          = loginInfo.GetCellCity();
            data.cell_num           = loginInfo.GetCellNum();
            data.rcv_sms            = loginInfo.GetRcvSMS();
            data.meal_type          = loginInfo.GetMealType();
            data.seat_code          = loginInfo.GetSeatCode();


            /**稱謂*/
            int surnIndex = CISignUpReq.getSurnameCodeArray().indexOf(data.surname);
            String[] salu = getResources().getStringArray(R.array.sign_up_salutation_list);
            if(0 <= surnIndex){
                m_SalutationFragment.setText(salu[surnIndex]);
            }
            m_SalutationFragment.setLock(true);

            /**國籍*/
            m_NationalityTextFieldFragment.setCountryCd(data.nation_code);
            if(null != m_NationalMap && true == m_NationalMap.containsKey(data.nation_code)){
                CINationalEntity entity = m_NationalMap.get(data.nation_code);
                if(null != entity){
                    m_NationalityTextFieldFragment.setText(entity.country_name);
                }
            }
            m_NationalityTextFieldFragment.setLock(true);

            m_PassportChineseNameFragment.setText(data.chin_name);
            m_PassportChineseNameFragment.setLock(true);

            m_IdentityCardNoFragment.setText(data.id_num);
            m_IdentityCardNoFragment.setLock(true);


            m_FirstNamefragment.setText(data.first_name);
            m_FirstNamefragment.setLock(true);

            m_LastNamefragment.setText(data.last_name);
            m_LastNamefragment.setLock(true);

            m_CIDateOfBirthdayFragment.setFormatedDate(data.birth_date);
            m_CIDateOfBirthdayFragment.setLock(true);

            m_CIDateOfBirthdayFragment.setText(data.birth_date);

            m_PassportNumberfragment.setText(data.passport);

            m_DEFMembershipCardNumber.setText(data.guard_card_no);
            m_DEFMembershipCardNumber.setLock(true);

            m_DEFFirstNameName.setText(data.guard_first_name);
            m_DEFFirstNameName.setLock(true);

            m_DEFLastNameName.setText(data.guard_last_name);
            m_DEFLastNameName.setLock(true);

            if( !TextUtils.isEmpty(data.guard_birth_date) ) {
                m_DEFDateOfBirthdayFragment.setFormatedDate(data.guard_birth_date);
                m_DEFDateOfBirthdayFragment.setText(data.guard_birth_date);
            }
            m_DEFDateOfBirthdayFragment.setLock(true);

            m_Emailfragment.setText(data.email);

            /**國碼*/
            m_MobilePhoneCountryCodefragment.setText(data.cell_city);

            m_MobilePhoneNumberfragment.setText(data.cell_num);

            /**餐點喜好*/
            String meal = "";
            CIMealEntity entity = null;
            if(true == m_MealMap.containsKey(data.meal_type)){
                entity =  m_MealMap.get(data.meal_type);
            }

            if(null != entity){
                Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
                switch (locale.toString()){
                    case "zh_TW":
                        meal = entity.meal_name;
                        break;
                    case "zh_CN":
                        meal = entity.meal_name_cn;
                        break;
                    case "en":
                        meal = entity.meal_name_e;
                        break;
                    case "ja_JP":
                        meal = entity.meal_name_jp;
                        break;
                }
                m_SpecialMealSelectFragment.setText(meal);
                m_SpecialMealSelectFragment.setLock(true);
            }


            /**座位喜好*/
            int seatIndex = CISignUpReq.getSeatCodeArray().indexOf(data.seat_code);
            String[] seat = getResources().getStringArray(R.array.sign_up_special_seat_list);
            if(0 <= seatIndex){
                m_SpecialSeatSelectFragment.setText(seat[seatIndex]);
                m_SpecialSeatSelectFragment.setLock(true);
            }

            HashMap<String,Boolean> map = CISignUpReq.getReciverCodeMap();
            boolean receiver_email = false;
            boolean receiver_sms   = false;

            if(true == map.containsKey(data.rcv_email)) {
                receiver_email = map.get(data.rcv_email);
            }

            if(true == map.containsKey(data.rcv_sms)){
                receiver_sms   = map.get(data.rcv_sms);
            }


            //更新郵寄地址
            CIInquiryAddressPresenter.getInstance().clearDatabyChangeNational();
            if ( null != m_profile ){

                String[] arAddresstype = getResources().getStringArray(R.array.sign_up_address_type_list);
                String[] arAddresstypeCode = getResources().getStringArray(R.array.sign_up_address_type_list_code);
                for ( int iIdx = 0; iIdx < arAddresstypeCode.length; iIdx++ ){
                    String str = arAddresstypeCode[iIdx];
                    if ( TextUtils.equals(m_profile.addressType, str) ){
                        m_AddressTypeFragment.setPosition(iIdx);
                        m_AddressTypeFragment.setText(arAddresstype[iIdx]);
                    }
                }

                m_AddressCountryFragment.setCode(m_profile.countryCode);
                m_AddressCountryFragment.setText(m_profile.countryName);

                m_AddressCityFragment.setCode(m_profile.cityCode);
                m_AddressCityFragment.setText(m_profile.cityName);

                m_AddressCountyFragment.setCode(m_profile.countyCode);
                m_AddressCountyFragment.setText(m_profile.countyName);

                m_AddressStreetFragment.setCode(m_profile.street1);
                m_AddressStreetFragment.setText(m_profile.street1_name);

                m_AddressStreetNumFragment.setText(m_profile.street2);
                m_AddressInfoFragment.setText(m_profile.street3);

                m_AddressZipCodeFragment.setText(m_profile.zipCode);
                if ( m_profile.zipCodeList.size() > 0 ){
                    String[] zipCode = m_profile.zipCodeList.toArray(new String[0]);

                    Bundle bundle = m_AddressZipCodeFragment.getArguments();
                    bundle.putStringArray(CIDropDownMenuTextFieldFragment.ITEM_STRING_ARRAY, zipCode);
                    m_AddressZipCodeFragment.setArguments(bundle);
                }

                m_AddressNearestAreaFragment.setCode(m_profile.currAreaCode);
                m_AddressNearestAreaFragment.setText(m_profile.currAreaName);

                m_AddressCountyCNFragment.setText(m_profile.countyName);
                m_AddressStreetCNFragment.setText(m_profile.street1);

                m_AddressZipCodeTextFragment.setText(m_profile.zipCode);

                m_AddressStreet1Fragment.setText(m_profile.street1);
                m_AddressStreet2Fragment.setText(m_profile.street2);
                m_AddressStreet3Fragment.setText(m_profile.street3);
                m_AddressCityStateTextFragment.setText(m_profile.cityName);

            }


            m_bReceivePromotionEmail = setViewIconSwitch(findViewById(R.id.iv_Receive_promotion_email),
                    !receiver_email);
            m_bReceiveSmsFromChinaAirlines = setViewIconSwitch(findViewById(R.id.iv_Receive_SMS_from_China_Airlines),
                    !receiver_sms);

            /**調整隱藏鍵盤的欄位*/
            m_LastNamefragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
            m_DEFLastNameName.setImeOptions(EditorInfo.IME_ACTION_DONE);
            m_Emailfragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
            m_MobilePhoneNumberfragment.setImeOptions(EditorInfo.IME_ACTION_DONE);

            m_AddressStreet1Fragment.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            m_AddressStreet2Fragment.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            m_AddressStreet3Fragment.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            m_AddressCityStateTextFragment.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        }
    };

    /**加入會員*/
    Runnable runInitDataForBase = new Runnable() {
        @Override
        public void run() {
            m_FirstNamefragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
            m_DEFLastNameName.setImeOptions(EditorInfo.IME_ACTION_DONE);
            m_Emailfragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
            m_confirmPasswordfragment.setImeOptions(EditorInfo.IME_ACTION_DONE);

            m_AddressInfoFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
            m_AddressZipCodeTextFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);

            m_AddressStreet1Fragment.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            m_AddressStreet2Fragment.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            m_AddressStreet3Fragment.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            m_AddressCityStateTextFragment.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            m_AddressCityFragment.setClickLock(true);
            m_AddressCountyFragment.setClickLock(true);
            m_AddressStreetFragment.setClickLock(true);
            m_AddressNearestAreaFragment.setClickLock(true);
        }
    };

    private void initTermsAndConditionsTextFormat(TextView view, String msg) {
        SpannableString spannableString = new SpannableString(msg);
        int                    TextHeadIndex = -1;
        int                    TextTailIndex = -1 ;
        int                    AdjustTextIndex = 0;
        SpannableStringBuilder stringBuilder          = new SpannableStringBuilder(spannableString.toString().replace("/", ""));
        while(true){
            TextHeadIndex  = spannableString.toString().indexOf("/", TextTailIndex + 1);
            TextTailIndex  = spannableString.toString().indexOf("/", TextHeadIndex + 1);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    changeActivityToTermsAndConditions();
                }
            };
            if(-1 < TextHeadIndex && -1 < TextTailIndex){

                int HeadIndex = TextHeadIndex - AdjustTextIndex;
                int TailIndex = TextTailIndex - (AdjustTextIndex + 1);
                stringBuilder.setSpan(clickableSpan,
                        HeadIndex,
                        TailIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                        HeadIndex,
                        TailIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.setSpan(new ForegroundColorSpan(Color.WHITE),
                        HeadIndex,
                        TailIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                AdjustTextIndex = AdjustTextIndex + 2 ;
            } else {
                break;
            }
        }
        view.setText(stringBuilder);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void changeActivityToTermsAndConditions(){
        Intent data = new Intent();
        String jsData = GsonTool.toJson(m_TermsAndConditionsContentList);
        data.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA_TITLE,getString(R.string.sign_up_terms_and_conditions_title));
        data.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA,jsData);
        changeActivity(CITermsAndConditionsActivity.class, data);
    }

    CITextFieldFragment.afterTextChangedListener passwordListener = new CIBaseTextFieldFragment.afterTextChangedListener() {
        @Override
        public void afterTextChangedListener(Editable editable) {
            if (null != m_passwordfragment.getText()
                    && 0 < m_passwordfragment.getText().length()
                    && null != m_confirmPasswordfragment.getText()
                    && 0 < m_confirmPasswordfragment.getText().length()) {
                String pw = m_passwordfragment.getText();
                String cpw = m_confirmPasswordfragment.getText();
                m_bIsPasswordSame = pw.equals(cpw);
                m_confirmPasswordfragment.setErrorMsg(getString(R.string.passwords_are_different));
                if (true == m_bIsPasswordSame) {
                    m_confirmPasswordfragment.setIsFormatCorrect(true);
                    if (false == m_confirmPasswordfragment.isFocused()) {
                        m_confirmPasswordfragment.hideError();
                    }
                } else {
                    m_confirmPasswordfragment.setIsFormatCorrect(false);
                    if (false == m_confirmPasswordfragment.isFocused()) {
                        m_confirmPasswordfragment.showError();
                    }
                }
            } else {
                m_confirmPasswordfragment.setIsFormatCorrect(true);
                m_confirmPasswordfragment.hideError();
            }
        }
    };

    CITextFieldFragment.afterTextChangedListener nationalityListener = new CIBaseTextFieldFragment.afterTextChangedListener() {
        @Override
        public void afterTextChangedListener(Editable editable) {
            /**護照號碼可選擇性填寫，另提醒國籍為中華民國籍時，身分證字號為必填選項
             * ，國籍為中華民國或中國大陸國籍，護照中文姓名亦為必填選項。*/
            String strCountryCd = m_NationalityTextFieldFragment.getCountryCd();
            final String TAIWAN_COUNTRY_CD  = "TW";
            final String CHINA_COUNTRY_CD   = "CN";

            /**連動設定國碼欄位資訊*/
            if(null != m_NationalMap){
                String phoneCd = m_NationalMap.get(strCountryCd).country_phone_cd;

                /**切換國籍後需要重新設定法定成人年齡，以確保正確顯示監護人欄位*/
                try {
                    m_legalAdultAge =  Integer.valueOf(m_NationalMap.get(strCountryCd).legal_adult_age);
                } catch (Exception e) {
                    m_legalAdultAge = 2;
                }

                m_MobilePhoneCountryCodefragment.setText(phoneCd);
                m_MobilePhoneCountryCodefragment.setCountryCd(strCountryCd);
                m_MobilePhoneCountryCodefragment.setPhoneCd(phoneCd);
            }

            /**如果選擇國籍是台灣或中國大陸秀出護照欄位*/
            if (strCountryCd.equals(TAIWAN_COUNTRY_CD)) {
                findViewById(R.id.fragment2_1).setVisibility(View.VISIBLE);
                findViewById(R.id.fragment2_2).setVisibility(View.VISIBLE);
                m_bIsCheckPassportChinessName = true;
                m_bIsCheckIdentityCardNo = true;
            } else if (strCountryCd.equals(CHINA_COUNTRY_CD)) {
                findViewById(R.id.fragment2_1).setVisibility(View.VISIBLE);
                findViewById(R.id.fragment2_2).setVisibility(View.GONE);
                m_bIsCheckPassportChinessName = true;
                m_bIsCheckIdentityCardNo = false;
            } else {
                findViewById(R.id.fragment2_1).setVisibility(View.GONE);
                findViewById(R.id.fragment2_2).setVisibility(View.GONE);
                m_bIsCheckPassportChinessName = false;
                m_bIsCheckIdentityCardNo = false;
            }
            showGuardianDetailForlegalAdultAge();
        }
    };

    CITextFieldFragment.afterTextChangedListener dateListener = new CIBaseTextFieldFragment.afterTextChangedListener() {
        @Override
        public void afterTextChangedListener(Editable editable) {
            showGuardianDetailForlegalAdultAge();
        }
    };

    private void showGuardianDetailForlegalAdultAge(){

        Calendar caleNow = (Calendar) Calendar.getInstance().clone();
        caleNow.set(Calendar.HOUR_OF_DAY, 0);
        caleNow.set(Calendar.MINUTE, 0);
        caleNow.set(Calendar.SECOND, 0);
        caleNow.set(Calendar.MILLISECOND, 0);

        Calendar maxCale = (Calendar) caleNow.clone();
        Calendar minCale = (Calendar) caleNow.clone();
        minCale.add(Calendar.YEAR, -2);
        maxCale.add(Calendar.YEAR, -m_legalAdultAge);
        String    strDate         = m_CIDateOfBirthdayFragment.getText();
        int       iBirthday       = m_CIDateOfBirthdayFragment.getIntForDate();
        int       iNowTime        = (int) (caleNow.getTimeInMillis() / 1000);
        m_iTwoYearOldMillis = iNowTime - (int) (minCale.getTimeInMillis() / 1000);
        m_iLegalAdultAgeMillis = iNowTime - (int) (maxCale.getTimeInMillis() / 1000);
        m_iAgeMillis = iNowTime - iBirthday;

        /**如果年齡大於等於兩歲和小於法定成人年齡就秀出監護人欄位*/
        if (m_iTwoYearOldMillis <= m_iAgeMillis && m_iLegalAdultAgeMillis > m_iAgeMillis
                && !TextUtils.isEmpty(strDate)) {
            findViewById(R.id.fragment5_1).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment5_2).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment5_3).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment5_4).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_guardian_details).setVisibility(View.VISIBLE);
            m_bIsCheckGuardianDetail = true;
        } else {
            findViewById(R.id.fragment5_1).setVisibility(View.GONE);
            findViewById(R.id.fragment5_2).setVisibility(View.GONE);
            findViewById(R.id.fragment5_3).setVisibility(View.GONE);
            findViewById(R.id.fragment5_4).setVisibility(View.GONE);
            findViewById(R.id.rl_guardian_details).setVisibility(View.GONE);
            m_bIsCheckGuardianDetail = false;
        }
    }

    CITextFieldFragment.afterTextChangedListener m_AddressNationalListener = new CIBaseTextFieldFragment.afterTextChangedListener() {

        @Override
        public void afterTextChangedListener(Editable editable) {
            showDiffAddressLayout();
        }
    };

    private void clearSelectCityData(){
        if ( !TextUtils.isEmpty(m_AddressCityFragment.getText()) ){
            m_AddressCityFragment.setCode("");
            m_AddressCityFragment.setText("");
        }
    }

    private void clearSelectCountyData(){
        if ( !TextUtils.isEmpty(m_AddressCountyFragment.getText()) ){
            m_AddressCountyFragment.setCode("");
            m_AddressCountyFragment.setText("");
        }
    }

    private void clearSelectNearsetAreaData(){
        if ( !TextUtils.isEmpty(m_AddressNearestAreaFragment.getText()) ){
            m_AddressNearestAreaFragment.setCode("");
            m_AddressNearestAreaFragment.setText("");
        }
    }

    private void clearSelectStreetData(){
        if ( !TextUtils.isEmpty(m_AddressStreetFragment.getText()) ) {
            m_AddressStreetFragment.setCode("");
            m_AddressStreetFragment.setText("");
        }
    }

    //-----2019-01-24 新增郵寄地址-----
    private void showDiffAddressLayout(){

        String strCountryCd = m_AddressCountryFragment.getCode();
        CIInquiryAddressPresenter.getInstance().setCountryCode(strCountryCd);
        final String TAIWAN_COUNTRY_CD  = "TW";
        final String CHINA_COUNTRY_CD   = "CN";
        //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if ( TextUtils.equals(strCountryCd, TAIWAN_COUNTRY_CD) ){

            m_AddressCityFragment.setClickLock(false);
            m_AddressCountyFragment.setClickLock(true);
            m_AddressStreetFragment.setClickLock(true);

            if ( m_enumAddressMode == AddMode.TW ){
                return;
            } else {
                m_enumAddressMode = AddMode.TW;

                clearSelectCityData();
                m_AddressStreetNumFragment.setText("");
                m_AddressInfoFragment.setText("");
                m_AddressZipCodeFragment.setText("");
                m_AddressNearestAreaFragment.setText("");
            }

            findViewById(R.id.fragment_address1_tw).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address2_tw).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address3_tw).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address4_tw).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address5_tw).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address6_tw).setVisibility(View.VISIBLE);
            findViewById(R.id.tvaddress1_note).setVisibility(View.GONE);
            findViewById(R.id.tvNearest_Area).setVisibility(View.GONE);

            findViewById(R.id.fragment_address1).setVisibility(View.GONE);
            findViewById(R.id.fragment_address2).setVisibility(View.GONE);
            findViewById(R.id.fragment_address3).setVisibility(View.GONE);
            //findViewById(R.id.fragment_address4).setVisibility(View.GONE);
            findViewById(R.id.fragment_address5).setVisibility(View.GONE);
            findViewById(R.id.fragment_address6).setVisibility(View.GONE);

            findViewById(R.id.fragment_address2_cn).setVisibility(View.GONE);
            findViewById(R.id.fragment_address3_cn).setVisibility(View.GONE);

            m_AddressZipCodeTextFragment.setHint("*"+getString(R.string.sign_up_zip_code));
            m_AddressNearestAreaFragment.setHint("*"+getString(R.string.sign_up_nearest_area));

            m_AddressInfoFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);

            m_AddressCityFragment.setAfterTextChangedListener(m_AddressCityListener);
            m_AddressCountyFragment.setAfterTextChangedListener(m_AddressCountyListener);
            m_AddressStreetFragment.setAfterTextChangedListener(m_AddressStreetListener);

        } else if ( TextUtils.equals(strCountryCd, CHINA_COUNTRY_CD) ){

            m_AddressCityFragment.setClickLock(false);
            m_AddressNearestAreaFragment.setClickLock(true);

            if ( m_enumAddressMode == AddMode.CN ){
                return;
            } else {
                m_enumAddressMode = AddMode.CN;

                clearSelectCityData();
                m_AddressCountyCNFragment.setText("");
                m_AddressStreetCNFragment.setText("");
                m_AddressStreetNumFragment.setText("");
                m_AddressInfoFragment.setText("");
                m_AddressZipCodeTextFragment.setText("");
                m_AddressZipCodeTextFragment.setMaxLenght(6);
                m_AddressNearestAreaFragment.setText("");
            }

            findViewById(R.id.fragment_address1_tw).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address2_cn).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address3_cn).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address4_tw).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address5_tw).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address6).setVisibility(View.VISIBLE);

            findViewById(R.id.fragment_address2_tw).setVisibility(View.GONE);
            findViewById(R.id.fragment_address3_tw).setVisibility(View.GONE);
            findViewById(R.id.fragment_address6_tw).setVisibility(View.GONE);

            findViewById(R.id.fragment_address1).setVisibility(View.GONE);
            findViewById(R.id.fragment_address2).setVisibility(View.GONE);
            findViewById(R.id.fragment_address3).setVisibility(View.GONE);
            //findViewById(R.id.fragment_address4).setVisibility(View.GONE);
            findViewById(R.id.fragment_address5).setVisibility(View.GONE);
            findViewById(R.id.tvaddress1_note).setVisibility(View.GONE);
            findViewById(R.id.tvNearest_Area).setVisibility(View.GONE);

//            transaction
//                    .replace(R.id.fragment_address1,        m_AddressCityFragment)
//                    .replace(R.id.fragment_address2,        m_AddressCountyCNFragment)
//                    .replace(R.id.fragment_address3,        m_AddressStreetCNFragment)
//                    .replace(R.id.fragment_address4,        m_AddressStreetNumFragment)
//                    .replace(R.id.fragment_address5,        m_AddressInfoFragment)
//                    .replace(R.id.fragment_address6,        m_AddressZipCodeTextFragment)
//                    .commitAllowingStateLoss();

            m_AddressInfoFragment.setImeOptions(EditorInfo.IME_ACTION_NEXT);

            m_AddressZipCodeTextFragment.setHint("*"+getString(R.string.sign_up_zip_code));
            m_AddressNearestAreaFragment.setHint("*"+getString(R.string.sign_up_nearest_area));

        } else {
            //中台以外
            m_enumAddressMode = AddMode.OTHER;

            m_AddressNearestAreaFragment.setClickLock(false);

            m_AddressStreet1Fragment.setText("");
            m_AddressStreet2Fragment.setText("");
            m_AddressStreet3Fragment.setText("");
            m_AddressCityStateTextFragment.setText("");
            m_AddressZipCodeTextFragment.setText("");
            m_AddressZipCodeTextFragment.setMaxLenght(9);
            m_AddressNearestAreaFragment.setText("");

            findViewById(R.id.fragment_address1_tw).setVisibility(View.GONE);
            findViewById(R.id.fragment_address2_tw).setVisibility(View.GONE);
            findViewById(R.id.fragment_address3_tw).setVisibility(View.GONE);
            findViewById(R.id.fragment_address4_tw).setVisibility(View.GONE);
            findViewById(R.id.fragment_address5_tw).setVisibility(View.GONE);
            findViewById(R.id.fragment_address6_tw).setVisibility(View.GONE);

            findViewById(R.id.fragment_address2_cn).setVisibility(View.GONE);
            findViewById(R.id.fragment_address3_cn).setVisibility(View.GONE);

            findViewById(R.id.fragment_address1).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address2).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address3).setVisibility(View.VISIBLE);
            //findViewById(R.id.fragment_address4).setVisibility(View.GONE);
            findViewById(R.id.fragment_address5).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_address6).setVisibility(View.VISIBLE);
            findViewById(R.id.tvaddress1_note).setVisibility(View.VISIBLE);
            findViewById(R.id.tvNearest_Area).setVisibility(View.VISIBLE);

//            transaction
//                    .replace(R.id.fragment_address1,        m_AddressStreet1Fragment)
//                    .replace(R.id.fragment_address2,        m_AddressStreet2Fragment)
//                    .replace(R.id.fragment_address3,        m_AddressStreet3Fragment)
//                    //.replace(R.id.fragment_address4,        m_AddressStreetNumFragment)
//                    .replace(R.id.fragment_address5,        m_AddressCityStateTextFragment)
//                    .replace(R.id.fragment_address6,        m_AddressZipCodeTextFragment)
//                    .commitAllowingStateLoss();

            m_AddressZipCodeTextFragment.setHint("*"+getString(R.string.sign_up_zip_code_en));
            m_AddressNearestAreaFragment.setHint("*"+getString(R.string.sign_up_nearest_area_en));

        }

        //變更國籍後要清掉城市以下的資料
        CIInquiryAddressPresenter.getInstance().clearDatabyChangeNational();
    }

    CITextFieldFragment.afterTextChangedListener m_AddressCityListener = new CIBaseTextFieldFragment.afterTextChangedListener() {

        @Override
        public void afterTextChangedListener(Editable editable) {
            String strCityCode = m_AddressCityFragment.getCode();
            CIInquiryAddressPresenter.getInstance().setCityCode(strCityCode);
            CIInquiryAddressPresenter.getInstance().clearDatabyChangeCity();
            clearSelectCountyData();
            clearSelectNearsetAreaData();

            if ( !TextUtils.isEmpty(editable.toString()) ){
                m_AddressCountyFragment.setClickLock(false);
                m_AddressNearestAreaFragment.setClickLock(false);
            } else {
                m_AddressCountyFragment.setClickLock(true);
                m_AddressNearestAreaFragment.setClickLock(true);
            }
        }
    };

    CITextFieldFragment.afterTextChangedListener m_AddressCountyListener = new CIBaseTextFieldFragment.afterTextChangedListener() {

        @Override
        public void afterTextChangedListener(Editable editable) {
            String strCountyCode = m_AddressCountyFragment.getCode();
            CIInquiryAddressPresenter.getInstance().setCountyCode(strCountyCode);
            CIInquiryAddressPresenter.getInstance().clearDatabyChangeCounty();
            clearSelectStreetData();

            if ( !TextUtils.isEmpty(editable.toString()) ){
                m_AddressStreetFragment.setClickLock(false);
                //m_AddressNearestAreaFragment.setClickLock(false);
            } else {
                m_AddressStreetFragment.setClickLock(true);
                //m_AddressNearestAreaFragment.setClickLock(true);
            }
        }
    };

    CITextFieldFragment.afterTextChangedListener m_AddressStreetListener = new CIBaseTextFieldFragment.afterTextChangedListener() {

        @Override
        public void afterTextChangedListener(Editable editable) {
            String strCode = m_AddressStreetFragment.getCode();
            CIInquiryAddressPresenter.getInstance().setStreetCode(strCode);

            CIInquiryAddressPresenter.getInstance().clearDatabyChangeStreet();
            m_AddressZipCodeFragment.setText("");
            m_AddressNearestAreaFragment.setText("");

            if ( !TextUtils.isEmpty(editable.toString()) && m_enumAddressMode != AddMode.OTHER ){

                CIInquiryAddressPresenter.getInstance(new CIInquiryAddressListner() {
                    @Override
                    public void onInquirySuccess(String rt_code, String rt_msg) {

                        ArrayList<CICodeNameEntity> arDataList = CIInquiryAddressPresenter.getInstance().getZipCodeList();
                        List<String> arTmpList = new ArrayList<>();
                        for ( CICodeNameEntity code : arDataList){
                            arTmpList.add(code.Name);
                        }
                        String[] zipCode = arTmpList.toArray(new String[0]);

                        Bundle bundle = m_AddressZipCodeFragment.getArguments();
                        bundle.putStringArray(CIDropDownMenuTextFieldFragment.ITEM_STRING_ARRAY, zipCode);
                        m_AddressZipCodeFragment.setArguments(bundle);
                    }

                    @Override
                    public void onInquiryError(String rt_code, String rt_msg) {
                        showDialog(getString(R.string.warning),
                                rt_msg);
                    }

                    @Override
                    public void showProgress() {
                        showProgressDialog();
                    }

                    @Override
                    public void hideProgress() {
                        hideProgressDialog();
                    }
                }).getZipCodeList();
            }
        }
    };

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_Receive_promotion_email), 24, 24);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_Receive_SMS_from_China_Airlines), 24, 24);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_Terms_and_Conditions), 24, 24);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_passwordfragment.setAfterTextChangedListener(passwordListener);
        m_confirmPasswordfragment.setAfterTextChangedListener(passwordListener);
        m_NationalityTextFieldFragment.setAfterTextChangedListener(nationalityListener);
        m_CIDateOfBirthdayFragment.setAfterTextChangedListener(dateListener);
        //2019-01-18 郵寄地址
        m_AddressCountryFragment.setAfterTextChangedListener(m_AddressNationalListener);
        m_AddressCityFragment.setAfterTextChangedListener(m_AddressCityListener);
        m_AddressCountyFragment.setAfterTextChangedListener(m_AddressCountyListener);
        m_AddressStreetFragment.setAfterTextChangedListener(m_AddressStreetListener);
        //2019-01-18 郵寄地址
        m_btnSignUp.setOnClickListener(this);
        m_btnSave.setOnClickListener(this);
        findViewById(R.id.ll_Receive_promotion_email).setOnClickListener(this);
        findViewById(R.id.ll_Receive_SMS_from_China_Airlines).setOnClickListener(this);
        findViewById(R.id.iv_Terms_and_Conditions).setOnClickListener(this);
        findViewById(R.id.tv_change_password).setOnClickListener(this);
        m_scrollView.setOnTouchListener(this);
        m_scrollView.getViewTreeObserver().addOnScrollChangedListener(m_onScroll);
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
    protected void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_signup:

                if(false == isFillCompleteAndCorrect()){
                    showDialog(getString(R.string.warning),
                            m_errorMsg);
                    return;
                }
                sendSignUpDataToWS();

                break;
            case R.id.ll_Receive_promotion_email:
                m_bReceivePromotionEmail =
                        setViewIconSwitch(findViewById(R.id.iv_Receive_promotion_email),
                                m_bReceivePromotionEmail);

                break;
            case R.id.ll_Receive_SMS_from_China_Airlines:
                m_bReceiveSmsFromChinaAirlines =
                        setViewIconSwitch(findViewById(R.id.iv_Receive_SMS_from_China_Airlines),
                                m_bReceiveSmsFromChinaAirlines);
                break;
            case R.id.iv_Terms_and_Conditions:
                m_bTermsAndConditions =
                        setViewIconSwitch(findViewById(R.id.iv_Terms_and_Conditions),
                                m_bTermsAndConditions);
                break;
            case R.id.btn_save:

                if(false == isFillCompleteAndCorrect()){
                    showDialog(getString(R.string.warning),
                            m_errorMsg);
                    return;
                }

                sendUpdateProfileToWS();

                break;
            case R.id.tv_change_password:
                changeActivity(CIPersonalProfileChangePasswordActivity.class);
                break;
        }
    }

    private boolean isFillCompleteAndCorrect(){
        /**
         * 初始化錯誤訊息
         */
        m_errorMsg = getString(R.string.please_fill_all_text_field_that_must_to_fill);

        /**
         * 護照中文名字如果必填，則判斷是否填寫
         * */
        if(true == m_bIsCheckPassportChinessName){
            String str = m_PassportChineseNameFragment.getText();
            if(true == TextUtils.isEmpty(str)){
                return false;
            }

            if(false == m_PassportChineseNameFragment.getIsFormatCorrect()){
                m_errorMsg = m_Context.getString(R.string.sign_up_only_chinese);
                return false;
            }
        }

        /**
         * 身分證字號如果必填，則判斷是否填寫
         * */
        if(true == m_bIsCheckIdentityCardNo){
            String str = m_IdentityCardNoFragment.getText();
            if(true == TextUtils.isEmpty(str)){
                return false;
            }
            if(false == m_IdentityCardNoFragment.getIsFormatCorrect()){
                m_errorMsg = m_Context.getString(R.string.member_login_input_correvt_format_msg);
                return false;
            }
        }

        /**
         * 監護人相關資料如果必填，則判斷是否填寫
         * */
        if(true == m_bIsCheckGuardianDetail){
            String str1 = m_DEFMembershipCardNumber.getText();
            boolean bG1 = m_DEFMembershipCardNumber.getIsFormatCorrect();
            String str2 = m_DEFFirstNameName.getText();
            String str3 = m_DEFLastNameName.getText();
            String str4 = m_DEFDateOfBirthdayFragment.getText();
            if(true == TextUtils.isEmpty(str1) || true == TextUtils.isEmpty(str2)
                    || true == TextUtils.isEmpty(str3) || true == TextUtils.isEmpty(str4)){
                return false;
            }

            if(false == bG1){
                m_errorMsg = m_Context.getString(R.string.member_login_input_correvt_format_msg);
                return false;
            }
        }

        String str01 = m_SalutationFragment.getText();
        String str02 = m_NationalityTextFieldFragment.getText();
        String str03 = m_FirstNamefragment.getText();
        String str04 = m_LastNamefragment.getText();
        String str05 = m_CIDateOfBirthdayFragment.getText();
        String str06 = m_Emailfragment.getText();
        boolean b1   = m_Emailfragment.getIsFormatCorrect();
        String str07 = m_MobilePhoneCountryCodefragment.getText();
        String str08 = m_MobilePhoneNumberfragment.getText();
        boolean b2   = m_MobilePhoneNumberfragment.getIsFormatCorrect();
        String str09 = m_passwordfragment.getText();
        boolean b3   = m_passwordfragment.getIsFormatCorrect();
        String str10 = m_confirmPasswordfragment.getText();
        boolean b4   = m_confirmPasswordfragment.getIsFormatCorrect();

        /**
         * 其他必填欄位判斷是否有未填欄位
         * */
        if(TextUtils.isEmpty(str01) || TextUtils.isEmpty(str02) || TextUtils.isEmpty(str03)
                || TextUtils.isEmpty(str04) || TextUtils.isEmpty(str05) || TextUtils.isEmpty(str06)
                || TextUtils.isEmpty(str07) || TextUtils.isEmpty(str08) || (TextUtils.isEmpty(str09) && m_mode == EMode.BASE)
                || (TextUtils.isEmpty(str10) && m_mode == EMode.BASE) ){
            return false;
        }

        /**
         * 判斷相關欄位是否有格式錯誤
         * */
        if(false == b1 || false == b2 || false == b3){
            m_errorMsg = m_Context.getString(R.string.member_login_input_correvt_format_msg);
            return false;
        }

        /**
         * 判斷密碼是否不一致
         * */
        if(false == b4){
            m_errorMsg = m_Context.getString(R.string.passwords_are_different);
            return false;
        }

        if(false == m_bTermsAndConditions && m_mode == EMode.BASE ){
            m_errorMsg = m_Context.getString(R.string.not_check_agree);
            return false;
        }

        if ( -1 == m_AddressTypeFragment.getPosition() ){
            return false;
        }

        /**國家代碼*/
        if ( TextUtils.isEmpty(m_AddressCountryFragment.getCode()) ){
            return false;
        }

        if ( m_enumAddressMode == AddMode.TW ||  m_enumAddressMode == AddMode.CN){
            /**國家中文姓名，國家為TW、CN時必填*/
            if ( TextUtils.isEmpty(m_AddressCountryFragment.getText()) ){
                return false;
            }

            /**城市代碼，國家為TW、CN時必填*/
            /**城市中文姓名，國家為TW、CN時必填*/
            if ( TextUtils.isEmpty(m_AddressCityFragment.getText()) ){
                return false;
            }

            if (m_enumAddressMode == AddMode.TW){
                /**鄉鎮代碼，國家為TW時必填*/
                /**鄉鎮名稱，國家為TW、CN時必填*/
                if ( TextUtils.isEmpty(m_AddressCountyFragment.getText()) ){
                    return false;
                }
                /**路名代碼，國家為TW時必填*/
                if ( TextUtils.isEmpty(m_AddressStreetFragment.getCode()) ){
                    return false;
                }
                /**路名名稱，國家為TW、CN時必填*/
                if ( TextUtils.isEmpty(m_AddressStreetFragment.getText()) ){
                    return false;
                }

                /**郵遞區號，為TW、CN的國家必填*/
                if ( TextUtils.isEmpty(m_AddressZipCodeFragment.getText()) ){
                    return false;
                }

            } else {
                /**鄉鎮名稱，國家為TW、CN時必填*/
                if ( TextUtils.isEmpty(m_AddressCountyCNFragment.getText()) ){
                    return false;
                }

                /**路名名稱，國家為TW、CN時必填*/
                if ( TextUtils.isEmpty(m_AddressStreetCNFragment.getText()) ){
                    return false;
                }

                /**郵遞區號，為TW、CN的國家必填*/
                if ( TextUtils.isEmpty(m_AddressZipCodeTextFragment.getText()) ){
                    return false;
                }
            }

        } else {
            /**國家英文姓名，為TW、CN以外的國家時必填*/
            if ( TextUtils.isEmpty(m_AddressCountryFragment.getText()) ){
                return false;
            }

            /**城市英文姓名，國家為TW、CN以外時必填*/
            if ( TextUtils.isEmpty(m_AddressCityFragment.getText()) ){
                return false;
            }

            /**路名名稱，為TW、CN以外的國家時必填*/
            if ( TextUtils.isEmpty(m_AddressStreet1Fragment.getText()) ){
                return false;
            }

            /**郵遞區號，為TW、CN以外的國家必填*/
            if ( TextUtils.isEmpty(m_AddressZipCodeTextFragment.getText()) ){
                return false;
            }
        }

        /**鄰近城市代碼*/
        if ( TextUtils.isEmpty(m_AddressNearestAreaFragment.getText()) ){
            return false;
        }

        return true;
    }

    /**準備郵寄地址*/
    private CIAddressEntity preparAddressDataToWS(){

        CIAddressEntity addressEntity = new CIAddressEntity();

        /**國家代碼*/
        addressEntity.countryCode = m_AddressCountryFragment.getCode();

        if ( m_enumAddressMode == AddMode.TW ||  m_enumAddressMode == AddMode.CN){
            /**國家中文姓名，國家為TW、CN時必填*/
            addressEntity.countryName_CHN = m_AddressCountryFragment.getText();
            addressEntity.countryName_ENG = "";

            /**城市代碼，國家為TW、CN時必填*/
            addressEntity.cityCode = m_AddressCityFragment.getCode();
            /**城市中文姓名，國家為TW、CN時必填*/
            addressEntity.cityName_CHN = m_AddressCityFragment.getText();
            addressEntity.cityName_ENG = "";

            if (m_enumAddressMode == AddMode.TW){
                /**鄉鎮代碼，國家為TW時必填*/
                addressEntity.countyCode = m_AddressCountyFragment.getCode();
                /**鄉鎮名稱，國家為TW、CN時必填*/
                addressEntity.countyName_CHN = m_AddressCountyFragment.getText();
                /**路名代碼，國家為TW時必填*/
                addressEntity.streetCode = m_AddressStreetFragment.getCode();
                /**路名名稱，國家為TW、CN時必填*/
                addressEntity.streetName1_CHN = m_AddressStreetFragment.getText();

                /**郵遞區號，為TW、CN的國家必填*/
                addressEntity.zipCode_CHN = m_AddressZipCodeFragment.getText();

            } else {
                addressEntity.countyCode = "";
                /**鄉鎮名稱，國家為TW、CN時必填*/
                addressEntity.countyName_CHN = m_AddressCountyCNFragment.getText();

                addressEntity.streetCode = "";
                /**路名名稱，國家為TW、CN時必填*/
                addressEntity.streetName1_CHN = m_AddressStreetCNFragment.getText();
                /**門牌號碼，國家為TW、CN時選填*/
                addressEntity.streetName2_CHN = m_AddressStreetNumFragment.getText();

                /**郵遞區號，為TW、CN的國家必填*/
                addressEntity.zipCode_CHN = m_AddressZipCodeTextFragment.getText();
            }

            /**門牌號碼，國家為TW、CN時選填*/
            addressEntity.streetName2_CHN = m_AddressStreetNumFragment.getText();
            /**路名額外資訊，國家為TW、CN時選填*/
            addressEntity.streetName3_CHN = m_AddressInfoFragment.getText();

        } else {
            addressEntity.countryName_CHN = "";
            /**國家英文姓名，為TW、CN以外的國家時必填*/
            addressEntity.countryName_ENG = m_AddressCountryFragment.getText();

            /**城市英文姓名，國家為TW、CN以外時必填*/
            addressEntity.cityName_ENG = m_AddressCityStateTextFragment.getText();
            addressEntity.cityCode = "";
            addressEntity.cityName_CHN = "";
            addressEntity.countyCode = "";
            addressEntity.countyName_CHN = "";

            /**路名名稱，為TW、CN以外的國家時必填*/
            addressEntity.streetName1_ENG = m_AddressStreet1Fragment.getText();
            /**門牌號碼，為TW、CN以外的國家時選填*/
            addressEntity.streetName2_ENG = m_AddressStreet2Fragment.getText();
            /**路名額外資訊，為TW、CN以外的國家時選填*/
            addressEntity.streetName3_ENG = m_AddressStreet3Fragment.getText();

            /**郵遞區號，為TW、CN以外的國家必填*/
            addressEntity.zipCode_ENG = m_AddressZipCodeTextFragment.getText();
        }

        /**鄰近城市代碼*/
        addressEntity.currAreaCode = m_AddressNearestAreaFragment.getCode();

        return addressEntity;

    }

    private void sendSignUpDataToWS(){
        //要傳CISignUpReq給Presenter, CISignUpReq裡面有各欄位的資料(資料格式請參考CISignUpWSModel)
        CISignUpReq ciSignUpReq = new CISignUpReq();

        /**稱謂欄位 ex:MR/MS*/
        int position = m_SalutationFragment.getPosition();
        if(position > -1){
            ciSignUpReq.surname = CISignUpReq.getSurnameCodeArray().get(position);
        }

        /**國籍欄位 ex:TW*/
        ciSignUpReq.nation_code = m_NationalityTextFieldFragment.getCountryCd();

        /**護照中文名*/
        if(true == m_bIsCheckPassportChinessName){
            ciSignUpReq.chin_name = m_PassportChineseNameFragment.getText();
        }

        /**身分證字號*/
        if(true == m_bIsCheckIdentityCardNo){
            ciSignUpReq.id_num = m_IdentityCardNoFragment.getText();
        }

        /**名字*/
        ciSignUpReq.first_name = m_FirstNamefragment.getText();
        ciSignUpReq.last_name = m_LastNamefragment.getText();

        /**生日欄位 ex:yyyy-mm-dd*/
        ciSignUpReq.birth_date = m_CIDateOfBirthdayFragment.getFormatedDate();

        /**監護人欄位*/
        if(true == m_bIsCheckGuardianDetail){
            ciSignUpReq.guard_card_no = m_DEFMembershipCardNumber.getText();
            ciSignUpReq.guard_first_name = m_DEFFirstNameName.getText();
            ciSignUpReq.guard_last_name = m_DEFLastNameName.getText();

            //要傳給Server生日資料 ex:yyyy-mm-dd
            ciSignUpReq.guard_birth_date = m_DEFDateOfBirthdayFragment.getFormatedDate();
        }

        /**護照號碼*/
        ciSignUpReq.passport = m_PassportNumberfragment.getText();

        /**電子郵件*/
        ciSignUpReq.email = m_Emailfragment.getText();

        /**是否接受來自中華航空Email*/
        if ( m_bReceivePromotionEmail ){
            ciSignUpReq.rcv_email = "Y";
        }else {
            ciSignUpReq.rcv_email = "N";
        }

        /**國碼欄位 ex:886*/
        ciSignUpReq.cell_city = m_MobilePhoneCountryCodefragment.getPhoneCd();

        /**行動電話號碼*/
        ciSignUpReq.cell_num = m_MobilePhoneNumberfragment.getText();

        /** 是否接收中華航空簡訊*/
        if ( m_bReceiveSmsFromChinaAirlines ){
            ciSignUpReq.rcv_sms = "Y";
        }else {
            ciSignUpReq.rcv_sms = "N";
        }
        /**密碼*/
        ciSignUpReq.password = m_passwordfragment.getText();

        /**餐點喜好資料*/
        ciSignUpReq.meal_type = m_SpecialMealSelectFragment.getCode();

        /**選位欄位(原本非必填，但目前server要求必填, 不然無法註冊)*/
        int seatPosition = m_SpecialSeatSelectFragment.getPosition();
        if(seatPosition > -1){
            ciSignUpReq.seat_code = CISignUpReq.getSeatCodeArray().get(seatPosition);
        }

        /**
         * 需要綁定社群的註冊模式
         */
        if ( true == m_bIsSocialCombine
                && m_mode == EMode.BASE){
            ciSignUpReq.social_vendor       = CIApplication.getLoginInfo().GetLoginType();
            ciSignUpReq.social_id           = CIApplication.getLoginInfo().GetSocialLoginId();
            ciSignUpReq.social_email        = CIApplication.getLoginInfo().GetUserEmail().toUpperCase();
            ciSignUpReq.is_social_combine   = CISignUpReq.SOCIAL_COMBINE_YES;

            /**
             * 判斷註冊所填寫的Email是否與已經登入的社群帳號Email相同，若不同則需要跳到詢問是否改成使用社群Email
             * 做為註冊Email的頁面
             * */
            if (false == ciSignUpReq.social_email.equals(ciSignUpReq.email) ){
                m_bIsChangeToSocialEmail = true;
            }
            /**
             * 當註冊填寫的Email不同於社群Email時，需跳轉到詢問是否使用社群Email來加入會員的頁面
             * 如果相同，則會在此加入會員頁面進行註冊
             */
            if(true == m_bIsChangeToSocialEmail){
                m_bIsChangeToSocialEmail = false;
                Intent data = new Intent();
                String jsonData = GsonTool.toJson(ciSignUpReq);
                data.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA,jsonData);
                changeActivityForResult(CISignUpAskChangeEmailActivity.class, data);
                /**因轉到詢問頁面去註冊，所以就返回不去執行之後的註冊方法*/
                return;
            }
        }

        //郵寄地址
        String[] addressType = getResources().getStringArray(R.array.sign_up_address_type_list_code);
        ciSignUpReq.addressType = addressType[m_AddressTypeFragment.getPosition()];

        ciSignUpReq.addressInfo = preparAddressDataToWS();
        //

        /**註冊*/
        CISignUpWSPresenter.getInstance(m_SignUpWSListener).SignUpFromWS(ciSignUpReq);

    }

    private void sendUpdateProfileToWS() {

        CIUpdateProfileEntity ciUpdateProfileReq = new CIUpdateProfileEntity();
        /**護照號碼*/
        ciUpdateProfileReq.pass_port = m_PassportNumberfragment.getText();

        /**電子郵件*/
        ciUpdateProfileReq.email = m_Emailfragment.getText();

        /**是否接受來自中華航空Email*/
        if ( m_bReceivePromotionEmail ){
            ciUpdateProfileReq.rcv_email = "Y";
        }else {
            ciUpdateProfileReq.rcv_email = "N";
        }

        /**國碼欄位 ex:886*/
        ciUpdateProfileReq.cell_city = m_MobilePhoneCountryCodefragment.getPhoneCd();

        /**行動電話號碼*/
        ciUpdateProfileReq.cell_num = m_MobilePhoneNumberfragment.getText();

        /** 是否接收中華航空簡訊*/
        if ( m_bReceiveSmsFromChinaAirlines ){
            ciUpdateProfileReq.rcv_sms = "Y";
        }else {
            ciUpdateProfileReq.rcv_sms = "N";
        }

        /**餐點喜好資料*/
        ciUpdateProfileReq.meal_type = m_SpecialMealSelectFragment.getCode();

        /**選位欄位(原本非必填，但目前server要求必填, 不然無法註冊)*/
        int seatPosition = m_SpecialSeatSelectFragment.getPosition();
        if(seatPosition > -1){
            ciUpdateProfileReq.seat_code = CISignUpReq.getSeatCodeArray().get(seatPosition);
        }

        //郵寄地址
        String[] addressType = getResources().getStringArray(R.array.sign_up_address_type_list_code);
        ciUpdateProfileReq.addressType = addressType[m_AddressTypeFragment.getPosition()];

        ciUpdateProfileReq.addressInfo = preparAddressDataToWS();
        //

        CIProfilePresenter.getInstance(m_InquiryProfileListener).UpdateProfileFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo(),ciUpdateProfileReq);

    }

    private CIInquiryProfileListener m_InquiryProfileListener = new CIInquiryProfileListener() {
        @Override
        public void onInquiryProfileSuccess(String rt_code, String rt_msg, CIProfileEntity profile) { }

        @Override
        public void onInquiryProfileError(String rt_code, String rt_msg) { }

        @Override
        public void onUpdateProfileSuccess(String rt_code, String rt_msg) {

            setResult(RESULT_OK);
            finish();
            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);

        }

        @Override
        public void onUpdateProfileError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg);
            //註冊完成啟用註冊按鈕
            m_btnSave.setEnabled(true);
        }

        @Override
        public void showProgress() {
            showProgressDialog();
            //開始向ws註冊的話就不能再按註冊按鈕進行註冊
            m_btnSave.setEnabled(false);
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

    private boolean setViewIconSwitch(View v, boolean isOn) {
        if (true == isOn) {
            ((ImageView) v).setImageResource(R.drawable.btn_checkbox_off);
            return false;
        } else {
            ((ImageView) v).setImageResource(R.drawable.btn_checkbox_on);
            return true;
        }
    }

    private void changeActivity(Class clazz) {
        changeActivity(clazz, null);
    }

    private void changeActivity(Class clazz,Intent intent){
        if(null == intent){
            intent = new Intent();
        }
        intent.setClass(m_Context, clazz);
        intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, m_iViewId);
        startActivity(intent);

        //測試切換activity滑入滑出動畫
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private void changeActivityForResult(Class clazz,Intent intent){
        if(null == intent){
            intent = new Intent();
        }
        intent.setClass(m_Context, clazz);
        intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, m_iViewId);
        startActivityForResult(intent, UiMessageDef.REQUEST_CODE_SIGN_UP);

        //測試切換activity滑入滑出動畫
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (R.id.sign_up_scrollview == v.getId()) {
            HidekeyBoard();
        }
        return false;
    }

    ViewTreeObserver.OnScrollChangedListener m_onScroll = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
            int iScrollHeight = 0;
            int iCnt = m_scrollView.getChildCount();
            if (iCnt <= 0) {

            } else {
                iScrollHeight = m_scrollView.getChildAt(0).getHeight() - m_scrollView.getHeight();
            }

            //利用百分比決定 blurr 效果
            //減少分母, 提高Blur的效果
            float fAlpha = 0f;
            fAlpha = (float) m_scrollView.getScrollY() / iScrollHeight;
            fAlpha = 1 - fAlpha;

            m_vGradient.setAlpha(fAlpha);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_scrollView.getViewTreeObserver().removeOnScrollChangedListener(m_onScroll);
        CIInquiryAddressPresenter.getInstance(null);
        CIInquiryNationalPresenter.getInstance(null);
        CIInquiryMealListPresenter.getInstance(null);
        CIProfilePresenter.getInstance(null);
        CISignUpWSPresenter.getInstance(null);
    }

    private CISignUpWSListener m_SignUpWSListener = new CISignUpWSListener(){

        @Override
        public void onSignUpSuccess(final String rt_code, final String rt_msg, final CISignUpResp SignupResp) {

            CIAlertDialog.OnAlertMsgDialogListener listener = new CIAlertDialog.OnAlertMsgDialogListener() {
                @Override
                public void onAlertMsgDialog_Confirm() {

                    Intent intent = new Intent();
                    intent.putExtra(UiMessageDef.BUNDLE_LOGIN_USERNAME_TAG,
                            m_FirstNamefragment.getText() + ", " + m_LastNamefragment.getText());
                    intent.putExtra(UiMessageDef.BUNDLE_LOGIN_ACCOUNT_TAG, SignupResp.card_no);
                    intent.putExtra(UiMessageDef.BUNDLE_LOGIN_PASSWORD_TAG, m_passwordfragment.getText());
                    changeActivityForResult(CISignUpSuccessActivity.class, intent);
                    //測試切換activity滑入滑出動畫
                    overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                }

                @Override
                public void onAlertMsgDialogg_Cancel() {}
            };

            showDialog(getString(R.string.warning),
                    m_FirstNamefragment.getText() + " " + m_LastNamefragment.getText() + "\n註冊成功",
                    getString(R.string.confirm),
                    null,
                    listener);
        }

        @Override
        public void onSignUpError(final String rt_code, final String rt_msg) {
            showDialog(getString(R.string.warning),
                        rt_msg);
            //註冊完成啟用註冊按鈕
            m_btnSignUp.setEnabled(true);
        }

        @Override
        public void showProgress() {
            showProgressDialog();
            //開始向ws註冊的話就不能再按註冊按鈕進行註冊
            m_btnSignUp.setEnabled(false);
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    CIInquiryNationalListner m_InquiryNationalListener = new CIInquiryNationalListner() {
        @Override
        public void onInquiryNationalSuccess(String rt_code, String rt_msg) {
            m_NationalMap  = CIInquiryNationalPresenter.getInstance(m_InquiryNationalListener).getNationalMap();
            if(null == m_NationalMap){
                showDialog(getString(R.string.warning),
                        "ERROR",
                        getString(R.string.confirm),
                        null,
                        m_AlertListner);
                CIInquiryNationalPresenter.getInstance(null).interrupt();
                return;
            } else {
                if(EMode.EDIT == m_mode){
                    findViewById(R.id.root).post(runInitDataForEdit);
                }
            }
        }

        @Override
        public void onInquiryNationalError(String rt_code, String rt_msg) {

            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm),
                    null,
                    m_AlertListner);
        }

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    CIAlertDialog.OnAlertMsgDialogListener m_AlertListner = new CIAlertDialog.OnAlertMsgDialogListener() {
        @Override
        public void onAlertMsgDialog_Confirm() {
            onBackPressed();
        }

        @Override
        public void onAlertMsgDialogg_Cancel() {}
    };

    CIInquiryAddressListner m_InquiryAddressListner = new CIInquiryAddressListner() {
        @Override
        public void onInquirySuccess(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm),
                    null,
                    m_AlertListner);
        }

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == UiMessageDef.REQUEST_CODE_SIGN_UP){
            setResult(RESULT_OK);
            finish();
        }
    }
}
