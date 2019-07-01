package ci.ui.ApisCard;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import ci.function.Core.SLog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashMap;

import ci.function.Base.BaseView;
import ci.function.Core.CIApplication;
import ci.ui.ApisCard.item.CIApisCardViewItem;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Models.entities.CIApisEntity;
import ci.ws.Models.entities.CIApisQryRespEntity;
import ci.ws.Models.entities.CICompanionApisNameEntity;
import ci.ws.Presenter.CIAPISPresenter;
import ci.ws.cores.object.GsonTool;

/**
 * 個人管理 Aview
 * 兩種type
 * 1.瀏覽
 * 2.編輯
 * Created by jlchen on 2016/3/17.
 */
public class CIApisCardView extends BaseView implements View.OnClickListener {

    public interface OnPersonalApisCardViewListener {
        //按下個人旅客資料的新增按鈕
        void OnAddPersonalAPISClick();
        //按下同行旅客的新增按鈕
        void OnAddCompanionsAPISClick();
        //按下個人資料的詳細資訊
        void OnPersonalAPISDetailClick(String strAPISName);
        //按下旅客的詳細資訊
        //void OnCompanionsAPISDetailClick(String strCompanionsName, String strAPISNames);
        void OnCompanionsAPISDetailClick(String strCompanionsName);
    }

    private OnPersonalApisCardViewListener m_Listener = null;

    enum CIApisCardType {
        MY_APIS, COMPANIONS_APIS;
    }

    private CIApisCardType m_type = CIApisCardType.MY_APIS;

    private static final int DEF_IMG_WIDTH = 24;

    private RelativeLayout m_rlayout = null;
    private TextView m_tvTitle = null;
    private TextView m_tvSubTitle = null;
    private ImageButton m_ibtnAdd = null;

    private LinearLayout m_llBody = null;
    private TextView m_tvNoApis = null;

    private ImageButton m_ibtnArrow = null;

    private ArrayList<CIApisCardViewItem> m_alData = new ArrayList<>();

    private ArrayList<CIApisQryRespEntity.ApisRespDocObj> m_ar_apisList = null;

    private ArrayList<CIApisQryRespEntity.CIApispaxInfo> m_arCompanionApisList = null;

    private static HashMap<String, CIApisDocmuntTypeEntity> m_apisDocTypeList = null;

    public CIApisCardView(Context context) {
        super(context);
        initial();
    }

