package ci.ws.Presenter;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import ci.function.Core.CIApplication;
import ci.ws.Models.entities.CIGlobalServiceList;
import ci.function.Core.Location.ILocationListener;
import ci.function.Core.Location.SSingleLocationUpdater;
import ci.ws.Models.CIGlobalServiceModel;
import ci.ws.Presenter.Listener.IGlobalServiceListener;

/**
 * Created by kevin on 2016/3/6.
 */
public class CIGlobalServicePresenter implements CIGlobalServiceModel.CallBack{
    private IGlobalServiceListener m_listener           = null;
    private SSingleLocationUpdater m_locationUpdater    = null;
    private CIGlobalServiceModel   m_model              = null;
    private static Handler         s_hdUIThreadhandler  = null;

    public static CIGlobalServicePresenter getInstance(IGlobalServiceListener listener){

        CIGlobalServicePresenter m_Instance = new CIGlobalServicePresenter(listener);

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        return m_Instance;
    }

    public CIGlobalServicePresenter(IGlobalServiceListener listener) {
        this.m_listener = listener;
    }

    public void downloadDataFromWS() {
        if(null != m_listener){
            m_listener.showProgress();
        }
        if(null == m_model){
            m_model = new CIGlobalServiceModel(this);
        }
        m_model.getFromWS();
    }

    public CIGlobalServiceList fetchData() {
        if(null == m_model){
            m_model = new CIGlobalServiceModel(this);
        }
        return m_model.findData();
    }

    public void fetchLocation() {

        ILocationListener listener = new ILocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                if(Thread.currentThread() == Looper.getMainLooper().getThread()){
                    if(null != m_listener){
                        m_listener.onLocationBinded(location);
                    }
                } else {
                    s_hdUIThreadhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(null != m_listener){
                                m_listener.onLocationBinded(location);
                            }
                        }
                    });
                }

            }

            @Override
            public void onFailed() {

                s_hdUIThreadhandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        m_listener.hideProgress(); //失敗都不隱藏progress
                        if(null != m_listener){
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
        m_listener.onLocationBinding();
    }

    public CIGlobalServiceList findDataByBranch(String str){
        return m_model.findDataByBranch(str);
    }

    public void interruptGPS(){
        if(null != m_locationUpdater){
            m_locationUpdater.cancel();
        }
    }

    //request and response interrupt on pause
    public void interrupt(){
        if(null != m_listener){
            m_listener.hideProgress();
        }

        if(null != m_model){
            m_model.CancelRequest();
        }

        if(null != m_locationUpdater){
            m_locationUpdater.cancel();
        }
    }

    @Override
    public void onSuccess( String rt_code, String rt_msg) {

        s_hdUIThreadhandler.post(new Runnable() {
            @Override
            public void run() {
                if ( null != m_listener ) {
                    m_listener.onDownloadSuccess();
                    m_listener.hideProgress();
                }
            }
        });

    }

    @Override
    public void onError(final String rt_code,final String rt_msg) {

        s_hdUIThreadhandler.post(new Runnable() {
            @Override
            public void run() {
                if ( null != m_listener ) {
                    m_listener.onDownloadError(rt_code, rt_msg);
                    m_listener.hideProgress();
                }
            }
        });

    }
}
