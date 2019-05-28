package ci.ws.cores;

import java.util.Map;

import ci.ws.cores.base.CIWSBaseLanuch;

/**
 * Created by Ryan on 16/4/2.
 * WS 發射器, 透過此Class, 來發送WS,
 * Send request 邏輯寫在 CIWSBaseLanuch, 由於取授權不需要設定 Headers, 故另外拉此class
 * 使用此Lanuch 需要自帶Thread
 */
public class CIWSAuthLanuch extends CIWSBaseLanuch {


    public static final int    DEF_CONNECTION_TIME_OUT = 30 * 1000;
    public static final int    DEF_READ_TIME_OUT       = 30 * 1000;

    @Override
    protected int getConnectTimeout() {
        return DEF_CONNECTION_TIME_OUT;
    }

    @Override
    protected int getReadTimeout() {
        return DEF_READ_TIME_OUT;
    }

    @Override
    protected Map<String, String> getDefaultHeaders() { return null; }

}
