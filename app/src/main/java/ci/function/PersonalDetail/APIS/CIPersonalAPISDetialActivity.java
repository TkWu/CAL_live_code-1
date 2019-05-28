package ci.function.PersonalDetail.APIS;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Models.entities.CIApisEntity;
import ci.ws.Models.entities.CIApisNationalEntity;
import ci.ws.Presenter.CIAPISPresenter;
import ci.ws.cores.object.GsonTool;

import static ci.function.PersonalDetail.APIS.CIPersonalAddAPISActivity.CIPersonalAddAPISType;

/**
 * Created by jlchen on 2016/3/22.
 */
public class CIPersonalAPISDetialActivity extends BaseActivity implements View.OnClickListener{

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_strAPISName;
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            CIPersonalAPISDetialActivity.this.finish();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    private final String WS_FORMAT = "yyyy-MM-dd";
    private final String UI_FORMAT = "MMM dd, yyyy";

    private static final int RESIDENT_CD    = 1;
    private static final int ISSUE_CD       = 2;
//    private static final int COUNTRY_CD     = 0;

    private CIPersonalAddAPISType m_type = CIPersonalAddAPISType.EDIT_MY_APIS;

    private String m_strAPISName = "";
    private String m_strUserName = "";

    public NavigationBar m_Navigationbar = null;
    public FrameLayout m_flayout_Content = null;

    private Bitmap          m_bitmap    = null;
    private RelativeLayout  m_rlBg      = null;

    private TextView m_tvName = null;
    private TextView m_tvResidentCountry = null;
    private TextView m_tvResidentCountryData = null;
    private TextView m_tvNationality = null;
    private TextView m_tvNationalityData = null;
    private TextView m_tvDocumentType= null;
    private TextView m_tvDocumentTypeData = null;
    private TextView m_tvDocumentNo = null;
    private TextView m_tvDocumentNoData = null;
    private TextView m_tvIssueCountry = null;
    private TextView m_tvIssueCountryData = null;
    private TextView m_tvExpiryDate = null;
    private TextView m_tvExpiryDateData = null;

