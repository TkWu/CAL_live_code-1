package ci.ui.ScrollView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;


/**
 * Created by flowmahuang on 2016/3/23.
 */
public class ReachEndScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener = null;

    public interface ScrollViewListener {
       void onScrollChanged(ReachEndScrollView scrollView,
                             int x, int y, int oldx, int oldy);
    }

    public ReachEndScrollView(Context context ,AttributeSet arrts) {
        super(context,arrts);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }
}
