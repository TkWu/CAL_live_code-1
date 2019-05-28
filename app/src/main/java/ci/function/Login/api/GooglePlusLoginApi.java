package ci.function.Login.api;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import ci.function.Core.SLog;

import com.chinaairlines.mobile30.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import ci.function.Core.CIApplication;
import ci.function.Login.listener.SocialConnectListener;
import ci.ui.define.UiMessageDef;
import ci.ui.dialog.CIAlertDialog;

/** google登入
 * Created by jlchen on 2016/3/7.
 */
public class GooglePlusLoginApi implements GoogleApiClient.OnConnectionFailedListener {

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST    = 9000;
    public static final int GPLUS_SIGN_IN                       = 9001;

    private AppCompatActivity       m_Activity                  = null;

    private SocialConnectListener   m_userLoginCallbackListener = null;

    /* Client for accessing Google APIs */
    private static GoogleApiClient  m_GoogleApiClient           = null;

    private int                     m_iRequestId                = 0;
    private String                  m_strId                     = "";
    private String                  m_strMail                   = "";

    private static final String     TAG                         = "GooglePlusLoginApi";

    public void createConnection(AppCompatActivity activity) {

        this.m_Activity = activity;

        if (checkPlayServices(activity)) {

//            validateServerClientID();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope(Scopes.PROFILE))
//                    .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                    .requestProfile()
//                    .requestIdToken(m_Activity.getString(R.string.server_client_id))
                    .requestEmail()
                    .requestId()
                    .build();

            if (m_GoogleApiClient == null) {
                // [START create_google_api_client]
                // Build GoogleApiClient with access to basic profile
                m_GoogleApiClient = new GoogleApiClient.Builder(activity)
                        .enableAutoManage(activity, this) //GoogleApiClient會自動於onStart()與onStop()時執行connect與disconnect()
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        //.addApi(Plus.API)
                        .build();
            }
        }
    }

    //確認是否有google service
    public static boolean checkPlayServices(Context act) {
        Activity activity = (Activity)act;

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(activity);

        if (resultCode != ConnectionResult.SUCCESS) {
           SLog.d("GooglePlayServices", "NOT SUCCESS, error code is: " + resultCode);
            return false;
        }
        return true;
    }

    //確認是否有google service並show出dialog
    public static boolean checkPlayServicesShowDialog(Context act) {
        final Activity activity = (Activity)act;

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(activity);

        if (resultCode != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }else {
                Dialog dialog = googleAPI.getErrorDialog(
                        activity,
                        resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST);
                if(dialog != null)
                {
                    dialog.show();
                }
                else
                {
                    dialog = new CIAlertDialog(activity, new CIAlertDialog.OnAlertMsgDialogListener() {
                        @Override
                        public void onAlertMsgDialog_Confirm() {
                            activity.finish();
                        }

                        @Override
                        public void onAlertMsgDialogg_Cancel() {}
                    });
                    ((CIAlertDialog)dialog).uiSetTitleText(activity.getString(R.string.warning));
                    ((CIAlertDialog)dialog).uiSetContentText(activity.getString(R.string.google_play_error));
                    ((CIAlertDialog)dialog).uiSetConfirmText(activity.getString(R.string.confirm));
                    dialog.show();
                }
            }
           SLog.d("GooglePlayServices", "NOT SUCCESS, error code is: " + resultCode);
            return false;
        }
        return true;
    }

    //登入
    public void GoogleSignIn(int identifier) {
        if (m_GoogleApiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope(Scopes.PROFILE))
//                    .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                    .requestProfile()
//                    .requestIdToken(m_Activity.getString(R.string.server_client_id))
                    .requestEmail()
                    .requestId()
                    .build();

            // [START create_google_api_client]
            // Build GoogleApiClient with access to basic profile
            m_GoogleApiClient = new GoogleApiClient.Builder(m_Activity)
                    .enableAutoManage(m_Activity, this) //GoogleApiClient會自動於onStart()與onStop()時執行connect與disconnect()
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    //.addApi(Plus.API)
                    .build();
        }

        m_iRequestId = identifier;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(m_GoogleApiClient);
        m_Activity.startActivityForResult(signInIntent, m_iRequestId);
    }

    //登出
    public void GoogleSignOut() {
        if (m_GoogleApiClient != null) {

            if (m_GoogleApiClient.isConnecting()||m_GoogleApiClient.isConnected()) {
                return;
            }

            try {
                //登出
                Auth.GoogleSignInApi.signOut(m_GoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                   SLog.d(TAG, "You are logged out successfully");

                                }
                            }
                        });

                //移除帳戶資料
                Auth.GoogleSignInApi.revokeAccess(m_GoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                   SLog.d(TAG, "You are Revoke Access successfully");

                                }
                            }
                        });

            }catch (Exception e){
               SLog.e(TAG, e.toString());
            }
