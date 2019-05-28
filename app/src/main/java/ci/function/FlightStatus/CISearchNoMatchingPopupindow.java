package ci.function.FlightStatus;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.ui.define.ViewScaleDef;

/**
 * Created by flowmahuang on 2016/3/31.
 */
public class CISearchNoMatchingPopupindow extends PopupWindow {
    private TextView m_TilteText = null;
    private TextView m_ContentText = null;
    private Context m_context = null;
    private View contentView;
    private ViewScaleDef vScale;

    public CISearchNoMatchingPopupindow(Context context, int iWidth, int iHeight) {
        contentView = LayoutInflater.from(context).inflate(R.layout.popupwindow_search_no_matching, null);

        m_context = context;
        setContentView(contentView);
        setWidth(iWidth);
        setHeight(iHeight);

        setFocusable(false);
        setOutsideTouchable(false);

        vScale = ViewScaleDef.getInstance(context);
        m_TilteText = (TextView) contentView.findViewById(R.id.no_matching_title);
        m_ContentText = (TextView) contentView.findViewById(R.id.no_matching_content);
        vScale.setMargins(m_TilteText, 20, 10, 20, 0);
        vScale.setMargins(m_ContentText, 20, 6, 20, 12.7);
        vScale.setTextSize(16, m_TilteText);
        vScale.setTextSize(14, m_ContentText);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        ObjectAnimator oa = ObjectAnimator.ofFloat(contentView, "translationY", -vScale.getLayoutHeight(80), 0);
        oa.setDuration(500);
        oa.start();

        new View(m_context).postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator oa = ObjectAnimator.ofFloat(contentView, "translationY", 0, -vScale.getLayoutHeight(80));
                oa.setDuration(500);
                oa.start();
            }
        },2500);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
