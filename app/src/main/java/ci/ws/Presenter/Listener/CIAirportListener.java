package ci.ws.Presenter.Listener;

import android.location.Location;


/**
 * Created by kevincheng on 2016/3/6.
 */
public interface CIAirportListener {
    void showProgress();
    void hideProgress();
    void onLocationBinded(Location location);
    void onfetchLocationFail();
    void onNoAvailableLocationProvider();
}
