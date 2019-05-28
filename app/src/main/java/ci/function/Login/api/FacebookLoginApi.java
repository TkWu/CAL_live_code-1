package ci.function.Login.api;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import ci.function.Core.SLog;

import com.chinaairlines.mobile30.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.function.Login.listener.SocialConnectListener;
import ci.ui.define.UiMessageDef;

/** fb登入
 * Created by jlchen on 2016/3/14.
 */
public class FacebookLoginApi {

    //fb sdk初始化 需傳入當前activity
    private AppCompatActivity       m_Activity              = null;

    private CallbackManager         m_callbackManager       = null;
    private AccessToken             m_accessToken           = null;

    private int                     m_iRequestId            = 0;
    private String                  m_strId                 = "";
    private String                  m_strMail               = "";

    private static final String     TAG                     = "FacebookLoginApi";

    //fb login的 callback interface
    private SocialConnectListener   m_userCallbackListener  = null;

    public FacebookLoginApi(AppCompatActivity activity) {
        this.m_Activity = activity;

        FacebookSdk.sdkInitialize(m_Activity);
    }

    public void setCallbackManager(){
        m_callbackManager = CallbackManager.Factory.create();
    }

    public CallbackManager getCallbackManager() {
        return m_callbackManager;
    }

    //登出
    public void signOut()
    {
        LoginManager.getInstance().logOut();
       SLog.d(TAG, "signOut()");
    }

    //一般登入
    public void signIn(int identifier, ArrayList<String> permissions)
    {
        //解決帳號名稱是中文，會登入失敗的問題
        signOut();
        //
       SLog.d(TAG, "signIn()");

        m_iRequestId = identifier;
        LoginManager.getInstance().logInWithReadPermissions(m_Activity, permissions);

        startFacebookLogin();
    }

