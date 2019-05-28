package ci.ui.CAL_Map;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import ci.function.Core.SLog;

import java.util.Timer;
import java.util.TimerTask;

import ci.ws.Models.entities.CITripListResp_Itinerary;

/**
 * Created by kevincheng on 2016/5/26.
 * 用來管理向華航ws取得飛航經緯度
 */
public class FlightLocationManager {

    public interface Callback{
        void onDataBinded(String result);
    }

    private QueryWebservice m_QWebservice;
    private Timer           m_timer;
    private Callback        m_CallBack;
    private int             m_tsec = 0;
    private GetLAtLongDataTask  m_task;

    public FlightLocationManager(Callback callback){
        m_CallBack = callback;
    }



    private class GetLAtLongDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            try {
                result = m_QWebservice.QueryMapLatLong();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
           SLog.d("cal_map_result",result);
            if(null != m_CallBack){
                m_CallBack.onDataBinded(result);
            }
        }
    }

    private Handler m_timeHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                m_task = new GetLAtLongDataTask();
                m_task.execute();
            }
        }
    };

    private TimerTask getTimerTask(){
        return new TimerTask() {
            @Override
            public void run() {
                m_tsec++;
                //每三百秒執行一次
                if (m_tsec % 300 == 0) {
                    Message message = new Message();
                    message.what = 1;
                    m_timeHandler.sendMessage(message);
                }
            }
        };
    }

    /**
     * 執行AsynTask
     */
    public void executeTask(CITripListResp_Itinerary data){
        m_QWebservice = new QueryWebservice(1,data);
        m_task = new GetLAtLongDataTask();
        m_task.execute();
        m_timer = new Timer();
        m_timer.schedule(getTimerTask(), 0, 1000);
    }

    /**
     * 取消AsynTask
     */
    public void cancleTask(){
        try {
            m_QWebservice = null;
            m_timer.cancel();
            m_task.cancel(true);
        } catch(Exception e){

        }
    }
}
