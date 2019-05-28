package ci.function.Core.Location;

import android.location.Location;
import android.location.LocationManager;

import java.util.List;

/**
 * Created by 1500242 on 2015/6/4.
 */
public class SLocationUtils {
    private SLocationUtils() {
        // non-instance
    }


    public static Location getLastBestLocation(LocationManager locationManager) {
        Location result          = null;
        Location compareLocation = null;

        List<String> providers = locationManager.getAllProviders();
        for (String provider : providers) {
            compareLocation = locationManager.getLastKnownLocation(provider);
            if (null == result && compareLocation != null) {
                result = compareLocation;
                continue;
            }
            if (compareLocation != null) {
                boolean bBetter = SLocationUtils.compareBetterLocation(
                        compareLocation,
                        result);
                result = bBetter ? compareLocation : result;
            }
        }
        return result;
    }

    public static boolean compareBetterLocation(Location currentLocation,
                                                Location previousLocation) {
        final int INTERVAL_TIME            = 1000 * 5;
        final int INTERVAL_DISTANCE        = 10;
        final int EXCEED_TIME              = 1000 * 5;
        final int ACCURACY_DEVIATION_LIMIT = 200;

        if (previousLocation == null) {
            return true;
        }

        long    timePass                    =
                currentLocation.getTime() - previousLocation.getTime();
        boolean bCurrentLocationExceedNewer = timePass > EXCEED_TIME;
        boolean bCurrentLocationExceedOlder = timePass < -EXCEED_TIME;
        boolean bNewer                      = timePass > 0;

        // Estimate data is newer,otherwise estimate the accuracy
        if (bCurrentLocationExceedNewer) {
            return true;
        } else if (bCurrentLocationExceedOlder) {
            return false;
        }

        int iAccuracyDelta = (int) (currentLocation.getAccuracy() -
                                    previousLocation.getAccuracy());
        boolean bMoreAccurate = iAccuracyDelta < 0;
        boolean bLessAccurate = iAccuracyDelta > 0;
        boolean bNotAccuracy  = iAccuracyDelta > ACCURACY_DEVIATION_LIMIT;
        boolean bFromSameProvider =
                isSameProvider(currentLocation.getProvider(),
                               previousLocation.getProvider());

        if (bMoreAccurate) {
            return true;
        } else if (bNewer && !bLessAccurate) {
            // the accurate equal
            return true;
        } else if (bNewer && !bNotAccuracy && bFromSameProvider) {
            // using same provider if the data newer and accuracy in the accepted range
            return true;
        }
        return false;
    }

    private static boolean isSameProvider(String provider1, String provider2) {
        if (null == provider1) {
            return null == provider2;
        }
        return provider1.equals(provider2);
    }
}