    private ImageButton m_ibtnEdit = null;
    private CIApisEntity m_apisEntity = null;
    private ArrayList<CIApisNationalEntity> m_arApisNationList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String mode = getIntent().getStringExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE);
        if (null != mode) {
            m_type = CIPersonalAddAPISType.valueOf(mode);
        }

        String strAPISName = getIntent().getStringExtra(UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_TAG);
        if (null != strAPISName) {
//            m_strAPISName = strAPISName;

            m_apisEntity = GsonTool.toObject(strAPISName, CIApisEntity.class);
            m_strAPISName = getDocmuntName( m_apisEntity.doc_type );
        }

        String strUserName = getIntent().getStringExtra(UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_USER_NAME_TAG);
        if (null != strUserName) {
            m_strUserName = strUserName;
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initialLayoutComponent() {
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_flayout_Content = (FrameLayout) findViewById(R.id.container);

        View ViewContent = View.inflate(this, R.layout.fragment_personal_apis_detail, null);
        m_flayout_Content.addView(ViewContent);

        m_rlBg      = (RelativeLayout) ViewContent.findViewById(R.id.root);
        m_bitmap        = ImageHandle.getLocalBitmap(m_Context, R.drawable.bg_login, 1);
        Drawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        m_rlBg.setBackground(drawable);

        m_ibtnEdit = (ImageButton) ViewContent.findViewById(R.id.ibtn_edit);
        m_ibtnEdit.setOnClickListener(this);

        m_tvName = (TextView) ViewContent.findViewById(R.id.tv_title);
        m_tvName.setSingleLine();
        m_tvName.setEllipsize(TextUtils.TruncateAt.END);
        m_tvName.setText(m_strUserName);

        m_tvResidentCountry = (TextView) ViewContent.findViewById(R.id.tv_passport_chinese_name_title);
        m_tvResidentCountry.setText(getString(R.string.resident_country)+":");
        m_tvResidentCountryData = (TextView) ViewContent.findViewById(R.id.tv_passport_chinese_name_value);
//        m_tvResidentCountryData.setText("Taiwan");
        m_tvResidentCountry.setMaxLines(2);
        m_tvResidentCountry.setEllipsize(TextUtils.TruncateAt.END);
        m_tvResidentCountryData.setText( getCountryName(m_apisEntity.resident_city,RESIDENT_CD) );

        m_tvNationality = (TextView) ViewContent.findViewById(R.id.tv_identity_card_no_title);
        m_tvNationality.setText(getString(R.string.sign_up_nationality)+":");

        m_tvNationalityData = (TextView) ViewContent.findViewById(R.id.tv_identity_card_no_value);
//        m_tvNationalityData.setText("Taiwan");
        m_tvNationalityData.setMaxLines(2);
        m_tvNationalityData.setEllipsize(TextUtils.TruncateAt.END);
        m_tvNationalityData.setText( getCountryName(m_apisEntity.nationality,ISSUE_CD) );

        m_tvDocumentType = (TextView) ViewContent.findViewById(R.id.tv_date_of_birth_title);
        m_tvDocumentType.setText(getString(R.string.document_type)+":");
        m_tvDocumentTypeData = (TextView) ViewContent.findViewById(R.id.tv_date_of_birth_value);
//        m_tvDocumentTypeData.setText(m_strAPISName);
        m_tvDocumentTypeData.setMaxLines(2);
        m_tvDocumentTypeData.setEllipsize(TextUtils.TruncateAt.END);
        m_tvDocumentTypeData.setText(getDocmuntName(m_apisEntity.doc_type));

        m_tvDocumentNo = (TextView) ViewContent.findViewById(R.id.tv_passport_no_title);
        m_tvDocumentNo.setText(getString(R.string.document_number)+":");
        m_tvDocumentNoData = (TextView) ViewContent.findViewById(R.id.tv_passport_no_value);
//        m_tvDocumentNoData.setText("214188500");
        m_tvDocumentNoData.setMaxLines(2);
        m_tvDocumentNoData.setEllipsize(TextUtils.TruncateAt.END);
        m_tvDocumentNoData.setText(m_apisEntity.doc_no);

        m_tvIssueCountry = (TextView) ViewContent.findViewById(R.id.tv_email_title);
        m_tvIssueCountry.setText(getString(R.string.country_of_issuance)+":");
        m_tvIssueCountryData = (TextView) ViewContent.findViewById(R.id.tv_email_value);
//        m_tvIssueCountryData.setText("Taiwan");
        m_tvIssueCountryData.setMaxLines(2);
        m_tvIssueCountryData.setEllipsize(TextUtils.TruncateAt.END);
        m_tvIssueCountryData.setText( getCountryName(m_apisEntity.issue_country,ISSUE_CD) );

        m_tvExpiryDate = (TextView) ViewContent.findViewById(R.id.tv_mobile_phone_title);
        m_tvExpiryDate.setText(getString(R.string.date_of_expiry)+":");
        m_tvExpiryDateData = (TextView) ViewContent.findViewById(R.id.tv_mobile_phone_value);
//        m_tvExpiryDateData.setText("May 21, 2023");
        m_tvExpiryDateData.setText( formatDate(m_apisEntity.doc_expired_date) );

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
//        } else if( COUNTRY_CD == iCountryType ) {
//            return CIInquiryNationalPresenter.getInstance(null).getNationalMap().get(strCountryCd).country_name;
//        }

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

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_flayout_Content);
        vScaleDef.selfAdjustSameScaleView(m_ibtnEdit, 24, 24);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
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
    protected void onDestroy() {
        if (null != m_rlBg) {
            m_rlBg.setBackground(null);
        }
        if (null != m_bitmap) {
            ImageHandle.recycleBitmap(m_bitmap);
        }
        System.gc();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UiMessageDef.REQUEST_CODE_PERSONAL_EDIT_APIS_TAG &&
                resultCode == RESULT_OK){
            setResult(RESULT_OK);
            CIPersonalAPISDetialActivity.this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        if ( v.getId() == R.id.ibtn_edit){

//            String strData = "台灣;台灣;214188500;台灣;2023/3/21";
            String strData = GsonTool.toJson(m_apisEntity);

            Intent intent = new Intent();

            switch (m_type){
                case EDIT_MY_APIS:
                    intent.putExtra(
                            UiMessageDef.BUNDLE_ACTIVITY_MODE,
                            CIPersonalAddAPISType.EDIT_MY_APIS.name());
                    break;

                case EDIT_COMPANAIONS_APIS:
                    intent.putExtra(
                            UiMessageDef.BUNDLE_ACTIVITY_MODE,
                            CIPersonalAddAPISType.EDIT_COMPANAIONS_APIS.name());
                    break;
            }

            intent.putExtra(
                    UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_USER_NAME_TAG,
                    m_strUserName);
            intent.putExtra(
                    UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_TAG,
                    m_strAPISName);
            intent.putExtra(
                    UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_DATA_TAG,
                    strData);
            intent.setClass(m_Context, CIPersonalAddAPISActivity.class);
            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_PERSONAL_EDIT_APIS_TAG);

            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }
    }

    private String formatDate(String strDate) {

        if( TextUtils.isEmpty(strDate)
                || !strDate.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$") ) {
            return "";
        }

        return strDate;
        //return AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(strDate);

//        try {
//            // 定義時間格式
//            SimpleDateFormat sdfA = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
//            sdfA.applyPattern(WS_FORMAT);
//
//            Date date = sdfA.parse(strDate);
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(UI_FORMAT);
//            return simpleDateFormat.format(date.getTime());
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return "";
//        }
    }
}
