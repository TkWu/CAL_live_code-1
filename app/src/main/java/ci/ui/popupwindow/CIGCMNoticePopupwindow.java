package ci.ui.popupwindow;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.CINotiflyItem;
import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.Main.CIMainActivity;
import ci.function.MyTrips.Detail.CIQuestionnaireActivity;
import ci.ui.WebView.CIWebViewActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIPullQuestionnaireReq;
import ci.ws.cores.object.GsonTool;

/**
 * 收到內部推播通知
 * Created by Ryan
 */
public class CIGCMNoticePopupwindow extends PopupWindow {

    private Context         m_Context   = null;
    private ViewScaleDef    m_vScaleDef = null;
    private View            m_vContentView;

    private Button          m_btnGo     = null;
    private TextView        m_tv        = null;

    private int             m_iX        = 0;
    private int             m_iY        = 0;
    private int             m_iGravity  = 0;

    private CINotiflyItem   m_Notify = null;

    public CIGCMNoticePopupwindow( Context context, CINotiflyItem notify ) {
        m_Context           = context;
        m_vScaleDef         = ViewScaleDef.getInstance(m_Context);
        m_vContentView      = LayoutInflater.from(m_Context).inflate(R.layout.layout_view_gcm_notify_toast, null);

        m_Notify = notify;

        setContentView(m_vContentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(m_vScaleDef.getLayoutHeight(80));

        //設進出場動畫
        setAnimationStyle(R.style.PopupAnimation);
        setFocusable(false);
        setOutsideTouchable(false);
        //m_vContentView.setFocusableInTouchMode(true);

        //按鈕
        m_btnGo = (Button) m_vContentView.findViewById(R.id.btn_go);
        m_btnGo.getLayoutParams().height = m_vScaleDef.getLayoutHeight(32);
        m_btnGo.getLayoutParams().width  = m_vScaleDef.getLayoutHeight(72);
        ((RelativeLayout.LayoutParams)m_btnGo.getLayoutParams()).rightMargin = m_vScaleDef.getLayoutWidth(20);
        m_vScaleDef.setTextSize(13, m_btnGo);
        m_btnGo.setOnClickListener(m_onclick);

        //文字
        m_tv = (TextView) m_vContentView.findViewById(R.id.tv_msg);
        m_tv.getLayoutParams().height = m_vScaleDef.getLayoutHeight(60);
        m_tv.getLayoutParams().width  = m_vScaleDef.getLayoutHeight(228);
        ((RelativeLayout.LayoutParams)m_tv.getLayoutParams()).leftMargin = m_vScaleDef.getLayoutWidth(20);
        m_vScaleDef.setTextSize(14, m_tv);
        m_tv.setText(m_Notify.msg);
    }

    public void UpdateNotifyData( CINotiflyItem notify ){

        if ( null != notify ){
            m_Notify = notify;
            if ( null != m_tv ){
                m_tv.setText(m_Notify.msg);
            }
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        m_iGravity = gravity;
        m_iX = x;
        m_iY = y;
        ObjectAnimator oa = ObjectAnimator.ofFloat(
                m_vContentView,
                "translationY",
                -m_vScaleDef.getLayoutHeight(30),
                0);
        oa.setDuration(500);
        oa.start();
    }

    public void dismissNotice(){
        ObjectAnimator oa = ObjectAnimator.ofFloat(
                m_vContentView,
                "translationY",
                0,
                -m_vScaleDef.getLayoutHeight(30));
        oa.setDuration(500);
        oa.start();
    }

    View.OnClickListener m_onclick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            Intent intent = null;

            if ( TextUtils.equals(CINotiflyItem.TYPE_EWALLET, m_Notify.type) ){

                /**Promotion - 確認推播*/
                intent =  new Intent(m_Context, CIMainActivity.class);
                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra( CINotiflyItem.NOTIFY_INFO, m_Notify);

            } else if ( TextUtils.equals(CINotiflyItem.TYPE_EMERGENCY_MSG, m_Notify.type) ){

                /**重大事件推播*/
                intent =  new Intent(Intent.ACTION_VIEW);
                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(m_Notify.url));

            } else if ( TextUtils.equals(CINotiflyItem.TYPE_PROMOTION_SELL, m_Notify.type) ){

                /**Promotion - 促銷*/
                intent =  new Intent(m_Context, CIWebViewActivity.class);
                intent.putExtra( CINotiflyItem.NOTIFY_INFO, m_Notify);
                intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, m_Context.getString(R.string.menu_title_promotions));
                intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, m_Notify.url);

            } else if ( TextUtils.equals(CINotiflyItem.TYPE_PROMOTION_VIP, m_Notify.type)   ||
                        TextUtils.equals(CINotiflyItem.TYPE_PROMOTION_WIFI, m_Notify.type)  ){

                /**Promotion - 兌換里程*/
                /**Promotion - 兌換里程*/
                //url 須額外帶參數
                String strURL = "";
                if ( CIApplication.getLoginInfo().GetLoginStatus() ){
                    strURL = m_Notify.url + "&t=" + CIApplication.getLoginInfo().GetMemberToken();
                } else {
                    strURL = m_Notify.url;
                }
                //再加上變數pusht=GCM token MRQ
                //strURL += String.format( "&pusht=" + CIApplication.getMrqManager().getGcmRegistrationId() );
                intent =  new Intent(m_Context, CIWebViewActivity.class);
                intent.putExtra( CINotiflyItem.NOTIFY_INFO, m_Notify);
                intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, m_Context.getString(R.string.menu_title_promotions));
                intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, strURL);

            } else if ( TextUtils.equals(CINotiflyItem.TYPE_FLIGHT_CHANGE, m_Notify.type)    ||
                    TextUtils.equals(CINotiflyItem.TYPE_FLIGHT_GATE_CHANGE, m_Notify.type)   ||
                    TextUtils.equals(CINotiflyItem.TYPE_FLIGHT_CANCEL, m_Notify.type)        ){

                /**航班異動 - 改班/改時*/
                /**航班異動 - 改登機門*/
                /**航班異動 - 取消*/
                intent =  new Intent(m_Context, CIMainActivity.class);
                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra( CINotiflyItem.NOTIFY_INFO, m_Notify);

            } else if ( TextUtils.equals(CINotiflyItem.TYPE_QUESTIONNAIRE, m_Notify.type)){
                /**問卷調查*/
                intent =  new Intent(m_Context, CIQuestionnaireActivity.class);
                CIPullQuestionnaireReq req = new CIPullQuestionnaireReq();
                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                req.token = m_Notify.data.token;
                req.cardid = CIApplication.getLoginInfo().GetUserMemberCardNo();
                String strJson = GsonTool.toJson(req);
                Bitmap bitmap = ImageHandle.getScreenShot((Activity)m_Context);
                Bitmap blur = ImageHandle.BlurBuilder(m_Context, bitmap, 13.5f, 0.15f);
                Bundle bundle       = new Bundle();
                bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
                bundle.putString(UiMessageDef.BUNDLE_PULL_QUES_REQ_DATA, strJson);
                intent.putExtras(bundle);
            } else {
                intent = new Intent();
            }

            //更新推播訊息讀取狀態
            CIApplication.getFCMManager().UpdateMsgIdToCIServer(m_Notify.msg_id);

            m_Context.startActivity(intent);

            dismiss();
        }
    };
}
