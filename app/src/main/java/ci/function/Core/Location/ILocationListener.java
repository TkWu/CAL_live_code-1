package ci.function.Core.Location;

import android.location.Location;

/**
 * Created by 1500242 on 2015/7/11.
 */
public interface ILocationListener {
    void onLocationChanged(Location location);
    void onFailed();
}
