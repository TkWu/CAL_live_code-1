package ci.ui.object;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;

import java.util.HashSet;
import java.util.Set;

import ci.function.Core.EncryptValueManager;
import ci.function.Core.Encryption;

/**
 * Created by KevinCheng on 16/5/18.
 * 提供Model使用SharePreference記錄組態值
 */
public class CIModelInfo {

    private final String TAG = "ModelInfo";

    private Context m_context = null;

    private SharedPreferences m_spInfo = null;

    //SharedPreferences 檔名定義
    /** 總表Model最後更新日期*/
    private static final String KEY_STATION_LIST_TIME   = "KEY_STATION_LIST_TIME";
    /** 飛航狀態Model最後更新日期*/
    private static final String KEY_FLIGHT_STATUS_TIME  = "KEY_FLIGHT_STATUS_TIME";
    /** 訂位購票Model最後更新日期*/
    private static final String KEY_BOOK_TICKET_TIME    = "KEY_BOOK_TICKET_TIME";
    /** 時刻表Model最後更新日期*/
    private static final String KEY_TIME_TABLE_TIME     = "KEY_TIME_TABLE_TIME";
    /** 總表Model最後更新語言*/
    private static final String KEY_STATION_LIST_LANG   = "KEY_STATION_LIST_LANG";
    /** 首頁Boarding Pass資料*/
    private static final String KEY_HOMEPAGE_BOARDING_PASS_DATA   = "KEY_HOMEPAGE_BOARDING_PASS_DATA";
    /** 資料表名稱*/
    private static final String KEY_DATABASE_TABLE_NAME_SET  = "KEY_DATABASE_TABLE_NAME_SET";

    //SharedPreferences 欄位預設值
    /** 預設最後更新時間*/
    public static final String DEF_LAST_UPDATE_DATE    = "2016-01-01 00:00:00";
    /** 預設最後更新語言*/
    public static final String DEF_LAST_UPDATE_LANG    = "zh-TW";
    /** 預設Boarding Pass資料*/
    public static final String DEF_HOMEPAGE_BOARDING_PASS_DATA    = "";
    /** 預設資料表集*/
    public static final Set<String> DEF_DATABASE_TABLE_NAME_SET    = null;

    public CIModelInfo(Context context ) {

        this.m_context = context;
        m_spInfo = context.getSharedPreferences( TAG, Context.MODE_PRIVATE);
    }

    /**
     * 設定 「CIInquiryStationListModel」最後更新時間
     * @param lastDate 最後更新時間
     */
    public void SetStationListLastUpdateDate(String lastDate){

        if (null == m_spInfo) {
            return;
        }

        EncryptValueManager.setValue(m_spInfo, KEY_STATION_LIST_TIME, lastDate);
    }

    /**@return 「CIInquiryStationListModel」最後更新時間*/
    public String GetStationListLastUpdateDate(){

        if (null == m_spInfo) {
            return DEF_LAST_UPDATE_DATE;
        }

        return EncryptValueManager.getString(m_spInfo, KEY_STATION_LIST_TIME, DEF_LAST_UPDATE_DATE);
    }

    /**
     * 設定 「CIInquiryFlightStatusODModel」最後更新時間
     * @param lastDate 最後更新時間
     */
    public void SetFlightStatusODLastUpdateDate(String lastDate){

        if (null == m_spInfo) {
            return;
        }

        EncryptValueManager.setValue(m_spInfo, KEY_FLIGHT_STATUS_TIME, lastDate);
    }

    /**@return 「CIInquiryFlightStatusODModel」最後更新時間*/
    public String GetFlightStatusODLastUpdateDate(){

        if (null == m_spInfo) {
            return DEF_LAST_UPDATE_DATE;
        }

        return EncryptValueManager.getString(m_spInfo, KEY_FLIGHT_STATUS_TIME, DEF_LAST_UPDATE_DATE);

    }

    /**
     * 設定 「CIInquiryFlightBookTicketODListModel」最後更新時間
     * @param lastDate 最後更新時間
     */
    public void SetBookTicketODLastUpdateDate(String lastDate){

        if (null == m_spInfo) {
            return;
        }

        EncryptValueManager.setValue(m_spInfo, KEY_BOOK_TICKET_TIME, lastDate);
    }

