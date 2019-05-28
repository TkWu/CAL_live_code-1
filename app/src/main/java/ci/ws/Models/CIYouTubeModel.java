package ci.ws.Models;

import android.content.Context;
import android.text.TextUtils;
import ci.function.Core.SLog;

import com.chinaairlines.mobile30.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ci.function.Core.CIApplication;
import ci.ws.Models.entities.CIYouTubeDataEntity;
import ci.ws.Models.entities.CIYouTubeDataList;
import ci.ws.Models.entities.CIYouTubePlayListItemsEntity;
import ci.ws.Models.entities.CIYouTubeResponceDTO;
import ci.ws.cores.CIResponseCallback;
import ci.ws.cores.base.CIWSBaseReqLanuch;
import ci.ws.cores.object.CIRequest;
import ci.ws.cores.object.CIResponse;
import ci.ws.cores.object.EMethod;
import ci.ws.cores.object.GsonTool;

/**
 * Created by kevincheng on 2016/4/14.
 */
public class CIYouTubeModel {
    private static       Context                                           s_context              = CIApplication.getContext();
    private final static String                                            YOUTUBE_DATA_KEY       = s_context.getString(R.string.server_api_key);
    public final static  String                                            YOUTUBE_PLAYLIST_ID    = "PLIbi5Q5us_bt4PmksYrmD4IjOu_ku2nB2";
    private final static String                                            URL_YOUTUBE_API        = "https://www.googleapis.com/youtube/v3/";
    private final static String                                            PARAM_PLAYLISTITEM     = "playlistItems?";
    private final static String                                            INTEGER_PLAYLIST_ITEMS = "20";
    private              String                                             m_nextPageToken       = null;
    private              RetrievePlayListItemsAsyncTask                    m_task                 = null;
    private static final String                                            TAG                    = CIYouTubeModel.class.getSimpleName();
    private static       RuntimeExceptionDao<CIYouTubeDataEntity, Integer> s_dao
                                                                                                  = CIApplication.getDbManager().getRuntimeExceptionDao(CIYouTubeDataEntity.class);
    private static       ConnectionSource                                  s_connectionSource
                                                                                                  = CIApplication.getDbManager().getConnectionSource();
    public interface CallBack {
        void onDownloadSuccess();
        void onDownloadFailed(String msg);
    }

    public static void insert(CIYouTubeDataList datas) {
        try {
            s_dao.create(datas);
        } catch (Exception e) {
           SLog.e("Exception", e.toString());
        }

    }

    /**
     * 清空資料表所有資料
     * */
    public static void clear() {
        try {
            TableUtils.clearTable(s_connectionSource,
                    CIYouTubeDataEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void findDataByPlayListId(String id, CallBack callback) {
        if (true == TextUtils.isEmpty(m_nextPageToken)) {
            clear();
        }
        m_task = new RetrievePlayListItemsAsyncTask();
        String                         url  = getPlayListUrl(id);
        m_task.connection(getRequest(url), getResponceCallback(callback));
    }

    public static CIYouTubeDataList findLocalData() {
        List<CIYouTubeDataEntity> list = s_dao.queryForAll();
        CIYouTubeDataList datas = new CIYouTubeDataList();
        for(CIYouTubeDataEntity data:list){
            datas.add(data);
        }
        return datas;
    }

    public void Cancel(){
        if(null != m_task){
            m_task.cancel(true);
        }
    }


    private CIRequest getRequest(String url) {
        return new CIRequest(url, EMethod.GET, null, null);
    }

    private String getPlayListUrl(String id) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("part", "snippet");
        params.put("maxResults", INTEGER_PLAYLIST_ITEMS);
        if (!TextUtils.isEmpty(m_nextPageToken)) {
            params.put("pageToken", m_nextPageToken);
            m_nextPageToken = null;
        }
        params.put("playlistId", id);
        params.put("fields", "items%2Fsnippet(title%2Cdescription%2CresourceId%2CplaylistId)%2CnextPageToken");
        params.put("key", YOUTUBE_DATA_KEY);
        String url = URL_YOUTUBE_API + PARAM_PLAYLISTITEM;
        Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            url+= "&"+key+"="+val;
        }
        return url;
    }

    static class RetrievePlayListItemsAsyncTask extends CIWSBaseReqLanuch{

        @Override
        protected int getConnectTimeout() {
            return 30000;
        }

        @Override
        protected int getReadTimeout() {
            return 30000;
        }

        @Override
        protected Map<String, String> getDefaultHeaders() {
            return null;
        }
    }

    private CIResponseCallback getResponceCallback(final CallBack listener){
        CIResponseCallback callback = new CIResponseCallback() {
            @Override
            public void onSuccess(String respBody, int code) {
                CIYouTubeResponceDTO dtoDatas = GsonTool.getGson().fromJson(respBody, CIYouTubeResponceDTO.class);
                //Log.e("YouTube Data", GsonTool.toJson(dtoDatas));
                m_nextPageToken = dtoDatas.nextPageToken;
                CIYouTubeDataList datas = new CIYouTubeDataList();
                for(CIYouTubePlayListItemsEntity item : dtoDatas.items){
                    CIYouTubeDataEntity data = new CIYouTubeDataEntity();
                    data.title = item.snippet.title;
                    data.description = item.snippet.description;
                    data.videoId = item.snippet.resourceId.videoId;
                    datas.add(data);
                }

                //沒有下一頁的資料的話就清空資料表，不累加
                if (true == TextUtils.isEmpty(m_nextPageToken)) {
                    clear();
                }
                insert(datas);

                String  playlistId = dtoDatas.items.get(0).snippet.playlistId;
                if(false == TextUtils.isEmpty(m_nextPageToken)) {
                    findDataByPlayListId(playlistId,listener);
                } else {
                    if(null != listener){
                        listener.onDownloadSuccess();
                    }
                }
            }

            @Override
            public void onError(CIResponse response, int code, Exception exception) {
                if(null != listener){
                    listener.onDownloadFailed(exception.getMessage());
                }
            }
        };
        return callback;
    }

}
