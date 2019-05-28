package ci.ui.SocialNetworkCard;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseView;
import ci.function.Core.CIApplication;
import ci.ui.define.ViewScaleDef;
import ci.ws.Models.entities.CIProfileEntity;

/** 個人管理 社群媒體view
 * 兩種type
 * 1.瀏覽
 * 2.編輯
 * Created by jlchen on 2016/3/17.
 */
public class CIPersonalSocialNetworkView extends BaseView implements View.OnClickListener{

    public interface OnPersonalSocialNetworkViewListener {
        //按下編輯按鈕
        void OnEditClick();
        //按下fb連線
        void OnFacebookConnectClick();
        //按下fb中斷連線
        void OnFacebookDisconnectClick();
        //按下google連線
        void OnGoogleConnectClick();
        //按下google中斷連線
        void OnGoogleDisconnectClick();
    }

    enum GoogleConnectStatus{
        CONNECTED, DISCONNECT;
    }

    enum FacebookConnectStatus{
        CONNECTED, DISCONNECT;
    }

    private FacebookConnectStatus m_fbType;
    private GoogleConnectStatus m_googleType;

    private static final int DEF_IMG_WIDTH = 24;

    private OnPersonalSocialNetworkViewListener m_Listener;

    private RelativeLayout m_rlHead = null;
    private TextView m_tvTitle = null;
    private ImageButton m_ibtnEdit = null;

    private LinearLayout m_llBody = null;

    private RelativeLayout m_rlFb = null;
    private ImageView m_ivFb = null;
    private TextView m_tvFb = null;
    private TextView m_tvFbIdLeft = null;
    private TextView m_tvFbIdRight = null;
    private Button m_btnFb = null;
    private View m_vLine1 = null;

    private RelativeLayout m_rlGoogle = null;
    private ImageView m_ivGoogle = null;
    private TextView m_tvGoogle = null;
    private TextView m_tvGoogleIdLeft = null;
    private TextView m_tvGoogleIdRight = null;
    private Button m_btnGoogle = null;
    private View m_vLine2 = null;

    public CIPersonalSocialNetworkView(Context context){
        super(context);
        initial();
    }

    public CIPersonalSocialNetworkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_view_personal_social_network;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater) {

        m_rlHead = (RelativeLayout) findViewById(R.id.rl_head);
        m_tvTitle = (TextView) findViewById(R.id.tv_title);
        m_ibtnEdit = (ImageButton) findViewById(R.id.ibtn_edit);
        findViewById(R.id.ll_edit).setOnClickListener(this);

        m_llBody = (LinearLayout) findViewById(R.id.ll_body);

        m_rlFb = (RelativeLayout) findViewById(R.id.rl_facebook);
        m_ivFb = (ImageView) findViewById(R.id.iv_fb);
        m_tvFb = (TextView) findViewById(R.id.tv_facebook);
        m_tvFbIdLeft = (TextView) findViewById(R.id.tv_fb_id_l);
        m_tvFbIdRight = (TextView) findViewById(R.id.tv_fb_id);
        m_btnFb = (Button) findViewById(R.id.btn_fb);
        m_btnFb.setOnClickListener(this);
        m_vLine1 = (View) findViewById(R.id.v_line1);

        m_rlGoogle = (RelativeLayout) findViewById(R.id.rl_google);
        m_ivGoogle = (ImageView) findViewById(R.id.iv_google);
        m_tvGoogle = (TextView) findViewById(R.id.tv_google);
        m_tvGoogleIdLeft = (TextView) findViewById(R.id.tv_google_id_l);
        m_tvGoogleIdRight = (TextView) findViewById(R.id.tv_google_id);
        m_btnGoogle = (Button) findViewById(R.id.btn_google);
        m_btnGoogle.setOnClickListener(this);
        m_vLine2 = (View) findViewById(R.id.v_line2);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.personal_social_network_root));
        vScaleDef.selfAdjustSameScaleView(m_ibtnEdit, DEF_IMG_WIDTH, DEF_IMG_WIDTH);
        vScaleDef.selfAdjustSameScaleView(m_ivFb, DEF_IMG_WIDTH, DEF_IMG_WIDTH);
        vScaleDef.selfAdjustSameScaleView(m_ivGoogle, DEF_IMG_WIDTH, DEF_IMG_WIDTH);
        m_tvFb.setMaxWidth(vScaleDef.getLayoutWidth(166));
        m_tvFbIdLeft.setMaxWidth(vScaleDef.getLayoutWidth(166));
        m_tvGoogle.setMaxWidth(vScaleDef.getLayoutWidth(166));
        m_tvGoogleIdLeft.setMaxWidth(vScaleDef.getLayoutWidth(166));

