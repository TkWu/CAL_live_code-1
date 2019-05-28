package ci.ws.cores.base;

import ci.ws.cores.CIResponseCallback;
import ci.ws.cores.object.CIRequest;

/**
 * Created by JobsNo5 on 16/4/2.
 */
public interface CIWebservice {

    void connection(CIRequest request, CIResponseCallback callback);

    boolean cancel(boolean mayInterruptIfRunning);

    boolean isCancelled();
}
