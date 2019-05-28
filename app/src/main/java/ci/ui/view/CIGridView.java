package ci.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/** 畫多段式座位圖用的GridView
 * Created by jlchen on 2016/6/29.
 */
public class CIGridView extends GridView{

    private boolean bhaveScrollbar = false;

    public CIGridView(Context context) {
        super(context);
    }

    public CIGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CIGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 設置是否有ScrollBar，當要在ScollView中顯示時，應設置為false。 默認為true
     * @param haveScrollbar
     */
    public void setHaveScrollbar(boolean haveScrollbar) {
        this.bhaveScrollbar = haveScrollbar;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