//        vScaleDef.setPadding(m_rlHead, 20, 0, 20, 0);
//        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) m_rlHead.getLayoutParams();
//        lParams.height = vScaleDef.getLayoutHeight(64);
//
//        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_20, m_tvTitle);
//
//        vScaleDef.setPadding(m_llBody, 20, 10, 20, 20);
//
//        lParams = (LinearLayout.LayoutParams) m_rlFb.getLayoutParams();
//        lParams.height = vScaleDef.getLayoutHeight(60);
//
//        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) m_tvFb.getLayoutParams();
//        rParams.leftMargin = vScaleDef.getLayoutWidth(10);
//        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, m_tvFb);
//
//        rParams = (RelativeLayout.LayoutParams) m_tvFbIdRight.getLayoutParams();
//        rParams.leftMargin = vScaleDef.getLayoutWidth(10);
//        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, m_tvFbIdRight);
//
//        rParams = (RelativeLayout.LayoutParams) m_btnFb.getLayoutParams();
//        rParams.width = vScaleDef.getLayoutWidth(80);
//        rParams.height = vScaleDef.getLayoutHeight(32);
//        rParams.leftMargin = vScaleDef.getLayoutWidth(10);
//        vScaleDef.setTextSize(13, m_btnFb);
//
//        vScaleDef.setPadding(m_vLine1, 20, 0, 20, 0);
//        lParams = (LinearLayout.LayoutParams) m_vLine1.getLayoutParams();
//        lParams.height = vScaleDef.getLayoutHeight(1);
//
//        lParams = (LinearLayout.LayoutParams) m_rlGoogle.getLayoutParams();
//        lParams.height = vScaleDef.getLayoutHeight(60);
//
//        rParams = (RelativeLayout.LayoutParams) m_tvGoogle.getLayoutParams();
//        rParams.leftMargin = vScaleDef.getLayoutWidth(10);
//        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, m_tvGoogle);
//
//        rParams = (RelativeLayout.LayoutParams) m_tvGoogleIdRight.getLayoutParams();
//        rParams.leftMargin = vScaleDef.getLayoutWidth(10);
//        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, m_tvGoogleIdRight);
//
//        rParams = (RelativeLayout.LayoutParams) m_btnGoogle.getLayoutParams();
//        rParams.width = vScaleDef.getLayoutWidth(80);
//        rParams.height = vScaleDef.getLayoutHeight(32);
//        rParams.leftMargin = vScaleDef.getLayoutWidth(10);
//        vScaleDef.setTextSize(13, m_btnGoogle);
//
//        vScaleDef.setPadding(m_vLine2, 20, 0, 20, 0);
//        lParams = (LinearLayout.LayoutParams) m_vLine2.getLayoutParams();
//        lParams.height = vScaleDef.getLayoutHeight(1);
    }

    public void uiSetOnParameterAndListener(OnPersonalSocialNetworkViewListener listener) {
        this.m_Listener = listener;
    }

    //瀏覽模式-個人資訊頁面用
    public void setBrowse(String strFb, String strGoogle){

        m_ibtnEdit.setVisibility(VISIBLE);
        m_tvFbIdRight.setVisibility(VISIBLE);
        m_tvGoogleIdRight.setVisibility(VISIBLE);
        m_tvFbIdLeft.setVisibility(GONE);
        m_tvGoogleIdLeft.setVisibility(GONE);
        m_btnFb.setVisibility(GONE);
        m_btnGoogle.setVisibility(GONE);

//        if ( null != strFb && 0 < strFb.length()){
        if(true == CIApplication.getLoginInfo().GetFbCombineStatus() ) {
            m_fbType = FacebookConnectStatus.CONNECTED;

            m_tvFbIdRight.setText(strFb);
            m_tvFbIdRight.setTextColor(ContextCompat.getColor(m_Context, R.color.black_one));
        } else {
            m_fbType = FacebookConnectStatus.DISCONNECT;

            m_tvFbIdRight.setText(m_Context.getString(R.string.not_connect));
            m_tvFbIdRight.setTextColor(ContextCompat.getColor(m_Context, R.color.grey_four));
        }

//        if ( null != strGoogle && 0 < strGoogle.length()){
        if( true == CIApplication.getLoginInfo().GetGoogleCombineStatus() ) {
            m_googleType = GoogleConnectStatus.CONNECTED;

            m_tvGoogleIdRight.setText(strGoogle);
            m_tvGoogleIdRight.setTextColor(ContextCompat.getColor(m_Context, R.color.black_one));
        } else {
            m_googleType = GoogleConnectStatus.DISCONNECT;

            m_tvGoogleIdRight.setText(m_Context.getString(R.string.not_connect));
            m_tvGoogleIdRight.setTextColor(ContextCompat.getColor(m_Context, R.color.grey_four));
        }

    }

    //編輯模式-社群連結詳細頁面用
    public void setEdit(String strFb, String strGoogle){

        m_ibtnEdit.setVisibility(GONE);
        m_tvFbIdRight.setVisibility(GONE);
        m_tvGoogleIdRight.setVisibility(GONE);
        m_btnFb.setVisibility(VISIBLE);
        m_btnGoogle.setVisibility(VISIBLE);

//        if ( null != strFb && 0 < strFb.length()){
        if( true == CIApplication.getLoginInfo().GetFbCombineStatus() ) {
            m_fbType = FacebookConnectStatus.CONNECTED;

            m_tvFb.setVisibility(GONE);
            m_tvFbIdLeft.setVisibility(VISIBLE);
            m_tvFbIdLeft.setText(strFb);

            m_btnFb.setText(m_Context.getString(R.string.disconnect));
            m_btnFb.setTextColor(ContextCompat.getColor(m_Context, R.color.french_blue));
            m_btnFb.setBackgroundResource(R.drawable.bg_btn_check_in_view);
        }else {
            m_fbType = FacebookConnectStatus.DISCONNECT;

            m_tvFb.setVisibility(VISIBLE);
            m_tvFb.setText(m_Context.getString(R.string.facebook));
            m_tvFbIdLeft.setVisibility(GONE);

            m_btnFb.setText(m_Context.getString(R.string.connected));
            m_btnFb.setTextColor(ContextCompat.getColor(m_Context, R.color.white_four));
            m_btnFb.setBackgroundResource(R.drawable.bg_btn_pinkish_red_selector);
        }

//        if ( null != strGoogle && 0 < strGoogle.length()){
        if( true == CIApplication.getLoginInfo().GetGoogleCombineStatus() ) {
            m_googleType = GoogleConnectStatus.CONNECTED;

            m_tvGoogle.setVisibility(GONE);
            m_tvGoogleIdLeft.setVisibility(VISIBLE);
            m_tvGoogleIdLeft.setText(strGoogle);

            m_btnGoogle.setText(m_Context.getString(R.string.disconnect));
            m_btnGoogle.setTextColor(ContextCompat.getColor(m_Context, R.color.french_blue));
            m_btnGoogle.setBackgroundResource(R.drawable.bg_btn_check_in_view);
        }else {
            m_googleType = GoogleConnectStatus.DISCONNECT;

            m_tvGoogle.setText(m_Context.getString(R.string.google_plus));
            m_tvGoogle.setVisibility(VISIBLE);
            m_tvGoogleIdLeft.setVisibility(GONE);

            m_btnGoogle.setText(m_Context.getString(R.string.connected));
            m_btnGoogle.setTextColor(ContextCompat.getColor(m_Context, R.color.white_four));
            m_btnGoogle.setBackgroundResource(R.drawable.bg_btn_pinkish_red_selector);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ll_edit: //R.id.ibtn_edit:
                if ( null != m_Listener)
                    m_Listener.OnEditClick();
                break;
            case R.id.btn_fb:
                if ( null != m_Listener){
                    if (m_fbType == FacebookConnectStatus.CONNECTED){
                        m_Listener.OnFacebookDisconnectClick();
                    }else {
                        m_Listener.OnFacebookConnectClick();
                    }
                }
                break;
            case R.id.btn_google:
                if ( null != m_Listener){
                    if (m_googleType == GoogleConnectStatus.CONNECTED){
                        m_Listener.OnGoogleDisconnectClick();
                    }else {
                        m_Listener.OnGoogleConnectClick();
                    }
                }
                break;
        }
    }

}
