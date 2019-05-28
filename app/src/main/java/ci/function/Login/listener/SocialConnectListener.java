package ci.function.Login.listener;

/**
 * 社群登入用的callback
 * Created by jlchen on 2016/3/7.
 */
public interface SocialConnectListener {
    //連線成功
    void onUserConnected(int requestIdentifier, String strId, String strMail);
    //連線出錯
    void onConnectionError(int requestIdentifier,String message);
    //連線取消
    void onCancelled(int requestIdentifier,String message);
}
