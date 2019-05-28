package ci.ui.view.ShadowBar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseView;
import ci.ui.define.ViewScaleDef;

/**
 * Created by ryan on 16/3/22.
 */
public class ShadowBarScrollview extends BaseView {

    private ScrollView      m_ScrollView        = null;
    private LinearLayout    m_llayout_Content   = null;
    private View            m_vGradient         = null;

    //Y軸可滾動的範圍大小,
    private int m_iScrollHeight = 0;

    public ShadowBarScrollview(Context context) {
        super(context);
        initial();
    }

    public ShadowBarScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_shadowbar_scrollview;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater) {

        m_ScrollView        = (ScrollView)findViewById(R.id.scrollView);
        m_llayout_Content   = (LinearLayout)findViewById(R.id.llayout_content);
        m_vGradient         = findViewById(R.id.vGradient);

        m_ScrollView.getViewTreeObserver().addOnScrollChangedListener(m_onScroll);

        ViewTreeObserver vto = m_ScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                m_ScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                //Log.e("sv.getHeight()",""+m_ScrollView.getHeight());

                int iCnt = m_ScrollView.getChildCount();
                if ( iCnt <= 0 ){

                } else {
                    //內容高度 <= ScrollView高度, 沒有可滾動區域, 無法監聽onScrollChanged(), 需隱藏陰影
                    if ( m_ScrollView.getChildAt(0).getHeight() <= m_ScrollView.getHeight() ){
                        m_vGradient.setAlpha(0);
                    }
                    //Log.e("sv.Child(0).getHeight()",""+m_ScrollView.getChildAt(0).getHeight());
                }
            }
        });

    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        m_vGradient.getLayoutParams().height = vScaleDef.getLayoutHeight(16);
    }

    public ScrollView getScrollView(){
        return m_ScrollView;
    }

    public LinearLayout getContentView(){
        return m_llayout_Content;
    }

    public void setShadowBarHeight( int iPx ){
        m_vGradient.getLayoutParams().height = iPx;
    }

    ViewTreeObserver.OnScrollChangedListener m_onScroll = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {

            int iCnt = m_ScrollView.getChildCount();
            if ( iCnt <= 0 ){

            } else {
                m_iScrollHeight = m_ScrollView.getChildAt(0).getHeight() - m_ScrollView.getHeight();
            }

            if(m_iScrollHeight <= 0) {
                m_vGradient.setAlpha(0);
                return;
            }

            //利用百分比決定 blurr 效果
            //減少分母, 提高Blur的效果
            float fAlpha = (m_ScrollView.getScrollY() / (float)m_iScrollHeight);

            // Apply a ceil
            if (fAlpha > 1) {
                fAlpha = 1;
            } else if ( fAlpha < 0 ){
                fAlpha = 0;
            }

            fAlpha = 1 - fAlpha;

            m_vGradient.setAlpha(fAlpha);
        }
    };
}
