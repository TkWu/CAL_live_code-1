package ci.ui.object;

import android.content.Context;
import android.content.SharedPreferences;

import ci.function.Core.EncryptValueManager;

/**
 * Created by KevinCheng on 17/6/13.
 * 提供 CITimeTableTrack 及 CIFlightResultDetialActivity 使用SharePreference記錄組態值
 */
public class CIFlightTrackInfo {

    private final String TAG = "FLIGHTTRACK";

    private Context m_context = null;

    private SharedPreferences m_spInfo = null;

    /** SharedPreferences 檔名定義 */
    public static final String SHAREPRE_TAG_TRACKDATA       = "TRACKDATA_";
    public static final String SHAREPRE_TAG_TRACKDATA_0     = "TRACKDATA_0";
    public static final String SHAREPRE_TAG_TRACKDATA_1     = "TRACKDATA_1";
    public static final String SHAREPRE_TAG_TRACKDATA_2     = "TRACKDATA_2";


    /** 預設資料*/
    public static final String DEF_DATA    = "";

    public CIFlightTrackInfo(Context context) {

        this.m_context = context;
        m_spInfo = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
    }

    public void setFlightTrackData(String key, String lastDate){

        if (null == m_spInfo) {
            return;
        }

        EncryptValueManager.setValue(m_spInfo, key, lastDate);
    }

    public String getFlightTrackData(String key){

        if (null == m_spInfo) {
            return DEF_DATA;
        }

        return EncryptValueManager.getString(m_spInfo, key, DEF_DATA);
    }

    public void removeFlightTrackData(String key){

        if (null == m_spInfo) {
            return ;
        }

        m_spInfo.edit().remove(key).commit();
    }

    public void clear(){
        m_spInfo.edit().clear().commit();
    }

}
