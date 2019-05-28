package ci.ws.cores.object;


import java.util.Map;

/**
 * Created by Ryan on 16/3/28.
 */
public class CIRequest {

    protected String              m_strUrl     = null;
    protected EMethod             m_method     = null;
    protected Map<String, String> m_mapHeaders = null;
    protected String              m_strBody    = null;


    public CIRequest(String url, EMethod method, Map<String, String> header, String body) {
        this.m_strUrl = url;
        this.m_method = (method == null ? EMethod.GET : method);
        this.m_mapHeaders = header;
        this.m_strBody = body;
    }

    public String url() {
        return m_strUrl;
    }

    public EMethod method() {
        return m_method;
    }

    public Map<String, String> headers() {
        return m_mapHeaders;
    }

    public String body() {
        return m_strBody;
    }

    public void setheaders( Map<String, String> header ){
        m_mapHeaders = header;
    }
}
