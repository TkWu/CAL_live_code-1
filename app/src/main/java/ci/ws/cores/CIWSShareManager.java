package ci.ws.cores;

import android.content.Context;
import android.content.SharedPreferences;

import ci.function.Core.CIApplication;
import ci.function.Core.EncryptValueManager;

/**
 * Created by Ryan on 16/3/26.
 */
public class CIWSShareManager {

    private static CIWSShareManager m_Instance  = null;
    public static final String WS_SHARE         = "WS_SHARE";
    public static final String KEY_AUTH         = "KEY_AUTH";
    public static final String KEY_TIME         = "KEY_TIME";
    public static final String KEY_VALID_TIME   = "KEY_VALID_TIME";

    private static final String KEY_LOGIN_TOKEN = "KEY_LOGIN_TOKEN";

    private SharedPreferences   m_spCommon      = null;

    public static CIWSShareManager getAPI(){
        if ( null == m_Instance ){
            m_Instance = new CIWSShareManager();
        }
        return m_Instance;
    }

    public CIWSShareManager(){
        m_spCommon = CIApplication.getContext().getSharedPreferences( WS_SHARE, Context.MODE_PRIVATE );
    }

    /**檢查授權是否過期
     * @return 未過期就給授權字串, 過期回覆空字串*/
    public String IsCIAuthValid(){

        //先檢查授權是否存在
        String strAuth = m_spCommon.getString(KEY_AUTH, "");
        if ( strAuth.length() <= 0 ){
            return "";
        }

        //檢查授權是否已經過期
        long defValue = 0;
        long lLastReqtime   = EncryptValueManager.getLong(m_spCommon, KEY_TIME, defValue);
        long lValidTime     = EncryptValueManager.getLong(m_spCommon, KEY_VALID_TIME, defValue);

        long lTime = System.currentTimeMillis();

        if ( lTime - lValidTime > lLastReqtime ){
            return "";
        } else {
            return strAuth;
        }
    }

    /**@return 取得授權字串*/
    public String getCIAuth(){

        if (null == m_spCommon) {
            return "";
        }

        return EncryptValueManager.getString(m_spCommon, KEY_AUTH, "");
    }

    /**儲存取得授權當下的時間, 以及授權有效時間, 以及授權
     * @param strAuth 要存檔的授權字串
     * @param lSystemcurrTime 取道授權當下的時間
     * @param lValidtime 授權有效時間
     * @return 授權字串*/
    public  String  SaveCIWSAuth( String strAuth, long lSystemcurrTime, long lValidtime ){

        EncryptValueManager.setValue(m_spCommon, KEY_AUTH, strAuth);
        EncryptValueManager.setValue(m_spCommon, KEY_TIME, lSystemcurrTime);
        EncryptValueManager.setValue(m_spCommon, KEY_VALID_TIME, lValidtime);

        return strAuth;
    }

    /**@return 「取得登入成功後的Token，此參數為其他WS的必填參數之一」*/
    public String getLoginToken(){

        if (null == m_spCommon) {
            return "";
        }

        return EncryptValueManager.getString(m_spCommon, KEY_LOGIN_TOKEN, "");
    }

    /**
     * 設定「儲存登入成功後的Token，此參數為其他WS的必填參數之一」
     * @param strLoginToken Token
     */
    public void setLoginToken(String strLoginToken){

        if (null == m_spCommon) {
            return;
        }
        EncryptValueManager.setValue(m_spCommon, KEY_LOGIN_TOKEN, strLoginToken);
    }

    public void clear(){
        m_spCommon.edit().clear().commit();
    }
}
