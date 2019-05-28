package ci.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseView;
import ci.ui.define.ViewScaleDef;

/**
 * Created by Ryan on 16/3/21.
 */
public class TwoItemSelectBar extends BaseView {

    private ImageButton m_ivLeftRadio   = null;
    private TextView    m_tvLeftText    = null;
    private ImageButton m_ivRightRadio  = null;
    private TextView    m_tvRightText   = null;

    private ESelectSMode m_eSelectMode  = ESelectSMode.LEFT;

    public enum ESelectSMode {
        LEFT,RIGHT
    }

    public TwoItemSelectBar(Context context) {
        super(context);
        initial();
    }

    public TwoItemSelectBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_twoitem_select_bar;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater) {

        m_ivLeftRadio   = (ImageButton)findViewById(R.id.iv_leftbtn);
        m_ivLeftRadio.setOnClickListener(m_onClick);
        m_tvLeftText    = (TextView)findViewById(R.id.tv_leftText);

        m_ivRightRadio  = (ImageButton)findViewById(R.id.iv_rightbtn);
        m_ivRightRadio.setOnClickListener(m_onClick);
        m_tvRightText   = (TextView)findViewById(R.id.tv_rightText);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        //vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        //vScaleDef.selfAdjustSameScaleView(m_ivLeftRadio, 24, 24);
        //vScaleDef.selfAdjustSameScaleView(m_ivRightRadio, 24, 24);


        setSelectMode(m_eSelectMode);
    }

    public void setSelectMode( ESelectSMode eSelect ){

        if ( eSelect == ESelectSMode.LEFT ){
            m_ivLeftRadio.setSelected(true);
            m_ivRightRadio.setSelected(false);
        } else {
            m_ivLeftRadio.setSelected(false);
            m_ivRightRadio.setSelected(true);
        }
    }

    public ESelectSMode getSelectModeParam() {
        return m_eSelectMode;
    }

    public void setText( String strLeft, String strRight ){
        m_tvLeftText.setText(strLeft);
        m_tvRightText.setText(strRight);
    }

    public String getSelectMode(){

        if ( m_eSelectMode == ESelectSMode.LEFT ){
            return m_tvLeftText.getText().toString();
        } else {
            return m_tvRightText.getText().toString();
        }
    }

    View.OnClickListener m_onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if ( v.getId() == m_ivLeftRadio.getId() ){
                m_eSelectMode = ESelectSMode.LEFT;
            } else if ( v.getId() == m_ivRightRadio.getId() ) {
                m_eSelectMode = ESelectSMode.RIGHT;
            }
            setSelectMode(m_eSelectMode);
        }
    };

}
