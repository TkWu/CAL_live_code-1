package ci.ws.Presenter;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import ci.function.Core.CIApplication;
import ci.function.Core.Location.ILocationListener;
import ci.function.Core.Location.SSingleLocationUpdater;
import ci.ws.Presenter.Listener.CIAirportListener;

/**
 * Created by s on 2016/4/1.
 */

public class CISelectDepartureAirportPresenter {
    private Handler m_hdUIThreadhandler = null;
    private CIAirportListener m_listener = null;
    private SSingleLocationUpdater m_locationUpdater   = null;

    public CISelectDepartureAirportPresenter(CIAirportListener listener) {
        this.m_listener = listener;
        startUIThreadHandler();
    }

    public void fetchLocation() {

        ILocationListener listener = new ILocationListener() {
            @Override
            public void onLocationChanged(final Location location) {

                m_hdUIThreadhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(null != m_listener){
                            m_listener.hideProgress();
                            m_listener.onLocationBinded(location);
                        }
                    }
                });

            }

            @Override
            public void onFailed() {

                m_hdUIThreadhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(null != m_listener) {
//                        m_listener.hideProgress(); //失敗都不隱藏progress
                            m_listener.onfetchLocationFail();
                        }
                    }
                });
            }
        };
        m_locationUpdater = CIApplication.getLocationUpdater(listener);
        m_locationUpdater.getLastKnownLocation();

        /**無可用GPS服務*/
        if (false == CIApplication.getSysResourceManager()
                .isLocationServiceAvailable()) {
            m_listener.onNoAvailableLocationProvider();
            return;
        }

        m_locationUpdater.requestUpdate();
        m_listener.showProgress();
    }

    //request and response interrupt on pause
    public void interrupt(){
        if(null != m_listener){
            m_listener.hideProgress();
        }
        if(null != m_locationUpdater){
            m_locationUpdater.cancel();
        }
    }

    private void startUIThreadHandler() {
        m_hdUIThreadhandler = new Handler(Looper.getMainLooper());
    }


}
