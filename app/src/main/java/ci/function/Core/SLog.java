package ci.function.Core;

import android.text.TextUtils;
import android.util.Log;

import com.chinaairlines.mobile30.BuildConfig;


/**
 * Created by eric.w on 2016/9/26.
 * Debug: enable console log<br>
 *
 */
public class SLog {

    private static final boolean enable = BuildConfig.isLoggable && BuildConfig.DEBUG;

    private SLog() {
        // None Instance
    }

    private static final String TAG = "SLog";

    public static void i(String strMsg) {
        if (enable && !TextUtils.isEmpty(strMsg)) {
            Log.i(TAG, strMsg);
        }
    }

    public static void i(String strTag, String strMsg) {
        if (enable && !TextUtils.isEmpty(strMsg)) {
            Log.i(TAG + "[" + strTag + "]", strMsg);
        }
    }

    public static void e(String strMsg) {
        if (enable && !TextUtils.isEmpty(strMsg)) {
            Log.e(TAG, strMsg);
        }
    }

    public static void e(String strTag, String strMsg) {
        if (enable && !TextUtils.isEmpty(strMsg)) {
            Log.e(TAG + "[" + strTag + "]", strMsg);
        }
    }

    public static void e(String strTag, String strMsg, Exception e) {
        if (enable && !TextUtils.isEmpty(strMsg)) {
            Log.e(TAG + "[" + strTag + "]", strMsg, e);
        }
    }

    public static void w(String strMsg) {
        if (enable && !TextUtils.isEmpty(strMsg)) {
            Log.w(TAG, strMsg);
        }
    }

    public static void w(String strTag, String strMsg) {
        if (enable && !TextUtils.isEmpty(strMsg)) {
            Log.w(TAG + "[" + strTag + "]", strMsg);
        }
    }

    public static void d(String strMsg) {
        if (enable && !TextUtils.isEmpty(strMsg)) {
            Log.d(TAG, strMsg);
        }
    }

    public static void d(String strTag, String strMsg) {
        if (enable && !TextUtils.isEmpty(strMsg)) {
            Log.d(TAG + "[" + strTag + "]", strMsg);
        }
    }

    public static void d(String strTag, String strMsg, Exception e) {
        if (enable && !TextUtils.isEmpty(strMsg)) {
            Log.d(TAG + "[" + strTag + "]", strMsg, e);
        }
    }

    public static void v(String strMsg) {
        if (enable && !TextUtils.isEmpty(strMsg)) {
            Log.v(TAG, strMsg);
        }
    }

    public static void v(String strTag, String strMsg) {
        if (enable && !TextUtils.isEmpty(strMsg)) {
            Log.v(TAG + "[" + strTag + "]", strMsg);
        }
    }

}
