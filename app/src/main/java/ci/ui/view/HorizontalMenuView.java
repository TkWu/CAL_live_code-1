package ci.ui.view;

import android.content.Context;
import android.os.Build;
import android.text.AndroidCharacter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.ui.define.ViewScaleDef;

import java.util.ArrayList;


public class HorizontalMenuView extends HorizontalScrollView {

    public interface onHorizontalMenuViewParameter {
        /**取得Menu要顯示的文字*/
        public ArrayList<String> getMenuList();
    }

    public interface onHorizontalMenuViewListener {
        /**通知外面點擊位置*/
        public void onMenuClick(int iPos);
    }

    OnClickListener m_onMenuClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final int iIdx = (Integer) v.getTag();
            if (m_iClickIdx == iIdx) {
                return;
            }
            m_iClickIdx = iIdx;

            setMenuChangeAnim(HorizontalMenuView.this, v);

            //底線移動效果
            Animation animation = new TranslateAnimation(m_iLinePosition, v.getLeft(), 0.0f, 0.0f);
            animation.setDuration(200);
            animation.setFillAfter(true);
            m_llLine.startAnimation(animation);
            m_iLinePosition = v.getLeft();

            m_onListener.onMenuClick(iIdx);
        }
    };

    private static double 		            Weight_Menu_Height  = 175;

    private Context                         m_Context		 	= null;

    private int                             m_iClickIdx         = 0;
    private int                             m_iLinePosition     = 0;
    private ArrayList<String>               m_alMenuList        = null;

    private FrameLayout                     m_flLayout          = null;
    private LinearLayout                    m_llContent         = null;
    private LinearLayout                    m_llLine            = null;

    private onHorizontalMenuViewParameter   m_onParameter       = null;
    private onHorizontalMenuViewListener    m_onListener        = null;

    public HorizontalMenuView(Context context) {
        super(context);
        m_Context = context;
    }

    public HorizontalMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_Context = context;
    }

    public HorizontalMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        m_Context = context;
    }

    public void uiSetParameterListener(onHorizontalMenuViewParameter onParameter, onHorizontalMenuViewListener onListener){
        m_onParameter = onParameter;
        m_onListener  = onListener;

        setHorizontalMenuView();
    }

    protected int getLayoutResourceId() {
        return R.layout.layout_view_horizontal_menu;
    }

    public void setHorizontalMenuView(){
        initialLayoutComponent();

        if ( m_onParameter == null) {
            return;
        }

        m_alMenuList = m_onParameter.getMenuList();

        initialMenu();
    }

    private void initialLayoutComponent() {
        LayoutInflater.from(m_Context).inflate(getLayoutResourceId(), this, true);

        m_flLayout  = (FrameLayout) findViewById(R.id.fl_menu_layout);
        m_llContent = (LinearLayout) findViewById(R.id.ll_menu_content);

        //Menu高度
        ViewScaleDef textSizeDefine = ViewScaleDef.getInstance(m_Context);
		LayoutParams frParams = (LayoutParams)m_llContent.getLayoutParams();
		frParams.height	= textSizeDefine.getLayoutHeight(Weight_Menu_Height);

    }

    public void initialMenu(){
        m_llContent.removeAllViews();
        int iSize = m_alMenuList.size();

        //設定Menu按鈕寬度
        int iResWidth = 0;
        if ( 3 < iSize ){
            iResWidth = getResources().getInteger(R.integer.activity_weight_sum_horizontal) / 35 * 10;
        }else {
            iResWidth = getResources().getInteger(R.integer.activity_weight_sum_horizontal) / iSize;
            if( 0 >= iSize ){
                iResWidth = getResources().getInteger(R.integer.activity_weight_sum_horizontal);
            }
        }

        int iMenuWidth = ViewScaleDef.getInstance(m_Context).getLayoutWidth(iResWidth);
        //新增Menu按鈕
        for ( int iIdx = 0; iIdx < iSize; iIdx++ ){

            View vChildItem = LayoutInflater.from(m_Context).inflate(R.layout.layout_view_pushlist_menu, null);

            TextView tvItemName 	= (TextView) vChildItem.findViewById(R.id.tvName);

            tvItemName.setText(m_alMenuList.get(iIdx));
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                tvItemName.setTextColor(m_Context.getResources().getColor(R.color.white_four,null));
            }else{
                tvItemName.setTextColor(m_Context.getResources().getColor(R.color.white_four));
            }


            vChildItem.setOnClickListener(m_onMenuClick);

            vChildItem.setTag(iIdx);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iMenuWidth, LinearLayout.LayoutParams.MATCH_PARENT);
            m_llContent.addView(vChildItem, params);

            //設定字體大小
            ViewScaleDef mTextSizeDef = ViewScaleDef.getInstance(m_Context);
            mTextSizeDef.setTextSize( 16, tvItemName);
        }

        //底線
        if (null == m_llLine) {
            m_llLine = new LinearLayout(m_Context);
            LayoutParams layoutParams = new LayoutParams(iMenuWidth, ViewScaleDef.getInstance(m_Context).getLayoutHeight(2));
            layoutParams.gravity = Gravity.BOTTOM;
            m_llLine.setBackgroundColor(m_Context.getResources().getColor(R.color.white_four));
            m_flLayout.addView(m_llLine, layoutParams);
        }else{
            m_llLine.getLayoutParams().width = ViewScaleDef.getInstance(m_Context).getLayoutWidth(iResWidth);

            //將底線移至最左邊
            Animation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
            animation.setDuration(0);
            animation.setFillAfter(true);
            m_llLine.startAnimation(animation);
        }
    }

    /** 重新產生Menu */
    public void resetMenuList(ArrayList<String> alMenuList){
        m_alMenuList = alMenuList;
        m_iClickIdx  = 0;
        m_iLinePosition = 0;
        initialMenu();
    }

    /** Menu切換動畫 */
    private void setMenuChangeAnim(final HorizontalScrollView hsv, View v){
        //Menu移動效果
        DisplayMetrics dm = ViewScaleDef.getInstance(m_Context).getDisplayMetrics();
        int iPosSpace = v.getLeft() + ((v.getRight() - v.getLeft()) / 2);
        final int iPosition = iPosSpace - (dm.widthPixels / 2);
        hsv.post(new Runnable() {
            @Override
            public void run() {
                hsv.smoothScrollTo(iPosition, 0);
            }
        });
    }

    public void setLineX(float x){
        m_llLine.setTranslationX(x);
    }
}
