package ci.ws.Presenter.Listener;

import android.location.Location;

/**
 * Created by kevincheng on 2016/3/6.
 */
public interface IGlobalServiceListener {
    void showProgress();
    void hideProgress();
    void onLocationBinding();
    void onLocationBinded(Location location);
    void onfetchLocationFail();
    void onNoAvailableLocationProvider();
    void onDownloadSuccess();
    void onDownloadError(String rt_code, String rt_msg);
}
