package ci.ui.object;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.chinaairlines.mobile30.R;

import java.util.List;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.function.TimeTable.CISelectDepartureAirpotActivity;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;
import ci.ws.Presenter.Listener.CIInquiryFlightStatusStationListener;

/**
 * Created by kevincheng on 2016/8/5.
 */
public class CIAirportDataManager {

    private callBack                        m_callBack      = null;
    private Context                         m_context       = null;
    private CIInquiryFlightStationPresenter m_presenter     = null;
    private String                          m_iata          = null;
    private List<CIFlightStationEntity>     m_airportData   = null;
    private int                             m_esource       = -1;
    private boolean                         m_bIsToFragment = false;

    public interface callBack{
        void onDataBinded(List<CIFlightStationEntity> datas);
        void onDownloadFailed(String rt_msg);
        void showProgress();
        void hideProgress();
    }

    public CIAirportDataManager(callBack callBack){
        this.m_callBack = callBack;
        this.m_context  = CIApplication.getContext();
    }

    private void fetchAirportData(){
        fetchAirportData(m_bIsToFragment,
                        m_iata,
                        m_esource);
    }

    public void fetchAirportData(final boolean isToFragment,
                                 final String iata,
                                 int esource) {

        m_bIsToFragment = isToFragment;
        m_presenter     = getFlightStationPresenter(esource);
        m_iata          = iata;
        m_esource       = esource;
        new Thread(new Runnable() {
            @Override
            public void run() {

                //判斷是否為ToFragment呼叫，決定抓哪種機場資料
                if (true == isToFragment) {
                    //利用出發地判斷出發機場資料
                    m_airportData = m_presenter.getArrivalSrtationListByDeparture(iata);
                } else {
                    //抓取所有機場
                    m_airportData = m_presenter.getDepatureStationList();
                }

                if (m_airportData == null) {

                    if (null == m_presenter.getAllDepatureStationList()) {
                        //如果總站沒有資料，就向ws要總站資料 by kevin
                        m_presenter.initAllStationDB();
                        m_presenter.InquiryAllStationListFromWS();
                    } else {
                        //如果不是總站沒資料，就是對應表沒資料，跟ws要對應表資料 by kevin
                        m_presenter.initStationODDBForType();
                        m_presenter.InquiryStationODListFromWS();
                    }

                }else {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if(null != m_callBack) {
                                m_callBack.onDataBinded(m_airportData);
                            }
                        }
                    });

                }

            }
        }).start();
    }

    public CIInquiryFlightStationPresenter getFlightStationPresenter(){
        return getFlightStationPresenter(m_esource);
    }

    public CIInquiryFlightStationPresenter getFlightStationPresenter(int esource) {

        if(null != m_presenter && m_esource == esource){
            return m_presenter;
        } else if(null != m_presenter && m_esource != esource){
            m_presenter.cancel();
        }

        CIInquiryFlightStationPresenter.ESource eSource;
        switch (esource) {
            case CISelectDepartureAirpotActivity.FLIGHT_STATUS:
                eSource = CIInquiryFlightStationPresenter.ESource.FlightStatus;
                break;
            case CISelectDepartureAirpotActivity.BOOKT_TICKET:
                eSource = CIInquiryFlightStationPresenter.ESource.BookTicket;
                break;
            case CISelectDepartureAirpotActivity.BOOKT_TICKET_ISOriginal_Y:
                eSource = CIInquiryFlightStationPresenter.ESource.BookTicket_ISOriginal_Y;
                break;
            case CISelectDepartureAirpotActivity.TIME_TABLE:
                eSource = CIInquiryFlightStationPresenter.ESource.TimeTable;
                break;
            default: //by kevin
                eSource = CIInquiryFlightStationPresenter.ESource.TimeTable;
                break;
        }

        CIInquiryFlightStationPresenter presenter = CIInquiryFlightStationPresenter.getInstance(new CIInquiryFlightStatusStationListener() {
            @Override
            public void onAllStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {
                if (null != presenter.getAllDepatureStationList()) {
                    if (null != presenter.getDepatureStationList()) {
                        //重新取得機場資料
                        if(null != m_callBack){
                            fetchAirportData();
                        }
                    } else {
                        //沒有對應表資料 by kevin
                        presenter.InquiryStationODListFromWS();
                    }
                } else {
                    if(null != m_callBack){
                        if(null != m_context){
                            rt_msg = m_context.getString(R.string.download_airport_data_fail);
                        }
                        m_callBack.onDownloadFailed(rt_msg);
                    }
                }
            }

            @Override
            public void onStationError(String rt_code, String rt_msg) {
                m_callBack.onDownloadFailed(rt_msg);
            }

            @Override
            public void onODStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {
                if (null != presenter.getDepatureStationList()) {
                    //重新取得機場資料
                    fetchAirportData();

                } else {
                    //沒有對應表資料
                    if(null != m_callBack){
                        if(null != m_context){
                            rt_msg = m_context.getString(R.string.download_airport_data_fail);
                        }
                        m_callBack.onDownloadFailed(rt_msg);
                    }
                }
            }

            @Override
            public void showProgress() {
                if(null != m_callBack){
                    m_callBack.showProgress();
                }
            }

            @Override
            public void hideProgress() {
                if(null != m_callBack){
                    m_callBack.hideProgress();
                }
            }
        }, eSource);
        return presenter;
    }

    public void cancelTask(){
        if(null != m_presenter){
            m_presenter.cancel();
        }
    }
}
