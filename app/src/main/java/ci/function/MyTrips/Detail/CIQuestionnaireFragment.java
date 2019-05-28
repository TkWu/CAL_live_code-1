package ci.function.MyTrips.Detail;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.function.Base.BaseNoToolbarActivity;
import ci.function.Core.CIApplication;
import ci.function.Main.item.CIProgressBarStyleHandler;
import ci.function.Main.item.CIQuestionItem;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.view.ShadowBar.ShadowBarLogic;
import ci.ws.Models.entities.CIPendingQuestionnaireEntity;
import ci.ws.Models.entities.CIPullQuestionnaireReq;
import ci.ws.Models.entities.CIPushQuestionnaireReq;
import ci.ws.Presenter.CIQuestionnairePresenter;
import ci.ws.Presenter.Listener.CIQuestionnaireListner;
import ci.ws.cores.object.GsonTool;

import static ci.ws.cores.object.GsonTool.getGson;

/**
 * 問卷調查輸入頁面
 * zeplin: China Airlines_2 10.7-3
 * Created by kevincheng on 2017/5/9.
 */

public class CIQuestionnaireFragment extends BaseFragment implements View.OnClickListener{

    private TextView                m_tvTitle                   = null;
    private CIQuestionnaireAdapter  m_adapter                   = null;
    private RecyclerView            m_recyclerView              = null;
    private View                    m_vGradient                 = null;
    private Button                  m_btn                       = null;
    private Context                 m_applicationContext        = CIApplication.getContext();
    private LinearLayoutManager     m_linearLayoutManager       = null;
    private CIPullQuestionnaireReq  m_pullReq                   = null;


    private CIPendingQuestionnaireEntity    m_EntityFromDatabase        = null;
    private CIQuestionItem                  m_savedQuestionItem         = null;

