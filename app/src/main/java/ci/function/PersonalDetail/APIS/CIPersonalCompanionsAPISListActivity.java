package ci.function.PersonalDetail.APIS;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.function.Main.BaseActivity;
import ci.ui.ApisCard.item.CIApisCardViewItem;
import ci.ui.TextField.CIApisDocmuntTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Models.entities.CIApisEntity;
import ci.ws.Models.entities.CIApisQryRespEntity;
import ci.ws.Models.entities.CICompanionApisEntity;
import ci.ws.Presenter.CIAPISPresenter;
import ci.ws.cores.object.GsonTool;

import static ci.ui.view.NavigationBar.onNavigationbarListener;
import static ci.ui.view.NavigationBar.onNavigationbarParameter;

/**
 * Created by jlchen on 2016/3/22.
 */
public class CIPersonalCompanionsAPISListActivity extends BaseActivity {

    private onNavigationbarParameter m_onNavigationParameter = new onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_strUserName.replace(":","/");
        }
    };

    private onNavigationbarListener m_onNavigationbarListener = new onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            CIPersonalCompanionsAPISListActivity.this.finish();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    private String m_strUserName = "";
    //private String[] m_strAPISNames;

    public NavigationBar    m_Navigationbar     = null;
    public FrameLayout      m_flayout_Content   = null;

    private Bitmap          m_bitmap            = null;
    private RelativeLayout  m_rlBg              = null;

    private RecyclerView    m_rv                = null;
    private RecyclerAdpter  m_RecyclerAdpter    = null;

    private ArrayList<CIApisCardViewItem> m_alData = new ArrayList<>();

    private ArrayList<CICompanionApisEntity> m_arCompanionsApisList = null;

    private CIApisQryRespEntity.CIApispaxInfo companionApisEntity = null;
    private ArrayList<CIApisQryRespEntity.ApisRespDocObj> ar_companionApis = null;
    private String m_strFullName = null;

    private CIApisDocmuntTextFieldFragment.EType m_apisType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //ArrayList<CIApisQryRespEntity.CIApisRespPaxInfo>
