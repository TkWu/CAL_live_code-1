package ci.ws.cores;

import java.util.HashMap;
import java.util.Map;

import ci.ws.cores.base.CIWSBaseReqLanuch;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/3/25.
 * WS 發射器, 透過此Class, 來發送WS,
 * Send request 邏輯寫在 CIWSBaseReqLanuch
 */
public class CIWSLanuch extends CIWSBaseReqLanuch {

    @Override
    protected int getConnectTimeout() {
        return WSConfig.DEF_CONNECTION_TIME_OUT;
    }

    @Override
    protected int getReadTimeout() {
        return WSConfig.DEF_READ_TIME_OUT;
    }

    @Override
    protected Map<String, String> getDefaultHeaders() {

        Map<String, String > header = new HashMap<>();
        header.put(WSConfig.DEF_CONTENT_TYPE_KEY, WSConfig.DEF_CONTENT_TYPE);

        return header;
    }
}
