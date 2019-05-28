package ci.ws.cores.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ryan on 16/3/28.
 */
public class CIResponse extends CIRequest {

    private int                       m_iCode   = 0;
    private Map<String, List<String>> m_headers = null;

    public CIResponse(String url, EMethod method, Map<String, List<String>> headers, String body, int code) {
        super(url, method, null, body);
        this.m_headers = headers;
        this.m_iCode = code;
    }

    public int code() {
        return m_iCode;
    }


    public Map<String, List<String>> getHeaders() {
        return m_headers;
    }

    @Override
    public Map<String, String> headers() {
        if (null == m_mapHeaders) {
            m_mapHeaders = new HashMap<String, String>();
            StringBuilder sb = new StringBuilder();
            for (String key : m_headers.keySet()) {
                for (int i = 0; i < m_headers.get(key).size(); i++) {
                    sb.append(m_headers.get(key).get(i));
                    sb.append(",");
                }
                sb.setLength(sb.length() - 1);
                m_mapHeaders.put(key, sb.toString());
            }
        }

        return m_mapHeaders;
    }
}
