package ci.function.MyTrips.Detail;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseNoToolbarActivity;
import ci.function.Core.CIApplication;
import ci.function.Main.item.CIQuestionItem;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ws.Models.entities.CIPendingQuestionnaireEntity;
import ci.ws.Models.entities.CIPullQuestionnaireReq;
import ci.ws.Models.entities.CIPullQuestionnaireResp;
import ci.ws.Presenter.CIQuestionnairePresenter;
import ci.ws.Presenter.Listener.CIPullQuestionnaireListner;
import ci.ws.cores.object.GsonTool;

import static ci.ws.cores.object.GsonTool.getGson;


/**
 * 問卷調查
 * zeplin: China Airlines_2 10.7-3 / 10.7-4
 * doc: CAL_App_UI Wireframe_v0.3.3_160414_s.pdf [P.69]
 * Created by Kevin Cheng on 2017/5/9.
 */
public class CIQuestionnaireActivity extends BaseNoToolbarActivity implements View.OnClickListener{


    //背景
    private Bitmap                  m_bitmap                        = null;
    private ImageButton             m_ibtnClose                     = null;
    private CIPullQuestionnaireReq  m_req                           = null;
    private CIQuestionnaireFragment m_fragment                      = null;
    private String                  m_respVersion                   = "";
    private boolean                 m_bIsFetchQues                  = true;
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_questionnaire_card;
    }

    @Override
    protected void initialLayoutComponent() {
        Bundle bundle   = getIntent().getExtras();

        String strJson = "";

        if(null != bundle){
            //背景
            m_bitmap    = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
            strJson     = bundle.getString(UiMessageDef.BUNDLE_PULL_QUES_REQ_DATA);
        }

        if(false == TextUtils.isEmpty(strJson)){
            m_req = getGson().fromJson(strJson, CIPullQuestionnaireReq.class);
        }

        //EX: WE0401308CI0008TPELAX2017-05-19
        /**
         * A. 行程有綁卡號: 照現行方式, 卡號 +航班編號+ 起站+迄站+ 表定離開日
         * B: 行程沒綁卡號: 請回傳 Pnr +航班+起站+迄站+ 表定離開日
         * 不需補零, 所以長度應為 6+6+3+3+10
         * B種問卷回填因不會收到相對應的推播,
         * 所以server端只會以起飛日判斷能不能取得題目*/
        if(TextUtils.isEmpty(m_req.token)) {
            if(!TextUtils.isEmpty(m_req.cardid)){
                m_req.token = m_req.cardid
                        + m_req.fltnumber
                        + m_req.departure
                        + m_req.arrival
                        + m_req.departure_date;
            } else {
                m_req.token = m_req.PNR
                        + m_req.fltnumber
                        + m_req.departure
                        + m_req.arrival
                        + m_req.departure_date;
                m_req.cardid = m_req.PNR;
            }

        } else {
            try {
                m_req.cardid = m_req.token.substring(0, 9);
                m_req.fltnumber = m_req.token.substring(9, 15);
                m_req.departure = m_req.token.substring(15, 18);
                m_req.arrival = m_req.token.substring(18, 21);
                m_req.departure_date = m_req.token.substring(21, 31);
            }catch (Exception e) {}
        }

        m_ibtnClose     = (ImageButton)     findViewById(R.id.ibtn_close);

        m_ibtnClose.setOnClickListener(this);

        RelativeLayout rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);

        if(null != m_bitmap){
            BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmap);
            rl_bg.setBackground(drawable);
        }

        m_fragment = getCIQuestionnaireFragmentFromFragmentManager();

        if(true == checkNetworkConnectionState() &&
                false == isContentDisplay()
                ){
            fetchDataAndInitContent();
        } else if(false == checkNetworkConnectionState()){
            showNoWifiDialog();
        }
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if(null != m_fragment){
            CIQuestionItem item = m_fragment.getQuestionItem();
            if(null != item){
                m_respVersion = item.version;
            }
        }
    }

    private void fetchDataAndInitContent() {

        if(!m_bIsFetchQues){
            return;
        }

        CIQuestionnairePresenter presenter = CIQuestionnairePresenter.getInstance(new CIPullQuestionnaireListner() {
            @Override
            public void onFetchData(CIPullQuestionnaireResp resp) {
                if(null != resp){
                    m_respVersion = resp.version;
                    initContent(resp , getPendingQuestionOnDatabase());
                }
            }

            @Override
            public void onSuccess(String rt_code, String rt_msg) {

            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                showDialogAndfinishAfterClickConfirm(rt_msg);
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

        if(null != m_req){
            presenter.pullQuestionnaireFromWS(m_req);
        }
    }

    private CIPendingQuestionnaireEntity getPendingQuestionOnDatabase(){
        if(null == m_req){
            return null;
        }
        ArrayList<CIPendingQuestionnaireEntity> datas = CIQuestionnairePresenter
                .getInstance().getPendingQuestionnaie(m_req.token , m_respVersion);
        if(null != datas && datas.size() >0){
            return datas.get(0);
        } else {
            return null;
        }
    }



    private void initContent(CIPullQuestionnaireResp resp, CIPendingQuestionnaireEntity data) {
        CIQuestionItem item ;
        if(null != data ){
            item = getQuestionItemByPendingQuestionnaire(resp, data);
        } else {
            item = getQuestionItemByResponce(resp);
        }



        String strDataJson  = GsonTool.getGson().toJson(item);
        String strReqJson   = GsonTool.getGson().toJson(m_req);

        m_fragment = CIQuestionnaireFragment.getInstance(strDataJson, strReqJson);

        if(null != data ){
            m_fragment.setPendingQuestionFromDatabase(data);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, m_fragment, CIQuestionnaireFragment.class.getSimpleName()).commitAllowingStateLoss();
    }

    private CIQuestionItem getQuestionItemByResponce(CIPullQuestionnaireResp resp){
        CIQuestionItem item = new CIQuestionItem();

        item.title = resp.title;
        item.language = resp.language;
        item.version = resp.version;

        for(int loop = 0;loop < resp.ques.size();loop++){
            CIQuestionItem.Question question = new CIQuestionItem.Question();
            question.name = resp.ques.get(loop);
            question.code = String.valueOf(loop);
            item.questions.add(question);
        }
        return item;
    }

    private CIQuestionItem getQuestionItemByPendingQuestionnaire(CIPullQuestionnaireResp resp,
                                                                 CIPendingQuestionnaireEntity entity){
        String strJson = entity.questionItem;
        CIQuestionItem item = GsonTool.getGson().fromJson(strJson, CIQuestionItem.class);

        item.title = resp.title;
        item.language = resp.language;

        if(item.questions == null
                || item.questions.size() <= 0
                || resp.ques.size() != item.questions.size()
                || !resp.version.equals(item.version)){
            return getQuestionItemByResponce(resp);
        }

        for(int i = 0; i < resp.ques.size(); i++){
            String data = resp.ques.get(i);
            CIQuestionItem.Question question = item.questions.get(i);
            question.name = data;
        }
        return item;
    }

    private boolean checkNetworkConnectionState(){
        boolean bIsAvailable = CIApplication.getSysResourceManager().isNetworkAvailable();
        return bIsAvailable;
    }


    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(m_ibtnClose, 40, 40);

    }

    @Override
    public void onNetworkConnect() {
        dismissNoWifiDialog();
        //在沒有取得問卷題目之前，只要監聽網路開啟，都會重新再取得問卷一次
        if(false == isContentDisplay() && false == getIsShowProgressDialog()){
            fetchDataAndInitContent();
        }
    }

    @Override
    public void onNetworkDisconnect() {
        showNoWifiDialog();
        CIQuestionnairePresenter.getInstance().interrupt();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CIQuestionnairePresenter.getInstance(null).interrupt();
    }

    @Override
    public void onClick(View v) {
        saveDataByCheckFragmentState();
        finishActivity();

    }

    @Override
    public void onBackPressed() {
        saveDataByCheckFragmentState();
        finishActivity();
    }

    private void saveDataByCheckFragmentState(){

        if(true == isShowForCIQuestionnaireFragment()){
            CIPendingQuestionnaireEntity entity = getPendingQuestionOnDatabase();
            //如果entity是null，代表沒有儲存待上傳的問卷在DB
            m_fragment.savePendingQuestionnaire(entity);
        }
    }

    private boolean isShowForCIQuestionnaireFragment(){
        if(null == m_fragment){
            m_fragment = getCIQuestionnaireFragmentFromFragmentManager();
        }

        return null != m_fragment && m_fragment.isVisible();
    }

    private boolean isContentDisplay(){
        return null != getCIQuestionnaireFragmentFromFragmentManager() ||
                null != getCIQuestionnaireResultFragmentFromFragmentManager();
    }

    private CIQuestionnaireFragment getCIQuestionnaireFragmentFromFragmentManager(){
        return (CIQuestionnaireFragment) getSupportFragmentManager()
                .findFragmentByTag(CIQuestionnaireFragment.class.getSimpleName());
    }

    private CIQuestionnaireResultFragment getCIQuestionnaireResultFragmentFromFragmentManager(){
        return (CIQuestionnaireResultFragment) getSupportFragmentManager()
                .findFragmentByTag(CIQuestionnaireResultFragment.class.getSimpleName());
    }

    private void finishActivity(){
        m_bIsFetchQues = false;
        CIQuestionnaireActivity.this.finish();
    }

    private void showDialogAndfinishAfterClickConfirm(String msg){
        showDialog(getString(R.string.warning),
                   msg,
                   getString(R.string.confirm),
                   null,
                   new CIAlertDialog.OnAlertMsgDialogListener() {
                       @Override
                       public void onAlertMsgDialog_Confirm() {
                           CIQuestionnaireActivity.this.finish();
                       }

                       @Override
                       public void onAlertMsgDialogg_Cancel() {

                       }
                   });
    }

}
