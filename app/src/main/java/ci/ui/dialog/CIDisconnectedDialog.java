package ci.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.ui.define.ViewScaleDef;

/**
 * Created by jlchen on 2016/4/20.
 */
public class CIDisconnectedDialog extends Dialog{

    public interface OnDisconnectedDialogListener
    {
        void onDisconnectedDialog_Dismiss();
    }

    private OnDisconnectedDialogListener m_onListener = null;

    private static final double Weight_leftPadding	        = 16;
    private static final double Weight_RightPadding	        = 16;

    private static final double Weight_Width_Dialog			= 310;
    private static final double Weight_Height_Title			= 57;
    private static final double Weight_Height_Content_Gap	= 20;
    private static final double Weight_Height_Button		= 46;

    private ViewScaleDef m_ViewScaledef = null;
    private Context m_Context = null;

    private View.OnClickListener m_onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int viewID = v.getId();
            //設定
            if(viewID == R.id.button_Yes)
            {
                // 打開wifi設定頁面
                Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                m_Context.startActivity(intent);
            }
            //略過
            else if(viewID == R.id.button_No)
            {
                if (m_onListener != null)
                {
                    m_onListener.onDisconnectedDialog_Dismiss();
                }
                dismiss();
            }
        }
    };

    public CIDisconnectedDialog(Context context) {
        super(context, R.style.alertdialog);
        setCancelable(false);	//back 不可關閉
        setCanceledOnTouchOutside(false);	//點擊外面dialog不會dismiss
//		requestWindowFeature(Window.FEATURE_NO_TITLE);	//不顯示沒有title

        m_Context = context;
        m_ViewScaledef = ViewScaleDef.getInstance((Activity)m_Context);
    }

    public CIDisconnectedDialog(Context context, OnDisconnectedDialogListener listener) {
        super(context, R.style.alertdialog);
        setCancelable(false);	//back 不可關閉
        setCanceledOnTouchOutside(false);	//點擊外面dialog不會dismiss
//		requestWindowFeature(Window.FEATURE_NO_TITLE);	//不顯示沒有title

        m_Context = context;
        m_ViewScaledef = ViewScaleDef.getInstance((Activity)m_Context);
        m_onListener = listener;
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

        tvTitle.setText(m_Context.getString(R.string.system_no_network_connection));
        tvTitle.setVisibility(ViewGroup.VISIBLE);

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

        tvContent.setText(m_Context.getString(R.string.system_no_network_connection_message));

        //按鈕區
        LinearLayout layout_button = (LinearLayout)findViewById(R.id.layout_button);
        layout_button.getLayoutParams().height = m_ViewScaledef.getLayoutHeight(Weight_Height_Button);

        View vVLine = (View)findViewById(R.id.vVDiv);

        Button button;
        button = (Button) findViewById(R.id.button_Yes);
        m_ViewScaledef.setTextSize( 16, button);
        button.setOnClickListener(m_onClickListener);
        button.setText(m_Context.getString(R.string.system_settings));

        button = (Button) findViewById(R.id.button_No);
        m_ViewScaledef.setTextSize( 16, button);
        button.setOnClickListener(m_onClickListener);
        button.setText(m_Context.getString(R.string.system_dismiss));
    }
}
