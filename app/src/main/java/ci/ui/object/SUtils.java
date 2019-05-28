package ci.ui.object;

import android.text.TextUtils;

/**
 * Created by kevincheng on 2017/12/13.
 */

public class SUtils {
    private static final String NULL = "null";
    public String getFilteredText(String originText, String newText) {
        return !TextUtils.isEmpty(originText)
                && !TextUtils.equals(originText, NULL)
                ? originText : newText;
    }
}
