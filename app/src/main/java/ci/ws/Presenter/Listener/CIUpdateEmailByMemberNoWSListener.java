package ci.ws.Presenter.Listener;

/**
 * Created by Ryan on 16/4/19.
 */
public interface CIUpdateEmailByMemberNoWSListener {

    /**
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onUpdateSuccess( String rt_code, String rt_msg );

    /**
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onUpdateError( String rt_code, String rt_msg );

    /**
     * 顯示進度圖示
     */
    void showProgress();

    /**
     * 隱藏進度圖示
     */
    void hideProgress();

    /**
     * 收到rt_code為999或995, 表示授權失敗
     *
     * @param rt_code   result code
     * @param rt_msg    result msg
     */
    void onAuthorizationFailedError(String rt_code, String rt_msg);
}
