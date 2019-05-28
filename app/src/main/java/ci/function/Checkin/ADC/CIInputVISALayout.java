package ci.function.Checkin.ADC;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseView;
import ci.ui.define.ViewScaleDef;

public class CIInputVISALayout extends BaseView {


    private LinearLayout    m_root =null;
    private TextView        m_tvName = null;
    private LinearLayout    m_llayout_Content   = null;

    public CIInputVISALayout(Context context) {
        super(context);
        initial();
    }

    public CIInputVISALayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_check_in_input_visa;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater) {

        m_root = (LinearLayout)findViewById(R.id.llayout_root);
        m_tvName = (TextView)findViewById(R.id.tv_name);
        m_llayout_Content = findViewById(R.id.llayout_content);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_root);
    }

    public LinearLayout getContentView(){
        return m_llayout_Content;
    }

    public TextView getTextView(){ return m_tvName;}
}