    /**@return 「CIInquiryFlightBookTicketODListModel」最後更新時間*/
    public String GetBookTicketODLastUpdateDate(){

        if (null == m_spInfo) {
            return DEF_LAST_UPDATE_DATE;
        }

        return EncryptValueManager.getString(m_spInfo, KEY_BOOK_TICKET_TIME, DEF_LAST_UPDATE_DATE);
    }

    /**
     * 設定 「CIInquiryFlightTimeTableODListModel」最後更新時間
     * @param lastDate 最後更新時間
     */
    public void SetTimeTableODLastUpdateDate(String lastDate){

        if (null == m_spInfo) {
            return;
        }

        EncryptValueManager.setValue(m_spInfo, KEY_TIME_TABLE_TIME, lastDate);
    }

    /**@return 「CIInquiryFlightTimeTableODListModel」最後更新時間*/
    public String GetTimeTableODLastUpdateDate(){

        if (null == m_spInfo) {
            return DEF_LAST_UPDATE_DATE;
        }

        return EncryptValueManager.getString(m_spInfo, KEY_TIME_TABLE_TIME, DEF_LAST_UPDATE_DATE);
    }


    /**
     * 設定 「CIInquiryStationListModel」最後更新語言
     * @param Language 最後更新時間
     */
    public void SetStationListLastUpdateLanguage(String Language){

        if (null == m_spInfo) {
            return;
        }

        EncryptValueManager.setValue(m_spInfo, KEY_STATION_LIST_LANG, Language);
    }

    /**@return 「CIInquiryStationListModel」最後更新語言*/
    public String GetStationListLastUpdateLanguage(){

        if (null == m_spInfo) {
            return DEF_LAST_UPDATE_LANG;
        }

        return EncryptValueManager.getString(m_spInfo, KEY_STATION_LIST_LANG, DEF_LAST_UPDATE_LANG);
    }

    /**
     * 設定 首頁Boarding Pass資料
     * @param data 最後更新時間
     */
    public void setHomepageBoardingPassData(String data){

        if (null == m_spInfo) {
            return;
        }

        EncryptValueManager.setValue(m_spInfo, KEY_HOMEPAGE_BOARDING_PASS_DATA, data);
    }

    /**@return 首頁Boarding Pass資料*/
    public String GetHomepageBoardingPassData(){

        if (null == m_spInfo) {
            return DEF_HOMEPAGE_BOARDING_PASS_DATA;
        }

        return EncryptValueManager.getString(m_spInfo, KEY_HOMEPAGE_BOARDING_PASS_DATA, DEF_HOMEPAGE_BOARDING_PASS_DATA);
    }

    /**
     * 加入資料表名稱，主要給CIInquiryTripEntity使用
     * @param tableName 資料表名稱
     */
    public void addDatabaseTableName(String tableName){

        if (null == m_spInfo) {
            return;
        }
        Encryption.AES aes = Encryption.AES.getInstance();
        Set<String> oldSet = GetAllDatabaseTableName();
        Set<String> newSet = new HashSet<>();
        if(null != oldSet){
            for(String tableNameInSet : oldSet){
                newSet.add(aes.encrypt(tableNameInSet));
            }
        }
        if(!TextUtils.isEmpty(tableName)){
            newSet.add(aes.encrypt(tableName));
        }
        m_spInfo.edit().putStringSet(KEY_DATABASE_TABLE_NAME_SET, newSet).commit();
    }

    /**@return 全部儲存的資料表名稱*/
    public Set<String> GetAllDatabaseTableName(){

        if (null == m_spInfo) {
            return DEF_DATABASE_TABLE_NAME_SET;
        }
        Encryption.AES aes = Encryption.AES.getInstance();
        Set<String> oldSet = m_spInfo.getStringSet(KEY_DATABASE_TABLE_NAME_SET, DEF_DATABASE_TABLE_NAME_SET);
        Set<String> newSet = new HashSet<>();
        if(null != oldSet) {
            for (String tableNameInSet : oldSet) {
                newSet.add(aes.decrypt(tableNameInSet));
            }
        } else {
            newSet = null;
        }
        return newSet;
    }

    @Deprecated
    /**@return 全部儲存的資料表名稱*/
    public Set<String> GetAllDatabaseTableNameForOldVersion(){

        if (null == m_spInfo) {
            return DEF_DATABASE_TABLE_NAME_SET;
        }

        return m_spInfo.getStringSet(KEY_DATABASE_TABLE_NAME_SET, DEF_DATABASE_TABLE_NAME_SET);
    }

    public void clear(){
        m_spInfo.edit().clear().commit();
    }

}
