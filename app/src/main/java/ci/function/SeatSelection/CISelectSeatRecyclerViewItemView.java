package ci.function.SeatSelection;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.ui.define.ViewScaleDef;

/**
 * 選位頁面元件之View
 * Created by flowmahuang on 2016/4/21.
 */
@Deprecated
public class CISelectSeatRecyclerViewItemView extends RelativeLayout {
    private ViewScaleDef m_vScaleDef = null;
    private Context m_Context;
    public FrameLayout m_SeatImage;
    public TextView m_SeatText;
    public FrameLayout m_Translate;

    public CISelectSeatRecyclerViewItemView(Context context) {
        super(context);
        this.m_Context = context;
        m_vScaleDef = ViewScaleDef.getInstance(m_Context);
        m_SeatText = setTextView();
        m_SeatImage = setImageView();
        m_Translate = setImageView();
        m_vScaleDef.setPadding(this, 3, 3, 3, 3);
    }

    private TextView setTextView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(CENTER_IN_PARENT);
        TextView v = new TextView(m_Context);
        v.setLayoutParams(params);
        v.setGravity(Gravity.CENTER);
        return v;
    }

    private FrameLayout setImageView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(CENTER_IN_PARENT);
        FrameLayout v = new FrameLayout(m_Context);
        v.setLayoutParams(params);
        return v;
    }

    public void setTextViewText(String s) {
        m_SeatText.setText(s);
        if (Build.VERSION.SDK_INT > 22)
            m_SeatText.setTextColor(getResources().getColor(R.color.white_four, null));
        else
            m_SeatText.setTextColor(getResources().getColor(R.color.white_four));
        removeAllViews();
        m_vScaleDef.setPadding(this, 3, 3, 3, 3);
        m_vScaleDef.setTextSize(13, m_SeatText);
        addView(m_SeatText);
    }

    public void setImageViewImage(int id ,String num,int set) {
        m_SeatImage.setBackgroundResource(id);
        m_SeatText.setText(num);
        m_vScaleDef.setTextSize(16, m_SeatText);
        if (Build.VERSION.SDK_INT > 22)
            m_SeatText.setTextColor(getResources().getColor(R.color.white_four, null));
        else
            m_SeatText.setTextColor(getResources().getColor(R.color.white_four));
        removeAllViews();
        m_vScaleDef.setPadding(this, 3, 3, 3, 3);
        m_vScaleDef.selfAdjustSameScaleView(m_SeatImage, set, set);
        addView(m_SeatImage);
        addView(m_SeatText);
    }

    public void setFinalRow(){
        m_Translate.setBackgroundColor(Color.TRANSPARENT);
        m_vScaleDef.setPadding(this, 3, 3, 3, 3);
        removeAllViews();
        addView(m_Translate);
    }

}
