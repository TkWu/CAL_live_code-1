package ci.function.About;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.entities.CIYouTubeDataList;
import ci.ws.Models.CIYouTubeModel;
import ci.ws.Models.CIYouTubeModel.CallBack;
import ci.ws.Presenter.Listener.IYouTubeListListener;

/**
 * Created by kevincheng on 2016/4/14.
 */
public class CIYouTubeListPresenter {

    private IYouTubeListListener m_listener;
    private        Handler          m_handler;
    private        CIYouTubeModel   m_model;

    public CIYouTubeListPresenter(IYouTubeListListener listener) {
        m_listener = listener;
        initMainThreadHandler();
    }

    private void initMainThreadHandler() {
        m_handler = new Handler(Looper.getMainLooper());
    }

    public void fetchYouTubeData() {
        m_listener.showProgress();
        m_model = new CIYouTubeModel();
        m_model.findDataByPlayListId(CIYouTubeModel.YOUTUBE_PLAYLIST_ID, callBack);
    }

    public void interrupt(){
        m_listener.hideProgress();
        if(null != m_model){
            m_model.Cancel();
        }
    }

    CallBack callBack = new CallBack() {
        @Override
        public void onDownloadSuccess() {
            m_handler.post(new Runnable() {
                @Override
                public void run() {
                    CIYouTubeDataList datas = CIYouTubeModel.findLocalData();
                    if(null != m_listener){
                        m_listener.hideProgress();
                        m_listener.onDataBinded(datas);
                    }
                }
            });
        }

        @Override
        public void onDownloadFailed(final String msg) {
            m_handler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_listener) {
                        m_listener.hideProgress();
                        m_listener.onDataFetchFeild(msg);
                    }
                }
            });

        }
    };
}
