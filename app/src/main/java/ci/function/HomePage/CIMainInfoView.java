package ci.function.HomePage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseView;
import ci.ui.define.ViewScaleDef;

/**
 * Created by Ryan on 16/2/26.
 */
public class CIMainInfoView extends BaseView {

    private ImageView   m_imgIcon     = null;
    private TextView    m_tvTitle     = null;

    public CIMainInfoView(Context context) {
        super(context);
        initial();
    }

    public CIMainInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_main_infoview;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater) {

        m_imgIcon = (ImageView)findViewById(R.id.imgIcon);
        m_tvTitle = (TextView)findViewById(R.id.tvText);
    }

    @Override
    protected void setTextSizeAndLayoutParams( ViewScaleDef vScaleDef) {

        //
        vScaleDef.setTextSize( 20, m_tvTitle);
        ((LinearLayout.LayoutParams)m_imgIcon.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(24);
        ((LinearLayout.LayoutParams)m_tvTitle.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(10);

        m_imgIcon.getLayoutParams().height  = vScaleDef.getLayoutMinUnit(100);
        m_imgIcon.getLayoutParams().width   = vScaleDef.getLayoutMinUnit(100);
    }

    public void setTitleText( String strText ){
        if ( null != strText && strText.length() > 0 ){
            m_tvTitle.setText(strText);
        }
    }

    public void setIcon( int iResId ){
        if ( iResId > 0 ){
            m_imgIcon.setImageResource(iResId);
        }
    }
}
