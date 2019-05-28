package ci.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.ui.define.ViewScaleDef;

/**警告訊息視窗
 *<br>Title 為必要參數
 *<br>Content 為非必要參數, 放置要顯示的訊息,離開App時適用*/
public class CIAlertDialog extends Dialog
{
	public interface OnAlertMsgDialogListener
	{
		void onAlertMsgDialog_Confirm();
		void onAlertMsgDialogg_Cancel();
	}

	private static final double Weight_leftPadding	= 16;
	private static final double Weight_RightPadding	= 16;

	private static final double Weight_Width_Dialog			= 310;
	private static final double Weight_Height_Title			= 57;
	private static final double Weight_Height_Content_Gap	= 20;
	private static final double Weight_Height_Button		= 46;


	private String m_strTitle = null;
	private String m_strContent = null;
	private String m_strConfirm = null;
	private String m_strCancel = null;
	private ViewScaleDef m_ViewScaledef = null;
	private boolean m_bIsDismissByConfirm = true;
	private boolean m_bIsDismissByCancel = true;

	private OnAlertMsgDialogListener m_onAlertMsgDialogListener = null;

	private View.OnClickListener m_onClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			int viewID = v.getId();
			if(viewID == R.id.button_Yes)
			{
				if (m_onAlertMsgDialogListener != null)
				{
					m_onAlertMsgDialogListener.onAlertMsgDialog_Confirm();
				}
				if(m_bIsDismissByConfirm){
					dismiss();
				}

			}
			else if(viewID == R.id.button_No)
			{
				if (m_onAlertMsgDialogListener != null)
				{
					m_onAlertMsgDialogListener.onAlertMsgDialogg_Cancel();
				}
				if(m_bIsDismissByCancel){
					dismiss();
				}
			}
		}
	};

	public CIAlertDialog(Context context, OnAlertMsgDialogListener onAlertDialogListener)
	{
		super(context, R.style.alertdialog);
		setCancelable(false);	//back 不可關閉
		setCanceledOnTouchOutside(false);	//點擊外面dialog不會dismiss
//		requestWindowFeature(Window.FEATURE_NO_TITLE);	//不顯示沒有title

		m_ViewScaledef = ViewScaleDef.getInstance((Activity)context);
		m_onAlertMsgDialogListener = onAlertDialogListener;
	}

	public void uiSetTitleText(String strTitle) {
		m_strTitle = strTitle;
	}

	public void uiSetContentText(String strContent) {
		m_strContent = strContent;
	}

	public void uiSetConfirmText(String strConfirm) {
		m_strConfirm = strConfirm;
	}

	public void uiSetCancelText(String strCancel) {
		m_strCancel = strCancel;
	}

	public void uiSetIsDismissByConfirm(boolean bIsDismissByConfirm) {
		m_bIsDismissByConfirm = bIsDismissByConfirm;
	}

	public void uiSetIsDismissByCancel(boolean bIsDismissByCancel) {
		m_bIsDismissByCancel = bIsDismissByCancel;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_dialog_alert_msg);

		LinearLayout layout_bg = (LinearLayout)findViewById(R.id.layout_bg);
		layout_bg.getLayoutParams().width = m_ViewScaledef.getLayoutHeight(Weight_Width_Dialog);

		//調整變數命名 by ryan
		TextView tvTitle	= (TextView)findViewById(R.id.tv_Title);
		TextView tvContent	= (TextView)findViewById(R.id.textView_Content);
		View vHeadline = (View)findViewById(R.id.vHeadlineDiv);
		vHeadline.setVisibility(View.INVISIBLE);

		//如果沒有設定Title文字就Gone掉 by kevin
		if(null != m_strTitle && 0 < m_strTitle.length()){
			tvTitle.setText(m_strTitle);
			tvTitle.setVisibility(ViewGroup.VISIBLE);
		} else {
			tvTitle.setVisibility(ViewGroup.GONE);
		}

		m_ViewScaledef.setTextSize(20, tvTitle);
		tvTitle.getLayoutParams().height = m_ViewScaledef.getLayoutHeight(Weight_Height_Title);
		int nLeft = m_ViewScaledef.getLayoutWidth(Weight_leftPadding);
		int nRight= m_ViewScaledef.getLayoutWidth(Weight_RightPadding);
		tvTitle.setPadding(nLeft, 0, nRight, 0);

		m_ViewScaledef.setTextSize( 16, tvContent);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)tvContent.getLayoutParams();
		//當沒有輸入title時, 將上方間距加大, by ryan
		if ( View.VISIBLE == tvTitle.getVisibility() ){
			params.topMargin = m_ViewScaledef.getLayoutHeight(6);
		} else {
			params.topMargin = m_ViewScaledef.getLayoutHeight(Weight_Height_Content_Gap);
		}
		params.bottomMargin = m_ViewScaledef.getLayoutHeight(Weight_Height_Content_Gap);
		tvContent.setLayoutParams(params);
		tvContent.setPadding(nLeft, 0, nRight, 0);
		tvContent.setMaxHeight(m_ViewScaledef.getLayoutHeight(450));

		if ( m_strContent != null ){
			tvContent.setText(m_strContent);
		} else {
			tvContent.setVisibility(View.GONE);
			vHeadline.setVisibility(View.GONE);
		}	
		
		//按鈕區
		LinearLayout layout_button = (LinearLayout)findViewById(R.id.layout_button);
		layout_button.getLayoutParams().height = m_ViewScaledef.getLayoutHeight(Weight_Height_Button);
		
		View vVLine = (View)findViewById(R.id.vVDiv);

		Button button;
		button = (Button) findViewById(R.id.button_Yes);
		m_ViewScaledef.setTextSize( 16, button);
		if (m_strConfirm != null)
		{
			button.setOnClickListener(m_onClickListener);
			button.setText(m_strConfirm);
		}
		else
		{
			vVLine.setVisibility(View.GONE);
			button.setVisibility(View.GONE);
		}

		button = (Button) findViewById(R.id.button_No);
		m_ViewScaledef.setTextSize( 16, button);
		if (m_strCancel != null)
		{
			button.setOnClickListener(m_onClickListener);
			button.setText(m_strCancel);
		}
		else
		{
			vVLine.setVisibility(View.GONE);
			button.setVisibility(View.GONE);
		}
	}

}
