package ci.ui.popupwindow;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.ui.define.ViewScaleDef;

/**
 * 顯示沒有網路連線的通知
 * Created by JL Chen on 2016/4/20.
 */
public class CINoInternetNoticePopupwindow extends PopupWindow {

    public interface NoInternetNoticePopupwindowListener{
        void OnClick();
    }

    private NoInternetNoticePopupwindowListener m_Listener;

    private Context         m_Context   = null;
    private ViewScaleDef    m_vScaleDef = null;
    private LayoutInflater  m_layoutInflater;
    private View            m_vContentView;

    private LinearLayout    m_ll        = null;
    private ImageView       m_iv        = null;
    private TextView        m_tv        = null;

    private int             m_iX        = 0;
    private int             m_iY        = 0;
    private int             m_iGravity  = 0;

    public CINoInternetNoticePopupwindow(Context context, NoInternetNoticePopupwindowListener lisener) {
        m_Context           = context;
        m_layoutInflater    = (LayoutInflater) m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_vScaleDef         = ViewScaleDef.getInstance(m_Context);
        m_Listener          = lisener;

        m_vContentView      = m_layoutInflater.inflate(
                R.layout.popupwindow_no_internet_notice,
                null);

        setContentView(m_vContentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(m_vScaleDef.getLayoutHeight(32));

        //設進出場動畫
        setAnimationStyle(R.style.PopupAnimation);
        setFocusable(false);
        setOutsideTouchable(false);
        m_vContentView.setFocusableInTouchMode(true);

        //背景
        m_ll = (LinearLayout) m_vContentView.findViewById(R.id.ll);
        m_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( null != m_Listener ){
                    m_Listener.OnClick();
                }
            }
        });
//        m_vScaleDef.setViewSize(m_ll, MATCH_PARENT, 30);

        //圖片
        m_iv = (ImageView) m_vContentView.findViewById(R.id.iv);
        m_vScaleDef.selfAdjustSameScaleView(m_iv, 24, 24);

        //文字
        m_tv = (TextView) m_vContentView.findViewById(R.id.tv);
        m_vScaleDef.setTextSize(16, m_tv);
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

    public void moveNotice(int iEndX){
//        showAsDropDown(m_vContentView, iEndX, m_iY, m_iGravity);

//        update(iEndX, m_iY, -1, -1, true);

        ObjectAnimator oa = ObjectAnimator.ofFloat(
                m_vContentView,
                "translationX",
                m_iX,
                iEndX);
        oa.setDuration(0);
        oa.start();
        m_iX = iEndX;
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override
    public void update(int xoff, int yoff, int width, int height, boolean force) {
        super.update(xoff, yoff, width, height, force);
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
}
