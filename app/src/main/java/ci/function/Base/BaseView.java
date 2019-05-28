package ci.function.Base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chinaairlines.mobile30.R;

import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CIProgressDialog;

/**
 * Created by Ryan on 16/2/26.
 */
public abstract class BaseView extends LinearLayout {

    protected   Context         m_Context;
    protected   LayoutInflater  m_layoutInflater;
    private 	CIProgressDialog m_proDlg = null;
    private CIAlertDialog        m_dialog = null;

    public BaseView(Context context) {
        super(context);
        m_Context = context;
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_Context = context;
    }

    protected final void initial() {

        m_layoutInflater = LayoutInflater.from(getContext());
        m_layoutInflater.inflate( getLayoutResourceId(), this );

        initialLayoutComponent(m_layoutInflater);

        if (isInEditMode()) {//為了讓Xml能正常檢視，所以只讓Eclipse動態讀取到此為止
            return;
        }

        ViewScaleDef vScaleDef = ViewScaleDef.getInstance(m_Context);

        setTextSizeAndLayoutParams( vScaleDef );
    }

    /**
     * BaseFragment在
     * {@link BaseFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
     * onCreateView()}時 設定{@link LayoutInflater#inflate(int, ViewGroup, boolean)
     * inflate()}用
     *
     * @return 此畫面的 Layout Resource Id
     */
    protected abstract int getLayoutResourceId();

    /**
     * 元件初始化，靜態取得元件實體、動態建製元件實體…等
     *
     * @param inflater
     */
    protected abstract void initialLayoutComponent(LayoutInflater inflater);

    /**
     * 設定字型大小及版面大小
     * @param vScaleDef 請參閱{@link ViewScaleDef}
     */
    protected abstract void setTextSizeAndLayoutParams( ViewScaleDef vScaleDef);

    public void showProgressDialog(){
        showProgressDialog(null);
    }

    public void showProgressDialog(CIProgressDialog.CIProgressDlgListener listener){
        if(true == ((Activity)m_Context).isDestroyed()){
            return;
        }
        hideProgressDialog();
        if(null == m_proDlg){
            //m_proDlg = CIProgressDialog.createDialog(m_Context, listener);
            m_proDlg = new CIProgressDialog(m_Context, listener);
        }
        m_proDlg.show();
    }

    public void hideProgressDialog(){
        if(null != m_proDlg && true == m_proDlg.isShowing()){
            m_proDlg.dismiss();
        }
        m_proDlg = null;
    }

    protected void showDialog(String strTitle, String strMsg) {
        showDialog(strTitle, strMsg, m_Context.getString(R.string.confirm), null, null);
    }

    protected void showDialog(String strTitle, String strMsg, String strConfirm) {
        showDialog(strTitle, strMsg, strConfirm, null, null);
    }

    protected void showDialog(String strTitle, String strMsg, String strConfirm,String strCancel, CIAlertDialog.OnAlertMsgDialogListener listener) {
        if(true == ((Activity)m_Context).isDestroyed()){
            return;
        }
        if (null != m_dialog && true == m_dialog.isShowing()){
            m_dialog.dismiss();
        }

        if(null == listener){
            m_dialog = new CIAlertDialog(m_Context, new CIAlertDialog.OnAlertMsgDialogListener() {

                @Override
                public void onAlertMsgDialog_Confirm() {}

                @Override
                public void onAlertMsgDialogg_Cancel() {}
            });
        } else {
            m_dialog  = new CIAlertDialog(m_Context, listener);
        }

        if(false == TextUtils.isEmpty(strTitle)) {
            m_dialog.uiSetTitleText(strTitle);
        }
        if(false == TextUtils.isEmpty(strMsg)) {
            m_dialog.uiSetContentText(strMsg);
        }
        if(false == TextUtils.isEmpty(strConfirm)) {
            m_dialog.uiSetConfirmText(strConfirm);
        }
        if(false == TextUtils.isEmpty(strCancel)){
            m_dialog.uiSetCancelText(strCancel);
        }
        m_dialog.show();
    }
}
