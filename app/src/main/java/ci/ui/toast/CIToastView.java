package ci.ui.toast;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.ui.define.ViewScaleDef;

/**
 * Created by jlchen on 2016/2/25.
 *
 * 自定義快顯訊息
 *
 * 用法:
 * (1) 基本用法:
 *      CIToastView.makeText(context, string).show();
 * (2) 自定義顯示時間/位置:
 *      CIToastView toast = CIToastView.makeText(context, string);
 *      toast.setDuration(time);
 *      toast.setGravity(gravity, xOffset, yOffset);
 *      toast.show();
 *
 * (補充) 原生Toast: (部分紅米機只能使用原生Toast)
 *  @see #showNativeToast
 */
public class CIToastView {

    private final Runnable m_rShow = new Runnable() {
        public void run() {

            try{
                //sdk 23 以上版本改用原生Toast，避免閃退 by Kevin 2016/12
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    showNativeToast();
                } else {
                    handleShow();
                }
            }catch (Exception e){
                try {
                    showNativeToast();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    };

    private final Runnable m_rHide = new Runnable() {
        public void run() {

            try{
                handleHide();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private static final int    DEF_TOAST_SHOW_TIME = 3000;

    private Context             m_context           = null;
    private ViewScaleDef        m_vScaleDef         = null;
    private final Handler       m_handler           = new Handler();
    private int                 m_iDuration         = DEF_TOAST_SHOW_TIME;
    private int                 m_iGravity          = Gravity.CENTER;
    private int                 m_iX,   m_iY;
    private View                m_view, m_viewNext;
    private WindowManager       m_wm;
    private final WindowManager.LayoutParams m_params = new WindowManager.LayoutParams();

    public CIToastView (Context context){

        m_context = context;
        m_vScaleDef = ViewScaleDef.getInstance(context);
        init(context);
    }

    private void init(Context context) {

        final WindowManager.LayoutParams params = m_params;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = android.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        //params.setTitle("Toast");

        m_wm = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
    }

    //傳入text為要顯示的文字
    public static CIToastView makeText(Context context, CharSequence text) {

        CIToastView result = new CIToastView(context);

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_view_toast,
                (ViewGroup) ((Activity)context).findViewById(R.id.ll_toast_root));

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_toast_content);
        LinearLayout.LayoutParams params
                = (LinearLayout.LayoutParams)linearLayout.getLayoutParams();
        params.width = result.m_vScaleDef.getLayoutWidth(340);
        params.leftMargin = result.m_vScaleDef.getLayoutWidth(10);
        params.rightMargin = result.m_vScaleDef.getLayoutWidth(10);

        TextView tv = (TextView) view.findViewById(R.id.tv_toast);
        tv.setText(text);
        result.m_vScaleDef.setTextSize(result.m_vScaleDef.DEF_TEXT_SIZE_16, tv);
        result.m_vScaleDef.setPadding(tv, 20, 10, 20, 10);
        tv.setMinimumHeight(result.m_vScaleDef.getLayoutHeight(40));

        result.m_viewNext = view;

        //顯示位置 置中; 從狀態列開始 間隔66px
        result.setGravity(
                Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                0,
                result.m_vScaleDef.getLayoutHeight(66));

        return result;
    }

    /**
     * Set the view to show.
     * @see #getView
     */
    public void setView(View view) {
        m_viewNext = view;
    }

    /**
     * Return the view.
     * @see #setView
     */
    public View getView() {
        return m_viewNext;
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
     * @see android.view.Gravity
     * @see #getGravity
     */
    public void setGravity(int gravity, int xOffset, int yOffset) {
        m_iGravity = gravity;
        m_iX = xOffset;
        m_iY = yOffset;
    }

    /**
     * Get the location at which the notification should appear on the screen.
     * @see android.view.Gravity
     * @see #getGravity
     */
    public int getGravity() {
        return m_iGravity;
    }

    private void showNativeToast(){
        Toast toast = new Toast(CIApplication.getContext());
        toast.setGravity(m_iGravity, m_iX, m_iY);

        //原生toast,顯示時間只能固定設LENGTH_SHORT或LENGTH_LONG秒, 不能自定
        toast.setDuration(Toast.LENGTH_LONG);

        toast.setView(m_viewNext);
        toast.show();
    }

    /**
     * schedule handleShow into the right thread
     */
    public void show() {
        m_handler.post(m_rShow);

        if(0 < m_iDuration) {
            m_handler.postDelayed(m_rHide, m_iDuration);
        }
    }

    private void handleShow() {

        if (m_view != m_viewNext) {
            // remove the old view if necessary
            handleHide();

            m_view = m_viewNext;
            // m_WM = WindowManagerImpl.getDefault();
            final int gravity = m_iGravity;
            m_params.gravity = gravity;

            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                m_params.horizontalWeight = 1.0f;
            }

            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                m_params.verticalWeight = 1.0f;
            }

            m_params.x = m_iX;
            m_params.y = m_iY;

            if (null != m_view.getParent()) {
                m_wm.removeView(m_view);
            }

            m_wm.addView(m_view, m_params);
        }
    }

    private void handleHide() {

        if (null != m_view) {

            if (null != m_view.getParent()) {
                m_wm.removeView(m_view);
            }
            m_view = null;
        }
    }
}
