package ci.ui.TextField.Base;

import android.support.annotation.IdRes;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SViewHolder {
    private final SparseArray<View> views;
    private View vConvert = null;

    public SViewHolder(View vConvert) {
        this.views = new SparseArray<>();
        this.vConvert = vConvert;
        vConvert.setTag(this);
    }

    public static SViewHolder getViewHolder(View convertView) {
        if (null == convertView.getTag()) {
            return new SViewHolder(convertView);
        }

        return (SViewHolder) convertView.getTag();
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int iViewId) {
        View view = views.get(iViewId);
        if (null == view) {
            view = vConvert.findViewById(iViewId);
            views.put(iViewId, view);
        }
        return (T) view;
    }

    public TextView getTextView(@IdRes int iViewId){
        return (TextView)getView(iViewId);
    }
    public EditText getEditText(@IdRes int iViewId){
        return (EditText)getView(iViewId);
    }
}
