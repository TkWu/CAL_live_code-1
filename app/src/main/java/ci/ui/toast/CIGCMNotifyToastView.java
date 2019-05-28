package ci.ui.toast;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.CINotiflyItem;
import com.chinaairlines.mobile30.R;

import ci.ui.define.ViewScaleDef;

/**
 * 自定義快顯訊息
 * Created by Ryan 參考CIToastView.
 */
public class CIGCMNotifyToastView {

    private static final int TOAST_DURATION = 3000;

    //傳入text為要顯示的文字, 目前CIToastView固定顯示時間為3秒
    //用的時候記得要叫.show() XD
    public static CIGCMNotifyToastView makeText(final Context context, final CINotiflyItem notify ) {

        CIGCMNotifyToastView result = new CIGCMNotifyToastView(context);

        ViewScaleDef viewScaleDef = ViewScaleDef.getInstance(context);

        LinearLayout mLayout = new LinearLayout(context);
        //mLayout.setBackgroundResource(R.drawable.bg_gcm_notify_toast_view);
        mLayout.setMinimumWidth(viewScaleDef.getDisplayMetrics().widthPixels);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_view_gcm_notify_toast,null);
        TextView tv = (TextView)view.findViewById(R.id.tv_msg);
        tv.getLayoutParams().height = viewScaleDef.getLayoutHeight(60);
        tv.getLayoutParams().width  = viewScaleDef.getLayoutHeight(228);
        ((RelativeLayout.LayoutParams)tv.getLayoutParams()).leftMargin = viewScaleDef.getLayoutWidth(20);
        viewScaleDef.setTextSize(14, tv);
        tv.setText(notify.message);

        Button btn = (Button)view.findViewById(R.id.btn_go);
        btn.getLayoutParams().height = viewScaleDef.getLayoutHeight(32);
        btn.getLayoutParams().width  = viewScaleDef.getLayoutHeight(72);
        ((RelativeLayout.LayoutParams)btn.getLayoutParams()).rightMargin = viewScaleDef.getLayoutWidth(20);
        viewScaleDef.setTextSize(13, btn);

        mLayout.addView(view, viewScaleDef.getDisplayMetrics().widthPixels, viewScaleDef.getLayoutHeight(80));
        result.m_NextView = mLayout;
        //result.m_NextView = view;
        //顯示時間 固定3秒
        result.m_iDuration = TOAST_DURATION;
        //顯示位置 置中; 從狀態列開始
        result.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, viewScaleDef.getLayoutHeight(56));

        return result;
    }

    private final Handler m_Handler = new Handler();
    private int m_iDuration = TOAST_DURATION;
    private int m_iGravity = Gravity.CENTER;
    private int m_iX, m_iY;
    private View m_View;
    private View m_NextView;

    private WindowManager m_WM;
    private final WindowManager.LayoutParams m_Params = new WindowManager.LayoutParams();

    public CIGCMNotifyToastView(Context context){

        init(context);
    }

    private void init(Context context)
    {
        final WindowManager.LayoutParams params = m_Params;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = android.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        //params.setTitle("Toast");

        m_WM = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
    }

    private final Runnable mShow = new Runnable() {
        public void run() {
            handleShow();
        }
    };

    private final Runnable mHide = new Runnable() {
        public void run() {
            handleHide();
        }
    };

    private void handleShow() {

        if (m_View != m_NextView) {
// remove the old view if necessary
            handleHide();
            m_View = m_NextView;
//            m_WM = WindowManagerImpl.getDefault();
            final int gravity = m_iGravity;
            m_Params.gravity = gravity;
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL)
            {
                m_Params.horizontalWeight = 1.0f;
            }
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL)
            {
                m_Params.verticalWeight = 1.0f;
            }
            m_Params.x = m_iX;
            m_Params.y = m_iY;
            if (m_View.getParent() != null)
            {
                m_WM.removeView(m_View);
            }
            m_WM.addView(m_View, m_Params);
        }
    }

    private void handleHide()
    {
        if (m_View != null)
        {
            if (m_View.getParent() != null)
            {
                m_WM.removeView(m_View);
            }
            m_View = null;
        }
    }

    /**
     * Set the view to show.
     * @see #getView
     */
    public void setView(View view) {
        m_NextView = view;
    }

    /**
     * Return the view.
     * @see #setView
     */
    public View getView() {
        return m_NextView;
    }

    /**
     * Set how long to show the view for.
     */
    public void setDuration(int duration) {
        m_iDuration = duration;
    }

    /**
     * Return the duration.
     * @see #setDuration
     */
    public int getDuration() {
        return m_iDuration;
    }

    /**
     * Set the location at which the notification should appear on the screen.
     * @see Gravity
     * @see #getGravity
     */
    public void setGravity(int gravity, int xOffset, int yOffset) {
        m_iGravity = gravity;
        m_iX = xOffset;
        m_iY = yOffset;
    }

    /**
     * Get the location at which the notification should appear on the screen.
     * @see Gravity
     * @see #getGravity
     */
    public int getGravity() {
        return m_iGravity;
    }

    /**
     * schedule handleShow into the right thread
     */
    public void show() {
        m_Handler.post(mShow);

        if(m_iDuration >0)
        {
            m_Handler.postDelayed(mHide, m_iDuration);
        }
    }
}
