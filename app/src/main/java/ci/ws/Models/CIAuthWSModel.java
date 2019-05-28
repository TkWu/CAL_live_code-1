package ci.ws.Models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import ci.ws.cores.CIResponseCallback;
import ci.ws.cores.CIWSAuthLanuch;
import ci.ws.cores.CIWSShareManager;
import ci.ws.cores.object.CIRequest;
import ci.ws.cores.object.CIResponse;
import ci.ws.cores.object.EMethod;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;
import ci.ws.Models.entities.CIAuthResp;

/**
 * Created by Ryan on 16/4/13.
 * 此Model需要自帶thread 才能SenRequest,
 */
public class CIAuthWSModel {

    public interface AuthCallback {

        /**取得授權成功
         * @param strAuth  token_type 授權的Type + 空格 + access_token 授權碼
         * @param expires_in 授權有效時間*/
        void onAuthSuccess( String strAuth, String expires_in );

        void onAuthError( String rt_code, String rt_msg, Exception exception );
    }

    public static final String API_NAME = "/CIAPP/Authenticate";
    protected Map<String, String> m_body = new HashMap<>();

    private AuthCallback m_authListener = null;

    protected enum eParaTag {

        grant_type("grant_type"),
        username("username"),
        password("password");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    /**取得授權的必要參數，與GW定義後的固定值*/
    public CIAuthWSModel(){

        m_body.put( eParaTag.grant_type.getString(), "password");
        m_body.put( eParaTag.username.getString(),   "Admin");
        m_body.put( eParaTag.password.getString(),   "secret");
    }

    private String getUrlencodedBody(){

        StringBuilder strUrlPara = new StringBuilder();
        Boolean bFirst = true;

        for ( Map.Entry<String, String> entry : m_body.entrySet() ){

            String strKey = entry.getKey();
            String strValue = entry.getValue();

            try {

                if ( bFirst ){
                    bFirst = false;
                } else {
                    strUrlPara.append("&");
                }

                strUrlPara.append(URLEncoder.encode(strKey, "UTF-8"));
                strUrlPara.append("=");
                strUrlPara.append(URLEncoder.encode(strValue, "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return strUrlPara.toString();
    }

    public void doAuthConnection( AuthCallback listener ){

        this.m_authListener = listener;

        CIRequest reqAuth = new CIRequest( WSConfig.DEF_WS_SITE + API_NAME, EMethod.POST, null, getUrlencodedBody() );

        new CIWSAuthLanuch().connection(reqAuth, new CIResponseCallback(){

            @Override
            public void onSuccess(String respBody, int code) {
                DecodeResponse(respBody, code);
            }

            @Override
            public void onError(CIResponse response, int code, Exception exception) {
                if ( null != m_authListener ){
                    String strMsg = "";
                    if ( null != response ){
                        strMsg = response.body();
                    } else {
                        strMsg = exception.toString();
                    }
                    m_authListener.onAuthError( String.valueOf(code), strMsg, exception );
                }
            }
        });
    }

    protected void DecodeResponse(String respBody, int code){

        CIAuthResp authResp = GsonTool.toObject(respBody, CIAuthResp.class);
        if ( null != authResp && null != m_authListener ){

            String strAuth = authResp.token_type + " " +authResp.access_token;
            //轉換成 long , 方便比較時間是否過期
            long lVaildtime = 0;
            if ( authResp.expires_in.length() > 0 ){
                lVaildtime = (Integer.parseInt(authResp.expires_in)*1000);
            }
            CIWSShareManager.getAPI().SaveCIWSAuth( strAuth, System.currentTimeMillis(), lVaildtime);

            m_authListener.onAuthSuccess( strAuth, authResp.expires_in );
        } else {
            if ( null != m_authListener )
                m_authListener.onAuthError( "", "", null );
        }
    }
}