    public CIApisCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_view_personal_apis_card;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater) {
        m_tvTitle = (TextView) findViewById(R.id.tv_title);
        m_tvSubTitle = (TextView) findViewById(R.id.tv_sub_title);
        m_ibtnAdd = (ImageButton) findViewById(R.id.ibtn_add);
        findViewById(R.id.ll_add).setOnClickListener(this);

        m_llBody = (LinearLayout) findViewById(R.id.ll_body);
        m_tvNoApis = (TextView) findViewById(R.id.tv_no_api);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.personal_apis_card_root));
        vScaleDef.selfAdjustSameScaleView(m_ibtnAdd, DEF_IMG_WIDTH, DEF_IMG_WIDTH);
    }

    public void setMyApisView() {
        m_type = CIApisCardType.MY_APIS;

        m_tvTitle.setText(m_Context.getString(R.string.my_apis));
        m_tvSubTitle.setText(m_Context.getString(R.string.advance_passenger_information_system));
    }

    public void setCompanionsApisView() {
        m_type = CIApisCardType.COMPANIONS_APIS;

        m_tvTitle.setText(m_Context.getString(R.string.companions_apis));
        m_tvSubTitle.setText(m_Context.getString(R.string.advance_passenger_information_system));
    }

    public void setHaveData() {
        m_tvNoApis.setVisibility(GONE);

        initialAPISInfo();

        refreshView();
    }

    private void refreshView() {

        m_llBody.removeAllViews();

        if( null == m_alData || 0 >= m_alData.size() ) {
            m_tvNoApis.setVisibility(VISIBLE);
            m_llBody.addView(m_tvNoApis);
        } else {
            m_tvNoApis.setVisibility(GONE);
        }

        SLog.d("m_type "+ m_type);
        switch (m_type) {
            case MY_APIS:
                SLog.d("MY_APIS: "+m_ar_apisList.size()+" "+m_alData.size());
                for (int i = 0; i < m_alData.size(); i++) {

                    View vApisData = m_layoutInflater.inflate(R.layout.layout_view_personal_apis_card_item, null);
                    m_ibtnArrow = (ImageButton) vApisData.findViewById(R.id.ibtn_arrow);
                    m_rlayout = (RelativeLayout) vApisData.findViewById(R.id.rlayout);

                    CIApisQryRespEntity.ApisRespDocObj apisEntity = m_ar_apisList.get(i);
                    m_rlayout.setTag(apisEntity);
                    m_rlayout.setOnClickListener(this);

                    TextView tvHead = (TextView) vApisData.findViewById(R.id.tv_head);
                    tvHead.setText(m_alData.get(i).GetHeadText());
                    TextView tvBody = (TextView) vApisData.findViewById(R.id.tv_body);
                    tvBody.setText(m_alData.get(i).GetBodyText());

                    m_llBody.addView(vApisData);

                    ViewScaleDef.getInstance(m_Context).selfAdjustAllView(vApisData.findViewById(R.id.personal_apis_card_item_root));
                    ViewScaleDef.getInstance(m_Context).selfAdjustSameScaleView(
                            m_ibtnArrow, DEF_IMG_WIDTH, DEF_IMG_WIDTH);

                    View vMarginTop = (View) vApisData.findViewById(R.id.v_marginTop);
                    if ( 0 == i ){
                        vMarginTop.setVisibility(VISIBLE);
                    }else {
                        vMarginTop.setVisibility(GONE);
                    }
                }
                break;
            case COMPANIONS_APIS:
                SLog.d("COMPANIONS_APIS: "+m_arCompanionApisList.size()+" "+m_alData.size());
                for (int i = 0; i < m_alData.size(); i++) {

                    View vApisData = m_layoutInflater.inflate(R.layout.layout_view_personal_apis_card_item, null);
                    m_ibtnArrow = (ImageButton) vApisData.findViewById(R.id.ibtn_arrow);
                    m_rlayout = (RelativeLayout) vApisData.findViewById(R.id.rlayout);

                    m_rlayout.setTag(m_arCompanionApisList.get(i));
                    m_rlayout.setOnClickListener(this);

                    TextView tvHead = (TextView) vApisData.findViewById(R.id.tv_head);
                    tvHead.setText(m_alData.get(i).GetHeadText());
                    TextView tvBody = (TextView) vApisData.findViewById(R.id.tv_body);
                    tvBody.setText(m_alData.get(i).GetBodyText());

                    m_llBody.addView(vApisData);

                    ViewScaleDef.getInstance(m_Context).selfAdjustAllView(vApisData.findViewById(R.id.personal_apis_card_item_root));
                    ViewScaleDef.getInstance(m_Context).selfAdjustSameScaleView(
                            m_ibtnArrow, DEF_IMG_WIDTH, DEF_IMG_WIDTH);

                    View vMarginTop = (View) vApisData.findViewById(R.id.v_marginTop);
                    if ( 0 == i ){
                        vMarginTop.setVisibility(VISIBLE);
                    }else {
                        vMarginTop.setVisibility(GONE);
                    }
                }
                break;
        }



        //MyApis新增四筆後隱藏新增[+]按鈕
        //2016/06/22 改為不隱藏新增[+]按鈕
//        if (m_type.equals(CIApisCardType.MY_APIS) && m_alData.size() >= 4 ){
//            m_ibtnAdd.setVisibility(GONE);
//        }else {
//            m_ibtnAdd.setVisibility(VISIBLE);
//        }
    }

    private <T> String getTag(T apisEntity) {

        if( null == apisEntity ) {
            return "";
        }

        return GsonTool.toJson(apisEntity);
    }

    public void notifyMyApisDataUpdate(ArrayList<CIApisQryRespEntity.ApisRespDocObj> ar_apisList) {

        m_alData.clear();

        if( m_ar_apisList != null ) {
            m_ar_apisList.clear();
        }

        m_ar_apisList = (ArrayList<CIApisQryRespEntity.ApisRespDocObj>) ar_apisList.clone();

        for(CIApisQryRespEntity.ApisRespDocObj apis : ar_apisList ) {

            String strPassport = getDocTypeName(apis.documentName);

            if( TextUtils.isEmpty(strPassport) ) {
                strPassport = apis.documentName;
            }

            m_alData.add(new CIApisCardViewItem(
                    strPassport, getResources().getString(R.string.document_type1)+": "+getDocTypeName(apis.documentType)));
        }


        refreshView();
    }

    private String getDocTypeName(String strDocType) {
        if( null == m_apisDocTypeList || 0 >= m_apisDocTypeList.size() ) {
            m_apisDocTypeList = CIAPISPresenter.getInstance().fetchApisDocmuntMap();
        }

        CIApisDocmuntTypeEntity apisDocmuntTypeEntity = m_apisDocTypeList.get(strDocType);

        if( null != apisDocmuntTypeEntity ) {
            return apisDocmuntTypeEntity.getName(CIApplication.getLanguageInfo().getLanguage_Locale());
        }

        return "";
    }

    public void notifyCompanionApisDataUpdate(ArrayList<CIApisQryRespEntity.CIApispaxInfo> ar_apisList) {
        m_alData.clear();

        if( m_arCompanionApisList != null ) {
            m_arCompanionApisList.clear();
        }


        if( null != ar_apisList ) {
            m_arCompanionApisList = (ArrayList<CIApisQryRespEntity.CIApispaxInfo>) ar_apisList.clone();

            for(CIApisQryRespEntity.CIApispaxInfo paxinfo : m_arCompanionApisList ) {

                if (paxinfo.firstName.equals(CIApplication.getLoginInfo().GetUserFirstName()) &&
                        paxinfo.lastName.equals(CIApplication.getLoginInfo().GetUserLastName()))
                {
                    m_arCompanionApisList.remove(paxinfo);
                    break;
                }
            }

            for(CIApisQryRespEntity.CIApispaxInfo paxinfo : m_arCompanionApisList ) {
                StringBuffer sbPassport = new StringBuffer();
                for (CIApisQryRespEntity.ApisRespDocObj apis : paxinfo.documentInfos) {

                    if( null == apis ) {
                        continue;
                    }

                    String strPassport = getDocTypeName(apis.documentType);

                    if (TextUtils.isEmpty(strPassport)) {
                        continue;
                    }

                    if (0 < sbPassport.length()) {
                        sbPassport.append(" / " + strPassport);
                    } else {
                        sbPassport.append(strPassport);
                    }
                }
                m_alData.add(new CIApisCardViewItem(
                        paxinfo.firstName+" "+paxinfo.lastName, sbPassport.toString()));
            }
        }

        refreshView();
    }

    //初始化apis資訊
    private void initialAPISInfo() {
        m_alData.clear();

        String strPassport = m_Context.getString(R.string.passport);
        String strUSA = m_Context.getString(R.string.usa_permanent_resident_card);
        String strTaiwan = m_Context.getString(R.string.tai_bao_cheng);
        String strChina = m_Context.getString(R.string.mainland_residents_traveling_to_taiwan_pass);

        switch (m_type) {
            case MY_APIS:
                m_alData.add(new CIApisCardViewItem(
                        strPassport, "No: 214188500"));

                m_alData.add(new CIApisCardViewItem(
                        strUSA, "No: 214188500"));

                m_alData.add(new CIApisCardViewItem(
                        strTaiwan, "No: 214188500"));

                m_alData.add(new CIApisCardViewItem(
                        strChina, "No: 214188500"));
                break;
            case COMPANIONS_APIS:

                m_alData.add(new CIApisCardViewItem(
                        "Wei Wei, Chang", strPassport));

                m_alData.add(new CIApisCardViewItem(
                        "Roy, Wang", strPassport +" / "+ strUSA));

                m_alData.add(new CIApisCardViewItem(
                        "Cindy, Gu", strPassport +" / "+ strUSA +" / "+ strTaiwan +" / "+ strChina));

                break;
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_add://R.id.ibtn_add:
               SLog.d(AppInfo.APP_LOG_TAG, m_type.toString());

                if (m_type.equals(CIApisCardType.MY_APIS)) {
                    m_Listener.OnAddPersonalAPISClick();
                } else {
                    m_Listener.OnAddCompanionsAPISClick();
                }
                break;
            case R.id.rlayout:

                String strTag = "";
//                String strApisName = "";
//                if( CIApisCardType.MY_APIS == m_type ) {
//
//                    CIApisEntity apisEntity = (CIApisEntity)v.getTag();
//                    strTag = getTag(apisEntity);
//
//                } else if( CIApisCardType.COMPANIONS_APIS == m_type ) {
//                    String[] arTags = ((String)v.getTag()).split(";");
//                    strTag = arTags[0];
//                    strApisName = arTags[1];
//
//                }
                if( CIApisCardType.MY_APIS == m_type ) {

                    CIApisQryRespEntity.ApisRespDocObj apisEntity = (CIApisQryRespEntity.ApisRespDocObj)v.getTag();
                    strTag = getTag(apisEntity);

                } else if( CIApisCardType.COMPANIONS_APIS == m_type ) {
                    CIApisQryRespEntity.CIApispaxInfo paxInfo = (CIApisQryRespEntity.CIApispaxInfo)v.getTag();
                    strTag = getTag(paxInfo);
                }

                SLog.d(AppInfo.APP_LOG_TAG, m_type.toString() + strTag);

//                if (m_type.equals(CIApisCardType.MY_APIS)) {
//                    m_Listener.OnPersonalAPISDetailClick(strTag);
//                } else {
//                    m_Listener.OnCompanionsAPISDetailClick(strTag, strApisName);
//                }
                if (m_type.equals(CIApisCardType.MY_APIS)) {
                    m_Listener.OnPersonalAPISDetailClick(strTag);
                } else {
                    m_Listener.OnCompanionsAPISDetailClick(strTag);
                }
                break;
        }
    }

    public void uiSetOnParameterAndListener(OnPersonalApisCardViewListener listener) {
        this.m_Listener = listener;
    }
}