//            m_GoogleApiClient.disconnect();
        }
    }

    //如果之前有登入過, GoogleSignInResult內將會包含token,可用於認證並建立連線
    public void connectWithGPlus() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(m_GoogleApiClient);
        if (opr.isDone()) {
            //成功於獲取緩存憑證, 將可立即使用GoogleSignIn結果
           SLog.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            GetGoogleSignInResult(result);
        } else {
            //沒登入過或是token過期 需要花時間建立新token(新線程) 或是請用戶進行登入
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    //目前是直接作登出
//                    GetGoogleSignInResult(googleSignInResult);
//                }
//            });
//            CIApplication.getLoginInfo().ClearLoginData();

            if (m_userLoginCallbackListener != null) {
                m_userLoginCallbackListener.onConnectionError(m_iRequestId,
                        UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE +" "
                                + m_Activity.getString(R.string.menu_log_in)+" "
                                + m_Activity.getString(R.string.error));
            }
        }
    }

    public SocialConnectListener getUserLoginCallbackListener() {
        return m_userLoginCallbackListener;
    }

    public void setUserLoginCallbackListener(SocialConnectListener m_userLoginCallbackListener) {
        this.m_userLoginCallbackListener = m_userLoginCallbackListener;
    }

    //一般登入, 需寫入登入資訊
    public void GetGoogleSignInResult(GoogleSignInResult result) {
       SLog.d(TAG, "[Google+]handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            if (acct != null) {
//                String ServerAuthCode = acct.getServerAuthCode();
//                Log.e(TAG, "ServerAuthCode:" + ServerAuthCode);
//
//                String idToken = acct.getIdToken();
//                Log.e(TAG, "idToken:" + idToken);

                CIApplication.getLoginInfo().SetLoginStatus(true);
                CIApplication.getLoginInfo().SetLoginType(UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE);

                String strName = null;
                if (acct.getDisplayName() != null) {
                    CIApplication.getLoginInfo().SetUserName(acct.getDisplayName());
                    strName = acct.getDisplayName();
                }

                if (acct.getId() != null) {
                    CIApplication.getLoginInfo().SetSocialLoginId(acct.getId());
                    CIApplication.getLoginInfo().SetGoogleLoginId(acct.getId());
                    m_strId = acct.getId();
                }

                if (acct.getEmail() != null) {
                    CIApplication.getLoginInfo().SetUserEmail(acct.getEmail());
                    CIApplication.getLoginInfo().SetGoogleEmail(acct.getEmail());
                    m_strMail = acct.getEmail();
                }

                if (acct.getPhotoUrl() != null) {
                    CIApplication.getLoginInfo().SetUserPhotoUrl(acct.getPhotoUrl().toString());
                }

               SLog.d(TAG,"id:" + m_strId + ",email:" + m_strMail + ",name:" + strName);

                //登入成功
                if (m_userLoginCallbackListener != null) {
                    m_userLoginCallbackListener.onUserConnected(m_iRequestId, m_strId, m_strMail);
                }

            }else {
                //登入失敗
                if (m_userLoginCallbackListener != null) {
                    m_userLoginCallbackListener.onConnectionError(m_iRequestId,
                            UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE +" "
                                    + m_Activity.getString(R.string.menu_log_in)+" "
                                    + m_Activity.getString(R.string.error));
                }
            }
        } else {
            //登入取消
            if (m_userLoginCallbackListener != null) {
                m_userLoginCallbackListener.onCancelled(m_iRequestId,
                        UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE +" "
                                + m_Activity.getString(R.string.menu_log_in)+" "
                                + m_Activity.getString(R.string.cancel));
            }
        }
    }

    //個人資訊-社群媒體連結時,不寫入登入資訊
    public void GetSocialNetworkResult(GoogleSignInResult result) {
       SLog.d(TAG, "[Google+]handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            if (acct != null) {
                m_strId = acct.getId();
                m_strMail = acct.getEmail();

                //登入成功
                if (m_userLoginCallbackListener != null) {
                    m_userLoginCallbackListener.onUserConnected(m_iRequestId, m_strId, m_strMail);
                }
            }else {
                //登入失敗
                if (m_userLoginCallbackListener != null) {
                    m_userLoginCallbackListener.onConnectionError(m_iRequestId,
                            UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE +" "
                                    + m_Activity.getString(R.string.menu_log_in)+" "
                                    + m_Activity.getString(R.string.error));
                }
            }
        } else {
            //登入取消
            if (m_userLoginCallbackListener != null) {
                m_userLoginCallbackListener.onCancelled(m_iRequestId,
                        UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE +" "
                                + m_Activity.getString(R.string.menu_log_in)+" "
                                + m_Activity.getString(R.string.cancel));
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

       SLog.e(TAG, "onConnectionFailed:" + connectionResult);

        if (m_userLoginCallbackListener != null) {
            m_userLoginCallbackListener.onConnectionError(m_iRequestId, connectionResult.getErrorMessage());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (requestCode == GPLUS_SIGN_IN ){
            GetGoogleSignInResult(result);
        }else {
            GetSocialNetworkResult(result);
        }
    }

    /** 以下是web app才能用....
     * Validates that there is a reasonable server client ID in strings.xml, this is only needed
     * to make sure users of this sample follow the README.
     */
    private void validateServerClientID() {

//        String serverClientId = m_Activity.getString(R.string.server_client_id);
//        String suffix = ".apps.googleusercontent.com";
//        if (!serverClientId.trim().endsWith(suffix)) {
//            String message = "Invalid server client ID in strings.xml, must end with " + suffix;
//
//           SLog.d(TAG, message);
//        }
    }
}
