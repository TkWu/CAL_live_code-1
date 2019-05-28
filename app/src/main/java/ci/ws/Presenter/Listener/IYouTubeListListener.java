package ci.ws.Presenter.Listener;

import ci.ws.Models.entities.CIYouTubeDataList;

/**
 * Created by kevincheng on 2016/4/14.
 */
public interface IYouTubeListListener {
    void showProgress();
    void hideProgress();
    void onDataBinded(CIYouTubeDataList list);
    void onDataFetchFeild(String msg);
}