//        intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE, CIPersonalAddAPISActivity.CIPersonalAddAPISType.EDIT_MY_APIS.name()); //功能
//        intent.putExtra(CIAddSaveAPISDocTypeActivity.APIS_FUN_ENTRANCE, CIAddSaveAPISDocTypeActivity.EType.Personal.name());//個人／checkin入口分類
//        intent.putExtra(CIAddSaveAPISDocTypeActivity.APIS_OBJ_VALUE, strCompanionName);
//
//        String mode = getIntent().getStringExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE);//APIS功能(新增／編輯  自己／同行)
//        if (null != mode) {
//            m_type = CIPersonalAddSaveAPISActivity.CIPersonalAddAPISType.valueOf(mode);
//        }
//        SLog.d("m_type: "+m_type.name());
//        if (null != strUserName) {
//            String[] strTags = strUserName.split("&");
//            m_strUserName = strTags[0];
//            m_strFullName = strTags[1];
//
//            m_arCompanionsApisList = CIAPISPresenter.getInstance().getCompanionsApisListWithFullName(CIApplication.getLoginInfo().GetUserMemberCardNo(), m_strFullName);
//        }
//
//        String strAPISNames = getIntent().getStringExtra(UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_TAG);
//        if (null != strAPISNames) {
//            m_strAPISNames = strAPISNames.split(" / ");
//        }
//        String strUserName = getIntent().getStringExtra(UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_USER_NAME_TAG);


        String fun_entry = getIntent().getStringExtra(CIAddSaveAPISDocTypeActivity.APIS_FUN_ENTRANCE); //APIS編輯進入點  個人資訊／報到時
        m_apisType = CIApisDocmuntTextFieldFragment.EType.valueOf(fun_entry);

        String s_CompanionApisEntity = getIntent().getStringExtra(CIAddSaveAPISDocTypeActivity.APIS_OBJ_VALUE);//同行者ＡＰＩＳ物件
        if (null != s_CompanionApisEntity) {
            Gson gson = new Gson();

            try{
                companionApisEntity = gson.fromJson(s_CompanionApisEntity, CIApisQryRespEntity.CIApispaxInfo.class);
                m_strUserName = companionApisEntity.firstName+ ":"+companionApisEntity.lastName;
            } catch ( Exception e ){
                e.printStackTrace();
            }
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initialLayoutComponent() {
        initialAPISInfo();

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_flayout_Content = (FrameLayout) findViewById(R.id.container);

        View ViewContent = View.inflate(this, R.layout.fragment_personal_companions_apis_list, null);
        m_flayout_Content.addView(ViewContent);

        m_rlBg      = (RelativeLayout) ViewContent.findViewById(R.id.root);
        m_bitmap    = ImageHandle.getLocalBitmap(m_Context, R.drawable.bg_login, 1);
        Drawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        m_rlBg.setBackground(drawable);

        m_rv = (RecyclerView) ViewContent.findViewById(R.id.rv);
        m_rv.setLayoutManager(new LinearLayoutManager(this));
        m_RecyclerAdpter = new RecyclerAdpter();
        m_rv.setAdapter(m_RecyclerAdpter);
    }

    //初始化apis資訊
    private void initialAPISInfo() {
        m_alData.clear();

//        if( null != m_arCompanionsApisList ) {
//            for ( int i = 0 ; i < m_arCompanionsApisList.size() ; i++) {
//                m_alData.add(new CIApisCardViewItem(
//                    m_strAPISNames[i], m_arCompanionsApisList.get(i).doc_no));
//            }
//        }
        if (companionApisEntity != null) {
            ar_companionApis = (ArrayList<CIApisQryRespEntity.ApisRespDocObj>)companionApisEntity.documentInfos.clone();
            for (CIApisQryRespEntity.ApisRespDocObj tmp :ar_companionApis) {
                m_alData.add(new CIApisCardViewItem(
                        tmp.documentName, getDocmuntName(tmp.documentType)));
            }
        }
    }
    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_flayout_Content);

    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
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
    protected void onLanguageChangeUpdateUI() {

    }

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
            CIPersonalCompanionsAPISListActivity.this.finish();
        }
    }

    public class RecyvlerViewHolder extends RecyclerView.ViewHolder {

        LinearLayout    m_llayoutbg;
        RelativeLayout  m_rlayout;
        TextView        m_tvName;
        TextView        m_tvText;
        View            m_vDiv;
        ImageButton m_ibtnArrow;


        RecyvlerViewHolder(View view) {
            super(view);
            m_llayoutbg     = (LinearLayout)view.findViewById(R.id.root);
            m_rlayout       = (RelativeLayout)view.findViewById(R.id.rlayout);
            m_tvName        = (TextView)view.findViewById(R.id.tv_apis_name);
            m_tvText        = (TextView)view.findViewById(R.id.tv_apis_no);
            m_vDiv          = view.findViewById(R.id.vDiv);
            m_ibtnArrow     = (ImageButton)view.findViewById(R.id.ibtn_arrow);
        }
    }

    class RecyclerAdpter extends RecyclerView.Adapter<RecyvlerViewHolder>{

        private LayoutInflater m_LayoutInflate = null;
        private ViewScaleDef     m_viewScaleDef  = null;

        RecyclerAdpter(){
            m_LayoutInflate = LayoutInflater.from(m_Context);
            m_viewScaleDef = ViewScaleDef.getInstance(m_Context);
        }

        /**Create View*/
        @Override
        public RecyvlerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = null;
            view = m_LayoutInflate.inflate(R.layout.layout_view_personal_companions_list_item, parent, false);

            RecyvlerViewHolder recyclerholder = new RecyvlerViewHolder(view);
            m_viewScaleDef.selfAdjustAllView(recyclerholder.m_llayoutbg);
            m_viewScaleDef.selfAdjustSameScaleView(recyclerholder.m_ibtnArrow, 24, 24);

            return recyclerholder;
        }

        /**讓畫面與資料進行綁定*/
        @Override
        public void onBindViewHolder(RecyvlerViewHolder holder, final int position) {

            holder.m_rlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE, CIPersonalAddAPISActivity.CIPersonalAddAPISType.EDIT_COMPANAIONS_APIS.name()); //功能
                    intent.putExtra(CIAddSaveAPISDocTypeActivity.APIS_FUN_ENTRANCE, CIAddSaveAPISDocTypeActivity.EType.Personal.name());//個人／checkin入口分類
                    intent.putExtra(UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_USER_NAME_TAG, m_strUserName);//個人／checkin入口分類

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(CIAddSaveAPISDocTypeActivity.APIS_OBJ_VALUE, ar_companionApis.get(position));

                    intent.putExtras(bundle);
                    intent.setClass(m_Context, CIPersonalAddSaveAPISActivity.class);
                    //要改
                    startActivityForResult(intent, UiMessageDef.REQUEST_CODE_PERSONAL_EDIT_APIS_TAG);
                    overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);


