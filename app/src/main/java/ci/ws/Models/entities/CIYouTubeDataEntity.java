package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kevincheng on 2016/4/14.
 * 此資料表主要是YouTube播放清單中的影片資料
 */

@DatabaseTable(tableName = "youtube_datas")
public class CIYouTubeDataEntity implements Cloneable{

    /**generatedId = true 代表自動建立id*/
    @DatabaseField(generatedId = true)
    public int id;

    /**
     * @Expose 需加此註解才可以讓Gson解析
     * @DatabaseField 加此註解代表要讓ormlite加入table時建立此欄位
     */
    @Expose
    @DatabaseField
    public String title;        //影片標題

    @Expose
    @DatabaseField
    public String description;  //影片敘述

    @Expose
    @DatabaseField
    public String videoId;      //影片ID

    public CIYouTubeDataEntity clone(){
        try{
            return (CIYouTubeDataEntity)super.clone();
        }catch (Exception e){

        }
        return null;
    }
}