    protected void startFacebookLogin() {

       SLog.d(TAG, "startFacebookLogin()");

        LoginManager.getInstance().registerCallback(m_callbackManager,
                new FacebookCallback<LoginResult>() {

                    //登入成功
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                       SLog.d(TAG, "onSuccess");

                        m_accessToken = loginResult.getAccessToken();

                        getGraphRequest();
                    }

                    //登入取消
                    @Override
                    public void onCancel() {
                        if ( !TextUtils.isEmpty(CIApplication.getLoginInfo().GetFbLoginId())
                                && !TextUtils.isEmpty(CIApplication.getLoginInfo().GetFbEmail()) ){

                            if (getUserCallbackListener() != null) {
                                m_userCallbackListener.onUserConnected(
                                        m_iRequestId,
                                        CIApplication.getLoginInfo().GetFbLoginId(),
                                        CIApplication.getLoginInfo().GetFbEmail());
                            }

                        }else {
                           SLog.d(TAG, "onCancel");
                            LoginManager.getInstance().logOut();
                            if (getUserCallbackListener() != null) {
                                m_userCallbackListener.onCancelled(
                                        m_iRequestId, "Facebook Login request cancelled...");
                            }
                        }
                    }

                    //登入失敗
                    @Override
                    public void onError(FacebookException exception) {
                       SLog.e(TAG, "onError:" + exception.toString());

                        if (getUserCallbackListener() != null) {
                            m_userCallbackListener.onConnectionError(
                                    m_iRequestId, exception.getMessage());
                        }
                    }
                });
    }
 
    //取得回傳資料, 並寫入登入資訊
    private void getGraphRequest(){

        final Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture");

        GraphRequest request = GraphRequest.newMeRequest(
                m_accessToken,
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                       SLog.d(TAG, "getGraphRequest()-onCompleted");

                        CIApplication.getLoginInfo().SetLoginStatus(true);
                        CIApplication.getLoginInfo().SetLoginType(
                                UiMessageDef.BUNDLE_LOGIN_TYPE_FACEBOOK);

                        try {
                           SLog.d(TAG, user.toString());
                           SLog.d(TAG, graphResponse.toString());

                            CIApplication.getLoginInfo().SetUserName(user.optString("name"));
                            CIApplication.getLoginInfo().SetSocialLoginId(user.optString("id"));
                            CIApplication.getLoginInfo().SetFbLoginId(user.optString("id"));

                            JSONObject jsonObjectPicture = user.optJSONObject("picture");
                            JSONObject jsonObjectData = jsonObjectPicture.optJSONObject("data");
                            boolean bSilhouette = jsonObjectData.optBoolean("is_silhouette");
//                            boolean bSilhouette = user.optBoolean("is_silhouette");
                           SLog.d(TAG,String.valueOf(bSilhouette));

                            if (false == bSilhouette){
                                CIApplication.getLoginInfo().SetUserPhotoUrl(
                                        "https://graph.facebook.com/" + user.optString("id") + "/picture?type=normal");
                            }else {
                                CIApplication.getLoginInfo().SetUserPhotoUrl("");
                            }

                            CIApplication.getLoginInfo().SetUserEmail(user.optString("email"));
                            CIApplication.getLoginInfo().SetFbEmail(user.optString("email"));

                            m_strId     = user.optString("id");
                            m_strMail   = user.optString("email");

                            if (getUserCallbackListener() != null) {
                                m_userCallbackListener.onUserConnected(m_iRequestId, m_strId, m_strMail);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                            if (getUserCallbackListener() != null) {
                                m_userCallbackListener.onConnectionError(
                                        m_iRequestId, e.getMessage());
                            }
                        }
                    }
                });

        request.setParameters(parameters);
        request.executeAsync();
    }

    //如果之前有登入過, 用以前token重取資料, 否則當作登出
    public void connectWithFB() {

        m_accessToken = AccessToken.getCurrentAccessToken();

        if ( null != m_accessToken ){

           SLog.d(TAG, "" + m_accessToken.toString());

            getGraphRequest();

        }else {
            if (m_userCallbackListener != null) {
                m_userCallbackListener.onConnectionError(m_iRequestId,
                        UiMessageDef.BUNDLE_LOGIN_TYPE_FACEBOOK +" "
                                + m_Activity.getString(R.string.menu_log_in)+" "
                                + m_Activity.getString(R.string.error));
            }
        }
    }

    //個人資訊-社群媒體連結
    public void SocialNetworkSignIn(int identifier, ArrayList<String> permissions)
    {
       SLog.d(TAG, "SocialNetworkSignIn()");

        m_iRequestId = identifier;
        LoginManager.getInstance().logInWithReadPermissions(m_Activity, permissions);

        LoginManager.getInstance().registerCallback(m_callbackManager,
                new FacebookCallback<LoginResult>() {

                    //登入成功
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                       SLog.d(TAG, "SocialNetworkSignIn-onSuccess");

                        m_accessToken = loginResult.getAccessToken();

                        final Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,picture");

                        GraphRequest request = GraphRequest.newMeRequest(
                                m_accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {

                                    @Override
                                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                                       SLog.d(TAG, "SocialNetworkSignIn-onCompleted");

                                        try {
                                           SLog.d(TAG, user.toString());
                                           SLog.d(TAG, graphResponse.toString());

                                            m_strId = user.optString("id");
                                            m_strMail = user.optString("email");

                                            if (getUserCallbackListener() != null) {
                                                m_userCallbackListener.onUserConnected(m_iRequestId, m_strId, m_strMail);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();

                                            if (getUserCallbackListener() != null) {
                                                m_userCallbackListener.onConnectionError(
                                                        m_iRequestId, e.getMessage());
                                            }
                                        }
                                    }
                                });

                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    //登入取消
                    @Override
                    public void onCancel() {
                       SLog.d(TAG, "SocialNetworkSignIn-onCancel");

                        if (getUserCallbackListener() != null) {
                            m_userCallbackListener.onCancelled(
                                    m_iRequestId, "Facebook Login request cancelled...");
                        }
                    }

                    //登入失敗
                    @Override
                    public void onError(FacebookException exception) {
                       SLog.e(TAG, "SocialNetworkSignIn-onError:" + exception.toString());

                        if (getUserCallbackListener() != null) {
                            m_userCallbackListener.onConnectionError(
                                    m_iRequestId, exception.getMessage());
                        }
                    }
                });

    }

    public SocialConnectListener getUserCallbackListener() {
        return m_userCallbackListener;
    }

    public void setUserCallbackListener(SocialConnectListener m_userCallbackListener) {
        this.m_userCallbackListener = m_userCallbackListener;
    }

}