//                    Intent intent = new Intent();
//                    intent.putExtra(
//                            UiMessageDef.BUNDLE_ACTIVITY_MODE,
//                            CIPersonalAddAPISActivity.CIPersonalAddAPISType.EDIT_COMPANAIONS_APIS.name());
//                    intent.putExtra(
//                            UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_TAG,
//                            getApisData(position) );
////                            m_alData.get(position).GetHeadText());
//                    intent.putExtra(
//                            UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_USER_NAME_TAG,
//                            m_strUserName);
//                    intent.setClass(m_Context, CIPersonalAPISDetialActivity.class);
//                    startActivityForResult(intent, UiMessageDef.REQUEST_CODE_PERSONAL_EDIT_APIS_TAG);
//
//                    overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                }
            });

//            holder.m_tvName.setText(m_alData.get(position).GetHeadText());
//            holder.m_tvText.setText(m_alData.get(position).GetBodyText());
            ar_companionApis = (ArrayList<CIApisQryRespEntity.ApisRespDocObj>)companionApisEntity.documentInfos.clone();
            holder.m_tvName.setText(ar_companionApis.get(position).documentName);
            holder.m_tvText.setText(getDocmuntName(ar_companionApis.get(position).documentType));
        }

        @Override
        public int getItemCount() {
            if ( null != m_alData){
                return m_alData.size();
            }
            return 0;
        }

    }

//    private String getApisData(int iPosition) {
//        CICompanionApisEntity entity = m_arCompanionsApisList.get(iPosition);
//
//        CIApisEntity apisEntity     = new CIApisEntity();
//        apisEntity.doc_type         = entity.doc_type;
//        apisEntity.doc_no           = entity.doc_no;
//        apisEntity.nationality      = entity.nationality;
//        apisEntity.doc_expired_date = entity.doc_expired_date;
//        apisEntity.issue_country    = entity.issue_country;
//        apisEntity.resident_city    = entity.resident_city;
//        apisEntity.last_name        = entity.last_name;
//        apisEntity.first_name       = entity.first_name;
//        apisEntity.birthday         = entity.birthday;
//        apisEntity.sex              = entity.sex;
//        apisEntity.addr_street      = entity.addr_street;
//        apisEntity.addr_city        = entity.addr_city;
//        apisEntity.addr_state       = entity.addr_state;
//        apisEntity.addr_country     = entity.addr_country;
//        apisEntity.addr_zipcode     = entity.addr_zipcode;
//        apisEntity.card_no          = entity.card_no;
//        apisEntity.setId(entity.getId());
//
//        return GsonTool.toJson(apisEntity).toString();
//    }

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
}