    private final static String     DATA_ON_DB_TAG      = "DATA_ON_DB_TAG";
    private final static String     QUESTION_ITEM_TAG   = "QUESTION_ITEM_TAG";
    public static CIQuestionnaireFragment getInstance(String strItemJson, String strReqJson){
        Bundle bundle = new Bundle();
        bundle.putString(UiMessageDef.BUNDLE_QUESTIONNAIRE_DATA, strItemJson);
        bundle.putString(UiMessageDef.BUNDLE_PULL_QUES_REQ_DATA, strReqJson);
        CIQuestionnaireFragment fragment = new CIQuestionnaireFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_questionnaire_card;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_tvTitle       = (TextView) view.findViewById(R.id.question_title);
        m_btn           = (Button) view.findViewById(R.id.btn_send);
        m_recyclerView  = (RecyclerView)view.findViewById(R.id.recycler_view);
        m_vGradient     = view.findViewById(R.id.vGradient);

        Bundle bundle = getArguments();

        if(bundle == null){
            return;
        }

        String strDataJson  = bundle.getString(UiMessageDef.BUNDLE_QUESTIONNAIRE_DATA,"");
        String strReqJson   = bundle.getString(UiMessageDef.BUNDLE_PULL_QUES_REQ_DATA,"");
        CIQuestionItem item;
        if(null == m_savedQuestionItem){
            item = getGson().fromJson(strDataJson, CIQuestionItem.class);
        } else {
            item = m_savedQuestionItem;
        }


        m_pullReq = getGson().fromJson(strReqJson, CIPullQuestionnaireReq.class);
        m_tvTitle.setText(item.title);
        m_adapter       = new CIQuestionnaireAdapter((CIQuestionItem)item.clone());
        m_linearLayoutManager = new LinearLayoutManager(m_applicationContext);
        m_recyclerView.setLayoutManager(m_linearLayoutManager);
        m_recyclerView.setAdapter(m_adapter);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ShadowBarLogic.switchViewGradientByLastItemOnRecyclerView(m_recyclerView,
                                                                          m_vGradient,
                                                                          m_linearLayoutManager);
            }
        });
    }

    private CIPushQuestionnaireReq getPostData(){
        return setupPostData(null);
    }

    private CIPushQuestionnaireReq setupPostData(CIPendingQuestionnaireEntity entity){
        CIPendingQuestionnaireEntity postData = entity;
        if(null == postData){
            postData = new CIPendingQuestionnaireEntity();
        }
        CIQuestionItem item = getQuestionItem();

        postData.cardid           = m_pullReq.cardid;

        postData.fltnumber        = m_pullReq.fltnumber;
        postData.language         = item.language;

        postData.departure        = m_pullReq.departure;
        postData.departure_date   = m_pullReq.departure_date;
        postData.arrival          = m_pullReq.arrival;

        StringBuilder builder = new StringBuilder();
        for(CIQuestionItem.Question data: item.questions){
            builder.append(String.valueOf(data.progress /
                                                  CIQuestionItem.Question.PROGRESS_OF_OPTION *
                                                  CIProgressBarStyleHandler.SCORE_OF_LEVEL));
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        postData.answers = builder.toString();


        String questionItemJson = GsonTool.getGson().toJson(item);
        postData.questionItem = questionItemJson;
        return postData;
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_btn.setOnClickListener(this);

        m_recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        ShadowBarLogic.switchViewGradientByLastItemOnRecyclerView(recyclerView,
                                                                                  m_vGradient,
                                                                                  m_linearLayoutManager);
                    }
                }
        );
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_send){
            CIQuestionnairePresenter presenter = CIQuestionnairePresenter.getInstance(new CIQuestionnaireListner() {
                @Override
                public void onSuccess(String rt_code, String rt_msg) {
                    deleteSavedDataOnDatabase();
                    changeToQuestionnareResultFragment();
                    showDialog(m_applicationContext.getString(R.string.warning), rt_msg);
                }

                private void deleteSavedDataOnDatabase(){
                    if(null != m_EntityFromDatabase){
                        CIQuestionnairePresenter.getInstance()
                                                .deletePendingQuestionnaieById(m_EntityFromDatabase.id);
                    }
                }

                private void changeToQuestionnareResultFragment(){
                    if(null != m_adapter){
                        CIQuestionItem item = m_adapter.getData();
                        String strJson = GsonTool.getGson().toJson(item);
                        Fragment fragment = CIQuestionnaireResultFragment.getInstance(strJson);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, fragment, CIQuestionnaireResultFragment.class.getSimpleName())
                                   .commitAllowingStateLoss();
                    }
                }

                @Override
                public void onError(String rt_code, String rt_msg) {

                    savePendingQuestionnaire(m_EntityFromDatabase);
                    showDialog(getString(R.string.warning),
                               rt_msg,
                               getString(R.string.confirm),
                               null,
                               new CIAlertDialog.OnAlertMsgDialogListener() {
                                   @Override
                                   public void onAlertMsgDialog_Confirm() {
                                       getActivity().finish();
                                   }

                                   @Override
                                   public void onAlertMsgDialogg_Cancel() {

                                   }
                               });

                }

                @Override
                public void showProgress() {
                    showProgressDialog();
                }

                @Override
                public void hideProgress() {
                    hideProgressDialog();
                }
            });

            if(true == checkNetworkConnectionState()) {
                presenter.pushQuestionnaireToWS(getPostData());
            } else {
                ((BaseNoToolbarActivity)getActivity()).showNoWifiDialog();
            }
        }
    }

    public void savePendingQuestionnaire(CIPendingQuestionnaireEntity data){

        CIPendingQuestionnaireEntity postData = (CIPendingQuestionnaireEntity)setupPostData(data).clone();
        postData.version = getQuestionItem().version;
        postData.token   = m_pullReq.token;
        CIQuestionnairePresenter.getInstance()
                                .savePendingQuestionnaie(postData);
    }

    public CIQuestionItem getQuestionItem(){
        if(null != m_adapter){
            return  m_adapter.getData();
        }
        return null;
    }

    public void setPendingQuestionFromDatabase(CIPendingQuestionnaireEntity entity){
        m_EntityFromDatabase = entity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(null != savedInstanceState){
            CIPendingQuestionnaireEntity    savedEntityOnDatabase = null;
            String json1 = savedInstanceState.getString(DATA_ON_DB_TAG);
            if(!TextUtils.isEmpty(json1)){
                savedEntityOnDatabase =  GsonTool.getGson().fromJson(json1, CIPendingQuestionnaireEntity.class);
            }
            if(null != savedEntityOnDatabase){
                m_EntityFromDatabase = savedEntityOnDatabase;
            }

            String json2 = savedInstanceState.getString(QUESTION_ITEM_TAG);
            if(!TextUtils.isEmpty(json2)){
                m_savedQuestionItem =  GsonTool.getGson().fromJson(json2, CIQuestionItem.class);
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(null != m_EntityFromDatabase){
            String json1 = GsonTool.getGson().toJson(m_EntityFromDatabase);
            outState.putString(DATA_ON_DB_TAG, json1);
        }
        String json2 = GsonTool.getGson().toJson(getQuestionItem());
        outState.putString(QUESTION_ITEM_TAG, json2);
    }

    private boolean checkNetworkConnectionState(){
        boolean bIsAvailable = CIApplication.getSysResourceManager().isNetworkAvailable();
        return bIsAvailable;
    }
}
