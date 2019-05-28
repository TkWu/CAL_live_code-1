package ci.ws.cores;


import ci.ws.cores.object.CIResponse;

/**
 * Created by Ryan on 16/3/28.
 */
public interface CIResponseCallback {

    /**
     * 發送成功
     *
     * @param respBody response body
     * @param code     HTTP status code.
     */
    void onSuccess(String respBody, int code);

    /**
     * 發送失敗(網路異常,URL異常...)
     *
     * @param response  Response object
     * @param code      HTTP status code
     * @param exception Throws runtime exception
     */
    void onError(CIResponse response, int code, Exception exception);

}
