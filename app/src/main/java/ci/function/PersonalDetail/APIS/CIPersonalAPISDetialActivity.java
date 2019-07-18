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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.TextField.CIApisDocmuntTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIApisAddEntity;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Models.entities.CIApisNationalEntity;
import ci.ws.Models.entities.CIApisQryRespEntity;
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
    private String FUN_ENTRY = "";

    private static final int RESIDENT_CD    = 1;
    private static final int ISSUE_CD       = 2;
//    private static final int COUNTRY_CD     = 0;

    //private CIPersonalAddAPISType m_type = CIPersonalAddAPISType.EDIT_MY_APIS;
    private CIPersonalAddSaveAPISActivity.CIPersonalAddAPISType m_type;
    private CIApisDocmuntTextFieldFragment.EType m_apisType = null;

    private String m_strAPISName = "";
    private String m_strUserName = "";
    private String m_strAPISCode = "";

    public NavigationBar m_Navigationbar = null;
    public FrameLayout m_flayout_Content = null;

    private Bitmap          m_bitmap    = null;
    private RelativeLayout  m_rlBg      = null;

    private TextView m_tvName = null;

    private TextView m_tvdoctypeTitle = null;
    private TextView m_tvdoctypeData = null;
    private TextView m_tvdocfreenameTitle = null;
    private TextView m_tvdocfreenameData = null;


    private TextView m_tvdocattrs1 = null;
    private TextView m_tvdocattrs1Data = null;
    private TextView m_tvdocattrs2 = null;
    private TextView m_tvdocattrs2Data = null;
    private TextView m_tvdocattrs3 = null;
    private TextView m_tvdocattrs3Data = null;

    private RelativeLayout  m_rldocattrs4      = null;
    private TextView m_tvdocattrs4 = null;
    private TextView m_tvdocattrs4Data = null;

    private RelativeLayout  m_rldocattrs5      = null;
    private TextView m_tvdocattrs5 = null;
    private TextView m_tvdocattrs5Data = null;

    private ImageButton m_ibtnEdit = null;
    //private CIApisEntity m_apisEntity = null;
    private CIApisDocmuntTypeEntity m_apisDocmuntType = null;
    private CIApisQryRespEntity.ApisRespDocObj m_editApisEntity = null;
    private ArrayList<CIApisNationalEntity> m_arApisNationList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String mode = getIntent().getStringExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE);//APIS功能(新增／編輯  自己／同行)
        if (null != mode) {
            m_type = CIPersonalAddSaveAPISActivity.CIPersonalAddAPISType.valueOf(mode);
        }
        String fun_entry = getIntent().getStringExtra(CIAddSaveAPISDocTypeActivity.APIS_FUN_ENTRANCE); //APIS編輯進入點  個人資訊／報到時
        if (null != mode) {
            FUN_ENTRY = fun_entry;
        }

        m_apisType = CIApisDocmuntTextFieldFragment.EType.valueOf(fun_entry);
        String edtmy_apis = getIntent().getStringExtra(CIAddSaveAPISDocTypeActivity.APIS_OBJ_VALUE);
        if (edtmy_apis != null) {
            Gson gson = new Gson();

            try{
                m_editApisEntity = gson.fromJson(edtmy_apis, CIApisQryRespEntity.ApisRespDocObj.class);
                m_strAPISName = m_editApisEntity.documentName;
                m_strAPISCode = m_editApisEntity.documentType;
            } catch ( Exception e ){
                e.printStackTrace();
            }
        }

        String strUserName = getIntent().getStringExtra(UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_USER_NAME_TAG);
        if (null != strUserName && 0 < strUserName.length()) {
            m_strUserName = strUserName;
        }else {
            m_strUserName = CIApplication.getLoginInfo().GetUserName();
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
        m_tvName.setText(m_strUserName.replace(":"," "));


        m_tvdoctypeTitle = (TextView) ViewContent.findViewById(R.id.tv_doctype_title);
        m_tvdoctypeTitle.setText(getString(R.string.document_type)+":");
        m_tvdoctypeData = (TextView) ViewContent.findViewById(R.id.tv_doctype_value);
        m_tvdoctypeData.setEllipsize(TextUtils.TruncateAt.END);
        m_tvdoctypeData.setText(getDocmuntName(m_strAPISCode));

        m_tvdocfreenameTitle = (TextView) ViewContent.findViewById(R.id.tv_docfreename_title);
        m_tvdocfreenameTitle.setText(getString(R.string.apis_my_freename_hint)+":");
        m_tvdocfreenameData = (TextView) ViewContent.findViewById(R.id.tv_docfreename_value);
        m_tvdocfreenameData.setEllipsize(TextUtils.TruncateAt.END);
        m_tvdocfreenameData.setMaxLines(2);
        m_tvdocfreenameData.setText(m_editApisEntity.documentName);

        m_tvdocattrs1 = (TextView) ViewContent.findViewById(R.id.tv_docattrs1_title);
        m_tvdocattrs1Data = (TextView) ViewContent.findViewById(R.id.tv_docattrs1_value);
        m_tvdocattrs1Data.setMaxLines(2);
        m_tvdocattrs1Data.setEllipsize(TextUtils.TruncateAt.END);

        m_tvdocattrs2 = (TextView) ViewContent.findViewById(R.id.tv_docattrs2_title);
        m_tvdocattrs2Data = (TextView) ViewContent.findViewById(R.id.tv_docattrs2_value);
        m_tvdocattrs2Data.setMaxLines(2);
        m_tvdocattrs2Data.setEllipsize(TextUtils.TruncateAt.END);

        m_tvdocattrs3 = (TextView) ViewContent.findViewById(R.id.tv_docattrs3_title);
        m_tvdocattrs3Data = (TextView) ViewContent.findViewById(R.id.tv_docattrs3_value);
        m_tvdocattrs3Data.setMaxLines(2);
        m_tvdocattrs3Data.setEllipsize(TextUtils.TruncateAt.END);

        m_rldocattrs4 = (RelativeLayout) ViewContent.findViewById(R.id.rl_docattrs4);
        m_tvdocattrs4 = (TextView) ViewContent.findViewById(R.id.tv_docattrs4_title);
        m_tvdocattrs4Data = (TextView) ViewContent.findViewById(R.id.tv_docattrs4_value);
        m_tvdocattrs4Data.setMaxLines(2);
        m_tvdocattrs4Data.setEllipsize(TextUtils.TruncateAt.END);

        m_rldocattrs5 = (RelativeLayout) ViewContent.findViewById(R.id.rl_docattrs5);
        m_tvdocattrs5 = (TextView) ViewContent.findViewById(R.id.tv_docattrs5_title);
        m_tvdocattrs5Data = (TextView) ViewContent.findViewById(R.id.tv_docattrs5_value);
        m_tvdocattrs5Data.setMaxLines(2);
        m_tvdocattrs5Data.setEllipsize(TextUtils.TruncateAt.END);


        switch(m_strAPISCode) {
            case "A":
                m_tvdocattrs1.setText(getString(R.string.apis_address_country)+":");
                m_tvdocattrs1Data.setText(getCountryName("USA",ISSUE_CD));

                m_tvdocattrs2.setText(getString(R.string.city_stat)+":");
                m_tvdocattrs2Data.setText(m_editApisEntity.docas.state);

                m_tvdocattrs3.setText(getString(R.string.city_county_district)+":");
                m_tvdocattrs3Data.setText(m_editApisEntity.docas.city);

                m_tvdocattrs4.setText(getString(R.string.street)+":");
                m_tvdocattrs4Data.setText(m_editApisEntity.docas.address);

                m_tvdocattrs5Data.setText(m_editApisEntity.docas.zipcode);

                m_rldocattrs4.setVisibility(View.VISIBLE);
                m_rldocattrs5.setVisibility(View.VISIBLE);
                break;
            case "N":
                m_tvdocattrs1.setText(getString(R.string.gender)+":");
                m_tvdocattrs1Data.setText(getGender(m_editApisEntity.basicDocuments.gender));


                m_tvdocattrs2.setText(getString(R.string.date_of_birth));
                m_tvdocattrs2Data.setText(m_editApisEntity.basicDocuments.birthday);

                m_tvdocattrs3.setText(getString(R.string.resident_country)+":");
                m_tvdocattrs3Data.setText(getCountryName(m_editApisEntity.basicDocuments.residence,RESIDENT_CD));

                m_tvdocattrs4.setText(getString(R.string.sign_up_nationality)+":");
                m_tvdocattrs4Data.setText(getCountryName(m_editApisEntity.basicDocuments.nationality, ISSUE_CD));

                m_rldocattrs4.setVisibility(View.VISIBLE);
                m_rldocattrs5.setVisibility(View.GONE);

                break;
            default:
                m_tvdocattrs1.setText(getString(R.string.document_number)+":");
                m_tvdocattrs1Data.setText(m_editApisEntity.otherDocuments.documentNo);

                m_tvdocattrs2.setText(getString(R.string.country_of_issuance)+":");
                m_tvdocattrs2Data.setText(getCountryName(m_editApisEntity.otherDocuments.issueCountry,ISSUE_CD));

                m_tvdocattrs3.setText(getString(R.string.date_of_expiry)+":");
                m_tvdocattrs3Data.setText(m_editApisEntity.otherDocuments.expireDay);
                m_rldocattrs4.setVisibility(View.GONE);
                m_rldocattrs5.setVisibility(View.GONE);

                break;

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
//        } else if( COUNTRY_CD == iCountryType ) {
//            return CIInquiryNationalPresenter.getInstance(null).getNationalMap().get(strCountryCd).country_name;
//        }

        return "";
    }

    private String getGender(String strGender) {
        if( TextUtils.isEmpty(strGender) ) {
            return "";
        }

        if (strGender.equals(CIApisAddEntity.SEX_FEMALE)) {
            return getString(R.string.female);
        }else{
            return getString(R.string.male);
        }
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
            String strData = GsonTool.toJson(m_editApisEntity);

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

            intent.putExtra(CIAddSaveAPISDocTypeActivity.APIS_FUN_ENTRANCE, FUN_ENTRY);//個人／checkin入口分類

            intent.putExtra(CIAddSaveAPISDocTypeActivity.APIS_OBJ_VALUE, strData);

            intent.putExtra(UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_USER_NAME_TAG, m_strUserName);

            intent.setClass(m_Context, CIPersonalAddSaveAPISActivity.class);
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
