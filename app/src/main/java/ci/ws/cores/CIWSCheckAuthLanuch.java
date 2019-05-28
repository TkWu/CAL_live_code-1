package ci.ws.cores;

import java.util.HashMap;
import java.util.Map;

import ci.ws.Models.CIAuthWSModel;
import ci.ws.cores.base.CIWSBaseReqLanuch;
import ci.ws.cores.object.CIRequest;
import ci.ws.define.CIWSResultCode;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/3/25.
 * WS 發射器, 透過此Class, 來發送WS,
 * Send request 邏輯寫在 CIWSBaseReqLanuch, 此Lanuch 會先
 */
public class CIWSCheckAuthLanuch extends CIWSBaseReqLanuch {

    /**授權檢查次數*/
    private int iCheckAuthCnt = 1;

    /**預設-1, 代表使用 共用的time out 設定*/
    private int m_iConnectionTimeout = -1;

    private int m_iReadTimeout = -1;

    /**是否需要授權碼才能向ws要資料*/
    private boolean m_bIsNeedAuth = true;

    @Override
    protected int getConnectTimeout() {

        if ( m_iConnectionTimeout != -1 ){
            return m_iConnectionTimeout;
        }
        return WSConfig.DEF_CONNECTION_TIME_OUT;
    }

    @Override
    protected int getReadTimeout() {

        if ( m_iReadTimeout != -1 ){
            return m_iReadTimeout;
        }
        return WSConfig.DEF_READ_TIME_OUT;
    }

    @Override
    protected Map<String, String> getDefaultHeaders() {

        Map<String, String > header = new HashMap<>();
        if(m_bIsNeedAuth){
            header.put(WSConfig.HEADER_KEY_AUTH, CIWSShareManager.getAPI().getCIAuth());
        }
        header.put(WSConfig.DEF_CONTENT_TYPE_KEY, WSConfig.DEF_CONTENT_TYPE);

        return header;
    }

    /**由於CI的WS都需要帶授權, 而授權有時效性, 所以在發request時, 須先檢查授權, 取得授權,*/
    @Override
    protected void doConnection( final CIRequest requests ){

        //先檢查授權, 當授權過期或者是沒授權, 則去取授權
        if ( CIWSShareManager.getAPI().IsCIAuthValid().length() <= 0 && iCheckAuthCnt >= 1
                && m_bIsNeedAuth){

            //只檢查一次授權, 避免取不到授權, 一直卡在這
            iCheckAuthCnt--;
            CIAuthWSModel AuthWSModel = new CIAuthWSModel();
            AuthWSModel.doAuthConnection(new CIAuthWSModel.AuthCallback() {
                @Override
                public void onAuthSuccess(String strAuth, String expires_in) {
                    Doconnection(requests);
                }

                @Override
                public void onAuthError(String rt_code, String rt_msg, Exception exception) {
                    //2016-07-20 授權失敗直接顯示Timeout 訊息
                    m_callback.onError(null, Integer.valueOf(CIWSResultCode.HTTP_RESPONSE_TIME_OUT), null);
                    //Doconnection(requests);
                }
            });

        } else {
            super.doConnection(requests);
        }
    }

    public  void Doconnection( CIRequest requests ){
        doConnection(requests);
    }

    /**提供外部設定Timeout時間, 不設定則會抓取WSConfig裡的定義*/
    public void setConnectTimeout( int iConnectionTimeout ) {
        m_iConnectionTimeout = iConnectionTimeout;
    }

    /**提供外部設定Timeout時間, 不設定則會抓取WSConfig裡的定義*/
    public void setReadTimeout( int iReadTimeout ) {
        m_iReadTimeout = iReadTimeout;
    }

    /**設定reqeust是否需要授權碼*/
    public void setIsNeedAuth(boolean isNeedAuth){
        this.m_bIsNeedAuth = isNeedAuth;
    }

}
